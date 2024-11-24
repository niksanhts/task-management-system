package com.sarf.task_management_system.domain.services;

import com.sarf.task_management_system.domain.dto.JwtResponse;
import com.sarf.task_management_system.domain.dto.LoginRequest;
import com.sarf.task_management_system.domain.dto.RegisterRequest;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtResponse register(RegisterRequest registerRequest) {

        userService.save(registerRequest);

        return createResponse(
                userService.getByEmail(
                        registerRequest.getEmail()
                )
        );
    }

    public JwtResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        ApplicationUser user = userService.getByEmail(email);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        return createResponse(user);

    }

    private JwtResponse createResponse(ApplicationUser user) {
        Long id = user.getId();
        String email = user.getEmail();
        String accessToken = jwtTokenProvider.createAccessToken(id, email, user.getRoles());
        String refreshToken = jwtTokenProvider.createRefreshToken(id, email);

        return new JwtResponse(
                id,
                email,
                accessToken,
                refreshToken
        );
    }
}
