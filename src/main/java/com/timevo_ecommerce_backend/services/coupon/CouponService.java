package com.timevo_ecommerce_backend.services.coupon;

import com.timevo_ecommerce_backend.dtos.CouponDTO;
import com.timevo_ecommerce_backend.entities.Coupon;
import com.timevo_ecommerce_backend.entities.CouponCondition;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.repositories.CouponConditionRepository;
import com.timevo_ecommerce_backend.repositories.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CouponService implements ICouponService {
    private final CouponRepository couponRepository;
    private final CouponConditionRepository couponConditionRepository;
    private final ModelMapper modelMapper;
    @Override
    @Transactional
    public Coupon insertCoupon(CouponDTO couponDTO) {
        Coupon coupon = modelMapper.map(couponDTO, Coupon.class);
        coupon.setActive(true);
        return couponRepository.save(coupon);
    }

    @Override
    @Transactional
    public Coupon updateCoupon(Long couponId, CouponDTO couponDTO) throws DataNotFoundException {
        Coupon existingCoupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Coupon with ID = " + couponId));
        existingCoupon.setCode(couponDTO.getCode());
        existingCoupon.setActive(couponDTO.isActive());
        return couponRepository.save(existingCoupon);
    }

    @Override
    public Coupon getCouponById(Long couponId) throws DataNotFoundException {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Coupon with ID = " + couponId));
    }

    @Override
    public Page<Coupon> getAllCoupons(String keyword, Pageable pageable) {
        return couponRepository.findAll(keyword, pageable);
    }

    @Override
    public void deleteCoupon(Long couponId) {
        couponRepository.deleteById(couponId);
    }

    @Override
    public double calculateCouponValue(String couponCode, double totalAmount) throws DataNotFoundException {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Coupon with Coupon Code = " + couponCode));
        if (!coupon.isActive()) {
            throw new IllegalArgumentException("Coupon is not active");
        }
        double discount = calculateDiscount(coupon, totalAmount);
        double finalAmount = totalAmount - discount;
        return finalAmount;
    }

    private double calculateDiscount (Coupon coupon, double totalAmount) {
        List<CouponCondition> conditions = couponConditionRepository.findByCouponId(coupon.getId());
        double discount = 0.0;
        double updateTotalAmount = totalAmount;
        for (CouponCondition condition : conditions) {
            // EAV (Entity - Attribute - Value) Model
            String attribute = condition.getAttribute();
            String operator = condition.getOperator();
            String value = condition.getValue();

            double percentDiscount = Double.parseDouble(
                    String.valueOf(condition.getDiscountAmount())
            );
            if (attribute.equals("minimum_amount")) {
                if (operator.equals(">") && updateTotalAmount > Double.parseDouble(value)) {
                    discount += updateTotalAmount * percentDiscount / 100;
                }
            } else if (attribute.equals("applicable_date")) {
                LocalDate applicableDate = LocalDate.parse(value);
                LocalDate currentDate = LocalDate.now();
                if (operator.equalsIgnoreCase("BETWEEN")
                        && currentDate.isEqual(applicableDate)) {
                    discount += updateTotalAmount * percentDiscount / 100;
                }
            }
            // more conditions ...
            updateTotalAmount = updateTotalAmount - discount;
        }
        return discount;
    }
}
