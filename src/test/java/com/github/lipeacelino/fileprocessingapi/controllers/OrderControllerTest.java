package com.github.lipeacelino.fileprocessingapi.controllers;

import com.github.lipeacelino.fileprocessingapi.dto.*;
import com.github.lipeacelino.fileprocessingapi.services.OrderService;
import com.github.lipeacelino.fileprocessingapi.util.TestUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private static final String BASE_URL = "/orders";

    private static final Integer USER_ID = 80;

    private static final String NAME = "Tabitha Kuhn";

    private static final Integer ORDER_ID = 877;

    private static final Integer PRODUCT_ID = 4;

    private static final BigDecimal VALUE = new BigDecimal("817.13");

    private static final String DATE = "20210612";

    private static final String RESULT_RESPONSE = "Orders processed successfully";

    @Test
    @SneakyThrows
    void saveOrderDetailFromFile() {
        when(orderService.saveOrderDetailFromFile(Mockito.any(MultipartFile.class)))
                .thenReturn(new ProcessingResultDTO(List.of(RESULT_RESPONSE)));
        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_URL.concat("/upload"))
                        .file(TestUtil.getMockMultipartFile())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result", hasSize(1)))
                .andExpect(content().contentType("application/json"));

    }

    @Test
    @SneakyThrows
    void findAllOrderDetail() {
        when(orderService.findAllOrderDetail(Mockito.any(Pageable.class), Mockito.any(ParametersInputDTO.class)))
                .thenReturn(new PageImpl<>(createOrderDetailResponseDTOList()));
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                .param("page", "0")
                .param("size", "5")
                .param("sort", "userId,asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(1))))
                .andExpect(content().contentType("application/json"));
    }

    private List<OrderDetailResponseDTO> createOrderDetailResponseDTOList() {
        var order1 = createOrderDetailResponseDTO();
        var order2 = createOrderDetailResponseDTO();
        return new ArrayList<>(List.of(order1, order2));
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

}