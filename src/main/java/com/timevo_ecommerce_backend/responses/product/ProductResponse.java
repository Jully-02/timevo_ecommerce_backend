package com.timevo_ecommerce_backend.responses.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.timevo_ecommerce_backend.entities.*;
import com.timevo_ecommerce_backend.responses.variant.ProductVariantResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private long id;

    private String title;

    private float price;

    private String description;

    private float discount;

    private String thumbnail;

    @JsonProperty("quantity_stock")
    private int quantityStock;

    @JsonProperty("average_rate")
    private float averageRate;

    private Category category;

//    private List<Color> colors;
//
//    @JsonProperty("material_names")
//    private List<String> materialNames;
//
//    @JsonProperty("screen_size_names")
//    private List<Float> screenSizeNames;
//
//    @JsonProperty("collection_names")
//    private List<String> collectionNames;
    private List<Collection> collections;

    private List<ProductVariantResponse> variants;

    @JsonProperty("product_images")
    private List<ProductImageResponse> productImages;
}
