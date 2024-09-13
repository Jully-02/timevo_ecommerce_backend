package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId (Long userId);

    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.product.id = :productId")
    Favorite findByUserIdAndProductId (Long userId, Long productId);

    @Query("DELETE FROM Favorite f WHERE f.user.id = :userId AND f.product.id = :productId")
    @Modifying
    void deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
