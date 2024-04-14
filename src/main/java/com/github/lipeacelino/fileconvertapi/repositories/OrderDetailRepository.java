package com.github.lipeacelino.fileconvertapi.repositories;

import com.github.lipeacelino.fileconvertapi.entities.OrderDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends MongoRepository<OrderDetail, String> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="10000")})
    Optional<OrderDetail> findOrderDetailByUserId(Integer userId);

    @Query("{'orders.orderId' : ?0}")
    Optional<OrderDetail> findOrderDetailByOrderId(Integer orderId);

}
