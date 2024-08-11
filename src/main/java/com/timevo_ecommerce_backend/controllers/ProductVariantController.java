package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.dtos.ProductVariantDTO;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.services.variant.IProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}variants")
@RequiredArgsConstructor
public class ProductVariantController {
    private final IProductVariantService productVariantService;

    @PostMapping("")
    public ResponseEntity<?> insertVariants (
            @Valid @RequestBody List<ProductVariantDTO> variantDTOs,
            BindingResult result
    ) {
         try {
             if (result.hasErrors()) {
                 List<String> errorMessages = result.getFieldErrors().stream()
                         .map(FieldError::getDefaultMessage)
                         .toList();
                 return ResponseEntity.badRequest().body(errorMessages);
             }
             return ResponseEntity.ok(productVariantService.insertVariant(variantDTOs));
         } catch (Exception e) {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getVariantByProductId (@PathVariable("id") Long productId) {
        return ResponseEntity.ok(productVariantService.getVariantByProductId(productId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVariantById (@PathVariable("id") Long variantId) throws DataNotFoundException {
        return ResponseEntity.ok(productVariantService.getVariantById(variantId));
    }

    @GetMapping("")
    public ResponseEntity<?> getAllVariants () {
        return ResponseEntity.ok(productVariantService.getAllVariants());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateVariant (
            @PathVariable("id") Long productId,
            @Valid @RequestBody List<ProductVariantDTO> variantDTOs,
            BindingResult result
    ) {
         try {
             if (result.hasErrors()) {
                 List<String> errorMessages = result.getFieldErrors().stream()
                         .map(FieldError::getDefaultMessage)
                         .toList();
                 return ResponseEntity.badRequest().body(errorMessages);
             }
             return ResponseEntity.ok(productVariantService.updateVariant(productId, variantDTOs));
         } catch (Exception e) {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteVariant (@PathVariable("id") Long productId) {
        productVariantService.deleteVariantByProductId(productId);
        return ResponseEntity.ok("Delete Successfully");
    }
}
