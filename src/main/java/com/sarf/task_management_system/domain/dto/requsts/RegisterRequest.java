package com.sarf.task_management_system.domain.dto.requsts;

import com.sarf.task_management_system.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class RegisterRequest {

    private String email;
    private String name;
    private String password;
    private Set<Role> roles;
}
