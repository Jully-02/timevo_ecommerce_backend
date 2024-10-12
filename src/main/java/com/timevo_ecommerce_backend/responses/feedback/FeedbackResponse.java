package com.timevo_ecommerce_backend.responses.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.timevo_ecommerce_backend.responses.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse extends BaseResponse {
    @JsonProperty("user_id")
    private long userId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("product_id")
    private long productId;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("rate")
    private float rate;
}
