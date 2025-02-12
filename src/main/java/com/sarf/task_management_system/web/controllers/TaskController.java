package com.sarf.task_management_system.web.controllers;

import com.sarf.task_management_system.domain.enums.Status;
import com.sarf.task_management_system.web.dto.requsts.TaskRequest;
import com.sarf.task_management_system.web.dto.response.CommentResponse;
import com.sarf.task_management_system.web.dto.response.TaskResponse;
import com.sarf.task_management_system.domain.factories.ResponseFactory;
import com.sarf.task_management_system.domain.models.Comment;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.domain.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;

/**
 * Контроллер для управления задачами в системе.
 * <p>
 * Этот контроллер предоставляет методы для создания новой задачи (доступный только администраторам),
 * получения списка всех задач, а также задач, созданных определенным автором или назначенных определенному исполнителю.
 * Контроллер также включает метод для удаления задачи с проверкой прав доступа,
 * взаимодействуя с сервисом {@link TaskService} для выполнения бизнес-логики и возвращая соответствующие ответы.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("api/v1/task")
@AllArgsConstructor
@Tag(name = "Task Controller", description = """
				Управляет задачами в системе. Он предоставляет методы для создания новой 
				задачи (доступный только администраторам), получения списка всех задач, а также задач, созданных определенным автором
				или назначенных определенному исполнителю. Контроллер также включает метод для удаления задачи с проверкой прав доступа,
				взаимодействуя с сервисом TaskService для выполнения бизнес-логики и возвращая соответствующие ответы.
				""")
public class TaskController {

    private final TaskService taskService;

    /**
     * Создает новую задачу на основе предоставленных данных.
     * <p>
     * Доступно только пользователям с ролью администратора.
     * </p>
     *
     * @param taskRequest объект, содержащий данные для создания задачи.
     * @return ResponseEntity с сообщением о результате создания задачи.
     */
    @PutMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(description = "Creates a new task based on the provided data.")
    public ResponseEntity<String> create(@RequestBody TaskRequest taskRequest) {
        log.trace("Task creation request with data: {}", taskRequest);
        try {
            taskService.save(taskRequest);
            log.info("Task successfully created");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Task successfully saved");
        }
        catch (Exception exception) {
            log.error("Task creation failed: {}", exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Task creation failed");
        }
    }

    /**
     * Обновляет статус задачи.
     *
     * @param id идентификатор задачи, которую необходимо обновить.
     * @param status новый статус задачи.
     * @param accessToken токен доступа, предоставленный в заголовке запроса.
     * @return ResponseEntity с сообщением о результате обновления задачи.
     */
    @PutMapping("/update/{id}")
    @Operation(description = "Updates status of the task.")
    public ResponseEntity<String> change(@PathVariable Long id,
                                         @RequestParam("status") Status status,
                                         @RequestHeader(name = "Authorization") String accessToken) {
        log.trace("Task update request for task ID {} with status {}", id, status);
        try {
            taskService.update(id, status, accessToken);
            log.info("Task {} successfully updated", id);
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Task successfully updated");
        }
        catch (Exception exception) {
            log.error("Task update failed for task ID {}: {}", id, exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .body("Task update failed");
        }
    }

    /**
     * Получает список всех задач в системе.
     *
     * @return ResponseEntity со списком всех задач в формате JSON.
     */
    @GetMapping("/all")
    @Operation(description = "Retrieves a list of all tasks in the system.")
    public ResponseEntity<List<TaskResponse>> getAll() {
        log.trace("Request to retrieve all tasks");
        List<Task> tasks = taskService.getAll();
        List<TaskResponse> response = tasks.stream()
                .map(ResponseFactory::createTask)
                .toList();
        log.info("Retrieved {} tasks", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Получает список задач для текущего пользователя на основе предоставленного токена доступа.
     *
     * @param accessToken токен доступа, предоставленный в заголовке запроса.
     * @return ResponseEntity со списком задач текущего пользователя в формате JSON.
     */
    @GetMapping("/my")
    public ResponseEntity<List<TaskResponse>> getMy(@RequestHeader(name = "Authorization") String accessToken) {
        log.trace("Request to retrieve tasks for user with access token: {}", accessToken);
        List<Task> tasks = taskService.getByToken(accessToken);
        List<TaskResponse> response = tasks.stream()
                .map(ResponseFactory::createTask)
                .toList();
        log.info("Retrieved {} tasks for user", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Получает список задач, созданных автором с указанным идентификатором.
     *
     * @param id идентификатор автора, чьи задачи необходимо получить.
     * @return ResponseEntity со списком задач, созданных указанным автором, в формате JSON.
     */
    @GetMapping("/author/{id}")
    @Operation(description = "Retrieves a list of tasks created by the author with the specified identifier.")
    public ResponseEntity<List<TaskResponse>> getByAuthor(@PathVariable Long id) {
        log.trace("Request to retrieve tasks by author ID {}", id);
        try {
            List<Task> tasks = taskService.getByAuthor(id);
            List<TaskResponse> response = tasks.stream()
                    .map(ResponseFactory::createTask)
                    .toList();
            log.info("Retrieved {} tasks for author ID {}", response.size(), id);
            return ResponseEntity.ok(response);
        }
        catch (Exception exception) {
            log.error("Failed to retrieve tasks for author ID {}: {}", id, exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ArrayList<>());
        }
    }

    /**
     * Получает список задач, назначенных исполнителю с указанным идентификатором.
     *
     * @param id идентификатор исполнителя, чьи задачи необходимо получить.
     * @return ResponseEntity со списком задач, назначенных указанному исполнителю, в формате JSON.
     */
    @GetMapping("/assignee/{id}")
    @Operation(description = "Retrieves a list of tasks assigned to the assignee with the specified identifier.")
    public ResponseEntity<List<TaskResponse>> getByAssignee(@PathVariable Long id) {
        log.trace("Request to retrieve tasks by assignee ID {}", id);
        try {
            List<Task> tasks = taskService.getByAssignee(id);
            List<TaskResponse> response = tasks.stream()
                    .map(ResponseFactory::createTask)
                    .toList();
            log.info("Retrieved {} tasks for assignee ID {}", response.size(), id);
            return ResponseEntity.ok(response);
        }
        catch (Exception exception) {
            log.error("Failed to retrieve tasks for assignee ID {}: {}", id, exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ArrayList<>());
        }
    }

    /**
     * Удаляет задачу с указанным идентификатором.
     * <p>
     * Доступно только пользователям с ролью администратора.
     * </p>
     *
     * @param id идентификатор задачи, которую необходимо удалить.
     * @return ResponseEntity с сообщением о результате удаления задачи.
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(description = "Deletes the task with the specified identifier.")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.trace("Delete task request for task ID {}", id);
        try {
            taskService.delete(id);
            log.info("Task {} successfully deleted", id);
            return ResponseEntity
                    .ok("Task successfully deleted");
        }
        catch (Exception exception) {
            log.error("Failed to delete task ID {}: {}", id, exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("No task or it cannot be deleted");
        }
    }
}
