package com.sarf.task_management_system.web.dto.requsts;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    private String email;
    private String password;
}
