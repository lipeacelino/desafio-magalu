package com.github.lipeacelino.fileconvertapi.repositories;

import com.github.lipeacelino.fileconvertapi.documents.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDetailRepository extends MongoRepository<OrderDetail, String> {

    @Query("{'userId': ?0, 'name' : { $regex: ?1, $options: 'i' }}")
    Optional<OrderDetail> findOrderDetailByUserIdAndName(Integer userId, String name);

    @Query("{'orders': {$elemMatch: {'orderId': ?0, 'products': {$elemMatch: {'productId': ?1}}}}}")
    Optional<OrderDetail> findOrderDetailByOrderIdAndProductId(Integer orderId, Integer productId);

    @Query("{'userId': { $ne: ?0 }, 'orders': { $elemMatch: { 'orderId' : ?1 } }}")
    Optional<OrderDetail> findOrderDetailByUserIdAndOrderId(Integer userId, Integer orderId);

    Page<OrderDetail> findAll(Pageable pageable);
}
