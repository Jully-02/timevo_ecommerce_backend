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
@Table(name = "material")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private long id;

    @Column(name = "name")
    private String name;

//    @ManyToMany(fetch = FetchType.LAZY,
//            cascade = {
//            CascadeType.PERSIST, CascadeType.DETACH,
//            CascadeType.MERGE, CascadeType.REFRESH
//    })
//    @JoinTable(name = "product_material",
//            joinColumns = @JoinColumn(name = "material_id"),
//            inverseJoinColumns = @JoinColumn(name = "product_id")
//    )
//    @JsonIgnore
//    private List<Product> products;

    @OneToMany(mappedBy = "material")
    @JsonIgnore
    private List<ProductVariant> productVariants;

    @OneToMany(mappedBy = "material",
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH
            }
    )
    @JsonIgnore
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "material",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private List<CartItem> cartItems;}
