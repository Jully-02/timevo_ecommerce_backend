package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.dtos.OrderDetailDTO;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.services.order_detail.IOrderDetailService;
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

    private final IOrderDetailService orderDetailService;

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
            return ResponseEntity.ok(orderDetailService.insertOrderDetail(orderDetailDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail (@PathVariable("id") Long orderDetailId) throws DataNotFoundException {
        return ResponseEntity.ok(orderDetailService.getOrderDetail(orderDetailId));
    }

    @GetMapping("/order/{order_id}")
    public ResponseEntity<?> getOrderDetailByOrder (@PathVariable("order_id") Long orderId) {
        return ResponseEntity.ok(orderDetailService.findByOrderId(orderId));
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
            return ResponseEntity.ok(orderDetailService.updateOrderDetail(orderDetailId, orderDetailDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail (@PathVariable("id") Long orderDetailId) {
        orderDetailService.deleteOrderDetail(orderDetailId);
        return ResponseEntity.ok("Delete Successfully");
    }
}
