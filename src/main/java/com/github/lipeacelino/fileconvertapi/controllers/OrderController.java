package com.github.lipeacelino.fileconvertapi.controllers;

import com.github.lipeacelino.fileconvertapi.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

//    @ResponseStatus
    @PostMapping("/upload")
    public void saveOrderDetails(@RequestParam MultipartFile file) {
                orderService.saveOrderDetails(file);
//                String userId = line.replaceFirst("\\s.*$", ""); //remove tudo após o primeiro espaço
//
//                String username = line.replaceAll("\\d+.", "") //remove pontos e tudo que não for letra
//                       .replaceAll("^\\s+?(?=[a-zA-Z])", "") //remove espaços do início
//                       .replaceAll("(?<=[a-zA-Z])\\s+$", ""); //remove espaços do final
//
//                String orderId = line.replaceAll(".*(?<=[a-zA-Z])(\\d+)(?=\\s).*", "$1") //mantém apenas o orderId e productId
//                        .replaceAll("^(.{10}).*", "$1"); //mantém apenas o orderId
//
//                String productId = line.replaceAll(".*(?<=[a-zA-Z])(\\d+)(?=\\s).*", "$1") //mantém apenas o orderId e productId
//                        .replaceAll("^.{10}(.{10}).*", "$1"); //mantém apenas o productId
//
//                String value = line.replaceFirst("^.*\\s", "") //mantém apenas value e date
//                        .replaceAll("(\\.\\d{2}).*$", "$1"); //mantém apenas value
//
//                String date = line.replaceFirst("^.*\\s", "") //mantém apenas value e date
//                        .replaceAll("^.*(?=(.{8})$)", ""); //mantém apenas o date
            }

}
