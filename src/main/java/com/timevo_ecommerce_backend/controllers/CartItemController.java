package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.CartItemDTO;
import com.timevo_ecommerce_backend.entities.CartItem;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.Response;
import com.timevo_ecommerce_backend.responses.cart_item.CartItemResponse;
import com.timevo_ecommerce_backend.services.cart_item.ICartItemService;
import com.timevo_ecommerce_backend.utils.MessagesKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final ICartItemService cartItemService;
    private final LocalizationUtils localizationUtils;


    @PostMapping("")
    public ResponseEntity<Response> insertCartItem (
            @Valid @RequestBody CartItemDTO cartItemDTO,
            BindingResult result
    ) throws DataNotFoundException {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                            .build()
            );
        }

        CartItem cartItem = cartItemService.insertCartItem(cartItemDTO);
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.CREATED)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_SUCCESSFULLY))
                        .data(
                                CartItemResponse.builder()
                                        .productId(cartItem.getProduct().getId())
                                        .colorId(cartItem.getColor().getId())
                                        .materialId(cartItem.getMaterial().getId())
                                        .screenSizeId(cartItem.getScreenSize().getId())
                                        .userId(cartItem.getUser().getId())
                                        .quantity(cartItem.getQuantity())
                                        .build()
                        )
                        .build()
        );
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> getAllCartItems () {
        List<CartItem> cartItems = cartItemService.getAllCartItems();
        List<CartItemResponse> cartItemResponses = cartItems.stream()
                .map(cartItem -> CartItemResponse.builder()
                        .productId(cartItem.getProduct().getId())
                        .colorId(cartItem.getColor().getId())
                        .materialId(cartItem.getMaterial().getId())
                        .screenSizeId(cartItem.getScreenSize().getId())
                        .userId(cartItem.getUser().getId())
                        .quantity(cartItem.getQuantity())
                        .build()
                ).toList();
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.OK)
                        .message("Get all cart items successfully")
                        .data(cartItemResponses)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN)")
    public ResponseEntity<Response> getCartItemById (@PathVariable("id") Long id) throws Exception {
        CartItem cartItem = cartItemService.getCartItemById(id);
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.OK)
                        .data(
                                CartItemResponse.builder()
                                        .productId(cartItem.getProduct().getId())
                                        .colorId(cartItem.getColor().getId())
                                        .materialId(cartItem.getMaterial().getId())
                                        .screenSizeId(cartItem.getScreenSize().getId())
                                        .userId(cartItem.getUser().getId())
                                        .quantity(cartItem.getQuantity())
                                        .build()
                        )
                        .message("Get cart item successfully")
                        .build()
        );
    }

    @GetMapping("/users/{user-id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> getCartItemByUserId (@PathVariable("user-id") Long userId) throws Exception {
        List<CartItem> cartItems = cartItemService.findByUserId(userId);
        List<CartItemResponse> cartItemResponses = cartItems.stream()
                .map(cartItem -> CartItemResponse.builder()
                        .productId(cartItem.getProduct().getId())
                        .colorId(cartItem.getColor().getId())
                        .materialId(cartItem.getMaterial().getId())
                        .screenSizeId(cartItem.getScreenSize().getId())
                        .userId(cartItem.getUser().getId())
                        .quantity(cartItem.getQuantity())
                        .build()
                ).toList();
        return ResponseEntity.ok(
                Response.builder()
                        .message("Get cart item successfully")
                        .status(HttpStatus.OK)
                        .data(cartItemResponses)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateCartItem (
            @PathVariable("id") Long id,
            @Valid @RequestBody CartItemDTO cartItemDTO,
            BindingResult result
    ) throws DataNotFoundException {
        if (result.hasErrors()) {
            List<String> errorMessage = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessage.toString()))
                            .build()
            );
        }
        CartItem cartItem = cartItemService.updateCartItem(id, cartItemDTO);
        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                        .data(CartItemResponse.builder()
                                .productId(cartItem.getProduct().getId())
                                .colorId(cartItem.getColor().getId())
                                .materialId(cartItem.getMaterial().getId())
                                .screenSizeId(cartItem.getScreenSize().getId())
                                .userId(cartItem.getUser().getId())
                                .quantity(cartItem.getQuantity())
                                .build())
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteCartItem (@PathVariable("id") Long id) {
        cartItemService.deleteCartItem(id);
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .build()
        );
    }

    @DeleteMapping("/user-product")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteCartItemByUserIdAndProductIdAndAttributes (
            @RequestParam("user-id") Long userId,
            @RequestParam("product-id") Long productId,
            @RequestParam("color-id") Long colorId,
            @RequestParam("screen-size-id") Long screenSizeId,
            @RequestParam("material-id") Long materialId
    ) {
        cartItemService.deleteCartItemByUserIdAndProductIdAndAttributes(userId, productId, colorId, materialId, screenSizeId);
        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @DeleteMapping("/user/{user-id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteCartItemByUserId (
            @PathVariable("user-id") Long userId
    ) {
        cartItemService.deleteCartItemByUserId(userId);
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .build()
        );
    }
}
