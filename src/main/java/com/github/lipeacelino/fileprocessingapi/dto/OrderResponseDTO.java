package com.github.lipeacelino.fileprocessingapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Builder
public record OrderResponseDTO(

        @JsonProperty("order_id")
        Integer orderId,
        String total,
        LocalDate date,
        List<ProductResponseDTO> products

) implements Serializable {
}
