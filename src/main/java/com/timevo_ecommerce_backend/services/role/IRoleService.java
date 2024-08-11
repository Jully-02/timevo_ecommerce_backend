package com.timevo_ecommerce_backend.services.role;

import com.timevo_ecommerce_backend.dtos.RoleDTO;
import com.timevo_ecommerce_backend.entities.Role;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;

import java.util.List;

public interface IRoleService {
    Role insertRole (RoleDTO roleDTO);

    Role updateRole (Long id, RoleDTO roleDTO) throws DataNotFoundException;

    Role getRole (Long id) throws DataNotFoundException;

    List<Role> getAllRoles ();

    void deleteRole (Long id);
}
