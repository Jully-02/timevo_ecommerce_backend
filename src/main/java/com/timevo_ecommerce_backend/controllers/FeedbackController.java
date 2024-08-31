package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.FeedbackDTO;
import com.timevo_ecommerce_backend.responses.Response;
import com.timevo_ecommerce_backend.responses.feedback.FeedbackResponse;
import com.timevo_ecommerce_backend.services.feedback.IFeedbackService;
import com.timevo_ecommerce_backend.utils.MessagesKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {
    private final IFeedbackService feedbackService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> insertFeedback (
            @Valid @RequestBody FeedbackDTO feedbackDTO,
            BindingResult result
    ) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                            .build()
            );
        }
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.CREATED)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_SUCCESSFULLY))
                        .data(feedbackService.insertFeedBack(feedbackDTO))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getFeedback (@PathVariable("id") Long feedbackId) throws Exception {
        FeedbackResponse feedbackResponse = feedbackService.getFeedBackById(feedbackId);
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.OK)
                        .message("Get feedback successfully")
                        .data(feedbackResponse)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> updateFeedback (
            @PathVariable("id") Long id,
            @Valid @RequestBody FeedbackDTO feedbackDTO,
            BindingResult result
    ) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                            .build()
            );
        }
        FeedbackResponse feedbackResponse = feedbackService.updateFeedBack(id, feedbackDTO);
        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                        .data(feedbackResponse)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> deleteFeedback (@PathVariable("id") Long feedbackId) {
        feedbackService.deleteFeedBackById(feedbackId);
        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/user/{user_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getFeedbackByUserId (@PathVariable("user_id") Long userId) {
        List<FeedbackResponse> responses = feedbackService.getFeedBackByUserId(userId);
        return ResponseEntity.ok(
                Response.builder()
                        .data(responses)
                        .status(HttpStatus.OK)
                        .message("Get feedbacks successfully")
                        .build()
        );
    }

    @GetMapping("/product/{product_id}")
    public ResponseEntity<Response> getFeedbackByProductId (@PathVariable("product_id") Long productId) {
        List<FeedbackResponse> responses = feedbackService.getFeedBackByProductId(productId);
        return ResponseEntity.ok(
                Response.builder()
                        .data(responses)
                        .message("Get feedbacks successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

}