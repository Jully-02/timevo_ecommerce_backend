package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "o.firstName LIKE %:keyword% OR " +
            "o.lastName LIKE %:keyword% OR " +
            "o.note LIKE %:keyword% OR " +
            "o.email LIKE %:keyword% OR " +
            "o.phoneNumber LIKE %:keyword%)")
    Page<Order> findAll (@Param("keyword") String keyword, Pageable pageable);

    List<Order> findByUserId (Long userId);
}
