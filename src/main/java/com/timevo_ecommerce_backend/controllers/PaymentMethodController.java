package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.PaymentMethodDTO;
import com.timevo_ecommerce_backend.entities.PaymentMethod;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.responses.Response;
import com.timevo_ecommerce_backend.services.payment_method.IPaymentMethodService;
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
@RequestMapping("${api.prefix}/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final IPaymentMethodService paymentMethodService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertPaymentMethod (
            @Valid @RequestBody PaymentMethodDTO paymentMethodDTO,
            BindingResult result
    ) throws ExistDataException {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
            }
            PaymentMethod paymentMethod = paymentMethodService.insertPaymentMethod(paymentMethodDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .data(paymentMethod)
                            .status(HttpStatus.CREATED)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_SUCCESSFULLY))
                            .build()
            );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getPaymentMethod (@PathVariable("id") Long paymentMethodId) throws DataNotFoundException {
        PaymentMethod paymentMethod = paymentMethodService.getPaymentMethod(paymentMethodId);
        return ResponseEntity.ok(
                Response.builder()
                        .data(paymentMethod)
                        .message("Get payment method successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("")
    public ResponseEntity<Response> getPaymentMethods () {
        List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods();
        return ResponseEntity.ok(
                Response.builder()
                        .data(paymentMethods)
                        .status(HttpStatus.OK)
                        .message("Get all payment methods successfully")
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updatePaymentMethod (
            @PathVariable("id") Long paymentMethodId,
            @Valid @RequestBody PaymentMethodDTO paymentMethodDTO,
            BindingResult result
    ) throws DataNotFoundException {
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
             PaymentMethod paymentMethod = paymentMethodService.updatePaymentMethod(paymentMethodId, paymentMethodDTO);
             return ResponseEntity.ok(
                     Response.builder()
                             .data(paymentMethod)
                             .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                             .status(HttpStatus.OK)
                             .build()
             );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deletePaymentMethod (@PathVariable("id") Long paymentMethodId) {
        paymentMethodService.deletePaymentMethod(paymentMethodId);
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .build()
        );
    }
}

