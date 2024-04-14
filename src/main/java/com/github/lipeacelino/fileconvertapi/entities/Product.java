package com.github.lipeacelino.fileconvertapi.entities;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Product {

//    private String id;

    @Indexed
    private Integer productId;

    private BigDecimal value;

}
