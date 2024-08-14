package com.timevo_ecommerce_backend.services.user;

import com.timevo_ecommerce_backend.components.JwtTokenUtils;
import com.timevo_ecommerce_backend.dtos.UserDTO;
import com.timevo_ecommerce_backend.dtos.UserUpdateDTO;
import com.timevo_ecommerce_backend.entities.Role;
import com.timevo_ecommerce_backend.entities.User;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.PermissionDenyException;
import com.timevo_ecommerce_backend.repositories.RoleRepository;
import com.timevo_ecommerce_backend.repositories.UserRepository;
import com.timevo_ecommerce_backend.responses.UserResponse;
import com.timevo_ecommerce_backend.services.email.IEmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final IEmailService emailService;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;


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
    public UserResponse getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }
        String email = jwtTokenUtil.extractEmail(token);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return modelMapper.map(user.get(), UserResponse.class);
        }
        else {
            throw new Exception("User not found");
        }
    }

    @Override
    public Page<UserResponse> getUsers(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest)
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

    private void sendEmailActive (String email, String activeCode) {
        String subject = "Confirm customer account at Timevo Webiste";
        String text = "<!DOCTYPE html>" +
                "<html><body>" +
                "Please use the following code to activate your account <" + email + ">:<br/><br/>" +
                "<h1>" + activeCode + "</h1>" +
                "<br/> Click on the link to activate your account: <br/>" +
                "<a href=\"http://localhost:3000/active-account/" + email + "/" + activeCode + "\">" +
                "http://localhost:3000/active-account/" + email + "/" + activeCode + "</a>" +
                "</body></html>";
        emailService.sendMessages("timevo.service@gmail.com", email, subject, text);
    }
}
