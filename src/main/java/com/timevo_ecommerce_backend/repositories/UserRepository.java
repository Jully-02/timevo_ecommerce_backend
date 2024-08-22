package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail (String email);

    Optional<User> findByEmail (String email);

    @Query("SELECT o FROM User o JOIN o.roles r WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "o.fullName LIKE %:keyword% OR " +
            "o.address LIKE %:keyword% OR " +
            "o.email LIKE %:keyword% OR " +
            "o.phoneNumber LIKE %:keyword%) " +
            "AND LOWER(r.name) = 'user'")
    Page<User> findAll (@Param("keyword") String keyword, Pageable pageable);
}
