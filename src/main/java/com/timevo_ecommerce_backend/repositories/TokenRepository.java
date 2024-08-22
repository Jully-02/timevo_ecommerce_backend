package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.Token;
import com.timevo_ecommerce_backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUser(User user);
    Token findByToken(String token);

    Token findByRefreshToken (String token);
}
