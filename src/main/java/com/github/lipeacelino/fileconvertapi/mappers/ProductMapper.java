package com.github.lipeacelino.fileconvertapi.mappers;

import com.github.lipeacelino.fileconvertapi.dto.ProductResponseDTO;
import com.github.lipeacelino.fileconvertapi.documents.internals.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Named("mapProductToProductDTOResponse")
    List<ProductResponseDTO> mapProductToProductDTOResponse(List<Product> productList);

}
