package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.color.id = :colorId")
    List<ProductImage> findByProductIdAndColorId (Long productId, Long colorId);

    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId AND pi.color.id = :colorId")
    void deleteByProductIdAndColorId(Long productId, Long colorId);

}
