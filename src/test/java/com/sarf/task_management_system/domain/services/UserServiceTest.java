package com.sarf.task_management_system.domain.services;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sarf.task_management_system.domain.dto.RegisterRequest;
import com.sarf.task_management_system.domain.enums.Role;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.repositories.ApplicationUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

public class UserServiceTest {

    @Mock
    private ApplicationUserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private ApplicationUser user;
    private RegisterRequest registerRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new ApplicationUser(
                1L,
                "test@example.com",
                "name",
                "password",
                (Set<Role>) new HashSet<Role>(),
                (Set<Task>) new HashSet<Task>(),
                (Set<Task>) new HashSet<Task>()
        );

        registerRequest = new RegisterRequest(
                "test@example.com",
                "name",
                "password",
                (Set<Role>) new HashSet<Role>()
        );
    }

    @Test
    public void testGetAll() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<ApplicationUser> users = userService.getAll();

        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test
    public void testGetById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ApplicationUser foundUser = userService.getById(1L);

        assertEquals(user, foundUser);
    }

    @Test
    public void testGetByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userService.getById(1L);
        });

        assertEquals("No value present", exception.getMessage());
    }

    @Test
    public void testGetByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        ApplicationUser foundUser = userService.getByEmail("test@example.com");

        assertEquals(user, foundUser);
    }

    @Test
    public void testGetByEmailNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userService.getByEmail("test@example.com");
        });

        assertEquals("No value present", exception.getMessage());
    }

    @Test
    public void testSave() {
        userService.save(registerRequest);

        verify(userRepository).save(any(ApplicationUser.class));
    }

    @Test
    public void testDeleteByUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(user);

        verify(userRepository).delete(user);
    }

    @Test
    public void testDeleteById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).delete(user);
    }

    @Test
    public void testDeleteByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userService.delete(1L);
        });

        assertEquals("No value present", exception.getMessage());
    }
}
