package com.sarf.task_management_system.config;

import com.sarf.task_management_system.domain.security.JwtProperties;
import com.sarf.task_management_system.domain.security.JwtTokenProvider;
import com.sarf.task_management_system.domain.services.ApplicationUserDetailsService;
import com.sarf.task_management_system.domain.services.AuthService;
import com.sarf.task_management_system.domain.services.TaskService;
import com.sarf.task_management_system.domain.services.UserService;
import com.sarf.task_management_system.repositories.ApplicationUserRepository;
import com.sarf.task_management_system.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.module.Configuration;

@TestConfiguration
@RequiredArgsConstructor
public class TestConfig {

    @Bean
    @Primary
    public BCryptPasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtProperties jwtProperties() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret(
                "dfbdd573a0a14701f4395ea4c8f9ab5cfe41e729e05f919a1091651200e1b755"
        );
        return jwtProperties;
    }

    @Bean
    public JwtTokenProvider tokenProvider() {
        return new JwtTokenProvider(
                jwtProperties(),
                userDetailsService()
        );
    }

    @Bean
    public Configuration configuration() {
        return Mockito.mock(Configuration.class);
    }

    @Bean
    @Primary
    public UserService userService() {
        return new UserService(
                userRepository(),
                tokenProvider(),
                testPasswordEncoder()
        );
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new ApplicationUserDetailsService(userRepository());
    }

    @Bean
    @Primary
    public TaskService taskService() {
        return new TaskService(
                taskRepository(),
                userService()
        );
    }

    @Bean
    @Primary
    public AuthService authService() {
        return new AuthService(
                authenticationManager(),
                userService(),
                tokenProvider()
        );
    }

    @Bean
    public ApplicationUserRepository userRepository() {
        return Mockito.mock(ApplicationUserRepository.class);
    }

    @Bean
    public TaskRepository taskRepository() {
        return Mockito.mock(TaskRepository.class);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return Mockito.mock(AuthenticationManager.class);
    }
}
