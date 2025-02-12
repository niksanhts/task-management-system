package com.sarf.task_management_system.domain.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
@AllArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @SneakyThrows
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        log.debug("Starting JWT filter for request: {}", request.getRequestURI());

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7);
            log.debug("Bearer token extracted from Authorization header.");
        } else {
            log.debug("Authorization header is missing or does not start with 'Bearer '.");
        }

        try {
            if (bearerToken != null) {
                if (jwtTokenProvider.isValid(bearerToken)) {
                    log.debug("JWT token is valid.");
                    Authentication authentication = jwtTokenProvider.getAuthentication(bearerToken);
                    if (authentication != null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("User '{}' authenticated successfully.", authentication.getName());
                    } else {
                        log.debug("No authentication obtained from token.");
                    }
                } else {
                    log.warn("Invalid JWT token provided.");
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while validating JWT token.", e);
        }

        filterChain.doFilter(servletRequest, servletResponse);
        log.debug("Completed filter chain processing for request: {}", request.getRequestURI());
    }
}