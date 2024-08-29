package com.timevo_ecommerce_backend.responses.color;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColorResponse {
    private long id;

    private String name;

    private int red;

    private int green;

    private int blue;

    private float alpha;

    @JsonProperty("total_products")
    private long totalProducts;
}
