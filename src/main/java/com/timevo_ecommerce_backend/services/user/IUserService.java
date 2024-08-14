package com.timevo_ecommerce_backend.services.user;

import com.timevo_ecommerce_backend.dtos.UserDTO;
import com.timevo_ecommerce_backend.dtos.UserUpdateDTO;
import com.timevo_ecommerce_backend.entities.User;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IUserService {
    UserResponse insertUser (UserDTO userDTO) throws Exception;

    String login (String email, String password) throws Exception;

    boolean emailUnique (String email);

    int activeAccount (String email, String activeCode) throws DataNotFoundException;

    UserResponse getUserDetailsFromToken (String token) throws Exception;

    Page<UserResponse> getUsers (PageRequest pageRequest);

    UserResponse updateUser (Long userId, UserUpdateDTO userDTO) throws Exception;
}
