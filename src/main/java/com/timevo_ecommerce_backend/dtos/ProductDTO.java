package com.timevo_ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 500, message = "Title must be between 10 and 500 characters")
    private String title;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Max(value = 90_000_000, message = "Price must be less than or equal to 90_000_000")
    private float price;

    private String description;

    private String thumbnail;

    @Min(value = 0, message = "Discount must be greater than or equal to 0")
    @Max(value = 100, message = "Discount must be less than or equal to 100")
    private float discount;

    @Min(value = 0, message = "Average rate must be greater than or equal to 0")
    @Max(value = 5, message = "Average must be less than or equal to 5")
    @JsonProperty("average_rate")
    private float averageRate;

    @Min(value = 0, message = "Quantity in stock must be greater than or equal to 0")
    @Max(value = 90_000_000, message = "Quantity in stock must be less than or equal to 90_000_000")
    @JsonProperty("quantity_stock")
    private int quantityStock;

    @NotNull(message = "Category ID are required")
    @JsonProperty("category_id")
    @Min(value = 1, message = "Category ID must be greater than or equal to 1")
    private long categoryId;

//    @NotNull(message = "Color ID are required")
//    @JsonProperty("color_ids")
//    @Size(min = 1, message = "There must be at least one Color ID")
//    private List<Long> colorIds;
//
//    @NotNull(message = "Screen size ID are required")
//    @JsonProperty("screen_size_ids")
//    @Size(min = 1, message = "There must be at least one Screen size ID")
//    private List<Long> screenSizeIds;
//
//    @NotNull(message = "Material ID are required")
//    @JsonProperty("material_ids")
//    @Size(min = 1, message = "There must be at least one Material ID")
//    private List<Long> materialIds;

    @JsonProperty("collection_ids")
    private List<Long> collectionIds;
}
