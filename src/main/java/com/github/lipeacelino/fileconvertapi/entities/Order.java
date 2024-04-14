package com.github.lipeacelino.fileconvertapi.entities;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Order {

//    private String id;

    @Indexed(name="orderIdIndex")
    private Integer orderId;

    private BigDecimal total;

    private LocalDate date;

    private List<Product> products;

}
