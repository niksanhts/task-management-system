package com.sarf.task_management_system.domain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.sarf.task_management_system.domain.enums.Priority;
import com.sarf.task_management_system.domain.enums.Status;
import com.sarf.task_management_system.domain.exceptions.ResourceNotFoundException;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.services.TaskService;
import com.sarf.task_management_system.domain.services.UserService;
import com.sarf.task_management_system.repositories.TaskRepository;
import com.sarf.task_management_system.web.dto.requsts.TaskRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

	@Mock
	private TaskRepository taskRepository;

	@Mock
	private UserService userService;

	@InjectMocks
	private TaskService taskService;

	private Task task;
	private TaskRequest taskRequest;
	private ApplicationUser author;
	private ApplicationUser assignee;

	@BeforeEach
	void setUp() {
		author = new ApplicationUser();
		author.setEmail("author@example.com");

		assignee = new ApplicationUser();
		assignee.setEmail("assignee@example.com");

		task = new Task();
		task.setId(1L);
		task.setTitle("Test Task");
		task.setDescription("Test Description");
		task.setPriority(Priority.HIGH);
		task.setStatus(Status.TODO);
		task.setAuthor(author);
		task.setAssignee(assignee);

		taskRequest = new TaskRequest();
		taskRequest.setTitle("Test Task");
		taskRequest.setDescription("Test Description");
		taskRequest.setPriority(Priority.HIGH);
		taskRequest.setStatus(Status.TODO);
		taskRequest.setAuthorEmail("author@example.com");
		taskRequest.setAssigneeEmail("assignee@example.com");
	}

	@Test
	void testGetAll() {
		when(taskRepository.findAll()).thenReturn(List.of(task));

		List<Task> result = taskService.getAll();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(task, result.get(0));
	}

	@Test
	void testGetById() {
		when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

		Task result = taskService.getById(1L);

		assertNotNull(result);
		assertEquals(task, result);
	}

	@Test
	void testGetByIdThrowsResourceNotFoundException() {
		when(taskRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> taskService.getById(1L));
	}

	@Test
	void testGetByAuthor() {
		when(taskRepository.findByAuthor_Id(1L)).thenReturn(List.of(task));

		List<Task> result = taskService.getByAuthor(1L);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(task, result.get(0));
	}

	@Test
	void testGetByAssignee() {
		when(taskRepository.findByAssignee_Id(1L)).thenReturn(List.of(task));

		List<Task> result = taskService.getByAssignee(1L);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(task, result.get(0));
	}

	@Test
	void testSave() {
		when(userService.getByEmail("author@example.com")).thenReturn(author);
		when(userService.getByEmail("assignee@example.com")).thenReturn(assignee);
		when(taskRepository.save(any(Task.class))).thenReturn(task);

		taskService.save(taskRequest);

		verify(taskRepository, times(1)).save(any(Task.class));
	}

	@Test
	void testDelete() {
		when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

		taskService.delete(1L);

		verify(taskRepository, times(1)).delete(task);
	}

	@Test
	void testDeleteThrowsResourceNotFoundException() {
		when(taskRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> taskService.delete(1L));
	}
}