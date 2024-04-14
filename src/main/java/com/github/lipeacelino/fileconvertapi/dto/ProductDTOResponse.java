package com.github.lipeacelino.fileconvertapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductDTOResponse(
        @JsonProperty("product_id")
        Integer productId,
        String value
) {
}
