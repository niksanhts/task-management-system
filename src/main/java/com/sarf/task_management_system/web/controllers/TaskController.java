package com.sarf.task_management_system.web.controllers;

import com.sarf.task_management_system.domain.dto.requsts.TaskRequest;
import com.sarf.task_management_system.domain.dto.response.CommentResponse;
import com.sarf.task_management_system.domain.dto.response.TaskResponse;
import com.sarf.task_management_system.domain.factories.ResponseFactory;
import com.sarf.task_management_system.domain.models.Comment;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.domain.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@RestController
@RequestMapping("api/v1/task")
@AllArgsConstructor
@Tag(name = "Task Controller", description = "A controller for managing tasks in the system." +
                " It provides endpoints for creating, retrieving, commenting on," +
                " and deleting tasks. Access to certain operations is restricted based on user roles.")
public class TaskController {

    @Autowired
    private final TaskService taskService;

    @PutMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(description = "Creates a new task based on the provided data.")
    public ResponseEntity<String> create(@RequestBody TaskRequest taskRequest) {
        try {
            taskService.save(taskRequest);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Task successfully saved");
        }
        catch (Exception exception) {
            log.error("Task creation failed." + exception);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Task creation failed");
        }
    }

    @GetMapping("/all")
    @Operation(description = "Retrieves a list of all tasks in the system.")
    public ResponseEntity<List<TaskResponse>> getAll() {
        List<Task> tasks = taskService.getAll();
        List<TaskResponse> response = tasks.stream()
                .map(ResponseFactory::createTask)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/author/{id}")
    @Operation(description = "Retrieves a list of tasks created by the author with the specified identifier.")
    public ResponseEntity<List<TaskResponse>> getByAuthor(@PathVariable Long id) {
        try {
            List<Task> tasks = taskService.getByAuthor(id);
            List<TaskResponse> response = tasks.stream()
                    .map(ResponseFactory::createTask)
                    .toList();
            return ResponseEntity.ok(response);
        }
        catch (Exception exception) {
            return  ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ArrayList<>());
        }
    }

    @GetMapping("/assignee/{id}")
    @Operation(description = "Retrieves a list of tasks assigned to the assignee with the specified identifier.")
    public ResponseEntity<List<TaskResponse>> getByAssignee(@PathVariable Long id) {
        try {
            List<Task> tasks = taskService.getByAssignee(id);
            List<TaskResponse> response = tasks.stream()
                    .map(ResponseFactory::createTask)
                    .toList();
            return ResponseEntity.ok(response);
        }
        catch (Exception exception) {
            return  ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ArrayList<>());
        }
    }

    @PutMapping("{id}/comments/add")
    @Operation(description = "Adds a comment to the task with the specified identifier.")
    public ResponseEntity<String> comment(
            @PathVariable Long id,
            @RequestHeader(name = "Authorization") String accessToken,
            @RequestBody String content
    ) {
        try{
            taskService.addComment(id, accessToken, content);
            return ResponseEntity
                    .ok("Comment successfully saved");
        }
        catch (Exception exception) {
            return  ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Comment creation failed");
        }
    }

    @GetMapping("{id}/comments")
    @Operation(description = "Retrieves a list of comments for the task with the specified identifier.")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long id) {

        try {
            List<Comment> comments = taskService.getAllComments(id);
            List<CommentResponse> response = comments.stream()
                    .map(ResponseFactory::createComment)
                    .toList();
            return ResponseEntity
                    .ok(response);
        }
        catch (Exception exception) {
            return  ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ArrayList<>());
        }

    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(description = "Deletes the task with the specified identifier.")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            taskService.delete(id);
            return ResponseEntity
                    .ok("Task successfully deleted");
        }
        catch (Exception exception) {
            return ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("No task or it can not be deleted");
        }
    }
}
