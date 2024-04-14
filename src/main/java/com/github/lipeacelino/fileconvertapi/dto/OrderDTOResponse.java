package com.github.lipeacelino.fileconvertapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record OrderDTOResponse(

        @JsonProperty("order_id")
        Integer orderId,
        String total,
        LocalDate date,
        List<ProductDTOResponse> products
) {
}
