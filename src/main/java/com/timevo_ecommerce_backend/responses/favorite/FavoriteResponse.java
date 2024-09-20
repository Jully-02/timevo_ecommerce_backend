package com.timevo_ecommerce_backend.responses.favorite;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("color_id")
    private Long colorId;

    @JsonProperty("material_id")
    private Long materialId;

    @JsonProperty("screen_size_id")
    private Long screenSizeId;

}
