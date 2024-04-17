package com.github.lipeacelino.fileprocessingapi.exceptions;

import com.github.lipeacelino.fileprocessingapi.dto.ApiErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorDTO genericException(Exception ex) {
        return ApiErrorDTO
                .builder()
                .timestamp(LocalDateTime.now())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .errors(List.of(ex.getMessage()))
                .build();
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ApiErrorDTO multipartException(MultipartException ex) {
        return ApiErrorDTO
                .builder()
                .timestamp(LocalDateTime.now())
                .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.name())
                .errors(List.of(ex.getMessage()))
                .build();
    }

}
