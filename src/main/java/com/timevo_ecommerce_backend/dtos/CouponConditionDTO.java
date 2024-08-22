package com.timevo_ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponConditionDTO {

    @JsonProperty("coupon_id")
    @NotNull(message = "Coupon ID is required")
    @Min(value = 1, message = "Coupon ID must be > 0")
    private Long couponId;

    @JsonProperty("attribute")
    @NotBlank(message = "Attribute is required")
    private String attribute;

    @NotBlank(message = "Operator is required")
    private String operator;

    @NotBlank(message = "Value is required")
    private String value;

    @JsonProperty("discount_amount")
    @NotNull(message = "Discount amount is required")
    @Min(value = 0, message = "Discount amount must be > 0")
    private BigDecimal discountAmount;
}
