package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode (String couponCode);

    @Query("SELECT c FROM Coupon c WHERE " +
            "(:keyword is NULL OR :keyword = '' OR " +
            "c.code LIKE %:keyword%)")
    Page<Coupon> findAll (@Param("keyword") String keyword, Pageable pageable);
}
