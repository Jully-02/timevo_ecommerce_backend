package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserId (Long userId);

    List<Feedback> findByProductId (Long productId);
}

