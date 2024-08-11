package com.timevo_ecommerce_backend.services.variant;

import com.timevo_ecommerce_backend.dtos.ProductVariantDTO;
import com.timevo_ecommerce_backend.entities.ProductVariant;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;

import java.util.List;

public interface IProductVariantService {
    boolean existField (Long productId, Long colorId, Long materialId, Long screenSizeId);

    List<ProductVariant> insertVariant (List<ProductVariantDTO> productVariantDTOs) throws ExistDataException, DataNotFoundException;

    ProductVariant getVariantById (Long variantId) throws DataNotFoundException;

    List<ProductVariant> updateVariant (Long productId, List<ProductVariantDTO> productVariantDTOs) throws DataNotFoundException;

    List<ProductVariant> getVariantByProductId (Long productId);

    List<ProductVariant> getAllVariants ();

    void deleteVariantByProductId (Long productId);
}
