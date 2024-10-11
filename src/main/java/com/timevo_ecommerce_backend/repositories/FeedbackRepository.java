package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Page<Feedback> findByUserId (Long userId, Pageable pageable);

    Page<Feedback> findByProductId (Long productId, Pageable pageable);

    long countByUserId (Long userId);

    long countByProductId (Long productId);
}

