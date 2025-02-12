package com.sarf.task_management_system.web.dto.requsts;

import com.sarf.task_management_system.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String email;
    private String name;
    private String password;
    private List<Role> roles;
}
