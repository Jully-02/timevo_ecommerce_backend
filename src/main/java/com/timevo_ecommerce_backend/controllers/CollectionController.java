package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.dtos.CollectionDTO;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.CollectionListResponse;
import com.timevo_ecommerce_backend.responses.CollectionResponse;
import com.timevo_ecommerce_backend.services.collection.ICollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}collections")
@RequiredArgsConstructor
public class CollectionController {
    private final ICollectionService collectionService;

    @PostMapping("")
    public ResponseEntity<?> insertCollection (
            @Valid @RequestBody CollectionDTO collectionDTO,
            BindingResult result
    ) {
        try {
             if (result.hasErrors()) {
                 List<String> errorMessages = result.getFieldErrors().stream()
                         .map(FieldError::getDefaultMessage)
                         .toList();
                 return ResponseEntity.badRequest().body(errorMessages);
             }
             return ResponseEntity.ok(collectionService.insertCollection(collectionDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllCollections (
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
                CollectionListResponse.builder()
                        .collectionResponses(collectionResponses)
                        .totalPages(totalPages)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCollectionById (@PathVariable("id") Long collectionId) throws DataNotFoundException {
        return ResponseEntity.ok(collectionService.getCollectionById(collectionId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCollection (
            @PathVariable("id") Long collectionId,
            @Valid @RequestBody CollectionDTO collectionDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok(collectionService.updateCollection(collectionId, collectionDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollection (@PathVariable("id") Long collectionId) {
        collectionService.deleteCollection(collectionId);
        return ResponseEntity.ok("Delete Successfully");
    }
}
