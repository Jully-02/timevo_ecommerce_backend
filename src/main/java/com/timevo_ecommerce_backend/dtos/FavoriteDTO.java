package com.timevo_ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteDTO {
    private long id;

    @Min(value = 1, message = "User's ID must be > 0")
    @JsonProperty("user_id")
    private long userId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's ID must be > 0")
    private long productId;

    @JsonProperty("color_id")
    @NotNull(message = "Color ID is required")
    @Min(value = 1, message = "Color ID must be > 0")
    private long colorId;

    @JsonProperty("material_id")
    @NotNull(message = "Material ID is required")
    @Min(value = 1, message = "Material ID must be > 0")
    private long materialId;

    @JsonProperty("screen_size_id")
    @NotNull(message = "Screen size ID is required")
    @Min(value = 1, message = "Screen size ID must be > 0")
    private long screenSizeId;
}
