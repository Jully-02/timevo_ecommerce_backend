package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.dtos.OrderDetailDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}order-details")
@RequiredArgsConstructor
public class OrderDetailController {

    @PostMapping("")
    public ResponseEntity<?> insertOrderDetail (
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok("Insert Order Detail Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail (@PathVariable("id") Long orderDetailId) {
        return ResponseEntity.ok("Get Order Detail with ID");
    }

    @GetMapping("/order/{order_id}")
    public ResponseEntity<?> getOrderDetailByOrder (@PathVariable("order_id") Long orderId) {
        return ResponseEntity.ok("Get Order Detail by Order ID");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail (
            @PathVariable("id") Long orderDetailId,
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok("Update Order Detail Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail (@PathVariable("id") Long orderDetailId) {
        return ResponseEntity.ok("Delete Successfully");
    }
}
