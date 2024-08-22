package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.ColorDTO;
import com.timevo_ecommerce_backend.entities.Color;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.responses.ColorListResponse;
import com.timevo_ecommerce_backend.responses.ColorResponse;
import com.timevo_ecommerce_backend.responses.Response;
import com.timevo_ecommerce_backend.services.color.IColorService;
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
@RequestMapping("${api.prefix}/colors")
@RequiredArgsConstructor
public class ColorController {

    private final IColorService colorService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertColor (
            @Valid @RequestBody ColorDTO colorDTO,
            BindingResult result
    ) throws ExistDataException {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
            }
            Color color = colorService.insertColor(colorDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .data(color)
                            .status(HttpStatus.CREATED)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_SUCCESSFULLY))
                            .build()
            );
    }

    @GetMapping("")
    public ResponseEntity<Response> getAllColors (
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<ColorResponse> colorPages = colorService.getAllColor(pageRequest);
        int totalPages = colorPages.getTotalPages();
        List<ColorResponse> colorResponses = colorPages.getContent();
        return ResponseEntity.ok(
                Response.builder()
                        .data(ColorListResponse.builder()
                                .colorResponses(colorResponses)
                                .totalPages(totalPages)
                                .build())
                        .status(HttpStatus.OK)
                        .message("Get all colors successfully")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getColorById (@PathVariable("id") Long colorId) throws DataNotFoundException {
        Color color = colorService.getColorById(colorId);
        return ResponseEntity.ok(
                Response.builder()
                        .data(color)
                        .message("Get color successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateColor (
            @PathVariable("id") Long colorId,
            @Valid @RequestBody ColorDTO colorDTO,
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
             Color color = colorService.updateColor(colorId, colorDTO);
             return ResponseEntity.ok(
                     Response.builder()
                             .data(color)
                             .status(HttpStatus.OK)
                             .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                             .build()
             );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteColor (@PathVariable("id") Long colorId) {
        colorService.deleteColor(colorId);
        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}
