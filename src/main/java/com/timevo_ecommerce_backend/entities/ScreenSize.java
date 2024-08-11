package com.timevo_ecommerce_backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "screen_size")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenSize extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_size_id")
    private long id;

    @Column(name = "size")
    private float size;

//    @ManyToMany(fetch = FetchType.LAZY,
//        cascade = {
//            CascadeType.PERSIST, CascadeType.DETACH,
//            CascadeType.MERGE, CascadeType.REFRESH
//        }
//    )
//    @JoinTable(name = "product_screen_size",
//        joinColumns = @JoinColumn(name = "screen_size_id"),
//        inverseJoinColumns = @JoinColumn(name = "product_id")
//    )
//    @JsonIgnore
//    private List<Product> products;

    @OneToMany(mappedBy = "screenSize")
    @JsonIgnore
    private List<ProductVariant> productVariants;

    @OneToMany(mappedBy = "screenSize",
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH
            }
    )
    @JsonIgnore
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "screenSize",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private List<CartItem> cartItems;}
