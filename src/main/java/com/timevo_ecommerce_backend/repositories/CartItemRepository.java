package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId (Long userId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.user.id = :userId AND ci.product.id = :productId AND ci.color.id = :colorId AND ci.material.id = :materialId AND ci.screenSize.id = :screenSizeId")
    CartItem findByUserIdAndProductIdAndAttributes (
            Long userId,
            Long productId,
            Long colorId,
            Long materialId,
            Long screenSizeId
    );

    @Query("DELETE FROM CartItem ci WHERE ci.user.id = :userId AND ci.product.id = :productId AND ci.color.id = :colorId AND ci.material.id = :materialId AND ci.screenSize.id = :screenSizeId")
    @Modifying
    void deleteByUserIdAndProductIdAndAttributes (
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("colorId") Long colorId,
            @Param("materialId") Long materialId,
            @Param("screenSizeId") Long screenSizeId
    );

    @Query("DELETE FROM CartItem ci WHERE ci.user.id = :userId")
    @Modifying
    void deleteCartItemByUserId (@Param("userId") Long userId);
}
