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
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Event-driven approach with Spring Data JPA
@EntityListeners(ProductListener.class)
public class Product extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "price")
    private float price;

    @Column(name = "discount")
    private float discount;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "average_rate")
    private float averageRate;

    @Column(name = "quantity_stock")
    private int quantityStock;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.DETACH,
            CascadeType.REFRESH, CascadeType.MERGE
    })
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;

    @OneToMany(mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private List<Feedback> feedbackList;

    @OneToMany(mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH
            }
    )
    @JsonIgnore
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private List<CartItem> cartItems;

//    @ManyToMany(
//            fetch = FetchType.LAZY,
//            cascade = {
//                CascadeType.PERSIST, CascadeType.DETACH,
//                CascadeType.MERGE, CascadeType.REFRESH
//            }
//    )
//    @JoinTable(name = "product_color",
//            joinColumns = @JoinColumn(name = "product_id"),
//            inverseJoinColumns = @JoinColumn(name = "color_id")
//    )
//    @JsonIgnore
//    private List<Color> colors;
//
//    @ManyToMany(fetch = FetchType.LAZY,
//            cascade = {
//                CascadeType.PERSIST, CascadeType.MERGE,
//                CascadeType.DETACH, CascadeType.REFRESH
//            }
//    )
//    @JoinTable(name = "product_screen_size",
//            joinColumns = @JoinColumn(name = "product_id"),
//            inverseJoinColumns = @JoinColumn(name = "screen_size_id")
//    )
//    @JsonIgnore
//    private List<ScreenSize> screenSizes;
//
//    @ManyToMany(fetch = FetchType.LAZY,
//            cascade = {
//                CascadeType.PERSIST, CascadeType.MERGE,
//                CascadeType.DETACH, CascadeType.REFRESH
//            }
//    )
//    @JoinTable(name = "product_material",
//            joinColumns = @JoinColumn(name = "product_id"),
//            inverseJoinColumns = @JoinColumn(name = "material_id")
//    )
//    @JsonIgnore
//    private List<Material> materials;

    @OneToMany(mappedBy = "product",
        cascade = CascadeType.ALL,
        fetch = FetchType.EAGER
    )
    @JsonIgnore
    private List<ProductVariant> productVariants;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST, CascadeType.DETACH,
                    CascadeType.MERGE, CascadeType.REFRESH
            }
    )
    @JoinTable(name = "product_collection",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "collection_id")
    )
    @JsonIgnore
    private List<Collection> collections;
}
