package com.timevo_ecommerce_backend.services.email;

public interface IEmailService {
    void sendMessages (String from, String to, String subject, String text);
}
