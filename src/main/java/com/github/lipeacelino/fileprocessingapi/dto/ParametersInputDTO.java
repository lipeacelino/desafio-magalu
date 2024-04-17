package com.github.lipeacelino.fileprocessingapi.dto;

import lombok.Builder;

@Builder
public record ParametersInputDTO(
        Integer userId,
        String name
) {
}
