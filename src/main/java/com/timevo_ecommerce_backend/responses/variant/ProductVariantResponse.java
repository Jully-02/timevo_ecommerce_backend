package com.timevo_ecommerce_backend.responses.variant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.timevo_ecommerce_backend.entities.Color;
import com.timevo_ecommerce_backend.entities.Material;
import com.timevo_ecommerce_backend.entities.ScreenSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantResponse {
    private Color color;

    @JsonProperty("screen_size")
    private ScreenSize screenSize;

    private Material material;

    private int quantity;
}
