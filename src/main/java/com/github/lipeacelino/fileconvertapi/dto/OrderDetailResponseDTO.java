package com.github.lipeacelino.fileconvertapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record OrderDetailResponseDTO (
        @JsonProperty("user_id")
        Integer userId,
        String name,
        List<OrderResponseDTO> orders

) implements Serializable {
}
