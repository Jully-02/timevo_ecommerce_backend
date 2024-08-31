package com.timevo_ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {
    @JsonProperty("user_id")
    @NotNull(message = "User ID is required")
    private long userId;

    @JsonProperty("product_id")
    @NotNull(message = "Product ID is required")
    private long productId;

    @JsonProperty("comment")
    @NotBlank(message = "Comment is required")
    private String comment;

    @JsonProperty("rate")
    @NotNull(message = "Rate is required")
    private float rate;

//    @JsonProperty("name")
//    @NotBlank(message = "Name is required")
//    private String name;
}
