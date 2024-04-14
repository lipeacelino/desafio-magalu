package com.github.lipeacelino.fileconvertapi.documents;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Product {

    private Integer productId;

    private BigDecimal value;

}
