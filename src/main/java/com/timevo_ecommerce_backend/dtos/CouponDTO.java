package com.timevo_ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponDTO {

    @JsonProperty("code")
    @NotBlank(message = "Coupon code is required")
    private String code;

    @JsonProperty("active")
    private boolean active;
}
