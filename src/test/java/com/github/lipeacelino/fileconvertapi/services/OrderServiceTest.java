package com.github.lipeacelino.fileconvertapi.services;

import com.github.lipeacelino.fileconvertapi.documents.Order;
import com.github.lipeacelino.fileconvertapi.documents.OrderDetail;
import com.github.lipeacelino.fileconvertapi.documents.Product;
import com.github.lipeacelino.fileconvertapi.dto.OrderDetailResponseDTO;
import com.github.lipeacelino.fileconvertapi.dto.OrderResponseDTO;
import com.github.lipeacelino.fileconvertapi.dto.ParametersInputDTO;
import com.github.lipeacelino.fileconvertapi.dto.ProductResponseDTO;
import com.github.lipeacelino.fileconvertapi.mappers.OrderMapper;
import com.github.lipeacelino.fileconvertapi.repositories.OrderDetailRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private OrderService orderService;

    private final Integer userId = 80;

    private final String name = "Tabitha Kuhn";

    private final Integer orderId = 877;

    private final Integer productId = 4;

    private final Integer productIdInFile = 3;

    private final BigDecimal value = new BigDecimal("817.13");

    private final String date = "20210612";

    @Test
    @SneakyThrows
    void shouldUpdateWhenSaveOrderDetailFromFile() {

        var mockMultipartFile = getMockMultipartFile();

        var product = getProduct();

        var orderDetail = getOrderDetail();

        when(orderDetailRepository.findOrderDetailByUserIdAndName(userId, name)).thenReturn(Optional.of(orderDetail));
        orderService.saveOrderDetailFromFile(mockMultipartFile);
        verify(orderDetailRepository).findOrderDetailByUserIdAndName(userId, name);
        verify(orderDetailRepository).findOrderDetailByUserIdAndOrderId(userId, orderId);
        verify(orderDetailRepository).findOrderDetailByOrderIdAndProductId(orderId, productIdInFile);

        ArgumentCaptor<OrderDetail> orderDetailCaptor = ArgumentCaptor.forClass(OrderDetail.class);
        verify(orderDetailRepository).save(orderDetailCaptor.capture());

        var savedOrderDetail = orderDetailCaptor.getValue();
        var savedOrder = savedOrderDetail.getOrders().get(0);
        assertEquals(userId, savedOrderDetail.getUserId());
        assertEquals(orderId, savedOrder.getOrderId());
        assertEquals(productIdInFile, savedOrder.getProducts().get(1).getProductId());
        assertTrue(savedOrder.getProducts().size() > 1);
    }

    @Test
    @SneakyThrows
    void shouldCreateWhenSaveOrderDetailFromFile() {
        var mockMultipartFile = getMockMultipartFile();

        when(orderDetailRepository.findOrderDetailByUserIdAndName(userId, name)).thenReturn(Optional.empty());
        orderService.saveOrderDetailFromFile(mockMultipartFile);
        verify(orderDetailRepository).findOrderDetailByUserIdAndName(userId, name);
        verify(orderDetailRepository).findOrderDetailByUserIdAndOrderId(userId, orderId);
        verify(orderDetailRepository).findOrderDetailByOrderIdAndProductId(orderId, productIdInFile);

        ArgumentCaptor<OrderDetail> orderDetailCaptor = ArgumentCaptor.forClass(OrderDetail.class);
        verify(orderDetailRepository).save(orderDetailCaptor.capture());

        var savedOrderDetail = orderDetailCaptor.getValue();
        var savedOrder = savedOrderDetail.getOrders().get(0);
        assertEquals(userId, savedOrderDetail.getUserId());
        assertEquals(orderId, savedOrder.getOrderId());
        assertEquals(productIdInFile, savedOrder.getProducts().get(0).getProductId());
        assertEquals(1, savedOrder.getProducts().size());
    }

    @Test
    void findAllOrderDetailWithFilters() {
        var pageable = PageRequest.of(0, 10);
        var parametersDTOInput = ParametersInputDTO.builder().userId(userId).name(name).build();

        var orderDetails = List.of(getOrderDetail());
        var dtoResponse = getOrderDetailResponseDTO();

        when(mongoTemplate.find(any(Query.class), eq(OrderDetail.class))).thenReturn(orderDetails);
        when(mongoTemplate.count(any(Query.class), eq(OrderDetail.class))).thenReturn((long) orderDetails.size());
        when(orderMapper.mapOrderDetailToOrderDetailDTOResponse(any(OrderDetail.class))).thenReturn(dtoResponse);

        var orderDetailResponseDTO = orderService.findAllOrderDetail(pageable, parametersDTOInput);

        assertTrue(orderDetailResponseDTO.getTotalElements() > 0);
        assertEquals(dtoResponse, orderDetailResponseDTO.getContent().get(0));

        verify(mongoTemplate).count(any(Query.class), eq(OrderDetail.class));
        verify(mongoTemplate).find(any(Query.class), eq(OrderDetail.class));
        verify(orderMapper).mapOrderDetailToOrderDetailDTOResponse(any(OrderDetail.class));
    }

    @SneakyThrows
    private MockMultipartFile getMockMultipartFile() {
        var path = Paths.get("src/test/resources", "data_1.txt");
        var file = path.toFile();

        byte[] content = Files.readAllBytes(file.toPath());

        return new MockMultipartFile(
                "file",
                file.getName(),
                "text/plain",
                content
        );
    }

    private ProductResponseDTO getProductResponseDTO() {
        return ProductResponseDTO.builder()
                .productId(productId)
                .value(String.valueOf(value))
                .build();
    }

    private OrderResponseDTO getOrderResponseDTO() {
        return OrderResponseDTO.builder()
                .orderId(orderId)
                .total(String.valueOf(value))
                .date(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")))
                .products(new ArrayList<>(List.of(getProductResponseDTO())))
                .build();
    }

    private OrderDetailResponseDTO getOrderDetailResponseDTO() {
        return OrderDetailResponseDTO.builder()
                .userId(userId)
                .name(name)
                .orders(new ArrayList<>(List.of(getOrderResponseDTO())))
                .build();
    }

    private Product getProduct() {
        return Product.builder()
                .productId(productId)
                .value(value)
                .build();
    }

    private Order getOrder() {
        return Order.builder()
                .orderId(orderId)
                .total(value)
                .date(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")))
                .products(new ArrayList<>(List.of(getProduct())))
                .build();
    }

    private OrderDetail getOrderDetail() {
        return OrderDetail.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .name(name)
                .orders(new ArrayList<>(List.of(getOrder())))
                .build();
    }

}