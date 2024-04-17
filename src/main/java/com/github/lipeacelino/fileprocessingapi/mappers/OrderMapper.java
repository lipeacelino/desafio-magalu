package com.github.lipeacelino.fileprocessingapi.mappers;

import com.github.lipeacelino.fileprocessingapi.documents.OrderDetail;
import com.github.lipeacelino.fileprocessingapi.dto.OrderResponseDTO;
import com.github.lipeacelino.fileprocessingapi.dto.OrderDetailResponseDTO;
import com.github.lipeacelino.fileprocessingapi.documents.internals.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderMapper {

    @Named("mapOrderDetailToOrderDetailDTOResponse")
    @Mapping(target = "orders", qualifiedByName = "mapOrderToOrderDetail")
    OrderDetailResponseDTO mapOrderDetailToOrderDetailDTOResponse(OrderDetail orderDetail);

    @Named("mapOrderToOrderDetail")
    @Mapping(target = "products")
    OrderResponseDTO mapOrderToOrderDetail(Order order);

}
