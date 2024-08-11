package com.timevo_ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageDTO {

    @JsonProperty("image_name")
    @Size(min = 5, max = 300, message = "Image's name")
    private String imageName;

    @JsonProperty("image_url")
    @Size(min = 5, max = 500, message = "Image's url")
    private String imageUrl;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's ID must be > 0")
    private Long productId;

    @JsonProperty("color_id")
    @Min(value = 1, message = "Color's ID must be > 0")
    private Long colorId;
}
