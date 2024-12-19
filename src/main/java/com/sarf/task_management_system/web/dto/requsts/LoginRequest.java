package com.sarf.task_management_system.web.dto.requsts;

import lombok.*;

@Data
@AllArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}
