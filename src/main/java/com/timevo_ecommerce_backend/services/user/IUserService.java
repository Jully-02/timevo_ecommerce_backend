package com.timevo_ecommerce_backend.services.user;

import com.timevo_ecommerce_backend.dtos.UserDTO;
import com.timevo_ecommerce_backend.dtos.UserUpdateDTO;
import com.timevo_ecommerce_backend.entities.User;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    UserResponse insertUser (UserDTO userDTO) throws Exception;

    String login (String email, String password) throws Exception;

    boolean emailUnique (String email);

    int activeAccount (String email, String activeCode) throws DataNotFoundException;

    User getUserDetailsFromToken (String token) throws Exception;

    Page<UserResponse> getUsers (String keyword, Pageable pageable);

    UserResponse updateUser (Long userId, UserUpdateDTO userDTO) throws Exception;

    User getUserDetailsFromRefreshToken (String refreshToken) throws  Exception;

    void resetPassword (Long userId, String newPassword) throws DataNotFoundException;

    void blockOrEnable (Long userId, Boolean active) throws DataNotFoundException;
}
