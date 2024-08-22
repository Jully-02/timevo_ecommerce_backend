package com.timevo_ecommerce_backend.services.coupon_condition;

import com.timevo_ecommerce_backend.dtos.CouponConditionDTO;
import com.timevo_ecommerce_backend.entities.CouponCondition;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;

import java.util.List;

public interface ICouponConditionService {
    CouponCondition insertCouponCondition (CouponConditionDTO couponConditionDTO) throws DataNotFoundException;

    CouponCondition updateCouponCondition (Long conditionId, CouponConditionDTO conditionDTO) throws DataNotFoundException;

    CouponCondition getCouponCondition (Long conditionId) throws DataNotFoundException;

    List<CouponCondition> getAllConditions ();

    List<CouponCondition> getConditionByCouponId (Long couponId);

    void deleteCouponCondition (Long conditionId);
}
