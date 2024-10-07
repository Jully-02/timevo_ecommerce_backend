package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.responses.payment.PaymentResponse;
import com.timevo_ecommerce_backend.services.payment_method.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("${api.prefix}/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @GetMapping("/create-payment")
    public ResponseEntity<PaymentResponse> pay(
            HttpServletRequest request,
            @RequestParam("amount") Long amount,
            @RequestParam("bankCode") String bankCode

    ) {
        return ResponseEntity.ok(paymentService.createVnPayPayment(amount, bankCode, request));
    }
    @GetMapping("/vn-pay-callback")
    public ResponseEntity<PaymentResponse> payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String status = request.getParameter("vnp_ResponseCode");
        response.sendRedirect("http://localhost:3000/checkout?vnp_ResponseCode=" + status);
        if (status.equals("00")) {
            return ResponseEntity.ok(PaymentResponse
                    .builder()
                    .status("00")
                    .message("Successfully")
                    .build());
        } else {
            return ResponseEntity.badRequest().body(
                    PaymentResponse
                            .builder()
                            .status("99")
                            .message("Failed")
                            .build()
            );
        }
    }
}
