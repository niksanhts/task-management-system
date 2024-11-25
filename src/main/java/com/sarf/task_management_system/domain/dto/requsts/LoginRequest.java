package com.sarf.task_management_system.domain.dto.requsts;

import lombok.*;

@Data
@AllArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}
