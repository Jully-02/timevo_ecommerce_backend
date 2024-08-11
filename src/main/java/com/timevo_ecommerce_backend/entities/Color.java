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
@Table(name = "color")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "color_id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "red")
    private int red;

    @Column(name = "green")
    private int green;

    @Column(name = "blue")
    private int blue;

    @Column(name = "alpha")
    private float alpha;

//    @ManyToMany(fetch = FetchType.LAZY, cascade = {
//            CascadeType.PERSIST, CascadeType.MERGE,
//            CascadeType.DETACH, CascadeType.REFRESH
//    })
//    @JoinTable(name = "product_color",
//        joinColumns = @JoinColumn(name = "color_id"),
//        inverseJoinColumns = @JoinColumn(name = "product_id")
//    )
//    @JsonIgnore
//    private List<Product> products;

    @OneToMany(mappedBy = "color")
    @JsonIgnore
    private List<ProductVariant> productVariants;

    @OneToMany(mappedBy = "color",
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH
            }
    )
    @JsonIgnore
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "color",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private List<CartItem> cartItems;
}
