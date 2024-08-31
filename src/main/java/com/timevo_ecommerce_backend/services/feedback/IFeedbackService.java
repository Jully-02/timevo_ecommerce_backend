package com.timevo_ecommerce_backend.services.feedback;

import com.timevo_ecommerce_backend.dtos.FeedbackDTO;
import com.timevo_ecommerce_backend.responses.feedback.FeedbackResponse;

import java.util.List;

public interface IFeedbackService {
    FeedbackResponse insertFeedBack (FeedbackDTO feedbackDTO) throws Exception;

    FeedbackResponse updateFeedBack (Long id, FeedbackDTO feedbackDTO) throws Exception;

    FeedbackResponse getFeedBackById (long id) throws Exception;

    List<FeedbackResponse> getFeedBackByProductId  (long productId);

    List<FeedbackResponse> getFeedBackByUserId (long userId);

    void deleteFeedBackById (long id);
}
