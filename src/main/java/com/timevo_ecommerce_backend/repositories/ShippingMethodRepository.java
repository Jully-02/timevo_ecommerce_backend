package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
    boolean existsByName (String name);
}
