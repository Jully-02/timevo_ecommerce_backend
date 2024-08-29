package com.timevo_ecommerce_backend.responses.order_detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {

    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    private float price;

    private int quantity;

    @JsonProperty("total_money")
    private float totalMoney;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("color_id")
    private Long colorId;

    @JsonProperty("material_id")
    private Long materialId;

    @JsonProperty("screen_size_id")
    private Long screenSizeId;
}
