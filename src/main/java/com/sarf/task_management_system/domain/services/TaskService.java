package com.sarf.task_management_system.domain.services;

import com.sarf.task_management_system.domain.enums.Status;
import com.sarf.task_management_system.domain.exceptions.ResourceNotFoundException;
import com.sarf.task_management_system.web.dto.requsts.TaskRequest;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервисный класс для управления задачами в приложении.
 * Этот класс предоставляет методы для выполнения операций CRUD над задачами,
 * а также методы для получения задач на основе различных критериев, таких как автор и исполнитель.
 * Он использует репозиторий для доступа к данным и сервис пользователя для операций, связанных с пользователями.
 *
 * <p>
 * Класс аннотирован {@link Service}, чтобы указать, что он является компонентом сервиса в контексте Spring.
 * Также используется {@link Slf4j} от Lombok для логирования и {@link RequiredArgsConstructor} для инъекции зависимостей через конструктор.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;


    /**
     * Получает все задачи из репозитория.
     *
     * @return список всех задач.
     */
    public List<Task> getAll() {
        log.debug("Fetching all tasks");
        List<Task> tasks = taskRepository.findAll();
        log.debug("Fetched {} tasks", tasks.size());
        return tasks;
    }

    /**
     * Получает задачу по её идентификатору.
     *
     * @param id идентификатор задачи.
     * @return задача с указанным идентификатором.
     * @throws ResourceNotFoundException если задача с указанным идентификатором не найдена.
     */
    public Task getById(Long id) {
        log.debug("Fetching task with id: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task with id {} not found", id);
                    return new ResourceNotFoundException("Task with ID %d not found".formatted(id));
                });
        log.debug("Fetched task: {}", task);
        return task;
    }

    /**
     * Получает список задач, созданных автором с указанным идентификатором.
     *
     * @param authorId идентификатор автора.
     * @return список задач, созданных автором.
     */
    public List<Task> getByAuthor(final Long authorId) {
        log.debug("Fetching tasks by author with id: {}", authorId);
        List<Task> tasks = taskRepository.findByAuthor_Id(authorId);
        log.debug("Fetched {} tasks for author id: {}", tasks.size(), authorId);
        return tasks;
    }

    /**
     * Получает список задач, назначенных исполнителю с указанным идентификатором.
     *
     * @param assigneeId идентификатор исполнителя.
     * @return список задач, назначенных исполнителю.
     */
    public List<Task> getByAssignee(final Long assigneeId) {
        log.debug("Fetching tasks by assignee with id: {}", assigneeId);
        List<Task> tasks = taskRepository.findByAssignee_Id(assigneeId);
        log.debug("Fetched {} tasks for assignee id: {}", tasks.size(), assigneeId);
        return tasks;
    }

    /**
     * Получает список задач для пользователя, используя токен.
     *
     * @param token токен пользователя.
     * @return список задач, связанных с пользователем.
     */
    public List<Task> getByToken(final String token) {
        log.debug("Fetching tasks using token");
        long userId = userService.getByToken(token).getId();
        log.debug("Extracted user id {} from token", userId);

        List<Task> result = new ArrayList<>();
        result.addAll(getByAssignee(userId));
        result.addAll(getByAuthor(userId));
        log.info("Fetched {} tasks for user id {}", result.size(), userId);
        return result;
    }

    /**
     * Сохраняет новую задачу, используя данные из TaskRequest.
     *
     * @param taskRequest объект, содержащий данные для создания задачи.
     */
    public void save(TaskRequest taskRequest) {
        log.debug("Saving task from TaskRequest: {}", taskRequest);
        Task task = convertRequestToTask(taskRequest);
        taskRepository.save(task);
        log.info("Task saved successfully with title: {}", task.getTitle());
    }

    /**
     * Обновляет статус задачи по её идентификатору.
     *
     * @param id идентификатор задачи.
     * @param status новый статус задачи.
     * @param accessToken токен доступа пользователя.
     */
    public void update(Long id, Status status, String accessToken) {
        log.debug("Updating task with id: {} to status: {}", id, status);
        Task task = getById(id);
        task.setStatus(status);
        taskRepository.save(task);
        log.info("Task with id {} updated to status {}", id, status);
    }

    /**
     * Удаляет задачу по её идентификатору.
     *
     * @param id идентификатор задачи, которую необходимо удалить.
     * @throws ResourceNotFoundException если задача с указанным идентификатором не найдена.
     */
    public void delete(Long id) {
        log.debug("Deleting task with id: {}", id);
        Task task = getById(id);
        taskRepository.delete(task);
        log.info("Task with id {} deleted successfully", id);
    }


    /**
     * Преобразует объект TaskRequest в объект Task.
     *
     * @param request объект TaskRequest, содержащий данные для создания задачи.
     * @return созданный объект Task.
     */
    private Task convertRequestToTask(final TaskRequest request) {
        log.debug("Converting TaskRequest to Task for title: {}", request.getTitle());
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
        log.debug("Conversion complete for task with title: {}", task.getTitle());
        return task;
    }
}
