package com.timevo_ecommerce_backend.repositories;

import com.timevo_ecommerce_backend.entities.Collection;
import com.timevo_ecommerce_backend.responses.collection.CollectionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    @Query("SELECT new com.timevo_ecommerce_backend.responses.collection.CollectionResponse(c.id, c.name, COUNT(p)) " +
            "FROM Collection c " +
            "LEFT JOIN c.products p " +
            "GROUP BY c.id, c.name")
    Page<CollectionResponse> getAllCollections (Pageable pageable);
}
