package com.github.lipeacelino.fileprocessingapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ApiErrorDTO(
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,

        Integer code,

        String status,

        List<String> errors
) {
}
