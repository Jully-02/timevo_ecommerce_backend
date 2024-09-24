package com.timevo_ecommerce_backend.services.favorite;

import com.timevo_ecommerce_backend.dtos.FavoriteDTO;
import com.timevo_ecommerce_backend.entities.Favorite;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IFavoriteService {
    Favorite insertFavorite (FavoriteDTO favoriteDTO) throws Exception;

    Favorite getFavoriteById (Long id) throws Exception;

    List<Favorite> getAllFavorites ();

    Favorite updateFavorite (Long id, FavoriteDTO favoriteDTO) throws DataNotFoundException;

    void deleteFavorite (Long id);

    Page<Favorite> findByUserId (Long userId, PageRequest pageRequest) throws Exception;

    void deleteFavoriteByUserIdAndProductIdAndAttributes (Long userId, Long productId, Long colorId, Long materialId, Long screenSizeId);
}
