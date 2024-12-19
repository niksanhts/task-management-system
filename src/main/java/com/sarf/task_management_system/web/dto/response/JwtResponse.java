package com.sarf.task_management_system.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JwtResponse {

    private Long id;
    private String email;
    private String accessToken;
    private String refreshToken;
}
