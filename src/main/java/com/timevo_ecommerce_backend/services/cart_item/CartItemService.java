package com.timevo_ecommerce_backend.services.cart_item;

import com.timevo_ecommerce_backend.dtos.CartItemDTO;
import com.timevo_ecommerce_backend.entities.*;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final MaterialRepository materialRepository;
    private final ScreenSizeRepository screenSizeRepository;
    private final ModelMapper modelMapper;
    private final ProductVariantRepository productVariantRepository;
    @Override
    @Transactional
    public CartItem insertCartItem(CartItemDTO cartItemDTO) throws DataNotFoundException {
        CartItem existingCartItem = cartItemRepository
                .findByUserIdAndProductIdAndAttributes(
                        cartItemDTO.getUserId(),
                        cartItemDTO.getProductId(),
                        cartItemDTO.getColorId(),
                        cartItemDTO.getMaterialId(),
                        cartItemDTO.getScreenSizeId()
                );
        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemDTO.getQuantity());
            return cartItemRepository.save(existingCartItem);
        }

        User existingUser = userRepository.findById(cartItemDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + cartItemDTO.getUserId()));

        Product existingProduct = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + cartItemDTO.getProductId()));

        Color existingColor = colorRepository.findById(cartItemDTO.getColorId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + cartItemDTO.getColorId()));

        Material existingMaterial = materialRepository.findById(cartItemDTO.getMaterialId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Material with ID = " + cartItemDTO.getMaterialId()));

        ScreenSize existingScreenSize = screenSizeRepository.findById(cartItemDTO.getScreenSizeId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Screen size with ID = " + cartItemDTO.getScreenSizeId()));
        if (!productVariantRepository.existsByProductIdAndColorIdAndMaterialIdAndScreenSizeId(
                existingProduct.getId(),
                existingColor.getId(),
                existingMaterial.getId(),
                existingScreenSize.getId()
        )) {
            throw new DataNotFoundException("No products found with these attributes");
        }

        CartItem newCartItem = modelMapper.map(cartItemDTO, CartItem.class);
        newCartItem.setUser(existingUser);
        newCartItem.setProduct(existingProduct);
        newCartItem.setColor(existingColor);
        newCartItem.setMaterial(existingMaterial);
        newCartItem.setScreenSize(existingScreenSize);

        return cartItemRepository.save(newCartItem);
    }

    @Override
    public CartItem getCartItemById(Long id) throws DataNotFoundException {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Cart item with ID = " + id));
    }

    @Override
    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

    @Override
    @Transactional
    public CartItem updateCartItem(Long id, CartItemDTO cartItemDTO) throws DataNotFoundException {
        CartItem existingCartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Cart item with ID = " + id));

        User existingUser = userRepository.findById(cartItemDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + cartItemDTO.getUserId()));

        Product existingProduct = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + cartItemDTO.getProductId()));

        Color existingColor = colorRepository.findById(cartItemDTO.getColorId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + cartItemDTO.getColorId()));

        Material existingMaterial = materialRepository.findById(cartItemDTO.getMaterialId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Material with ID = " + cartItemDTO.getMaterialId()));

        ScreenSize existingScreenSize = screenSizeRepository.findById(cartItemDTO.getScreenSizeId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Screen size with ID = " + cartItemDTO.getScreenSizeId()));


        existingCartItem.setQuantity(cartItemDTO.getQuantity());
        existingCartItem.setUser(existingUser);
        existingCartItem.setColor(existingColor);
        existingCartItem.setMaterial(existingMaterial);
        existingCartItem.setProduct(existingProduct);
        existingCartItem.setScreenSize(existingScreenSize);

        return cartItemRepository.save(existingCartItem);
    }


    @Override
    @Transactional
    public void deleteCartItem(Long id) {
        cartItemRepository.deleteById(id);
    }

    @Override
    public List<CartItem> findByUserId(Long userId) throws Exception {
        return cartItemRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteCartItemByUserIdAndProductIdAndAttributes (Long userId, Long productId, Long colorId, Long materialId, Long screenSizeId) {
        cartItemRepository.deleteByUserIdAndProductIdAndAttributes(userId, productId, colorId, materialId, screenSizeId);
    }

    @Override
    @Transactional
    public void deleteCartItemByUserId(Long userId) {
        cartItemRepository.deleteCartItemByUserId(userId);
    }
}
