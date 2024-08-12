package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.dtos.OrderDTO;
import com.timevo_ecommerce_backend.entities.Order;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.OrderListResponse;
import com.timevo_ecommerce_backend.responses.OrderResponse;
import com.timevo_ecommerce_backend.services.order.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

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
            return ResponseEntity.ok(orderService.insertOrder(orderDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder (@PathVariable("id") Long orderId) throws DataNotFoundException {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @GetMapping("")
    public ResponseEntity<?> getOrders (
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<OrderResponse> orderPages = orderService.getOrders(pageRequest);
        int totalPages = orderPages.getTotalPages();
        List<OrderResponse> orderResponses = orderPages.getContent();
        return ResponseEntity.ok(
                OrderListResponse.builder()
                        .orderResponses(orderResponses)
                        .totalPages(totalPages)
                        .build()
        );
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
            return ResponseEntity.ok(orderService.updateOrder(orderId, orderDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder (@PathVariable("id") Long orderId) throws DataNotFoundException {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Delete Successfully");
    }
}
