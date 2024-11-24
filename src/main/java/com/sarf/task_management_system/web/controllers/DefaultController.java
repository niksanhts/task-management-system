package com.sarf.task_management_system.web.controllers;

import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/base")
@RequiredArgsConstructor
public class DefaultController {

    private final UserService userService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("all works fine");
    }

    @GetMapping("/welcome")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("welcome to task-management-system site!");
    }

    @GetMapping("/all-u")
    public ResponseEntity<List<ApplicationUser>> all() {
        return ResponseEntity.ok( userService.getAll());
    }
}
