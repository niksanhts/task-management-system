package com.sarf.task_management_system.domain.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}
