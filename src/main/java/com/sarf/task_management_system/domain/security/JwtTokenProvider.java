package com.sarf.task_management_system.domain.security;

import com.sarf.task_management_system.web.dto.response.JwtResponse;
import com.sarf.task_management_system.domain.enums.Role;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Провайдер для работы с JWT (JSON Web Tokens).
 * Этот класс предоставляет методы для создания, проверки и извлечения информации из JWT-токенов,
 * а также для управления токенами доступа и обновления.
 *
 * <p>
 * Класс аннотирован {@link Service}, чтобы указать, что он является компонентом сервиса в контексте Spring.
 * Он использует Lombok's {@link Slf4j} для логирования и {@link RequiredArgsConstructor} для инъекции зависимостей через конструктор.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;

    private Key key;

    /**
     * Инициализирует провайдер JWT, создавая ключ для подписи токенов.
     */
    @PostConstruct
    public void init() {
        log.debug("Initializing JwtTokenProvider");
        key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        log.info("JwtTokenProvider initialized");
    }

    /**
     * Создает токен доступа для пользователя.
     *
     * @param userId идентификатор пользователя.
     * @param username имя пользователя (email).
     * @param roles список ролей пользователя.
     * @return созданный токен доступа.
     */
    public String createAccessToken(final Long userId, final String username, final List<Role> roles) {
        log.debug("Entering createAccessToken with userId={}, username={}, roles={}", userId, username, roles);
        if(key == null) {
            log.warn("JwtTokenProvider hasn't been initialized");
            init();
        }

        Claims claims = Jwts.claims()
                .subject(username)
                .add("id", userId)
                .add("roles", resolveRoles(roles))
                .build();
        Instant validity = Instant.now()
                .plus(jwtProperties.getAccess(), ChronoUnit.HOURS);

        log.info("Access token for {} (id: {}) created", username, userId);
        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    /**
     * Создает токен обновления для пользователя.
     *
     * @param userId идентификатор пользователя.
     * @param username имя пользователя (email).
     * @return созданный токен обновления.
     */
    public String createRefreshToken(final Long userId, final String username) {
        log.debug("Entering createRefreshToken with userId={}, username={}", userId, username);
        if(key == null) init();

        Claims claims = Jwts.claims()
                .subject(username)
                .add("id", userId)
                .build();
        Instant validity = Instant.now()
                .plus(jwtProperties.getRefresh(), ChronoUnit.DAYS);

        log.info("Refresh token for {} (id: {}) created", username, userId);
        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    /**
     * Обновляет токены пользователя, используя токен обновления.
     *
     * @param refreshToken токен обновления.
     * @param user объект пользователя.
     * @return объект JwtResponse, содержащий новые токены.
     * @throws IllegalArgumentException если токен обновления недействителен или не связан с пользователем.
     */
    public JwtResponse refreshUserTokens(final String refreshToken, final ApplicationUser user) {
        log.debug("Entering refreshUserTokens for user: {}", user.getEmail());
        JwtResponse jwtResponse = new JwtResponse();

        if (!isValid(refreshToken)) {
            log.warn("Refresh token isn't valid");
            throw new IllegalArgumentException();
        }

        Long userId = Long.valueOf(getId(refreshToken));

        if (userId != user.getId()) {
            log.warn("Refresh token isn't associate with user");
            throw new IllegalArgumentException();
        }

        jwtResponse.setId(userId);
        jwtResponse.setEmail(user.getEmail());

        jwtResponse.setAccessToken(
                createAccessToken(userId, user.getEmail(), user.getRoles())
        );
        jwtResponse.setRefreshToken(
                createRefreshToken(userId, user.getEmail())
        );

        log.info("JwtResponse for {} (id: {}) created", user.getEmail(), userId);
        return jwtResponse;
    }

    /**
     * Проверяет, является ли токен действительным.
     *
     * @param token токен для проверки.
     * @return true, если токен действителен; false в противном случае.
     */
    public boolean isValid(final String token) {
        log.debug("Validating token");
        if(key == null) {
            init();
        }

        Jws<Claims> claims = Jwts
                .parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token);

        return claims.getPayload().getExpiration()
                .after(new Date());
    }


    /**
     * Извлекает идентификатор пользователя из токена.
     *
     * @param token токен, из которого необходимо извлечь идентификатор.
     * @return идентификатор пользователя.
     */
    public String getId(final String token) {
        log.debug("Extracting id from token");
        if(key == null)
            init();

        return Jwts
                .parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", String.class);
    }

    /**
     * Извлекает адрес электронной почты пользователя из токена.
     *
     * @param token токен, из которого необходимо извлечь адрес электронной почты.
     * @return адрес электронной почты пользователя.
     */
    public String getEmail(final String token) {
        log.debug("Extracting email from token");
        if(key == null) init();

        return Jwts
                .parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Получает объект аутентификации для указанного токена.
     *
     * @param token токен, для которого необходимо получить аутентификацию.
     * @return объект Authentication, содержащий информацию о пользователе.
     */
    public Authentication getAuthentication(final String token) {
        log.debug("Getting authentication for token");
        String username = getEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        log.debug("Loaded user details for: {}", username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
        log.info("Authentication created for user: {}", username);
        return authentication;
    }

    /**
     * Преобразует список ролей в список строк.
     *
     * @param roles список ролей.
     * @return список строк, представляющих роли.
     */
    private List<String> resolveRoles(final List<Role> roles) {
        log.debug("Resolving roles for roles={}", roles);
        return roles.stream()
                .map(Enum::name)
                .toList();
    }
}
