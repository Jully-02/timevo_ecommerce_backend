package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.ProductVariantDTO;
import com.timevo_ecommerce_backend.entities.ProductVariant;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.responses.Response;
import com.timevo_ecommerce_backend.services.variant.IProductVariantService;
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
@RequestMapping("${api.prefix}/variants")
@RequiredArgsConstructor
public class ProductVariantController {
    private final IProductVariantService productVariantService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertVariants (
            @Valid @RequestBody List<ProductVariantDTO> variantDTOs,
            BindingResult result
    ) throws DataNotFoundException, ExistDataException {
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
            List<ProductVariant> productVariants = productVariantService.insertVariant(variantDTOs);
             return ResponseEntity.ok(
                     Response.builder()
                             .data(productVariants)
                             .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_SUCCESSFULLY))
                             .status(HttpStatus.CREATED)
                             .build()
             );
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Response> getVariantByProductId (@PathVariable("id") Long productId) {
        List<ProductVariant> productVariants = productVariantService.getVariantByProductId(productId);
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.OK)
                        .data(productVariants)
                        .message("Get variant by product successfully")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getVariantById (@PathVariable("id") Long variantId) throws DataNotFoundException {
        ProductVariant productVariant = productVariantService.getVariantById(variantId);
        return ResponseEntity.ok(
                Response.builder()
                        .data(productVariant)
                        .message("Get variant successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("")
    public ResponseEntity<Response> getAllVariants () {
        List<ProductVariant> variants = productVariantService.getAllVariants();
        return ResponseEntity.ok(
                Response.builder()
                        .data(variants)
                        .message("Get all variants successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateVariant (
            @PathVariable("id") Long productId,
            @Valid @RequestBody List<ProductVariantDTO> variantDTOs,
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
             List<ProductVariant> variants = productVariantService.updateVariant(productId, variantDTOs);
             return ResponseEntity.ok(
                     Response.builder()
                             .data(variants)
                             .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                             .status(HttpStatus.OK)
                             .build()
             );
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteVariant (@PathVariable("id") Long productId) {
        productVariantService.deleteVariantByProductId(productId);
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .build()
        );
    }
}
