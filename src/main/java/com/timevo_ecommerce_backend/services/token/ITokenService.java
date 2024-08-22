package com.timevo_ecommerce_backend.services.token;

import com.timevo_ecommerce_backend.entities.Token;
import com.timevo_ecommerce_backend.entities.User;

public interface ITokenService {
    Token addToken (User user, String token, boolean isMobileDevice);

    Token refreshToken (String refreshToken, User user) throws Exception;
}
