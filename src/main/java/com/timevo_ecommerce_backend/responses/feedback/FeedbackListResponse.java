package com.timevo_ecommerce_backend.responses.feedback;

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
public class FeedbackListResponse {

    @JsonProperty("feedback_responses")
    private List<FeedbackResponse> feedbackResponses;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("total_feedbacks")
    private long totalFeedbacks;
}
