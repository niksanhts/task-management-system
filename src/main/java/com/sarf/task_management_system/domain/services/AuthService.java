package com.sarf.task_management_system.domain.services;

import com.sarf.task_management_system.domain.factories.ResponseFactory;
import com.sarf.task_management_system.web.dto.response.JwtResponse;
import com.sarf.task_management_system.web.dto.requsts.LoginRequest;
import com.sarf.task_management_system.web.dto.requsts.RegisterRequest;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.security.JwtTokenProvider;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Сервисный класс для управления аутентификацией пользователей.
 * Этот класс предоставляет методы для регистрации и входа пользователей в систему,
 * а также для создания JWT-токенов для аутентифицированных пользователей.
 *
 * <p>
 * Класс аннотирован {@link Service}, чтобы указать, что он является компонентом сервиса в контексте Spring.
 * Он использует Lombok's {@link Slf4j} для логирования и {@link RequiredArgsConstructor} для инъекции зависимостей через конструктор.
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Регистрирует нового пользователя.
     *
     * @param registerRequest объект, содержащий данные для регистрации пользователя.
     * @return объект JwtResponse, содержащий JWT-токен для зарегистрированного пользователя.
     * @throws NullPointerException если registerRequest равен null.
     */
    public JwtResponse register(@Valid final RegisterRequest registerRequest) {
        log.debug("Received register request: {}", registerRequest);
        if(registerRequest == null)
            throw new NullPointerException();

        log.debug("Saving user with email: {}", registerRequest.getEmail());
        userService.save(registerRequest);
        log.info("User registered successfully with email: {}", registerRequest.getEmail());

        log.debug("Creating JWT response for user: {}", registerRequest.getEmail());
        return ResponseFactory.createJWTResponse(
                userService.getByEmail(
                        registerRequest.getEmail()
                ),
                jwtTokenProvider
        );
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param loginRequest объект, содержащий данные для входа пользователя.
     * @return объект JwtResponse, содержащий JWT-токен для аутентифицированного пользователя.
     * @throws NullPointerException если loginRequest равен null.
     */
    public JwtResponse login(@Valid final LoginRequest loginRequest) {
        log.debug("Received login request: {}", loginRequest);
        if(loginRequest == null)
            throw new NullPointerException();

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        log.debug("Fetching user with email: {}", email);
        ApplicationUser user = userService.getByEmail(email);
        log.info("User fetched for login: {}", email);

        log.debug("Authenticating user with email: {}", email);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        log.info("User authenticated successfully: {}", email);

        log.debug("Creating JWT response for user: {}", email);
        return ResponseFactory.createJWTResponse(user, jwtTokenProvider);
    }
}
