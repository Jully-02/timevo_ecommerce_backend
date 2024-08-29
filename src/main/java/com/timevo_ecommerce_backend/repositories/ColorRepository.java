package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.Color;
import com.timevo_ecommerce_backend.responses.color.ColorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {

    boolean existsByName (String name);

    @Query("SELECT new com.timevo_ecommerce_backend.responses.color.ColorResponse(c.id, c.name, c.red, c.green, c.blue, c.alpha, COUNT(pv)) " +
            "FROM Color c " +
            "LEFT JOIN c.productVariants pv " +
            "GROUP BY c.id, c.name, c.red, c.green, c.blue, c.alpha")
    Page<ColorResponse> getAllColors (Pageable pageable);
}
