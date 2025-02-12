package com.sarf.task_management_system.web.controllers;

import com.sarf.task_management_system.web.dto.response.UserResponse;
import com.sarf.task_management_system.domain.factories.ResponseFactory;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для управления пользователями в системе.
 * <p>
 * Этот контроллер предоставляет методы для получения списка всех пользователей, доступный только администраторам,
 * и для получения информации о текущем аутентифицированном пользователе на основе предоставленного токена доступа.
 * Оба метода взаимодействуют с сервисом {@link UserService} и возвращают соответствующие ответы в формате JSON.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = """
        Управляет пользователями в системе. Он предоставляет метод для
        получения списка всех пользователей, доступный только администраторам, и метод для получения информации о
        текущем аутентифицированном пользователе на основе предоставленного токена доступа. Оба метода взаимодействуют
        с сервисом UserService и возвращают соответствующие ответы в формате JSON.
        """)
public class UserController {

    private final UserService userService;

    /**
     * Получает список всех пользователей в системе.
     * <p>
     * Доступно только пользователям с ролью администратора.
     * </p>
     *
     * @return ResponseEntity с списком всех пользователей в формате JSON.
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(description = "Retrieves a list of all users in the system.")
    public ResponseEntity<List<UserResponse>> all() {
        log.info("Request to retrieve all users - method: all()");
        List<ApplicationUser> users = userService.getAll();
        List<UserResponse> response = users.stream()
                .map(ResponseFactory::createUser)
                .toList();
        log.info("Retrieved {} users", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Получает детали текущего аутентифицированного пользователя на основе предоставленного токена доступа.
     *
     * @param accessToken токен доступа, предоставленный в заголовке запроса.
     * @return ResponseEntity с информацией о текущем пользователе в формате JSON,
     *         или статус NO_CONTENT, если пользователь не найден.
     */
    @GetMapping("/me")
    @Operation(description = "Retrieves the details of the currently authenticated user based on the provided access token.")
    public ResponseEntity<UserResponse> me(
            @RequestHeader(name = "Authorization") String accessToken
    ) {
        log.info("Request to retrieve current user details - method: me()");
        try {
            ApplicationUser currentUser = userService.getByToken(accessToken);
            UserResponse userResponse = ResponseFactory.createUser(currentUser);
            log.info("Successfully retrieved details for user: {}", currentUser.getEmail());
            return ResponseEntity.ok(userResponse);
        }
        catch (Exception exception) {
            log.error("Failed to retrieve user details: {}", exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }
    }
}