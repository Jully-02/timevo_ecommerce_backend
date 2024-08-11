package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.ScreenSize;
import com.timevo_ecommerce_backend.responses.ScreenSizeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreenSizeRepository extends JpaRepository<ScreenSize, Long> {

    boolean existsBySize (float size);
    @Query("SELECT new com.timevo_ecommerce_backend.responses.ScreenSizeResponse(s.id, s.size, COUNT(pv)) " +
            "FROM ScreenSize s " +
            "LEFT JOIN s.productVariants pv " +
            "GROUP BY s.id, s.size")
    Page<ScreenSizeResponse> getAllScreenSizes (Pageable pageable);
}
