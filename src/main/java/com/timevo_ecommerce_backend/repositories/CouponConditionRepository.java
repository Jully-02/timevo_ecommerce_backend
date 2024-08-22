package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.CouponCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponConditionRepository extends JpaRepository<CouponCondition, Long> {

    List<CouponCondition> findByCouponId (Long couponId);
}
