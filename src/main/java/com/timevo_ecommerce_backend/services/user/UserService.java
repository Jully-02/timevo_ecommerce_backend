package com.timevo_ecommerce_backend.services.user;

import com.timevo_ecommerce_backend.components.JwtTokenUtil;
import com.timevo_ecommerce_backend.dtos.UserDTO;
import com.timevo_ecommerce_backend.dtos.UserUpdateDTO;
import com.timevo_ecommerce_backend.entities.Role;
import com.timevo_ecommerce_backend.entities.Token;
import com.timevo_ecommerce_backend.entities.User;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.PermissionDenyException;
import com.timevo_ecommerce_backend.repositories.RoleRepository;
import com.timevo_ecommerce_backend.repositories.TokenRepository;
import com.timevo_ecommerce_backend.repositories.UserRepository;
import com.timevo_ecommerce_backend.responses.UserResponse;
import com.timevo_ecommerce_backend.services.email.IEmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final IEmailService emailService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;


    @Override
    @Transactional
    public UserResponse insertUser(UserDTO userDTO) throws Exception {
        // Check if email exists or not ?
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        List<Role> roles = userDTO.getRoleIds().stream()
                .map(roleId -> {
                    try {
                        Role role = roleRepository.findById(roleId)
                                .orElseThrow(() -> new DataNotFoundException("Cannot find Role with ID = " + roleId));
                        if (role.getName().equalsIgnoreCase("ADMIN")) {
                            throw new PermissionDenyException("You can not register a admin account");
                        }
                        return role;
                    } catch (DataNotFoundException | PermissionDenyException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        User newUser = modelMapper.map(userDTO, User.class);

        String password = userDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setPassword(encodedPassword);

        newUser.setRoles(roles);
        newUser.setActiveCode(UUID.randomUUID().toString());
        newUser.setActive(false);

        userRepository.save(newUser);

        sendEmailActive(newUser.getEmail(), newUser.getActiveCode());
        return modelMapper.map(newUser, UserResponse.class);
    }

    @Override
    public String login(String email, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Invalid email or password");
        }
        User existingUser = optionalUser.get();
        // check password
        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new BadCredentialsException("Wrong email or password");
        }
        // authenticate with Java Spring security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email, password,
                existingUser.getAuthorities()
        );
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public boolean emailUnique(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public int activeAccount(String email, String activeCode) throws DataNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("Cannot find User with Email = " + email));

        if (user.isActive()) {
            return 1;
        }

        if (activeCode.equals(user.getActiveCode())) {
            user.setActive(true);
            userRepository.save(user);
            return 2;
        }
        return 0;
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }
        String email = jwtTokenUtil.extractEmail(token);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        else {
            throw new Exception("User not found");
        }
    }

    @Override
    public Page<UserResponse> getUsers(String keyword, Pageable pageable) {
        return userRepository.findAll(keyword, pageable)
                .map(user -> {
                    return modelMapper.map(user, UserResponse.class);
                });
    }

    @Override
    public UserResponse updateUser(Long userId, UserUpdateDTO userDTO) throws Exception {
        User exisitingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + userId));
        exisitingUser.setFirstName(userDTO.getFirstName());
        exisitingUser.setLastName(userDTO.getLastName());
        exisitingUser.setAddress(userDTO.getAddress());
        exisitingUser.setPhoneNumber(userDTO.getPhoneNumber());

        userRepository.save(exisitingUser);
        return modelMapper.map(exisitingUser, UserResponse.class);
    }

    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + userId));
        String encodedPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(encodedPassword);
        userRepository.save(existingUser);
        // Reset password - > clear token
        List<Token> tokens = tokenRepository.findByUser(existingUser);
        for (Token token : tokens) {
            tokenRepository.delete(token);
        }
        sendEmailResetPassword(existingUser.getEmail(), newPassword);
    }

    @Override
    @Transactional
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + userId));
        existingUser.setActive(active);
        userRepository.save(existingUser);
    }

    private void sendEmailActive(String email, String activeCode) {
        String subject = "Confirm customer account at Timevo Website";
        String text = "<!DOCTYPE html>" +
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;\">" +
                "<div style=\"max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">" +
                "<div style=\"text-align: center; padding-bottom: 20px; border-bottom: 1px solid #dddddd;\">" +
                "<h1 style=\"color: #333333;\">Timevo Website</h1>" +
                "</div>" +
                "<div style=\"margin-top: 20px;\">" +
                "<p style=\"font-size: 16px; line-height: 1.6; color: #666666;\">Please use the following code to activate your account associated with this email (<strong>" + email + "</strong>):</p>" +
                "<h1 style=\"font-size: 20px; color: #4CAF50; text-align: center;\">" + activeCode + "</h1>" +
                "<p style=\"font-size: 16px; line-height: 1.6; color: #666666;\">Click on the link below to activate your account:</p>" +
                "<p style=\"text-align: center;\"><a href=\"http://localhost:3000/active-account/" + email + "/" + activeCode + "\" style=\"font-size: 18px; color: #ffffff; background-color: #007BFF; padding: 10px 20px; text-decoration: none; border-radius: 4px;\">Activate Account</a></p>" +
                "</div>" +
                "<div style=\"margin-top: 30px; padding-top: 20px; border-top: 1px solid #dddddd; text-align: center; font-size: 14px; color: #aaaaaa;\">" +
                "<p>&copy; 2024 Timevo. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        emailService.sendMessages("timevo.service@gmail.com", email, subject, text);
    }

    private void sendEmailResetPassword(String email, String newPassword) {
        String subject = "Your New Password for Timevo Website";
        String text = "<!DOCTYPE html>" +
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;\">" +
                "<div style=\"max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">" +
                "<div style=\"text-align: center; padding-bottom: 20px; border-bottom: 1px solid #dddddd;\">" +
                "<h1 style=\"color: #333333;\">Timevo Website</h1>" +
                "</div>" +
                "<div style=\"margin-top: 20px;\">" +
                    "<p style=\"font-size: 16px; line-height: 1.6; color: #666666;\">Your password has been reset. Your new password for the Timevo account associated with this email (<strong>" + email + "</strong>) is:</p>" +
                "<p style=\"font-size: 20px; color: #ff5722; font-weight: bold; text-align: center;\">" + newPassword + "</p>" +
                "<p style=\"font-size: 16px; line-height: 1.6; color: #666666;\">Please change this password after logging in for security purposes.</p>" +
                "<p style=\"font-size: 16px; line-height: 1.6; color: #666666;\">If you did not request this change, please contact our support immediately.</p>" +
                "</div>" +
                "<div style=\"margin-top: 30px; padding-top: 20px; border-top: 1px solid #dddddd; text-align: center; font-size: 14px; color: #aaaaaa;\">" +
                "<p>&copy; 2024 Timevo. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        emailService.sendMessages("timevo.service@gmail.com", email, subject, text);
    }
}
