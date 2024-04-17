package com.github.lipeacelino.fileprocessingapi.documents;

import com.github.lipeacelino.fileprocessingapi.documents.internals.Order;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
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
@CompoundIndex(name = "index_userIdAndName)", def = "{'userId': 1, 'name': 1}")
@CompoundIndex(name = "index_userIdAndOrderId)", def = "{'userId': 1, 'orders.orderId': 1}")
@CompoundIndex(name = "index_userIdAndProductId)", def = "{'orders.orderId': 1, 'orders.products.productId': 1}")
public class OrderDetail {

    @MongoId
    private String id;

    @Indexed(name = "userIdIdx")
    private Integer userId;

    @Indexed(name = "nameIdx")
    private String name;

    private List<Order> orders;
}
