package com.github.lipeacelino.fileprocessingapi.mappers;

import com.github.lipeacelino.fileprocessingapi.dto.ProductResponseDTO;
import com.github.lipeacelino.fileprocessingapi.documents.internals.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Named("mapProductToProductDTOResponse")
    List<ProductResponseDTO> mapProductToProductDTOResponse(List<Product> productList);

}
