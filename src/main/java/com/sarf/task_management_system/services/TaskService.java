package com.sarf.task_management_system.services;

import com.github.javafaker.Faker;
import com.sarf.task_management_system.models.Task;
import com.sarf.task_management_system.reositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {

    @Autowired
    private final TaskRepository taskRepository;

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with ID %d not found".formatted(id)));
    }

    public List<Task> getByAuthor(Long authorId) {
        return taskRepository.findByAuthor_Id(authorId);
    }

    public List<Task> getByAssignee(Long assigneeId) {
        return taskRepository.findByAssignee_Id(assigneeId);
    }

    public void save(Task task) {
        taskRepository.save(task);
    }

    public void delete(Long id) {
        Task task = getById(id);
        taskRepository.delete(task);
    }

}
