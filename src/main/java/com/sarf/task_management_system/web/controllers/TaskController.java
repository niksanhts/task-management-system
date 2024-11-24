package com.sarf.task_management_system.web.controllers;

import com.sarf.task_management_system.domain.dto.TaskRequest;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.domain.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@RestController
@RequestMapping("api/v1/task")
@AllArgsConstructor
public class TaskController {

    @Autowired
    private final TaskService taskService;

    @PostMapping("/create")
    @PreAuthorize("ROLE_ADMIN")
    public ResponseEntity<String> create(@RequestBody TaskRequest taskRequest) {
        try {
            taskService.save(taskRequest);
            return ResponseEntity.ok("Task successfully saved");
        }
        catch (Exception exception) {
            log.error("Task creation failed." + exception);
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task creation failed");
        }
    }

    @GetMapping("/all")
    public List<Task> getAll() {
        return taskService.getAll();
    }

    @GetMapping("/author/{id}")
    public List<Task> getByAuthor(@PathVariable Long id) {
        return taskService.getByAuthor(id);
    }

    @GetMapping("/assignee/{id}")
    public List<Task> getByAssignee(@PathVariable Long id) {
        return taskService.getByAssignee(id);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("ROLE_ADMIN")
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
