package com.github.lipeacelino.fileconvertapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record ProductResponseDTO(
        @JsonProperty("product_id")
        Integer productId,
        String value

) implements Serializable {
}
