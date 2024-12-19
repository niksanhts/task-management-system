package com.sarf.task_management_system.web.controllers;

import com.sarf.task_management_system.web.dto.response.UserResponse;
import com.sarf.task_management_system.domain.factories.ResponseFactory;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Arrays.stream;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = """
		A controller for managing user-related operations in the system.
		It provides endpoints for retrieving user information, including a list of all
		users and details about the currently authenticated user.
		""")
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(description = "Retrieves a list of all users in the system.")
    public ResponseEntity<List<UserResponse>> all() {
        List<ApplicationUser> users = userService.getAll();

        List<UserResponse> response = users.stream()
                .map(ResponseFactory::createUser)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(description = " Retrieves the details of the currently authenticated" +
            " user based on the provided access token.")
    public ResponseEntity<UserResponse> me(
            @RequestHeader(name = "Authorization") String accessToken
    ) {
        try {
            return ResponseEntity.ok(
                    ResponseFactory.createUser(
                            userService.getByToken(accessToken)
                    )
            );
        }
        catch (Exception exception) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }


    }
}
