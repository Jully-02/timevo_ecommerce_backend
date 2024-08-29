package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.ScreenSizeDTO;
import com.timevo_ecommerce_backend.entities.ScreenSize;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.responses.Response;
import com.timevo_ecommerce_backend.responses.screen_size.ScreenSizeListResponse;
import com.timevo_ecommerce_backend.responses.screen_size.ScreenSizeResponse;
import com.timevo_ecommerce_backend.services.screen_size.IScreenSizeService;
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
@RequestMapping("${api.prefix}/screen-sizes")
@RequiredArgsConstructor
public class ScreenSizeController {
    private final IScreenSizeService screenSizeService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertScreenSize (
            @Valid @RequestBody ScreenSizeDTO screenSizeDTO,
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
            ScreenSize screenSize = screenSizeService.insertScreenSize(screenSizeDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .data(screenSize)
                            .status(HttpStatus.CREATED)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_SUCCESSFULLY))
                            .build()
            );
    }

    @GetMapping("")
    public ResponseEntity<Response> getAllScreenSizes (
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
                Response.builder()
                        .data(ScreenSizeListResponse.builder()
                                .screenSizeResponses(screenSizeResponses)
                                .totalPages(totalPages)
                                .build())
                        .status(HttpStatus.OK)
                        .message("Get all screen sizes successfully")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getScreenSizeById (@PathVariable("id") Long screenSizeId) throws DataNotFoundException {
        ScreenSize screenSize = screenSizeService.getScreenSizeById(screenSizeId);
        return ResponseEntity.ok(
                Response.builder()
                        .data(screenSize)
                        .status(HttpStatus.OK)
                        .message("Get screen size successfully")
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateScreenSize (
            @PathVariable("id") Long screenSizeId,
            @Valid @RequestBody ScreenSizeDTO screenSizeDTO,
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
            ScreenSize screenSize = screenSizeService.updateScreenSize(screenSizeId, screenSizeDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(screenSize)
                            .build()
            );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteScreenSize (@PathVariable("id") Long screenSizeId) {
        screenSizeService.deleteScreenSize(screenSizeId);
        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}
