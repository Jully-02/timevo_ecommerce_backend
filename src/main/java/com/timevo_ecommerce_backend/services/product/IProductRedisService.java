package com.timevo_ecommerce_backend.services.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.timevo_ecommerce_backend.responses.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProductRedisService {
    // Clear cached data in Redis
    void clear();

    Page<ProductResponse> getAllProducts (
            String categoryIds,
            String collectionIds,
            String colorIds,
            String materialIds,
            String screenSizeIds,
            String keyword,
            PageRequest pageRequest
    ) throws JsonProcessingException;

    void saveAllProducts (
            List<ProductResponse> productResponses,
            String categoryIds,
            String collectionIds,
            String colorIds,
            String materialIds,
            String screenSizeIds,
            String keyword,
            PageRequest pageRequest
    ) throws JsonProcessingException;
}
