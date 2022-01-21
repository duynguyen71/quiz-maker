package com.nkd.quizmaker.mapper;

import com.nkd.quizmaker.dto.RoleDto;
import com.nkd.quizmaker.model.Role;

public class RoleMapper {

    public static RoleDto toRoleDTO(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        return dto;
    }

    public static Role toRole(RoleDto dto) {
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        return role;
    }
}
