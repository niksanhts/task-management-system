package com.sarf.task_management_system.domain.services;

import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.factories.ApplicationUserDetailsFactory;
import com.sarf.task_management_system.repositories.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Сервисный класс для загрузки деталей пользователя по его имени пользователя (email).
 * Этот класс реализует интерфейс {@link UserDetailsService} и предоставляет метод
 * для получения информации о пользователе из репозитория.
 *
 * <p>
 * Класс аннотирован {@link Service}, чтобы указать, что он является компонентом сервиса в контексте Spring.
 * Он использует Lombok's {@link Slf4j} для логирования и {@link RequiredArgsConstructor} для инъекции зависимостей через конструктор.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository userRepository;

    /**
     * Загружает детали пользователя по его имени пользователя (email).
     *
     * @param email адрес электронной почты пользователя.
     * @return объект UserDetails, содержащий информацию о пользователе.
     * @throws UsernameNotFoundException если пользователь с указанным email не найден.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Entering loadUserByUsername with email: {}", email);
        ApplicationUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.debug("User found for email: {}", email);
        log.info("User loaded by username");
        return ApplicationUserDetailsFactory.create(user);
    }
}
