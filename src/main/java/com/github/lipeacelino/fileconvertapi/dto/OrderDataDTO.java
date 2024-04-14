package com.github.lipeacelino.fileconvertapi.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderDataDTO(
        Integer userId,
        String username,
        Integer orderId,
        Integer productId,
        BigDecimal value,
        String date
) {
}
