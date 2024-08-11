package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @NonNull
    Page<Order> findAll(@NonNull Pageable pageable);

    List<Order> findByUserId (Long userId);
}
