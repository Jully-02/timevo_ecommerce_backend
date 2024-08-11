package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.dtos.ShippingMethodDTO;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.services.shipping_method.IShippingMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}shipping-methods")
@RequiredArgsConstructor
public class ShippingMethodController {

    private final IShippingMethodService shippingMethodService;
    @PostMapping("")
    public ResponseEntity<?> insertShippingMethod (
            @Valid @RequestBody ShippingMethodDTO shippingMethodDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok(shippingMethodService.insertShippingMethod(shippingMethodDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShippingMethod (@PathVariable("id") Long shippingMethodId) throws DataNotFoundException {
        return ResponseEntity.ok(shippingMethodService.getShippingMethod(shippingMethodId));
    }

    @GetMapping("")
    public ResponseEntity<?> getShippingMethods () {
        return ResponseEntity.ok(shippingMethodService.getAllShippingMethod());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShippingMethod (
            @PathVariable("id") Long shippingMethodId,
            @Valid @RequestBody ShippingMethodDTO shippingMethodDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok(shippingMethodService.updateShippingMethod(shippingMethodId, shippingMethodDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShippingMethod (@PathVariable("id") Long shippingMethodId) {
        shippingMethodService.deleteShippingMethod(shippingMethodId);
        return ResponseEntity.ok("Delete Successfully");
    }
}

