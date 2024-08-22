package com.timevo_ecommerce_backend.services.coupon_condition;

import com.timevo_ecommerce_backend.dtos.CouponConditionDTO;
import com.timevo_ecommerce_backend.entities.Coupon;
import com.timevo_ecommerce_backend.entities.CouponCondition;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.repositories.CouponConditionRepository;
import com.timevo_ecommerce_backend.repositories.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponConditionService implements ICouponConditionService{
    private final CouponConditionRepository couponConditionRepository;
    private final CouponRepository couponRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public CouponCondition insertCouponCondition(CouponConditionDTO couponConditionDTO) throws DataNotFoundException {
        Coupon existingCoupon = couponRepository.findById(couponConditionDTO.getCouponId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Coupon with ID = " + couponConditionDTO.getCouponId()));
        CouponCondition condition = modelMapper.map(couponConditionDTO, CouponCondition.class);
        condition.setCoupon(existingCoupon);
        return couponConditionRepository.save(condition);
    }

    @Override
    @Transactional
    public CouponCondition updateCouponCondition(Long conditionId, CouponConditionDTO conditionDTO) throws DataNotFoundException {
        CouponCondition existingCondition = couponConditionRepository.findById(conditionId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Condition with ID = " + conditionId));
        Coupon existingCoupon = couponRepository.findById(conditionDTO.getCouponId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Coupon with ID = " + conditionDTO.getCouponId()));
        existingCondition.setAttribute(conditionDTO.getAttribute());
        existingCondition.setValue(conditionDTO.getValue());
        existingCondition.setOperator(conditionDTO.getOperator());
        existingCondition.setDiscountAmount(conditionDTO.getDiscountAmount());
        existingCondition.setCoupon(existingCoupon);

        return couponConditionRepository.save(existingCondition);
    }

    @Override
    public CouponCondition getCouponCondition(Long conditionId) throws DataNotFoundException {
        return couponConditionRepository.findById(conditionId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Condition with ID = " + conditionId));
    }

    @Override
    public List<CouponCondition> getAllConditions() {
        return couponConditionRepository.findAll();
    }

    @Override
    public List<CouponCondition> getConditionByCouponId(Long couponId) {
        return couponConditionRepository.findByCouponId(couponId);
    }

    @Override
    public void deleteCouponCondition(Long conditionId) {
        couponConditionRepository.deleteById(conditionId);
    }
}