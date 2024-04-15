package com.github.lipeacelino.fileconvertapi.controllers;

import com.github.lipeacelino.fileconvertapi.dto.OrderDetailDTOResponse;
import com.github.lipeacelino.fileconvertapi.dto.ParametersDTOInput;
import com.github.lipeacelino.fileconvertapi.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/upload")
    @CacheEvict(allEntries = true, value = "orderDetail")
    public void saveOrderDetailFromFile(@RequestParam MultipartFile file) {
                orderService.saveOrderDetailFromFile(file);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<OrderDetailDTOResponse> findAllOrderDetail(
            @PageableDefault(size = 5)
                             @SortDefault.SortDefaults({
                                @SortDefault(sort = "userId", direction = Sort.Direction.ASC)
                             })
                             Pageable pageable,
            ParametersDTOInput parametersDTOInput){
    return orderService.findAllOrderDetail(pageable, parametersDTOInput);
    }
}
