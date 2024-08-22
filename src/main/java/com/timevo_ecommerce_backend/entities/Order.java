package com.timevo_ecommerce_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "\"order\"")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "address", length = 512)
    private String address;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @Column(name = "order_date")
    private Date orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_money")
    private float totalMoney;

    @Column(name = "shipping_cost")
    private float shippingCost;

    @Column(name = "payment_cost")
    private float paymentCost;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "shipping_method_id", nullable = false)
    private ShippingMethod shippingMethod;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "payment_status")
    private boolean paymentStatus;

    @Column(name = "sub_total")
    private float subTotal;

    @OneToMany(mappedBy = "order",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private List<OrderDetail> orderDetailList;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.DETACH,
            CascadeType.MERGE, CascadeType.REFRESH
    })
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.DETACH,
            CascadeType.MERGE, CascadeType.REFRESH
    })
    @JoinColumn(name = "coupon_id")
    @JsonBackReference
    private Coupon coupon;
}
