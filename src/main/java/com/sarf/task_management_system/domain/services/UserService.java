package com.sarf.task_management_system.domain.services;

import com.sarf.task_management_system.domain.enums.Role;
import com.sarf.task_management_system.domain.exceptions.ResourceNotFoundException;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.web.dto.requsts.RegisterRequest;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.security.JwtTokenProvider;
import com.sarf.task_management_system.repositories.ApplicationUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Сервис для управления пользователями в системе.
 * <p>
 * Этот сервис предоставляет методы для получения, сохранения и удаления пользователей, а также для проверки прав доступа.
 * Все операции взаимодействуют с репозиторием {@link ApplicationUserRepository} и обеспечивают бизнес-логику для работы с пользователями.
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final ApplicationUserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Получает список всех пользователей в системе.
     *
     * @return список объектов {@link ApplicationUser}, представляющих всех пользователей.
     */
    public List<ApplicationUser> getAll () {
        log.debug("Fetching all users");
        List<ApplicationUser> users = userRepository.findAll();
        log.debug("Fetched {} users", users.size());
        return users;
    }

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return объект {@link ApplicationUser}, представляющий найденного пользователя.
     * @throws NoSuchElementException если пользователь с указанным идентификатором не найден.
     */
    public ApplicationUser getById(long id) throws NoSuchElementException {
        log.debug("Fetching user by id: {}", id);
        ApplicationUser user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No user found with id: {}", id);
                    return new NoSuchElementException("No value present");
                });
        log.debug("Fetched user: {}", user);
        return user;
    }

    /**
     * Получает пользователя по его электронной почте.
     *
     * @param email электронная почта пользователя.
     * @return объект {@link ApplicationUser}, представляющий найденного пользователя.
     * @throws NoSuchElementException если пользователь с указанной электронной почтой не найден.
     */
    public ApplicationUser getByEmail(final String email) throws NoSuchElementException {
        log.debug("Fetching user by email: {}", email);
        ApplicationUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("No user found with email: {}", email);
                    return new NoSuchElementException("No value present");
                });
        log.debug("Fetched user: {}", user);
        return user;
    }

    /**
     * Получает пользователя по предоставленному токену доступа.
     *
     * @param accessToken токен доступа пользователя.
     * @return объект {@link ApplicationUser}, представляющий найденного пользователя.
     */
    public ApplicationUser getByToken(final String accessToken) {
        log.debug("Getting user by token");
        String email = tokenProvider.getEmail(accessToken);
        log.debug("Extracted email: {} from token", email);
        ApplicationUser user = this.getByEmail(email);
        log.debug("Fetched user by token: {}", user);
        return user;
    }

    /**
     * Сохраняет нового пользователя на основе предоставленных данных регистрации.
     *
     * @param registerRequest объект, содержащий данные для регистрации нового пользователя.
     */
    public void save(RegisterRequest registerRequest) {
        log.debug("Saving new user with email: {}", registerRequest.getEmail());
        ApplicationUser user = new ApplicationUser();

        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setRoles(registerRequest.getRoles());
        user.setTasksAssignedToUser(new ArrayList<>());
        user.setTasksCreatedByUser(new ArrayList<>());
        user.setHashPassword(
                passwordEncoder.encode(
                        registerRequest.getPassword()
                )
        );

        userRepository.save(user);
        log.info("User saved with email: {}", registerRequest.getEmail());
    }

    /**
     * Удаляет указанного пользователя.
     *
     * @param user объект {@link ApplicationUser}, представляющий пользователя, которого необходимо удалить.
     */
    public void delete(final ApplicationUser user) {
        log.debug("Deleting user: {}", user);
        userRepository.delete(user);
        log.info("User deleted: {}", user);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя, которого необходимо удалить.
     * @throws ResourceNotFoundException если пользователь с указанным идентификатором не найден.
     */
    public void delete(Long id) {
        log.debug("Deleting user by id: {}", id);
        ApplicationUser user = userRepository.findById(id)
                .orElseThrow(
                        () -> {
                            log.warn("No user found with id: {}", id);
                            return new ResourceNotFoundException("No user with %s".formatted(id));
                        }
                );
        log.debug("User found for deletion: {}", user);
        delete(user);
    }

    /**
     * Проверяет, имеет ли указанный пользователь права доступа к указанной задаче.
     *
     * @param user объект {@link ApplicationUser}, для которого необходимо проверить права доступа.
     * @param task объект {@link Task}, к которому проверяются права доступа.
     * @return {@code true}, если пользователь имеет права доступа к задаче; {@code false} в противном случае.
     */
    public static boolean hasAuthority(final ApplicationUser user, final Task task) {
        log.debug("Checking authority for user: {} on task: {}", user, task);
        boolean authority = user.getRoles().contains(Role.ROLE_ADMIN) ||
                user.getTasksAssignedToUser().contains(task) ||
                user.getTasksCreatedByUser().contains(task);
        log.debug("Authority check result: {}", authority);
        return authority;
    }

    /**
     * Проверяет, имеет ли пользователь, идентифицированный по токену доступа, права доступа к указанной задаче.
     *
     * @param accessToken токен доступа пользователя.
     * @param task объект {@link Task}, к которому проверяются права доступа.
     * @return {@code true}, если пользователь имеет права доступа к задаче; {@code false} в противном случае.
     */
    public boolean hasAuthority(final String accessToken, final Task task) {
        log.debug("Checking authority using access token for task: {}", task);
        ApplicationUser user = getByToken(accessToken);
        log.debug("User fetched for authority check: {}", user);
        boolean authority = user.getRoles().contains(Role.ROLE_ADMIN) ||
                user.getTasksAssignedToUser().contains(task) ||
                user.getTasksCreatedByUser().contains(task);
        log.debug("Authority check result: {}", authority);
        return authority;
    }
}
