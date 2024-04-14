package com.github.lipeacelino.fileconvertapi.mappers;

import com.github.lipeacelino.fileconvertapi.dto.ProductDTOResponse;
import com.github.lipeacelino.fileconvertapi.documents.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Named("mapProductToProductDTOResponse")
    List<ProductDTOResponse> mapProductToProductDTOResponse(List<Product> productList);

}
