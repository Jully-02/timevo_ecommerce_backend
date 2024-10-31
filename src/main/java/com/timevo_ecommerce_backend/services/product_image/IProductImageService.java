package com.timevo_ecommerce_backend.services.product_image;

import com.timevo_ecommerce_backend.entities.ProductImage;

import java.util.List;

public interface IProductImageService {

    List<ProductImage> findByProductIdAndColorId (Long productId, Long colorId);

    void deleteByProductIdAndColorId (Long productId, Long colorId);

}
