package com.timevo_ecommerce_backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.timevo_ecommerce_backend.entities.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByTitle (String title);

    @Query("SELECT p FROM Product p " +
            "JOIN p.productVariants pv " +
            "LEFT JOIN pv.color co " +
            "LEFT JOIN pv.screenSize ss " +
            "LEFT JOIN pv.material m " +
            "LEFT JOIN p.collections col " +
            "WHERE (:categoryIds IS NULL OR p.category.id IN :categoryIds) " +
            "AND (:collectionIds IS NULL OR col.id IN :collectionIds) " +
            "AND (:colorIds IS NULL OR co.id IN :colorIds) " +
            "AND (:screenSizeIds IS NULL OR ss.id IN :screenSizeIds) " +
            "AND (:materialIds IS NULL OR m.id IN :materialIds) " +
            "AND (:keyword IS NULL OR :keyword = '' OR p.title LIKE %:keyword% OR p.description LIKE %:keyword%) " +
            "GROUP BY p.id " +
            "HAVING (:categoryIds IS NULL OR COUNT(DISTINCT p.category.id) = :categoryCount) " +
            "AND (:collectionIds IS NULL OR COUNT(DISTINCT col.id) = :collectionCount) " +
            "AND (:colorIds IS NULL OR COUNT(DISTINCT co.id) = :colorCount) " +
            "AND (:screenSizeIds IS NULL OR COUNT(DISTINCT ss.id) = :screenSizeCount) " +
            "AND (:materialIds IS NULL OR COUNT(DISTINCT m.id) = :materialCount)")
    Page<Product> searchProducts(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("categoryCount") long categoryCount,
            @Param("collectionIds") List<Long> collectionIds,
            @Param("collectionCount") long collectionCount,
            @Param("colorIds") List<Long> colorIds,
            @Param("colorCount") long colorCount,
            @Param("materialIds") List<Long> materialIds,
            @Param("materialCount") long materialCount,
            @Param("screenSizeIds") List<Long> screenSizeIds,
            @Param("screenSizeCount") long screenSizeCount,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
    List<Product> getProductsByIds (@Param("productIds") List<Long> productIds);
}
