package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.dtos.MaterialDTO;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.MaterialListResponse;
import com.timevo_ecommerce_backend.responses.MaterialResponse;
import com.timevo_ecommerce_backend.services.material.IMaterialService;
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
@RequestMapping("${api.prefix}materials")
@RequiredArgsConstructor
public class MaterialController {
    private final IMaterialService materialService;

    @PostMapping("")
    public ResponseEntity<?> insertMaterial (
            @Valid @RequestBody MaterialDTO materialDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok(materialService.insertMaterial(materialDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllMaterials (
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<MaterialResponse> materialPages = materialService.getAllMaterials(pageRequest);
        int totalPages = materialPages.getTotalPages();
        List<MaterialResponse> materialResponses = materialPages.getContent();
        return ResponseEntity.ok(
                MaterialListResponse.builder()
                        .materialResponses(materialResponses)
                        .totalPages(totalPages)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMaterialById (@PathVariable("id") Long materialId) throws DataNotFoundException {
        return ResponseEntity.ok(materialService.getMaterialById(materialId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMaterial (
            @PathVariable("id") Long materialId,
            @Valid @RequestBody MaterialDTO materialDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok(materialService.updateMaterial(materialId, materialDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMaterial (@PathVariable("id") Long materialId) {
        materialService.deleteMaterial(materialId);
        return ResponseEntity.ok("Delete Successfully");
    }
}
