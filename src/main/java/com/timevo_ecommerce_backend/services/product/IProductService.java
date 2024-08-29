package com.timevo_ecommerce_backend.services.product;

import com.timevo_ecommerce_backend.dtos.ProductDTO;
import com.timevo_ecommerce_backend.dtos.ProductImageDTO;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.cloudinary.CloudinaryResponse;
import com.timevo_ecommerce_backend.responses.product.ProductImageResponse;
import com.timevo_ecommerce_backend.responses.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    ProductResponse insertProduct (ProductDTO productDTO) throws Exception;

    ProductResponse getProductById (Long productId) throws DataNotFoundException;

    Page<ProductResponse> searchProducts (
            List<Long> categoryIds,
            long categoryCount,
            List<Long> collectionIds,
            long collectionCount,
            List<Long> colorIds,
            long colorCount,
            List<Long> materialIds,
            long materialCount,
            List<Long> screenSizeIds,
            long screenSizeCount,
            String keyword,
            PageRequest pageRequest
    );

    ProductResponse updateProduct (Long productId, ProductDTO productDTO) throws Exception;

    void deleteProduct (Long productId) throws DataNotFoundException;

    boolean existByTitle (String title);

    ProductImageResponse insertProductImage (
            Long productId,
            Long colorId,
            ProductImageDTO productImageDTO
    ) throws Exception;

    List<ProductResponse> getProductsByIds (List<Long> productIds);

    CloudinaryResponse uploadImage (MultipartFile file) throws Exception;
}
