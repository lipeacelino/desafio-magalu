package com.github.lipeacelino.fileconvertapi.services;

import com.github.lipeacelino.fileconvertapi.dto.OrderDataDTO;
import com.github.lipeacelino.fileconvertapi.entities.Order;
import com.github.lipeacelino.fileconvertapi.entities.OrderDetail;
import com.github.lipeacelino.fileconvertapi.entities.Product;
import com.github.lipeacelino.fileconvertapi.repositories.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class OrderService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    private static final Pattern ORDER_ID_PRODUCT_ID_PATTERN = Pattern.compile(".*(?<=[(a-zA-Z)+.])(\\d+)(?=\\s).*");
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("^(.{10}).*");
    private static final Pattern PRODUCT_ID_PATTERN = Pattern.compile("^.{10}(.{10}).*");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^\\d{10}\\s+|\\d{10}\\d+.*$");

    public void saveOrderDetails(MultipartFile file) {
//        ExecutorService executor = Executors.newFixedThreadPool(8);

        List<String> extractedDataList = new ArrayList<>();
        List<OrderDataDTO> orderDataList = Collections.synchronizedList(new ArrayList<>());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String finalLine = line;
                extractedDataList.add(finalLine);
//                System.out.println("Linha processada: " + line);
            }
            extractedDataList.forEach(extractedLine -> {
                processExtractedData(extractedLine, orderDataList);
//                executor.execute(() -> {
//                    processExtractedData(extractedLine, orderDataList);
//                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } //finally {
//            executor.shutdown();
//            try {
//                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
//                    executor.shutdownNow();
//                }
//            } catch (InterruptedException ex) {
//                executor.shutdownNow();
//                Thread.currentThread().interrupt();
//            }
//        }
        processForSave(orderDataList);
    }


    private void processExtractedData(String line, List<OrderDataDTO> orderDataList)  {
            var userId = getUserIdFromLine(line);
            var username = getUsernameFromLine(line);
            var orderId = getOrderIdFromLine(line);
            var productId = getProductIdFromLine(line);
            var value = getValueFromLine(line);
            var date = getDateFromLine(line);

            var orderDataDTO = OrderDataDTO.builder()
                    .userId(userId)
                    .username(username)
                    .orderId(orderId)
                    .productId(productId)
                    .value(value)
                    .date(date)
                    .build();
            orderDataList.add(orderDataDTO);
    }

    public void processForSave(List<OrderDataDTO> orderDataDTOList) {
        orderDataDTOList.forEach(orderDataDTO -> {
            var product = buildProduct(orderDataDTO);

            buildOrder(orderDataDTO, product);

//            buildOrderDetail(orderDataDTO, order, product);
        });
    }
    private Product buildProduct(OrderDataDTO orderDataDTO) {
        return Product.builder()
                .productId(orderDataDTO.productId())
                .value(orderDataDTO.value())
                .build();
    }

    private void buildOrder(OrderDataDTO orderDataDTO, Product product) {
        var orderDetailOptional = orderDetailRepository.findOrderDetailByOrderId(orderDataDTO.orderId());
        Order order;
        if (orderDetailOptional.isPresent()) {
            order = orderDetailOptional.get()
                    .getOrders()
                    .stream().filter(order1 -> order1.getOrderId().equals(orderDataDTO.orderId()))
                    .findFirst()
                    .get();
            order.getProducts().add(product);
            //update total
            order.setTotal(order.getTotal().add(product.getValue()));
        } else {
            order = Order.builder()
                    .orderId(orderDataDTO.orderId())
                    .total(orderDataDTO.value())
                    .date(LocalDate.parse(orderDataDTO.date(), DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .products(new ArrayList<>(List.of(product)))
                    .build();
        }
        buildOrderDetail(orderDataDTO, order);
        orderDetailRepository.save(orderDetailOptional.get());
    }

    private OrderDetail buildOrderDetail(OrderDataDTO orderDataDTO, Order order) {
        var orderDetailOptional = orderDetailRepository.findOrderDetailByUserId(orderDataDTO.userId());
        OrderDetail orderDetail;
        if (orderDetailOptional.isPresent()) {
            orderDetail = orderDetailOptional.get();
            if (orderDetail.getOrders().stream().filter(order1 -> order1.getOrderId().equals(order.getOrderId())).findFirst().isPresent()) {
                orderDetail.getOrders().remove(orderDetail.getOrders().stream()
                        .filter(order1 -> order1.getOrderId().equals(order.getOrderId()))
                        .findFirst().get());
                orderDetail.getOrders().add(order);
            } else {
                orderDetail.getOrders().add(order);
            }
        } else {
            orderDetail = OrderDetail.builder()
                    .userId(orderDataDTO.userId())
                    .username(orderDataDTO.username())
                    .orders(new ArrayList<>(List.of(order)))
                    .build();
//            var order = Order.builder().orderId(orderDataDTO.orderId()).total(orderDataDTO.value()).date(LocalDate.parse(orderDataDTO.date(), DateTimeFormatter.ofPattern("yyyyMMdd"))).products(new ArrayList<>(List.of(product))).build();
//            order.getProducts().forEach(prod -> order.setTotal(order.getTotal().add(prod.getValue())));
        }
        orderDetailRepository.save(orderDetail);
        return orderDetail;
    }

    private Integer getUserIdFromLine(String line) {
        return Integer.valueOf(line.substring(0, 10));
//        return Integer.valueOf(line.replaceFirst("\\s.*$", "")); //remove tudo após o primeiro espaço
    }

    private String getUsernameFromLine(String line) {
        return USERNAME_PATTERN.matcher(line).replaceAll("").trim();
//        return line.replaceAll("^\\d{10}\\s+|\\d{10}\\d+.*$", "").trim(); //remove pontos e tudo que não for letra
//                .replaceAll("^\\s+?(?=[a-zA-Z])", "") //remove espaços do início
//                .replaceAll("(?<=[a-zA-Z])\\s+$", ""); //remove espaços do final
    }

    private Integer getOrderIdFromLine(String line) {
        var orderIdAndProductId = ORDER_ID_PRODUCT_ID_PATTERN.matcher(line).replaceFirst("$1");
        return Integer.valueOf(ORDER_ID_PATTERN.matcher(orderIdAndProductId).replaceFirst("$1"));
//        return Integer.valueOf(line.replaceAll(".*(?<=[(a-zA-Z)+.])(\\d+)(?=\\s).*", "$1") //mantém apenas o orderId e productId
//                .replaceAll("^(.{10}).*", "$1")); //mantém apenas o orderId
    }

    private Integer getProductIdFromLine(String line) {
        var orderIdAndProductId = ORDER_ID_PRODUCT_ID_PATTERN.matcher(line).replaceFirst("$1");
        return Integer.valueOf(PRODUCT_ID_PATTERN.matcher(orderIdAndProductId).replaceFirst("$1"));
//        return Integer.valueOf(line.replaceAll(".*(?<=[(a-zA-Z)+.])(\\d+)(?=\\s).*", "$1") //mantém apenas o orderId e productId
//                .replaceAll("^.{10}(.{10}).*", "$1")); //mantém apenas o productId
    }

    private BigDecimal getValueFromLine(String line) {
        var valueAndDate = line.replaceFirst("^.*\\s", "");
        return new BigDecimal(valueAndDate.replaceFirst(valueAndDate.substring(valueAndDate.length()-8, valueAndDate.length()), ""));
//        return new BigDecimal(line.replaceFirst("^.*\\s", "")
                //mantém apenas value e date
//                .replaceAll("(\\.\\d{2}).*$", "$1")); //mantém apenas value
    }

    private String getDateFromLine(String line) {
        return line.substring(line.length()-8, line.length());
//        return line.replaceFirst("^.*\\s", "") //mantém apenas value e date
//                .replaceAll("^.*(?=(.{8})$)", ""); //mantém apenas o date
    }
}
