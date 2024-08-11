package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.Material;
import com.timevo_ecommerce_backend.responses.MaterialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    boolean existsByName (String name);
    @Query("SELECT new com.timevo_ecommerce_backend.responses.MaterialResponse(m.id, m.name, COUNT(pv)) " +
            "FROM Material m " +
            "LEFT JOIN m.productVariants pv " +
            "GROUP BY m.id, m.name")
    Page<MaterialResponse> getAllMaterial (Pageable pageable);
}
