package com.timevo_ecommerce_backend.responses.screen_size;

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
public class ScreenSizeListResponse {

    @JsonProperty("screen_size_responses")
    private List<ScreenSizeResponse> screenSizeResponses;

    @JsonProperty("total_pages")
    private int totalPages;
}
