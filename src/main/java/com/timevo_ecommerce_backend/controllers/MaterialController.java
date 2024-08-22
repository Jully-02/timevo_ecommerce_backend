package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.MaterialDTO;
import com.timevo_ecommerce_backend.entities.Material;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.responses.MaterialListResponse;
import com.timevo_ecommerce_backend.responses.MaterialResponse;
import com.timevo_ecommerce_backend.responses.Response;
import com.timevo_ecommerce_backend.services.material.IMaterialService;
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
@RequestMapping("${api.prefix}/materials")
@RequiredArgsConstructor
public class MaterialController {
    private final IMaterialService materialService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertMaterial (
            @Valid @RequestBody MaterialDTO materialDTO,
            BindingResult result
    ) throws ExistDataException {
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
            Material material = materialService.insertMaterial(materialDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_SUCCESSFULLY))
                            .data(material)
                            .status(HttpStatus.CREATED)
                            .build()
            );
    }

    @GetMapping("")
    public ResponseEntity<Response> getAllMaterials (
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
                Response.builder()
                        .data(MaterialListResponse.builder()
                                .materialResponses(materialResponses)
                                .totalPages(totalPages)
                                .build())
                        .status(HttpStatus.OK)
                        .message("Get all materials successfully")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getMaterialById (@PathVariable("id") Long materialId) throws DataNotFoundException {
        Material material = materialService.getMaterialById(materialId);
        return ResponseEntity.ok(
                Response.builder()
                        .data(material)
                        .message("Get material successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateMaterial (
            @PathVariable("id") Long materialId,
            @Valid @RequestBody MaterialDTO materialDTO,
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
            Material material = materialService.updateMaterial(materialId, materialDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .data(material)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteMaterial (@PathVariable("id") Long materialId) {
        materialService.deleteMaterial(materialId);
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .build()
        );
    }
}
