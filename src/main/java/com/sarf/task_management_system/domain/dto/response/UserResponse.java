package com.sarf.task_management_system.domain.dto.response;

import com.sarf.task_management_system.domain.enums.Role;
import com.sarf.task_management_system.domain.models.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private Set<Role> roles;
}
