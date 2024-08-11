package com.timevo_ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailDTO {

    @JsonProperty("quantity")
    @Min(value = 1, message = "Quantity must be > 0")
    private int quantity;

    @Min(value = 0, message = "Product's ID must be >= 0")
    private float price;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Total money must be >= 0")
    private float totalMoney;

    @JsonProperty("order_id")
    @NotNull(message = "Order's ID is required")
    @Min(value = 1, message = "Order's ID must be > 0")
    private Long orderId;

    @JsonProperty("product_id")
    @NotNull(message = "Product ID is required")
    @Min(value = 1, message = "Product's ID must be > 0")
    private Long productId;

    @JsonProperty("color_id")
    @NotNull(message = "Color ID is required")
    @Min(value = 1, message = "Color's ID must be > 0")
    private Long colorId;

    @JsonProperty("material_id")
    @NotNull(message = "Material ID is required")
    @Min(value = 1, message = "Material's ID must be > 0")
    private Long materialId;

    @JsonProperty("screen_size_id")
    @NotNull(message = "Screen size ID is required")
    @Min(value = 0, message = "Screen size's ID must be > 0")
    private Long screenSizeId;
}
