package com.github.lipeacelino.fileconvertapi.mappers;

import com.github.lipeacelino.fileconvertapi.documents.OrderDetail;
import com.github.lipeacelino.fileconvertapi.dto.OrderResponseDTO;
import com.github.lipeacelino.fileconvertapi.dto.OrderDetailResponseDTO;
import com.github.lipeacelino.fileconvertapi.documents.Order;
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
