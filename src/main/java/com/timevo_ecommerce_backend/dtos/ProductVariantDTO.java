package com.timevo_ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDTO {

    @NotNull(message = "Product ID is required")
    @Min(value = 1, message = "Product ID must be greater than or equal to 1")
    @JsonProperty("product_id")
    private Long productId;

    @NotNull(message = "Color ID is required")
    @Min(value = 1, message = "Color ID must be greater than or equal to 1")
    @JsonProperty("color_id")
    private Long colorId;

    @NotNull(message = "Screen size ID is required")
    @Min(value = 1, message = "Screen size ID must be greater than or equal to 1")
    @JsonProperty("screen_size_id")
    private Long screenSizeId;

    @NotNull(message = "Material ID is required")
    @Min(value = 1, message = "Material ID must be greater than or equal to 1")
    @JsonProperty("material_id")
    private Long materialId;

    @Min(value = 0, message = "Quantity in stock must be greater than or equal to 0")
    @Max(value = 90_000_000, message = "Quantity in stock must be less than or equal to 90_000_000")
    @JsonProperty("quantity")
    private int quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariantDTO that = (ProductVariantDTO) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(colorId, that.colorId) &&
                Objects.equals(materialId, that.materialId) &&
                Objects.equals(screenSizeId, that.screenSizeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, colorId, materialId, screenSizeId);
    }
}
