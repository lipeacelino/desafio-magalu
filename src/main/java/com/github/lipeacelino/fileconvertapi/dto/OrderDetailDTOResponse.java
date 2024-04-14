package com.github.lipeacelino.fileconvertapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OrderDetailDTOResponse(
        @JsonProperty("user_id")
        Integer userId,
        String name,
        List<OrderDTOResponse> orders
) {
}
