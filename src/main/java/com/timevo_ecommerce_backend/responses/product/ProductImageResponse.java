package com.timevo_ecommerce_backend.responses.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageResponse {

    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("color_id")
    private Long colorId;

    @JsonProperty("image_name")
    private String imageName;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("is_main_image")
    private boolean isMainImage;
}
