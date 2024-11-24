package com.sarf.task_management_system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private Long id;
    private String username;
    private String accessToken;
    private String refreshToken;
}
