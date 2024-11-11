package com.timevo_ecommerce_backend.controllers;

import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.*;
import com.timevo_ecommerce_backend.entities.Token;
import com.timevo_ecommerce_backend.entities.User;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.Response;
import com.timevo_ecommerce_backend.responses.cloudinary.CloudinaryResponse;
import com.timevo_ecommerce_backend.responses.user.*;
import com.timevo_ecommerce_backend.services.token.ITokenService;
import com.timevo_ecommerce_backend.services.user.IUserService;
import com.timevo_ecommerce_backend.utils.FileUploadUtil;
import com.timevo_ecommerce_backend.utils.MessagesKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final ITokenService tokenService;
    private final LocalizationUtils localizationUtils;
    private final ModelMapper modelMapper;
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "@$!%*?&";
    private static final SecureRandom RANDOM = new SecureRandom();

    @PostMapping("/register")
    public ResponseEntity<Response> insertUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ) {
        try {
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
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.PASSWORD_NOT_MATCH))
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
            }

            UserResponse userResponse = userService.insertUser(userDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .data(userResponse)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.REGISTER_SUCCESSFULLY))
                            .status(HttpStatus.CREATED)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.REGISTER_FAILED, e.getMessage()))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request
    ) throws Exception {
        try {
            String token = userService.login(userLoginDTO.getEmail(), userLoginDTO.getPassword());
            String userAgent = request.getHeader("User-Agent");
            User user = userService.getUserDetailsFromToken(token);
            Token jwtToken = tokenService.addToken(user, token, userAgent.toLowerCase().contains("mobile"));

            return ResponseEntity.ok(
                    LoginResponse.builder()
                            .messages(localizationUtils.getLocalizedMessage(MessagesKey.LOGIN_SUCCESSFULLY))
                            .token(token)
                            .tokenType(jwtToken.getTokenType())
                            .refreshToken(jwtToken.getRefreshToken())
                            .username(user.getUsername())
                            .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                            .id(user.getId())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .messages(localizationUtils.getLocalizedMessage(MessagesKey.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getUserDetails(
            @RequestHeader("Authorization") String token
    ) throws Exception {
        String extractToken = token.substring(7);
        User user = userService.getUserDetailsFromToken(extractToken);
        return ResponseEntity.ok(
                Response.builder()
                        .data(modelMapper.map(user, UserResponse.class))
                        .status(HttpStatus.OK)
                        .message("Get user details successfully")
                        .build()
        );
    }

    @GetMapping("/email-unique")
    public ResponseEntity<Response> emailUnique(@RequestParam("email") String email) {
        boolean isUnique = userService.emailUnique(email);
        if (!isUnique) {
            return ResponseEntity.ok(
                    Response.builder()
                            .status(HttpStatus.OK)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.EMAIL_NOT_EXIST))
                            .data(false)
                            .build()
            );
        }
        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.EMAIL_EXIST))
                        .status(HttpStatus.OK)
                        .data(true)
                        .build()
        );
    }

    @GetMapping("/active-account")
    public ResponseEntity<?> activeAccount(
            @RequestParam("email") String email,
            @RequestParam("active-code") String activeCode
    ) throws DataNotFoundException {
        int isActive = userService.activeAccount(email, activeCode);
        if (isActive == 1) {
            return ResponseEntity.ok(
                    ActiveAccountResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.ACTIVATED_ACCOUNT))
                            .build()
            );
        } else if (isActive == 2) {
            return ResponseEntity.ok(
                    ActiveAccountResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.ACTIVATION_SUCCESSFULLY))
                            .build()
            );
        }
        return ResponseEntity.badRequest().body(
                ActiveAccountResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.ACTIVATION_FAILED))
                        .build()
        );
    }

    @PutMapping(value = "/avatar/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateAvatarUser(
            @PathVariable("id") Long userId,
            @ModelAttribute("file") MultipartFile file
    ) throws Exception {
        String avatarUrl = "";
        String avatarName = "";
        if (!file.isEmpty()) {
            if (file.getSize() > FileUploadUtil.MAX_FILE_SIZE) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body("File is to large! Maximum size is 10MB");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("File must be an image");
            }
            CloudinaryResponse cloudinaryResponse = userService.uploadImage(file);
            avatarUrl = cloudinaryResponse.getUrl();
            avatarName = cloudinaryResponse.getPublicId();
        }
        if (!avatarUrl.equals("") && !avatarName.equals("")) {
            UserResponse userResponse = userService.updateAvatarUser(userId, avatarUrl, avatarName);
            return ResponseEntity.ok(
                    Response.builder()
                            .data(userResponse)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_FAILED))
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
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
    ) throws Exception {
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
        UserResponse userResponse = userService.updateUser(userId, userUpdateDTO);
        return ResponseEntity.ok(
                Response.builder()
                        .data(userResponse)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO,
            BindingResult result
    ) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .messages(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                            .build()
            );
        }
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
    }

    @PutMapping("/change-password")
    public ResponseEntity<Response> changePassword (
            @Valid @RequestBody UserActionPasswordDTO userActionPasswordDTO,
            BindingResult result
    ) throws Exception {
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
        boolean isChange = userService.changePassword(userActionPasswordDTO);
        if (isChange) {
            return ResponseEntity.ok(
                    Response.builder()
                            .data(true)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.CHANGE_PASSWORD_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
        }
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .data(false)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.CHANGE_PASSWORD_FAILED))
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
        );
    }

    @PutMapping("/reset-password/{user-id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> resetPassword(
            @PathVariable("user-id") Long userId
    ) throws DataNotFoundException {
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
        return ResponseEntity.ok(
                Response.builder()
                        .data(newPassword)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.RESET_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PutMapping("/generate-otp")
    public ResponseEntity<Response> generateOTP(
            @RequestParam("email") String email
    ) throws DataNotFoundException {
        int isGenerateOTP = userService.generateOTP(email);
        if (isGenerateOTP == 2) {
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.OTP_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
        }
        else  {
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_OTP_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
        }
    }

    @PutMapping("/check-otp")
    public ResponseEntity<Response> checkOTP(
            @RequestParam("email") String email,
            @RequestParam("otp") String otp
    ) throws DataNotFoundException {
        boolean isCheck = userService.checkOTP(email, otp);
        if (isCheck) {
            return ResponseEntity.ok(
                    Response.builder()
                            .data(true)
                            .status(HttpStatus.OK)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.CHECK_OTP_SUCCESSFULLY))
                            .build()
            );
        }
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .data(false)
                        .status(HttpStatus.BAD_REQUEST)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.CHECK_OTP_FAILED))
                        .build()
        );
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<Response> forgotPassword(
            @Valid @RequestBody UserActionPasswordDTO userActionPasswordDTO,
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
        boolean isUpdate = userService.forgotPassword(userActionPasswordDTO);
        if (isUpdate) {
            return ResponseEntity.ok(
                    Response.builder()
                            .data(true)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
        }
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .data(false)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_FAILED))
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
        );
    }

    @PutMapping("/block/{user-id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> blockOrEnable(
            @PathVariable("user-id") Long userId
    ) throws DataNotFoundException {
        User user = userService.blockOrEnable(userId);
        String message = user.isActive() ? "Successfully enabled the user." : "Successfully blocked the user.";
        return ResponseEntity.ok(
                Response.builder()
                        .message(message)
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}
