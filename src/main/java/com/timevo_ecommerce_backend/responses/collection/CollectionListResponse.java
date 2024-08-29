package com.timevo_ecommerce_backend.responses.collection;

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
public class CollectionListResponse {

    @JsonProperty("collection_responses")
    private List<CollectionResponse> collectionResponses;

    @JsonProperty("total_pages")
    private int totalPages;
}
