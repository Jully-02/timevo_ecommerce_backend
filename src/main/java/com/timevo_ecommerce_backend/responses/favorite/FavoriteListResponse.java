package com.timevo_ecommerce_backend.responses.favorite;

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
public class FavoriteListResponse {

    @JsonProperty("favorite_responses")
    private List<FavoriteResponse> favoriteResponses;

    @JsonProperty("total_pages")
    private int totalPages;
}
