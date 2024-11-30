package com.timevo_ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.timevo_ecommerce_backend.entities.OrderStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

    @JsonProperty("user_id")
    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User's ID must be > 0")
    private Long userId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^0\\d{9}$",
            message = "Phone number must be valid"
    )
    private String phoneNumber;

    private OrderStatus status;

    private String address;

    private String note;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Total money must be >= 0")
    private float totalMoney;

    @JsonProperty("sub_total")
    @Min(value = 0, message = "Total money must be >= 0")
    private float subTotal;

//    @JsonProperty("shipping_cost")
//    @Min(value = 0, message = "Delivery cost must be >= 0")
//    private float shippingCost;
//
//    @JsonProperty("payment_cost")
//    @Min(value = 0, message = "Payment cost must be >= 0")
//    private float paymentCost;

    @JsonProperty("shipping_method_id")
    @NotNull(message = "Shipping method is required")
    private Long shippingMethodId;

    @JsonProperty("payment_method_id")
    @NotNull(message = "Payment method is required")
    private Long paymentMethodId;

    @JsonProperty("payment_status")
    private boolean paymentStatus;

    @JsonProperty("shipping_address")
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @JsonProperty("cart_items")
    @NotNull(message = "Cart item is required")
    private List<CartItemDTO> cartItems;
}
