package com.sarf.task_management_system.web.controllers;

import com.sarf.task_management_system.domain.dto.response.JwtResponse;
import com.sarf.task_management_system.domain.dto.requsts.LoginRequest;
import com.sarf.task_management_system.domain.dto.requsts.RegisterRequest;
import com.sarf.task_management_system.domain.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller",
        description = "A controller for handling authentication-related " +
                "operations in the system. It provides endpoints for user" +
                " registration and login, allowing users to create accounts" +
                " and authenticate themselves.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(description = "Registers a new user based on the provided registration data.")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(
                authService.register(
                        registerRequest
                )
        );
    }

    @PostMapping("/login")
    @Operation(description = "Authenticates a user based on the provided login credentials.")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(
                authService.login(
                        loginRequest)
        );
    }
}
