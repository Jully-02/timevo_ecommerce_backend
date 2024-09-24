package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.FavoriteDTO;
import com.timevo_ecommerce_backend.entities.Favorite;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.Response;
import com.timevo_ecommerce_backend.responses.favorite.FavoriteListResponse;
import com.timevo_ecommerce_backend.responses.favorite.FavoriteResponse;
import com.timevo_ecommerce_backend.services.favorite.IFavoriteService;
import com.timevo_ecommerce_backend.utils.MessagesKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final IFavoriteService favoriteService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> insertFavorite(
            @Valid @RequestBody FavoriteDTO favoriteDTO,
            BindingResult result
    ) throws Exception {
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
        Favorite favorite = favoriteService.insertFavorite(favoriteDTO);
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.CREATED)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_SUCCESSFULLY))
                        .data(
                                FavoriteResponse.builder()
                                        .userId(favorite.getUser().getId())
                                        .productId(favorite.getProduct().getId())
                                        .colorId(favorite.getColor().getId())
                                        .screenSizeId(favorite.getScreenSize().getId())
                                        .materialId(favorite.getMaterial().getId())
                                        .id(favorite.getId())
                                        .build()
                        )
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> getFavorite(@PathVariable("id") Long id) throws Exception {
        Favorite favorite = favoriteService.getFavoriteById(id);
        return ResponseEntity.ok(
                Response.builder()
                        .data(FavoriteResponse.builder()
                                .userId(favorite.getUser().getId())
                                .productId(favorite.getProduct().getId())
                                .colorId(favorite.getColor().getId())
                                .screenSizeId(favorite.getScreenSize().getId())
                                .materialId(favorite.getMaterial().getId())
                                .id(favorite.getId())
                                .build()
                        )
                        .status(HttpStatus.OK)
                        .message("Get favorite successfully")
                        .build()
        );
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> getFavorites() {
        List<Favorite> favorites = favoriteService.getAllFavorites();
        return ResponseEntity.ok(
                Response.builder()
                        .message("Get all favorites successfully")
                        .data(
                                favorites.stream().map(
                                        favorite -> FavoriteResponse.builder()
                                                .userId(favorite.getUser().getId())
                                                .productId(favorite.getProduct().getId())
                                                .colorId(favorite.getColor().getId())
                                                .screenSizeId(favorite.getScreenSize().getId())
                                                .materialId(favorite.getMaterial().getId())
                                                .id(favorite.getId())
                                                .build()
                                ).toList()
                        )
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/user/{user-id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> getFavoritesByUserId(
            @PathVariable("user-id") Long userId,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "6", name = "limit") int limit
    ) throws Exception {
        PageRequest pageRequest = PageRequest.of(
                page, limit
        );
        Page<Favorite> favoritePage = favoriteService.findByUserId(userId, pageRequest);
        List<Favorite> favorites = favoritePage.getContent();
        int totalPages = favoritePage.getTotalPages();
        return ResponseEntity.ok(
                Response.builder()
                        .data(
                                FavoriteListResponse.builder()
                                        .favoriteResponses(
                                                favorites.stream().map(
                                                        favorite -> FavoriteResponse.builder()
                                                                .userId(favorite.getUser().getId())
                                                                .productId(favorite.getProduct().getId())
                                                                .colorId(favorite.getColor().getId())
                                                                .screenSizeId(favorite.getScreenSize().getId())
                                                                .materialId(favorite.getMaterial().getId())
                                                                .id(favorite.getId())
                                                                .build()
                                                ).toList()
                                        )
                                        .totalPages(totalPages)
                                        .build()
                        )
                        .message("Get favorites successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> updateFavorite(
            @PathVariable("id") Long id,
            @Valid @RequestBody FavoriteDTO favoriteDTO,
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
        Favorite favorite = favoriteService.updateFavorite(id, favoriteDTO);
        return ResponseEntity.ok(
                Response.builder()
                        .data(
                                FavoriteResponse.builder()
                                        .userId(favorite.getUser().getId())
                                        .productId(favorite.getProduct().getId())
                                        .colorId(favorite.getColor().getId())
                                        .screenSizeId(favorite.getScreenSize().getId())
                                        .materialId(favorite.getMaterial().getId())
                                        .id(favorite.getId())
                                        .build()
                        )
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteFavoriteById(@PathVariable("id") Long id) {
        favoriteService.deleteFavorite(id);
        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @DeleteMapping("/user-product")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteFavoriteByUserIdAndProductIdAndAttributes(
            @RequestParam("user-id") Long userId,
            @RequestParam("product-id") Long productId,
            @RequestParam("color-id") Long colorId,
            @RequestParam("screen-size-id") Long screenSizeId,
            @RequestParam("material-id") Long materialId
    ) {
        favoriteService.deleteFavoriteByUserIdAndProductIdAndAttributes(
                userId,
                productId,
                colorId,
                materialId,
                screenSizeId
        );
        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}
