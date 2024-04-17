package com.github.lipeacelino.fileprocessingapi.services;

import com.github.lipeacelino.fileprocessingapi.documents.internals.Order;
import com.github.lipeacelino.fileprocessingapi.documents.OrderDetail;
import com.github.lipeacelino.fileprocessingapi.documents.internals.Product;
import com.github.lipeacelino.fileprocessingapi.dto.OrderDetailResponseDTO;
import com.github.lipeacelino.fileprocessingapi.dto.OrderResponseDTO;
import com.github.lipeacelino.fileprocessingapi.dto.ParametersInputDTO;
import com.github.lipeacelino.fileprocessingapi.dto.ProductResponseDTO;
import com.github.lipeacelino.fileprocessingapi.mappers.OrderMapper;
import com.github.lipeacelino.fileprocessingapi.repositories.OrderDetailRepository;
import com.github.lipeacelino.fileprocessingapi.util.TestUtil;
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

import java.math.BigDecimal;
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

    private static final Integer USER_ID = 80;

    private static final String NAME = "Tabitha Kuhn";

    private static final Integer ORDER_ID = 877;

    private static final Integer PRODUCT_ID = 4;

    private static final Integer PRODUCT_ID_IN_FILE = 3;

    private static final BigDecimal VALUE = new BigDecimal("817.13");

    private static final String DATE = "20210612";

    @Test
    @SneakyThrows
    void shouldUpdateWhenSaveOrderDetailFromFile() {

        var mockMultipartFile = TestUtil.getMockMultipartFile();

        var orderDetail = createOrderDetail();

        when(orderDetailRepository.findOrderDetailByUserIdAndName(USER_ID, NAME)).thenReturn(Optional.of(orderDetail));
        orderService.saveOrderDetailFromFile(mockMultipartFile);
        verify(orderDetailRepository).findOrderDetailByUserIdAndName(USER_ID, NAME);
        verify(orderDetailRepository).findOrderDetailByUserIdAndOrderId(USER_ID, ORDER_ID);
        verify(orderDetailRepository).findOrderDetailByOrderIdAndProductId(ORDER_ID, PRODUCT_ID_IN_FILE);

        ArgumentCaptor<OrderDetail> orderDetailCaptor = ArgumentCaptor.forClass(OrderDetail.class);
        verify(orderDetailRepository).save(orderDetailCaptor.capture());

        var savedOrderDetail = orderDetailCaptor.getValue();
        var savedOrder = savedOrderDetail.getOrders().get(0);
        assertEquals(USER_ID, savedOrderDetail.getUserId());
        assertEquals(ORDER_ID, savedOrder.getOrderId());
        assertEquals(PRODUCT_ID_IN_FILE, savedOrder.getProducts().get(1).getProductId());
        assertTrue(savedOrder.getProducts().size() > 1);
    }

    @Test
    @SneakyThrows
    void shouldCreateWhenSaveOrderDetailFromFile() {
        var mockMultipartFile = TestUtil.getMockMultipartFile();

        when(orderDetailRepository.findOrderDetailByUserIdAndName(USER_ID, NAME)).thenReturn(Optional.empty());
        orderService.saveOrderDetailFromFile(mockMultipartFile);
        verify(orderDetailRepository).findOrderDetailByUserIdAndName(USER_ID, NAME);
        verify(orderDetailRepository).findOrderDetailByUserIdAndOrderId(USER_ID, ORDER_ID);
        verify(orderDetailRepository).findOrderDetailByOrderIdAndProductId(ORDER_ID, PRODUCT_ID_IN_FILE);

        ArgumentCaptor<OrderDetail> orderDetailCaptor = ArgumentCaptor.forClass(OrderDetail.class);
        verify(orderDetailRepository).save(orderDetailCaptor.capture());

        var savedOrderDetail = orderDetailCaptor.getValue();
        var savedOrder = savedOrderDetail.getOrders().get(0);
        assertEquals(USER_ID, savedOrderDetail.getUserId());
        assertEquals(ORDER_ID, savedOrder.getOrderId());
        assertEquals(PRODUCT_ID_IN_FILE, savedOrder.getProducts().get(0).getProductId());
        assertEquals(1, savedOrder.getProducts().size());
    }

    @Test
    void findAllOrderDetailWithFilters() {
        var pageable = PageRequest.of(0, 10);
        var parametersDTOInput = ParametersInputDTO.builder().userId(USER_ID).name(NAME).build();

        var orderDetails = List.of(createOrderDetail());
        var dtoResponse = createOrderDetailResponseDTO();

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

    private ProductResponseDTO createProductResponseDTO() {
        return ProductResponseDTO.builder()
                .productId(PRODUCT_ID)
                .value(String.valueOf(VALUE))
                .build();
    }

    private OrderResponseDTO createOrderResponseDTO() {
        return OrderResponseDTO.builder()
                .orderId(ORDER_ID)
                .total(String.valueOf(VALUE))
                .date(LocalDate.parse(DATE, DateTimeFormatter.ofPattern("yyyyMMdd")))
                .products(new ArrayList<>(List.of(createProductResponseDTO())))
                .build();
    }

    private OrderDetailResponseDTO createOrderDetailResponseDTO() {
        return OrderDetailResponseDTO.builder()
                .userId(USER_ID)
                .name(NAME)
                .orders(new ArrayList<>(List.of(createOrderResponseDTO())))
                .build();
    }

    private Product createProduct() {
        return Product.builder()
                .productId(PRODUCT_ID)
                .value(VALUE)
                .build();
    }

    private Order createOrder() {
        return Order.builder()
                .orderId(ORDER_ID)
                .total(VALUE)
                .date(LocalDate.parse(DATE, DateTimeFormatter.ofPattern("yyyyMMdd")))
                .products(new ArrayList<>(List.of(createProduct())))
                .build();
    }

    private OrderDetail createOrderDetail() {
        return OrderDetail.builder()
                .id(UUID.randomUUID().toString())
                .userId(USER_ID)
                .name(NAME)
                .orders(new ArrayList<>(List.of(createOrder())))
                .build();
    }

}