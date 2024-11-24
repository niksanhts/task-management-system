package com.sarf.task_management_system.domain.security;

import com.sarf.task_management_system.domain.dto.JwtResponse;
import com.sarf.task_management_system.domain.enums.Role;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.properties.JwtProperties;
import com.sarf.task_management_system.domain.services.ApplicationUserDetailsService;
import com.sarf.task_management_system.domain.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final ApplicationUserDetailsService userDetailsService;
    private final UserService userService;

    private Key key;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @PostConstruct
    public void init() {
        try {
            key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
            logger.info("JWT key initialized successfully.");
        } catch (Exception e) {
            logger.error("Error initializing JWT key: ", e);
        }
    }

    public String createAccessToken(final Long userId, final String username, final Set<Role> roles) {
        Claims claims = Jwts.claims()
                .subject(username)
                .add("id", userId)
                .add("roles", resolveRoles(roles))
                .build();
        Instant validity = Instant.now()
                .plus(jwtProperties.getAccess(), ChronoUnit.HOURS);

        logger.info("Access token for user with id:%s created.".formatted(userId));
        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    private List<String> resolveRoles(final Set<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .toList();
    }

    public String createRefreshToken(final Long userId, final String username) {
        Claims claims = Jwts.claims()
                .subject(username)
                .add("id", userId)
                .build();
        Instant validity = Instant.now()
                .plus(jwtProperties.getRefresh(), ChronoUnit.DAYS);
        logger.info("Refresh token for user with id:%s created.".formatted(userId));
        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();
    }


    public JwtResponse refreshUserTokens(final String refreshToken) {
        JwtResponse jwtResponse = new JwtResponse();
        if (!isValid(refreshToken)) {
            //    throw new AccessDeniedException();
            throw new IllegalArgumentException();
        }
        Long userId = Long.valueOf(getId(refreshToken));
        ApplicationUser user = userService.getById(userId);
        jwtResponse.setId(userId);
        jwtResponse.setUsername(user.getEmail());
        jwtResponse.setAccessToken(
                createAccessToken(userId, user.getEmail(), user.getRoles())
        );

        jwtResponse.setRefreshToken(
                createRefreshToken(userId, user.getEmail())
        );
        logger.info("Refresh token for user with id:%s created.".formatted(userId));

        return jwtResponse;
    }


    public boolean isValid(final String token) {
            Jws<Claims> claims = Jwts
                    .parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);

            return claims.getPayload().getExpiration()
               .after(new Date());
        }

    private String getId(final String token) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", String.class);
    }

    private String getUsername(final String token) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Authentication getAuthentication(final String token) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(
                username
        );
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }
}
