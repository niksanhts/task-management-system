package com.sarf.task_management_system.web.controllers;

import com.sarf.task_management_system.web.dto.response.JwtResponse;
import com.sarf.task_management_system.web.dto.requsts.LoginRequest;
import com.sarf.task_management_system.web.dto.requsts.RegisterRequest;
import com.sarf.task_management_system.domain.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для обработки запросов на регистрацию и аутентификацию пользователей.
 * <p>
 * Этот контроллер предоставляет два метода: {@code register} для регистрации нового пользователя и {@code login}
 * для входа с использованием учетных данных. Оба метода взаимодействуют с сервисом {@link AuthService}
 * и возвращают JWT-токены в случае успешного выполнения операций.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = """ 
					Обрабатывает запросы на регистрацию и аутентификацию пользователей.
					Он предоставляет два метода: register для регистрации нового пользователя и login для входа с использованием учетных данных.
					Оба метода взаимодействуют с сервисом AuthService и возвращают JWT-токены в случае успешного выполнения операций.
					""")
public class AuthController {

	private final AuthService authService;

	/**
	 * Контроллер для обработки запросов на регистрацию и аутентификацию пользователей.
	 * <p>
	 * Этот контроллер предоставляет два метода: {@code register} для регистрации нового пользователя и {@code login}
	 * для входа с использованием учетных данных. Оба метода взаимодействуют с сервисом {@link AuthService}
	 * и возвращают JWT-токены в случае успешного выполнения операций.
	 * </p>
	 */
	@PostMapping("/register")
	@Operation(description = "Registers a new user based on the provided registration data.")
	public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
		log.trace("Register request");
		log.debug("Processing registration for user with email: {}", registerRequest.getEmail());
		ResponseEntity<JwtResponse> response = ResponseEntity.ok(
				authService.register(
						registerRequest
				)
		);
		log.info("Registration successful for user with email: {}", registerRequest.getEmail());
		return response;
	}

	/**
	 * Аутентифицирует пользователя на основе предоставленных учетных данных.
	 *
	 * @param loginRequest объект, содержащий учетные данные для входа пользователя.
	 * @return ResponseEntity с JWT-токеном, если аутентификация прошла успешно.
	 */
	@PostMapping("/login")
	@Operation(description = "Authenticates a user based on the provided login credentials.")
	public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
		log.trace("Login request");
		log.debug("Processing login for user with email: {}", loginRequest.getEmail());
		ResponseEntity<JwtResponse> response = ResponseEntity.ok(
				authService.login(
						loginRequest)
		);
		log.info("Login successful for user with email: {}", loginRequest.getEmail());
		return response;
	}
}
