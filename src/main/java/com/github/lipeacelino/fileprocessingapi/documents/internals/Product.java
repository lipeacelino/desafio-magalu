package com.github.lipeacelino.fileprocessingapi.documents.internals;

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
