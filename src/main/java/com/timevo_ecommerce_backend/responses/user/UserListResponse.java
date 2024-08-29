package com.timevo_ecommerce_backend.responses.user;

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
public class UserListResponse {

    @JsonProperty("user_responses")
    private List<UserResponse> userResponses;

    @JsonProperty("total_pages")
    private int totalPages;
}
