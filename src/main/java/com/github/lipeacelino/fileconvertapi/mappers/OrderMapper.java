package com.github.lipeacelino.fileconvertapi.mappers;

import com.github.lipeacelino.fileconvertapi.documents.OrderDetail;
import com.github.lipeacelino.fileconvertapi.dto.OrderDTOResponse;
import com.github.lipeacelino.fileconvertapi.dto.OrderDetailDTOResponse;
import com.github.lipeacelino.fileconvertapi.documents.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderMapper {

    @Named("mapOrderDetailToOrderDetailDTOResponse")
    @Mapping(target = "orders", qualifiedByName = "mapOrderToOrderDetail")
    OrderDetailDTOResponse mapOrderDetailToOrderDetailDTOResponse(OrderDetail orderDetail);

    @Named("mapOrderToOrderDetail")
    @Mapping(target = "products")
    OrderDTOResponse mapOrderToOrderDetail(Order order);

}
