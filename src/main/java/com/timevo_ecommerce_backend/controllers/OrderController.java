package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.dtos.OrderDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}orders")
@RequiredArgsConstructor
public class OrderController {

    @PostMapping("")
    public ResponseEntity<?> insertOrder (
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok("Insert Order Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder (@PathVariable("id") Long orderId) {
        return ResponseEntity.ok("Get Order by ID");
    }

    @GetMapping("")
    public ResponseEntity<?> getOrders () {
        return ResponseEntity.ok("Get Orders");
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrderByUser (@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok("Get Order by User ID");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder (
            @PathVariable("id") Long orderId,
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok("Update Order Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder (@PathVariable("id") Long orderId) {
        return ResponseEntity.ok("Delete Successfully");
    }
}
