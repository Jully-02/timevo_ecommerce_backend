package com.timevo_ecommerce_backend.services.cart_item;

import com.timevo_ecommerce_backend.dtos.CartItemDTO;
import com.timevo_ecommerce_backend.entities.CartItem;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;

import java.util.List;

public interface ICartItemService {
    CartItem insertCartItem (CartItemDTO cartItemDTO) throws DataNotFoundException;

    CartItem getCartItemById (Long id) throws Exception;

    List<CartItem> getAllCartItems ();

    CartItem updateCartItem (Long id, CartItemDTO cartItemDTO) throws DataNotFoundException;

    void deleteCartItem (Long id);

    List<CartItem> findByUserId (Long userId) throws Exception;

    void deleteCartItemByUserIdAndProductIdAndAttributes (Long userId, Long productId, Long colorId, Long materialId, Long screenSizeId);

    void deleteCartItemByUserId (Long userId);
}
