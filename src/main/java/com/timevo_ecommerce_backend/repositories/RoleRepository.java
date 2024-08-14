package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByName (String name);
}
