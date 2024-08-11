package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.dtos.ColorDTO;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.ColorListResponse;
import com.timevo_ecommerce_backend.responses.ColorResponse;
import com.timevo_ecommerce_backend.services.color.IColorService;
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
@RequestMapping("${api.prefix}colors")
@RequiredArgsConstructor
public class ColorController {

    private final IColorService colorService;

    @PostMapping("")
    public ResponseEntity<?> insertColor (
            @Valid @RequestBody ColorDTO colorDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok(colorService.insertColor(colorDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllColors (
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
                ColorListResponse.builder()
                        .colorResponses(colorResponses)
                        .totalPages(totalPages)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getColorById (@PathVariable("id") Long colorId) throws DataNotFoundException {
        return ResponseEntity.ok(colorService.getColorById(colorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateColor (
            @PathVariable("id") Long colorId,
            @Valid @RequestBody ColorDTO colorDTO,
            BindingResult result
    ) {
        try {
             if (result.hasErrors()) {
                 List<String> errorMessages = result.getFieldErrors().stream()
                         .map(FieldError::getDefaultMessage)
                         .toList();
                 return ResponseEntity.badRequest().body(errorMessages);
             }
             return ResponseEntity.ok(colorService.updateColor(colorId, colorDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteColor (@PathVariable("id") Long colorId) {
        colorService.deleteColor(colorId);
        return ResponseEntity.ok("Delete Successfully");
    }
}
