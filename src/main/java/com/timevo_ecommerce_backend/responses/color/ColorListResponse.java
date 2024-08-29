package com.timevo_ecommerce_backend.responses.color;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorListResponse {
    @JsonProperty("color_responses")
    private List<ColorResponse> colorResponses;

    @JsonProperty("total_pages")
    private int totalPages;
}
