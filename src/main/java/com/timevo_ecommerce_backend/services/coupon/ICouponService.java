package com.timevo_ecommerce_backend.services.coupon;

import com.timevo_ecommerce_backend.dtos.CouponDTO;
import com.timevo_ecommerce_backend.entities.Coupon;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICouponService {
    Coupon insertCoupon (CouponDTO couponDTO);

    Coupon updateCoupon (Long couponId, CouponDTO couponDTO) throws DataNotFoundException;

    Coupon getCouponById (Long couponId) throws DataNotFoundException;

    Page<Coupon> getAllCoupons (String keyword, Pageable pageable);

    void deleteCoupon (Long couponId);

    double calculateCouponValue (String couponCode, double totalAmount) throws DataNotFoundException;
}
