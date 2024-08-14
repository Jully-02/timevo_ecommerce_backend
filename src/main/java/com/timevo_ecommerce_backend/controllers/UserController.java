package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.dtos.UserDTO;
import com.timevo_ecommerce_backend.dtos.UserLoginDTO;
import com.timevo_ecommerce_backend.dtos.UserUpdateDTO;
import com.timevo_ecommerce_backend.responses.ActiveAccountResponse;
import com.timevo_ecommerce_backend.responses.UserListResponse;
import com.timevo_ecommerce_backend.responses.UserResponse;
import com.timevo_ecommerce_backend.services.user.IUserService;
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
@RequestMapping("${api.prefix}users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> insertUser (
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                return ResponseEntity.badRequest().body(errorMessages);
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body("Password not match");
            }

            return ResponseEntity.ok(userService.insertUser(userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (
            @Valid @RequestBody UserLoginDTO userLoginDTO
    ) {
        try {
            String token = userService.login(userLoginDTO.getEmail(), userLoginDTO.getPassword());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getUsers (
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<UserResponse> userPages = userService.getUsers(pageRequest);
        int totalPages = userPages.getTotalPages();
        List<UserResponse> userResponses = userPages.getContent();
        return ResponseEntity.ok(
                UserListResponse.builder()
                        .totalPages(totalPages)
                        .userResponses(userResponses)
                        .build()
        );
    }

    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails (
            @RequestHeader("Authorization") String token
    ) {
        try {
            String extractToken = token.substring(7);
            return ResponseEntity.ok(userService.getUserDetailsFromToken(extractToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/active-account")
    public ResponseEntity<?> activeAccount (
            @RequestParam("email") String email,
            @RequestParam("active-code") String activeCode
    ) {
        try {
            int isActive = userService.activeAccount(email, activeCode);
            if (isActive == 1) {
                return ResponseEntity.ok(1);
            }
            else if (isActive == 2) {
                return ResponseEntity.ok(2);
            }
            return ResponseEntity.badRequest().body(0);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ActiveAccountResponse.builder()
                            .message("Active failed")
                            .build()
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser (
            @PathVariable("id") Long userId,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok(userService.updateUser(userId, userUpdateDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
