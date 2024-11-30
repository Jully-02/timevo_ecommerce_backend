package com.timevo_ecommerce_backend.services.feedback;

import com.timevo_ecommerce_backend.dtos.FeedbackDTO;
import com.timevo_ecommerce_backend.entities.Feedback;
import com.timevo_ecommerce_backend.responses.feedback.FeedbackResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IFeedbackService {
    FeedbackResponse insertFeedBack (FeedbackDTO feedbackDTO) throws Exception;

    FeedbackResponse updateFeedBack (Long id, FeedbackDTO feedbackDTO) throws Exception;

    FeedbackResponse getFeedBackById (long id) throws Exception;

    Page<Feedback> getFeedBackByProductId  (long productId, Pageable pageable);

    Page<Feedback> getFeedBackByUserId (long userId, Pageable pageable);

    Page<Feedback> getFeedbacks (Pageable pageable);

    long totalFeedbacks ();

    void deleteFeedBackById (long id);

    long countByUserId (Long userId);

    long countByProductId (Long productId);
}
