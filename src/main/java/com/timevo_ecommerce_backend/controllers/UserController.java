package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.RefreshTokenDTO;
import com.timevo_ecommerce_backend.dtos.UserDTO;
import com.timevo_ecommerce_backend.dtos.UserLoginDTO;
import com.timevo_ecommerce_backend.dtos.UserUpdateDTO;
import com.timevo_ecommerce_backend.entities.Token;
import com.timevo_ecommerce_backend.entities.User;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.ActiveAccountResponse;
import com.timevo_ecommerce_backend.responses.LoginResponse;
import com.timevo_ecommerce_backend.responses.UserListResponse;
import com.timevo_ecommerce_backend.responses.UserResponse;
import com.timevo_ecommerce_backend.services.token.ITokenService;
import com.timevo_ecommerce_backend.services.user.IUserService;
import com.timevo_ecommerce_backend.utils.MessagesKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final ITokenService tokenService;
    private final LocalizationUtils localizationUtils;
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "@$!%*?&";
    private static final SecureRandom RANDOM = new SecureRandom();

    @PostMapping("/register")
    public ResponseEntity<?> insertUser(
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
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body("Password not match");
            }

            return ResponseEntity.ok(userService.insertUser(userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "16") int limit,
            @RequestParam(value = "keyword", defaultValue = "") String keyword
    ) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<UserResponse> userPages = userService.getUsers(keyword, pageRequest);
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
    @PreAuthorize("hasRole('ROLE_ADMIN' or hasRole('ROLE_USER')")
    public ResponseEntity<?> getUserDetails(
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
    public ResponseEntity<?> activeAccount(
            @RequestParam("email") String email,
            @RequestParam("active-code") String activeCode
    ) {
        try {
            int isActive = userService.activeAccount(email, activeCode);
            if (isActive == 1) {
                return ResponseEntity.ok(1);
            } else if (isActive == 2) {
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
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateUser(
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

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO,
            BindingResult result
    ) {
        try {
            User userDetail = userService.getUserDetailsFromToken(refreshTokenDTO.getRefreshToken());
            Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);
            return ResponseEntity.ok(
                    LoginResponse.builder()
                            .messages("Refresh token successfully")
                            .token(jwtToken.getToken())
                            .tokenType(jwtToken.getTokenType())
                            .username(userDetail.getUsername())
                            .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                            .id(userDetail.getId())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .messages(localizationUtils.getLocalizedMessage(MessagesKey.LOGIN_FAILED))
                            .build()
            );
        }
    }

    @PutMapping("/reset-password/{user-id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> resetPassword(
            @PathVariable("user-id") Long userId
    ) {
        try {
            String newPassword = "" +
                    UPPER_CASE.charAt(RANDOM.nextInt(UPPER_CASE.length())) +
                    LOWER_CASE.charAt(RANDOM.nextInt(LOWER_CASE.length())) +
                    LOWER_CASE.charAt(RANDOM.nextInt(LOWER_CASE.length())) +
                    LOWER_CASE.charAt(RANDOM.nextInt(LOWER_CASE.length())) +
                    SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())) +
                    DIGITS.charAt(RANDOM.nextInt(DIGITS.length())) +
                    DIGITS.charAt(RANDOM.nextInt(DIGITS.length())) +
                    DIGITS.charAt(RANDOM.nextInt(DIGITS.length()));
            userService.resetPassword(userId, newPassword);
            return ResponseEntity.ok(newPassword);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body("User not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/block/{user-id}/active")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> blockOrEnable (
            @PathVariable("user-id") Long userId,
            @PathVariable("active") int active
    ) {
         try {
             userService.blockOrEnable(userId, active > 0);
             String message = active > 0 ? "Successfully enabled the user." : "Successfully blocked the user.";
             return ResponseEntity.ok(message);
         } catch (DataNotFoundException e) {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
    }
}
