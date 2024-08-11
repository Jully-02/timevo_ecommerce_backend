package com.timevo_ecommerce_backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "collection")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collection extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id")
    private long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "collections", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products;
}
