package com.github.lipeacelino.fileconvertapi.repositories;

import com.github.lipeacelino.fileconvertapi.documents.OrderDetail;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDetailRepository extends MongoRepository<OrderDetail, String> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="10000")})
    Optional<OrderDetail> findOrderDetailByUserId(Integer userId);
    Page<OrderDetail> findAll(Pageable pageable);
}
