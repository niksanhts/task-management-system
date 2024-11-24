package com.sarf.task_management_system.domain.dto;

import com.sarf.task_management_system.domain.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {

    private String email;
    private String name;
    private String password;
    private Set<Role> roles;
}
