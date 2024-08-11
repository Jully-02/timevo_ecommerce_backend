package com.timevo_ecommerce_backend.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.timevo_ecommerce_backend.entities.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private Long id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;

    private String note;

    @JsonProperty("order_date")
    private Date orderDate;

    private OrderStatus status;

    @JsonProperty("sub_total")
    private float subTotal;

    @JsonProperty("total_money")
    private float totalMoney;

    @JsonProperty("shipping_cost")
    private float shippingCost;

    @JsonProperty("payment_cost")
    private float paymentCost;

    @JsonProperty("shipping_method_id")
    private Long shippingMethodId;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_method_name")
    private String shippingMethodName;

    @JsonProperty("shipping_date")
    private java.util.Date shippingDate;

    @JsonProperty("tracking_number")
    private int trackingNumber;

    @JsonProperty("payment_method_id")
    private Long paymentMethodId;

    @JsonProperty("payment_method_name")
    private String paymentMethodName;

    @JsonProperty("is_active")
    private boolean isActive;

//    @JsonProperty("order_detail_ids")
//    private List<Long> orderDetailIds;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("payment_status")
    private boolean paymentStatus;
}
