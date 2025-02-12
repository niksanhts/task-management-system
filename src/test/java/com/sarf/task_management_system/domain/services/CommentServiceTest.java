package com.sarf.task_management_system.domain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import com.sarf.task_management_system.domain.exceptions.AccessDeniedException;
import com.sarf.task_management_system.domain.exceptions.ResourceNotFoundException;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.models.Comment;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.repositories.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

	@Mock
	private TaskService taskService;

	@Mock
	private UserService userService;

	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private CommentService commentService;

	private ApplicationUser author;
	private Task task;
	private Comment comment;

	@BeforeEach
	void setUp() {
		task = new Task();
		task.setId(1L);
		task.setTitle("Test Task");
		task.setDescription("Test Description");

		author = new ApplicationUser(
				1L,
				"author@example.com",
				"Test Author",
				"password",
				new ArrayList<>(),
				List.of(task),
				new ArrayList<>()
		);

		comment = new Comment(
				1L,
				author,
				task,
				"Test Comment"
		);
	}

	@Test
	void testGetByTask() {
		when(commentRepository.findByTaskId(1L)).thenReturn(List.of(comment));

		List<Comment> result = commentService.getByTask(1L);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(comment, result.get(0));
	}

	@Test
	void testGetByAuthor() {
		when(commentRepository.findByAuthorId(1L)).thenReturn(List.of(comment));

		List<Comment> result = commentService.getByAuthor(1L);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(comment, result.get(0));
	}

	@Test
	void testSave() throws AccessDeniedException {
		when(userService.getByToken("author@example.com")).thenReturn(author);
		when(taskService.getById(1L)).thenReturn(task);
		when(commentRepository.save(any(Comment.class))).thenReturn(comment);

		commentService.save("author@example.com", 1L, "Test Comment");

		verify(commentRepository, times(1)).save(any(Comment.class));
	}

	@Test
	void testSaveThrowsAccessDeniedException() {
		ApplicationUser testUser = new ApplicationUser(
				2L,
				"nonauthor@example.com",
				"Test User",
				"password",
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>()
		);

		when(userService.getByToken("nonauthor@example.com")).thenReturn(testUser);
		when(taskService.getById(1L)).thenReturn(task);

		assertThrows(AccessDeniedException.class, () ->
				commentService.save("nonauthor@example.com", 1L, "Test Comment"));
	}

	@Test
	void testDelete() {
		when(commentRepository.existsById(1L)).thenReturn(true);

		commentService.delete(1L);

		verify(commentRepository, times(1)).deleteById(1L);
	}

	@Test
	void testDeleteThrowsResourceNotFoundException() {
		when(commentRepository.existsById(1L)).thenReturn(false);

		assertThrows(ResourceNotFoundException.class, () -> commentService.delete(1L));
	}
}
