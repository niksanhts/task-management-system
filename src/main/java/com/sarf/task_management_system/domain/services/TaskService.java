package com.sarf.task_management_system.domain.services;

import com.sarf.task_management_system.domain.dto.TaskRequest;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task with ID %d not found".formatted(id)));
    }

    public List<Task> getByAuthor(Long authorId) {
        return taskRepository.findByAuthor_Id(authorId);
    }

    public List<Task> getByAssignee(Long assigneeId) {
        return taskRepository.findByAssignee_Id(assigneeId);
    }

    public void save(TaskRequest taskRequest) {
        taskRepository.save(convertRequestToTask(taskRequest));
    }

    public void delete(Long id) {
        Task task = getById(id);
        taskRepository.delete(task);
    }

    private Task convertRequestToTask(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());

        task.setAuthor(
                userService.getByEmail(
                        request.getAuthorEmail()
                )
        );
        task.setAssignee(
                userService.getByEmail(
                        request.getAssigneeEmail()
                )
        );

        return task;
    }

}
