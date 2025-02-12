package com.sarf.task_management_system.domain.services;

import com.sarf.task_management_system.domain.enums.Role;
import com.sarf.task_management_system.domain.exceptions.ResourceNotFoundException;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.security.JwtTokenProvider;
import com.sarf.task_management_system.repositories.ApplicationUserRepository;
import com.sarf.task_management_system.web.dto.requsts.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private ApplicationUserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtTokenProvider tokenProvider;

	@InjectMocks
	private UserService userService;

	private ApplicationUser user;
	private RegisterRequest registerRequest;

	@BeforeEach
	void setUp() {
		user = new ApplicationUser();
		user.setId(1L);
		user.setEmail("test@example.com");
		user.setName("Test User");
		user.setHashPassword("encodedPassword");

		registerRequest = new RegisterRequest(
				"test@example.com",
				"Test User",
				"password",
				List.of(Role.ROLE_USER)
		);
	}

	@Test
	void testGetAll() {
		List<ApplicationUser> users = List.of(user);
		when(userRepository.findAll()).thenReturn(users);

		List<ApplicationUser> result = userService.getAll();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(user, result.get(0));
	}

	@Test
	void testGetById() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		ApplicationUser result = userService.getById(1L);

		assertNotNull(result);
		assertEquals(user, result);
	}

	@Test
	void testGetByIdThrowsNoSuchElementException() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> userService.getById(1L));
	}

	@Test
	void testGetByEmail() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		ApplicationUser result = userService.getByEmail("test@example.com");

		assertNotNull(result);
		assertEquals(user, result);
	}

	@Test
	void testGetByEmailThrowsNoSuchElementException() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> userService.getByEmail("test@example.com"));
	}

	@Test
	void testGetByToken() {
		when(tokenProvider.getEmail("token")).thenReturn("test@example.com");
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		ApplicationUser result = userService.getByToken("token");

		assertNotNull(result);
		assertEquals(user, result);
	}

	@Test
	void testSave() {
		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
		when(userRepository.save(Mockito.any(ApplicationUser.class))).thenReturn(user);

		userService.save(registerRequest);

		verify(userRepository, times(1)).save(any(ApplicationUser.class));
	}

	@Test
	void testDeleteByUser() {
		userService.delete(user);

		verify(userRepository, times(1)).delete(user);
	}

	@Test
	void testDeleteById() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		userService.delete(1L);

		verify(userRepository, times(1)).delete(user);
	}

	@Test
	void testDeleteByIdThrowsResourceNotFoundException() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.delete(1L));
	}
}