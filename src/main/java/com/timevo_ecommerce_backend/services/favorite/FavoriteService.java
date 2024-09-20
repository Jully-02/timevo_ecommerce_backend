package com.timevo_ecommerce_backend.services.favorite;

import com.timevo_ecommerce_backend.dtos.FavoriteDTO;
import com.timevo_ecommerce_backend.entities.*;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService implements IFavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;
    private final ScreenSizeRepository screenSizeRepository;
    private final ColorRepository colorRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional
    public Favorite insertFavorite(FavoriteDTO favoriteDTO) throws Exception {
        User existingUser = userRepository.findById(favoriteDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with ID = " + favoriteDTO.getUserId()));

        Favorite existingFavorite = favoriteRepository.findByUserIdAndProductIdAndAttributes(
                favoriteDTO.getUserId(),
                favoriteDTO.getProductId(),
                favoriteDTO.getColorId(),
                favoriteDTO.getMaterialId(),
                favoriteDTO.getScreenSizeId());

        if (existingFavorite != null) {
            throw new ExistDataException("The product already exists in the favorites list");
        }

        Product existingProduct = productRepository.findById(favoriteDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + favoriteDTO.getProductId()));
        Material existingMaterial = materialRepository.findById(favoriteDTO.getMaterialId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Material with ID = " + favoriteDTO.getMaterialId()));
        ScreenSize existingScreenSize = screenSizeRepository.findById(favoriteDTO.getScreenSizeId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Screen size with ID = " + favoriteDTO.getScreenSizeId()));
        Color existingColor = colorRepository.findById(favoriteDTO.getColorId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + favoriteDTO.getColorId()));

        if (!productVariantRepository.existsByProductIdAndColorIdAndMaterialIdAndScreenSizeId(
                existingProduct.getId(),
                existingColor.getId(),
                existingMaterial.getId(),
                existingScreenSize.getId()
        )) {
            throw new DataNotFoundException("No products found with these attributes");
        }

        Favorite favorite = Favorite.builder()
                .user(existingUser)
                .product(existingProduct)
                .color(existingColor)
                .material(existingMaterial)
                .screenSize(existingScreenSize)
                .build();

        return favoriteRepository.save(favorite);
    }

    @Override
    public Favorite getFavoriteById(Long id) throws Exception {
        return favoriteRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Favorite with ID = " + id));
    }

    @Override
    public List<Favorite> getAllFavorites() {
        return favoriteRepository.findAll();
    }

    @Override
    @Transactional
    public Favorite updateFavorite(Long id, FavoriteDTO favoriteDTO) throws DataNotFoundException {
        Favorite existingFavorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Favorite with ID = " + id));

        User existingUser = userRepository.findById(favoriteDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with ID = " + favoriteDTO.getUserId()));

        Product existingProduct = productRepository.findById(favoriteDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + favoriteDTO.getProductId()));
        Material existingMaterial = materialRepository.findById(favoriteDTO.getMaterialId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Material with ID = " + favoriteDTO.getMaterialId()));
        ScreenSize existingScreenSize = screenSizeRepository.findById(favoriteDTO.getScreenSizeId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Screen size with ID = " + favoriteDTO.getScreenSizeId()));
        Color existingColor = colorRepository.findById(favoriteDTO.getColorId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + favoriteDTO.getColorId()));

        if (!productVariantRepository.existsByProductIdAndColorIdAndMaterialIdAndScreenSizeId(
                existingProduct.getId(),
                existingColor.getId(),
                existingMaterial.getId(),
                existingScreenSize.getId()
        )) {
            throw new DataNotFoundException("No products found with these attributes");
        }

        existingFavorite.setUser(existingUser);
        existingFavorite.setProduct(existingProduct);
        existingFavorite.setColor(existingColor);
        existingFavorite.setMaterial(existingMaterial);
        existingFavorite.setScreenSize(existingScreenSize);

        return favoriteRepository.save(existingFavorite);
    }

    @Override
    @Transactional
    public void deleteFavorite(Long id) {
        favoriteRepository.deleteById(id);
    }

    @Override
    public List<Favorite> findByUserId(Long userId) throws Exception {
        return favoriteRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteFavoriteByUserIdAndProductIdAndAttributes(Long userId, Long productId, Long colorId, Long materialId, Long screenSizeId) {
        favoriteRepository.deleteByUserIdAndProductIdAndAttributes(userId, productId, colorId, materialId, screenSizeId);
    }
}
