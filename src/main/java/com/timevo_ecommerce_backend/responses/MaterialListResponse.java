package com.timevo_ecommerce_backend.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialListResponse {

    @JsonProperty("material_responses")
    private List<MaterialResponse> materialResponses;

    @JsonProperty("total_pages")
    private int totalPages;
}
