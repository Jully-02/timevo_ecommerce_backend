package com.timevo_ecommerce_backend.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.timevo_ecommerce_backend.entities.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponResponse {
    private List<Coupon> coupons;

    @JsonProperty("total_pages")
    private int totalPages;

}
