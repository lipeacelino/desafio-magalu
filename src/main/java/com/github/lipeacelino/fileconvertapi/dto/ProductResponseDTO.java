package com.github.lipeacelino.fileconvertapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ProductResponseDTO(
        @JsonProperty("product_id")
        Integer productId,
        String value
) {
}
