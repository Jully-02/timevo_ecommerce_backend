package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    @Query("SELECT COUNT(pv) > 0 FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.color.id = :colorId AND pv.material.id = :materialId AND pv.screenSize.id = :screenSizeId")
    boolean existsByProductIdAndColorIdAndMaterialIdAndScreenSizeId(@Param("productId") Long productId, @Param("colorId") Long colorId, @Param("materialId") Long materialId, @Param("screenSizeId") Long screenSizeId);
    List<ProductVariant> findByProductId (Long productId);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.color.id = :colorId")
    List<ProductVariant> findByProductIdAndColorId (Long productId, Long colorId);

    void deleteByProductId(Long productId);

    @Modifying
    @Query("DELETE FROM ProductVariant pv WHERE pv.product.id = :productId")
    void deleteProductVariantByProductId (Long productId);
}
