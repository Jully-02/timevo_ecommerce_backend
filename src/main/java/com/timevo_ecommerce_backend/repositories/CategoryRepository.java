package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.Category;
import com.timevo_ecommerce_backend.responses.category.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName (String name);
    @Query("SELECT new com.timevo_ecommerce_backend.responses.category.CategoryResponse(c.id, c.name, COUNT(p)) " +
            "FROM Category c " +
            "LEFT JOIN c.products p " +
            "GROUP BY c.id, c.name")
    Page<CategoryResponse> getAllCategories (Pageable pageable);
}
