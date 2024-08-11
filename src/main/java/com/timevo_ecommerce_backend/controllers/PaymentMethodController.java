package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.dtos.PaymentMethodDTO;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.services.payment_method.IPaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final IPaymentMethodService paymentMethodService;
    @PostMapping("")
    public ResponseEntity<?> insertPaymentMethod (
            @Valid @RequestBody PaymentMethodDTO paymentMethodDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok(paymentMethodService.insertPaymentMethod(paymentMethodDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentMethod (@PathVariable("id") Long paymentMethodId) throws DataNotFoundException {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethod(paymentMethodId));
    }

    @GetMapping("")
    public ResponseEntity<?> getPaymentMethods () {
        return ResponseEntity.ok(paymentMethodService.getAllPaymentMethods());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePaymentMethod (
            @PathVariable("id") Long paymentMethodId,
            @Valid @RequestBody PaymentMethodDTO paymentMethodDTO,
            BindingResult result
    ) {
        try {
             if (result.hasErrors()) {
                 List<String> errorMessages = result.getFieldErrors().stream()
                         .map(FieldError::getDefaultMessage)
                         .toList();
                 return ResponseEntity.badRequest().body(errorMessages);
             }
             return ResponseEntity.ok(paymentMethodService.updatePaymentMethod(paymentMethodId, paymentMethodDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaymentMethod (@PathVariable("id") Long paymentMethodId) {
        paymentMethodService.deletePaymentMethod(paymentMethodId);
        return ResponseEntity.ok("Delete Successfully");
    }
}

