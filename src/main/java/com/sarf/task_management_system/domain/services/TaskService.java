package com.sarf.task_management_system.domain.services;

import com.sarf.task_management_system.domain.dto.requsts.TaskRequest;
import com.sarf.task_management_system.domain.dto.response.CommentResponse;
import com.sarf.task_management_system.domain.models.Comment;
import com.sarf.task_management_system.domain.factories.ResponseFactory;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.domain.security.JwtTokenProvider;
import com.sarf.task_management_system.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ResponseFactory responseFactory;
    private final JwtTokenProvider tokenProvider;

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

    public void addComment(Long id, String authorToken, String content) throws AccessDeniedException {
        Task task = getById(id);

        String authorEmail = tokenProvider.getEmail(authorToken);

        if (task.getAuthor().getEmail().equals(authorEmail) == false ||
                task.getAssignee().getEmail().equals(authorEmail) == false)
            throw new AccessDeniedException("Only creator or employer can comment this task");

        Comment comment = responseFactory.create(
                authorToken,
                task,
                content
        );

        task.getComments().add(comment);
    }

    public List<Comment> getAllComments(Long id) {
        return this.getById(id).getComments();
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
