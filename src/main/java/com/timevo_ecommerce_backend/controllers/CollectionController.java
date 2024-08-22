package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.CollectionDTO;
import com.timevo_ecommerce_backend.entities.Collection;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.CollectionListResponse;
import com.timevo_ecommerce_backend.responses.CollectionResponse;
import com.timevo_ecommerce_backend.responses.Response;
import com.timevo_ecommerce_backend.services.collection.ICollectionService;
import com.timevo_ecommerce_backend.utils.MessagesKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/collections")
@RequiredArgsConstructor
public class CollectionController {
    private final ICollectionService collectionService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertCollection (
            @Valid @RequestBody CollectionDTO collectionDTO,
            BindingResult result
    ) {
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
        Collection collection = collectionService.insertCollection(collectionDTO);
             return ResponseEntity.ok(
                     Response.builder()
                             .data(collection)
                             .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_SUCCESSFULLY))
                             .status(HttpStatus.CREATED)
                             .build()
             );
    }

    @GetMapping("")
    public ResponseEntity<Response> getAllCollections (
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<CollectionResponse> collectionPages = collectionService.getAllCollections(pageRequest);
        int totalPages = collectionPages.getTotalPages();
        List<CollectionResponse> collectionResponses = collectionPages.getContent();
        return ResponseEntity.ok(
                Response.builder()
                        .data(CollectionListResponse.builder()
                                .collectionResponses(collectionResponses)
                                .totalPages(totalPages)
                                .build())
                        .message("Get all collections successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getCollectionById (@PathVariable("id") Long collectionId) throws DataNotFoundException {
        Collection collection = collectionService.getCollectionById(collectionId);
        return ResponseEntity.ok(
                Response.builder()
                        .data(collection)
                        .message("Get collection successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateCollection (
            @PathVariable("id") Long collectionId,
            @Valid @RequestBody CollectionDTO collectionDTO,
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
            Collection collection = collectionService.updateCollection(collectionId, collectionDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .data(collection)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteCollection (@PathVariable("id") Long collectionId) {
        collectionService.deleteCollection(collectionId);
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .build()
        );
    }
}
