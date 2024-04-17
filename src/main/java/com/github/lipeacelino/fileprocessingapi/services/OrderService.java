package com.github.lipeacelino.fileprocessingapi.services;

import com.github.lipeacelino.fileprocessingapi.documents.internals.Order;
import com.github.lipeacelino.fileprocessingapi.documents.OrderDetail;
import com.github.lipeacelino.fileprocessingapi.documents.internals.Product;
import com.github.lipeacelino.fileprocessingapi.dto.OrderDataDTO;
import com.github.lipeacelino.fileprocessingapi.dto.OrderDetailResponseDTO;
import com.github.lipeacelino.fileprocessingapi.dto.ParametersInputDTO;
import com.github.lipeacelino.fileprocessingapi.dto.ProcessingResultDTO;
import com.github.lipeacelino.fileprocessingapi.mappers.OrderMapper;
import com.github.lipeacelino.fileprocessingapi.repositories.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderDetailRepository orderDetailRepository;

    private final OrderMapper orderMapper;

    private final MongoTemplate mongoTemplate;

    private static final Pattern ORDER_ID_PRODUCT_ID_PATTERN = Pattern.compile(".*(?<=[(a-zA-Z)+.])(\\d+)(?=\\s).*");
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("^(.{10}).*");
    private static final Pattern PRODUCT_ID_PATTERN = Pattern.compile("^.{10}(.{10}).*");
    private static final Pattern NAME_PATTERN = Pattern.compile("^\\d{10}\\s+|\\d{10}\\d+.*$");

    public ProcessingResultDTO saveOrderDetailFromFile(MultipartFile file) {
        List<String> resultDetails = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                var orderDataDTO = extractData(line);
                if (validData(line, orderDataDTO, resultDetails)){
                    orderDetailRepository.save(processData(orderDataDTO));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultDetails.isEmpty()
                ? new ProcessingResultDTO((List.of("Orders processed successfully")))
                : new ProcessingResultDTO(resultDetails);
    }

    @Cacheable("orderDetail")
    public Page<OrderDetailResponseDTO> findAllOrderDetail(Pageable pageable, ParametersInputDTO parametersInputDTO) {
        var query = new Query();
        query.with(pageable);
        if(parametersInputDTO.userId()!=null)query.addCriteria(Criteria.where("userId").is(parametersInputDTO.userId()));
        if(parametersInputDTO.name()!=null)query.addCriteria(Criteria.where("name").regex("^"+ parametersInputDTO.name(),"i"));
        var total = mongoTemplate.count(new Query(), OrderDetail.class);
        var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        var content = mongoTemplate.find(query, OrderDetail.class);
        var orderDetailPage = PageableExecutionUtils.getPage(content, pageRequest, () -> total);
        return orderDetailPage.map(orderMapper::mapOrderDetailToOrderDetailDTOResponse);
    }

    private OrderDataDTO extractData(String line)  {
            var userId = getUserIdFromLine(line);
            var name = getNameFromLine(line);
            var orderId = getOrderIdFromLine(line);
            var productId = getProductIdFromLine(line);
            var value = getValueFromLine(line);
            var date = getDateFromLine(line);

        return OrderDataDTO.builder()
                .userId(userId)
                .name(name)
                .orderId(orderId)
                .productId(productId)
                .value(value)
                .date(date)
                .build();
    }

    private OrderDetail processData(OrderDataDTO orderDataDTO) {
        var product = Product.builder()
                .productId(orderDataDTO.productId())
                .value(orderDataDTO.value())
                .build();
        var orderDetailOptional = orderDetailRepository.findOrderDetailByUserIdAndName(orderDataDTO.userId(), orderDataDTO.name());
        Order order;
        OrderDetail orderDetail;
        if (orderDetailOptional.isPresent()){
            boolean orderAlreadyExists = orderDetailOptional.get().getOrders().stream().anyMatch(order1 -> order1.getOrderId().equals(orderDataDTO.orderId()));
            order = orderAlreadyExists ? updateOrder(orderDetailOptional.get(), orderDataDTO, product) : createOrder(orderDataDTO, product);
            orderDetail = updateOrderDetail(orderDetailOptional.get(), order);
        } else {
            order = createOrder(orderDataDTO, product);
            orderDetail = createOrderDetail(orderDataDTO, order);
        }
        return orderDetail;
    }

    private boolean validData(String line, OrderDataDTO orderDataDTO, List<String> resultDetails) {
        if (orderExistsForAnotherCustomer(orderDataDTO.userId(), orderDataDTO.orderId())) {
            resultDetails.add(line.concat(" - Order exists for another user"));
            return false;
        }
        if (findOrderDetailByOrderIdAndProductId(orderDataDTO.orderId(), orderDataDTO.productId())) {
            resultDetails.add(line.concat(" - Order and product have already been registered before"));
            return false;
        }
        return true;
    }

    private boolean orderExistsForAnotherCustomer(Integer userId, Integer orderId) {
       return orderDetailRepository.findOrderDetailByUserIdAndOrderId(userId, orderId).isPresent();
    }

    private boolean findOrderDetailByOrderIdAndProductId(Integer orderId, Integer productId) {
        return orderDetailRepository.findOrderDetailByOrderIdAndProductId(orderId, productId).isPresent();
    }

    private Order updateOrder(OrderDetail orderDetail, OrderDataDTO orderDataDTO, Product product) {
        var order = orderDetail
                .getOrders()
                .stream().filter(order1 -> order1.getOrderId().equals(orderDataDTO.orderId()))
                .findFirst()
                .get();
        order.getProducts().add(product);
        order.setTotal(order.getTotal().add(product.getValue()));
        return order;
    }

    private Order createOrder(OrderDataDTO orderDataDTO, Product product) {
        return Order.builder()
                .orderId(orderDataDTO.orderId())
                .total(orderDataDTO.value())
                .date(LocalDate.parse(orderDataDTO.date(), DateTimeFormatter.ofPattern("yyyyMMdd")))
                .products(new ArrayList<>(List.of(product)))
                .build();
    }

    private OrderDetail updateOrderDetail(OrderDetail orderDetail, Order order) {
        if (orderDetail.getOrders().stream().anyMatch(order1 -> order1.getOrderId().equals(order.getOrderId()))) {
            orderDetail.getOrders().remove(orderDetail.getOrders().stream()
                    .filter(order1 -> order1.getOrderId().equals(order.getOrderId()))
                    .findFirst()
                    .get());
            orderDetail.getOrders().add(order);
        } else {
            orderDetail.getOrders().add(order);
        }
        return orderDetail;
    }

    private OrderDetail createOrderDetail(OrderDataDTO orderDataDTO, Order order) {
        return OrderDetail.builder()
                .userId(orderDataDTO.userId())
                .name(orderDataDTO.name())
                .orders(new ArrayList<>(List.of(order)))
                .build();
    }

    private Integer getUserIdFromLine(String line) {
        return Integer.valueOf(line.substring(0, 10));
    }

    private String getNameFromLine(String line) {
        return NAME_PATTERN.matcher(line).replaceAll("").trim();
    }

    private Integer getOrderIdFromLine(String line) {
        var orderIdAndProductId = ORDER_ID_PRODUCT_ID_PATTERN.matcher(line).replaceFirst("$1");
        return Integer.valueOf(ORDER_ID_PATTERN.matcher(orderIdAndProductId).replaceFirst("$1"));
    }

    private Integer getProductIdFromLine(String line) {
        var orderIdAndProductId = ORDER_ID_PRODUCT_ID_PATTERN.matcher(line).replaceFirst("$1");
        return Integer.valueOf(PRODUCT_ID_PATTERN.matcher(orderIdAndProductId).replaceFirst("$1"));
    }

    private BigDecimal getValueFromLine(String line) {
        var valueAndDate = line.replaceFirst("^.*\\s", "");
        return new BigDecimal(valueAndDate.replaceFirst(valueAndDate.substring(valueAndDate.length()-8, valueAndDate.length()), ""));
    }

    private String getDateFromLine(String line) {
        return line.substring(line.length()-8, line.length());
    }

}
