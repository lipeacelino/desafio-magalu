package com.github.lipeacelino.fileprocessingapi.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderDataDTO(
        Integer userId,
        String name,
        Integer orderId,
        Integer productId,
        BigDecimal value,
        String date
) {
}
