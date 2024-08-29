package com.timevo_ecommerce_backend.responses.category;

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
public class CategoryListResponse {

    @JsonProperty("category_responses")
    private List<CategoryResponse> categoryResponses;

    @JsonProperty("total_pages")
    private int totalPages;
}
