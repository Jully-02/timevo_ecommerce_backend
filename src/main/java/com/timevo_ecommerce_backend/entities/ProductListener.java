package com.timevo_ecommerce_backend.entities;

import com.timevo_ecommerce_backend.services.product.IProductRedisService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductListener {
    private final IProductRedisService productRedisService;

    @PrePersist
    public void prePersist (Product product) {
        System.out.println("prePersist");
    }

    @PostPersist // save = persis
    public void postPersist (Product product) {
        // Update Redis cache
        System.out.println("postPersist");
        productRedisService.clear();
    }

    @PreUpdate
    public void preUpdate (Product product) {
        // ApplicationEventPublisher.instance().publishEvent(event);
        System.out.println("preUpdate");
    }

    @PostUpdate
    public void postUpdate (Product product) {
        // Update Redis cache
        System.out.println("postUpdate");
        productRedisService.clear();
    }

    @PreRemove
    public void preRemove (Product product) {
        // ApplicationEventPublisher.instance().publishEvent(event);
        System.out.println("preRemove");
    }

    @PostRemove
    public void postRemove (Product product) {
        // Update Redis caches
        System.out.println("postRemove");
        productRedisService.clear();
    }

}
