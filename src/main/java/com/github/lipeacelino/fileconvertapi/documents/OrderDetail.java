package com.github.lipeacelino.fileconvertapi.documents;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderDetail {

    @MongoId
    private String id;

    @Indexed(name = "userIdIdx")
    private Integer userId;

    private String username;

    private List<Order> orders;
}
