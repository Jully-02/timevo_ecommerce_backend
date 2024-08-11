package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.dtos.ScreenSizeDTO;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.ScreenSizeListResponse;
import com.timevo_ecommerce_backend.responses.ScreenSizeResponse;
import com.timevo_ecommerce_backend.services.screen_size.IScreenSizeService;
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
@RequestMapping("${api.prefix}screen-sizes")
@RequiredArgsConstructor
public class ScreenSizeController {
    private final IScreenSizeService screenSizeService;

    @PostMapping("")
    public ResponseEntity<?> insertScreenSize (
            @Valid @RequestBody ScreenSizeDTO screenSizeDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok(screenSizeService.insertScreenSize(screenSizeDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllScreenSizes (
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<ScreenSizeResponse> screenSizePages = screenSizeService.getAllScreenSizes(pageRequest);
        int totalPages = screenSizePages.getTotalPages();
        List<ScreenSizeResponse> screenSizeResponses = screenSizePages.getContent();
        return ResponseEntity.ok(
                ScreenSizeListResponse.builder()
                        .screenSizeResponses(screenSizeResponses)
                        .totalPages(totalPages)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getScreenSizeById (@PathVariable("id") Long screenSizeId) throws DataNotFoundException {
        return ResponseEntity.ok(screenSizeService.getScreenSizeById(screenSizeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateScreenSize (
            @PathVariable("id") Long screenSizeId,
            @Valid @RequestBody ScreenSizeDTO screenSizeDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok(screenSizeService.updateScreenSize(screenSizeId, screenSizeDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScreenSize (@PathVariable("id") Long screenSizeId) {
        screenSizeService.deleteScreenSize(screenSizeId);
        return ResponseEntity.ok("Delete Successfully");
    }
}
