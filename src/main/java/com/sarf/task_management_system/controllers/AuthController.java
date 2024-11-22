package com.sarf.task_management_system.controllers;

import com.sarf.task_management_system.models.ApplicationUser;
import com.sarf.task_management_system.services.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody ApplicationUser newUser) {
        try {
            authService.addUser(newUser);
            return ResponseEntity.ok("User successfully registered");
        } catch (Exception ex) {
            log.error("User registration failed", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User cannot be created.");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("all works fine");
    }
}
