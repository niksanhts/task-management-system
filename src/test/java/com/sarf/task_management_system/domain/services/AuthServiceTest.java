package com.sarf.task_management_system.domain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sarf.task_management_system.domain.enums.Role;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.security.JwtTokenProvider;
import com.sarf.task_management_system.web.dto.requsts.LoginRequest;
import com.sarf.task_management_system.web.dto.requsts.RegisterRequest;
import com.sarf.task_management_system.web.dto.response.JwtResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private UserService userService;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@InjectMocks
	private AuthService authService;

	private String accessToken = "accessToken";
	private String refreshToken = "refreshToken";
	private String email = "test@example.com";
	private String password = "password";
	private String name = "Test Name";
	private List<Role> roles;

	private RegisterRequest registerRequest;
	private LoginRequest loginRequest;
	private ApplicationUser user;

	@BeforeEach
	void setUp() {
		roles = List.of(Role.ROLE_USER);

		registerRequest = new RegisterRequest(
				email,
				name,
				password,
				roles
		);

		loginRequest = new LoginRequest(
				email,
				password
		);

		user = new ApplicationUser();
		user.setEmail("test@example.com");
		user.setName("Test User");
	}

	@Test
	void testRegister() {

		when(userService.getByEmail("test@example.com")).thenReturn(user);
		when(jwtTokenProvider.createAccessToken(0L, email, null)).thenReturn(accessToken);
		when(jwtTokenProvider.createRefreshToken(0L, email)).thenReturn(refreshToken);

		JwtResponse result = authService.register(registerRequest);

		assertNotNull(result);
		assertEquals(accessToken, result.getAccessToken());
		assertEquals(refreshToken, result.getRefreshToken());

		verify(userService, times(1)).save(registerRequest);
	}

	@Test
	void testRegisterThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> authService.register(null));
	}

	@Test
	void testLogin() {
		when(userService.getByEmail("test@example.com")).thenReturn(user);
		when(jwtTokenProvider.createAccessToken(0L, email, null)).thenReturn(accessToken);
		when(jwtTokenProvider.createRefreshToken(0L, email)).thenReturn(refreshToken);

		JwtResponse result = authService.login(loginRequest);

		assertNotNull(result);
		assertEquals(accessToken, result.getAccessToken());
		assertEquals(refreshToken, result.getRefreshToken());

		verify(authenticationManager, times(1))
				.authenticate(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	void testLoginThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> authService.login(null));
	}
}