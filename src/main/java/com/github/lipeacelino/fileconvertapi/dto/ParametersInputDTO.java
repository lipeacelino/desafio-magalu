package com.github.lipeacelino.fileconvertapi.dto;

import lombok.Builder;

@Builder
public record ParametersInputDTO(
        Integer userId,
        String name
) {
}
