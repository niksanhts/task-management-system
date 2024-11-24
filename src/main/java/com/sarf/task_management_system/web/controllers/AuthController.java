package com.sarf.task_management_system.web.controllers;

import com.sarf.task_management_system.domain.dto.JwtResponse;
import com.sarf.task_management_system.domain.dto.LoginRequest;
import com.sarf.task_management_system.domain.dto.RegisterRequest;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.services.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(
                authService.register(
                        registerRequest
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(
                authService.login(
                        loginRequest)
        );
    }


}
