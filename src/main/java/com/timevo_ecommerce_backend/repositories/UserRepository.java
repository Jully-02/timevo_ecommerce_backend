package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail (String email);

    Optional<User> findByEmail (String email);

    @NonNull
    Page<User> findAll (@NonNull Pageable pageable);
}
