package com.sarf.task_management_system.domain.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sarf.task_management_system.domain.dto.requsts.TaskRequest;
import com.sarf.task_management_system.domain.enums.Priority;
import com.sarf.task_management_system.domain.enums.Status;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.domain.services.TaskService;
import com.sarf.task_management_system.domain.services.UserService;
import com.sarf.task_management_system.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequest taskRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setPriority(Priority.HIGH);
        task.setStatus(Status.TODO);

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Test Description");
        taskRequest.setPriority(Priority.HIGH);
        taskRequest.setStatus(Status.TODO);
        taskRequest.setAuthorEmail("author@example.com");
        taskRequest.setAssigneeEmail("assignee@example.com");
    }

    @Test
    public void testGetAll() {
        when(taskRepository.findAll()).thenReturn(Collections.singletonList(task));

        List<Task> tasks = taskService.getAll();

        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    public void testGetById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task foundTask = taskService.getById(1L);

        assertEquals(task, foundTask);
    }

    @Test
    public void testGetByIdNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            taskService.getById(1L);
        });

        assertEquals("Task with ID 1 not found", exception.getMessage());
    }

    @Test
    public void testGetByAuthor() {
        when(taskRepository.findByAuthor_Id(1L)).thenReturn(Collections.singletonList(task));

        List<Task> tasks = taskService.getByAuthor(1L);

        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    public void testGetByAssignee() {
        when(taskRepository.findByAssignee_Id(1L)).thenReturn(Collections.singletonList(task));

        List<Task> tasks = taskService.getByAssignee(1L);

        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    public void testSave() {
        when(userService.getByEmail("author@example.com")).thenReturn(new ApplicationUser());
        when(userService.getByEmail("assignee@example.com")).thenReturn(new ApplicationUser());

        taskService.save(taskRequest);

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    public void testDelete() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.delete(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    public void testDeleteNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            taskService.delete(1L);
        });

        assertEquals("Task with ID 1 not found", exception.getMessage());
    }
}
