package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.RoleDTO;
import com.timevo_ecommerce_backend.entities.Role;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.responses.Response;
import com.timevo_ecommerce_backend.services.role.IRoleService;
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
@RequestMapping("${api.prefix}/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertRole (
            @Valid @RequestBody RoleDTO roleDTO,
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
            Role role = roleService.insertRole(roleDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .data(role)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_SUCCESSFULLY))
                            .status(HttpStatus.CREATED)
                            .build()
            );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getRole (@PathVariable("id") Long roleId) throws DataNotFoundException {
        Role role = roleService.getRole(roleId);
        return ResponseEntity.ok(
                Response.builder()
                        .data(role)
                        .message("Get role successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> getAllRoles () {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(
                Response.builder()
                        .data(roles)
                        .message("Get all roles successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateRole (
            @PathVariable("id") Long roleId,
            @Valid @RequestBody RoleDTO roleDTO,
            BindingResult result
    ) throws DataNotFoundException {
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
            Role role = roleService.updateRole(roleId, roleDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .data(role)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteRole (@PathVariable("id") Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}
