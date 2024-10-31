package com.timevo_ecommerce_backend.services.product_image;

import com.timevo_ecommerce_backend.entities.ProductImage;
import com.timevo_ecommerce_backend.repositories.ProductImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductImageService implements IProductImageService {
    private final ProductImageRepository productImageRepository;


    @Override
    public List<ProductImage> findByProductIdAndColorId(Long productId, Long colorId) {
        return productImageRepository.findByProductIdAndColorId(productId, colorId);
    }

    @Override
    @Transactional
    public void deleteByProductIdAndColorId(Long productId, Long colorId) {
        productImageRepository.deleteByProductIdAndColorId(productId, colorId);
    }
}
