package com.timevo_ecommerce_backend.services.feedback;

import com.timevo_ecommerce_backend.dtos.FeedbackDTO;
import com.timevo_ecommerce_backend.entities.Feedback;
import com.timevo_ecommerce_backend.entities.Product;
import com.timevo_ecommerce_backend.entities.User;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.repositories.FeedbackRepository;
import com.timevo_ecommerce_backend.repositories.ProductRepository;
import com.timevo_ecommerce_backend.repositories.UserRepository;
import com.timevo_ecommerce_backend.responses.feedback.FeedbackResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService implements IFeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public FeedbackResponse insertFeedBack(FeedbackDTO feedbackDTO) throws Exception {
        User userExisting = userRepository.findById(feedbackDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + feedbackDTO.getUserId()));
        Product productExisting = productRepository.findById(feedbackDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + feedbackDTO.getProductId()));

        Feedback feedback = modelMapper.map(feedbackDTO, Feedback.class);
        feedback.setUser(userExisting);
        feedback.setProduct(productExisting);

        feedbackRepository.save(feedback);

        FeedbackResponse feedbackResponse = modelMapper.map(feedback, FeedbackResponse.class);
        feedbackResponse.setProductId(feedback.getProduct().getId());
        feedbackResponse.setUserId(feedback.getUser().getId());
        feedbackResponse.setName(feedback.getUser().getFullName());
        return feedbackResponse;
    }

    @Override
    @Transactional
    public FeedbackResponse updateFeedBack(Long id, FeedbackDTO feedbackDTO) throws Exception {
        Feedback feedbackExisting = feedbackRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Feedback with ID = " + id));

        User userExisting = userRepository.findById(feedbackDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + feedbackDTO.getUserId()));

        Product productExisting = productRepository.findById(feedbackDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + feedbackDTO.getProductId()));

        feedbackExisting.setUser(userExisting);
        feedbackExisting.setProduct(productExisting);
        feedbackExisting.setRate(feedbackDTO.getRate());
        feedbackExisting.setComment(feedbackDTO.getComment());

        feedbackRepository.save(feedbackExisting);
        FeedbackResponse feedbackResponse = modelMapper.map(feedbackExisting, FeedbackResponse.class);
        feedbackResponse.setProductId(feedbackExisting.getProduct().getId());
        feedbackResponse.setUserId(feedbackExisting.getUser().getId());
        feedbackResponse.setName(feedbackExisting.getUser().getFullName());
        return feedbackResponse;
    }

    @Override
    public FeedbackResponse getFeedBackById(long id) throws Exception {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Feedback with ID = " + id));

        FeedbackResponse feedbackResponse = modelMapper.map(feedback, FeedbackResponse.class);
        feedbackResponse.setProductId(feedback.getProduct().getId());
        feedbackResponse.setUserId(feedback.getUser().getId());
        feedbackResponse.setName(feedback.getUser().getFullName());
        return feedbackResponse;
    }

    @Override
    public Page<Feedback> getFeedBackByProductId(long productId, Pageable pageable) {
        return feedbackRepository.findByProductId(productId, pageable);
    }

    @Override
    public Page<Feedback> getFeedBackByUserId(long userId, Pageable pageable) {
        return feedbackRepository.findByUserId(userId, pageable);
    }

    @Override
    public long countByUserId (Long userId) {
        return feedbackRepository.countByUserId(userId);
    }

    @Override
    public long countByProductId (Long productId) {
        return feedbackRepository.countByProductId(productId);
    }

    @Override
    @Transactional
    public void deleteFeedBackById(long id) {
        feedbackRepository.deleteById(id);
    }
}
