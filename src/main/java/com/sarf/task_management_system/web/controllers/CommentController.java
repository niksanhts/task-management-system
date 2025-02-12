package com.sarf.task_management_system.web.controllers;

import com.sarf.task_management_system.domain.factories.ResponseFactory;
import com.sarf.task_management_system.domain.services.CommentService;
import com.sarf.task_management_system.web.dto.response.CommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления комментариями, связанными с задачами.
 * <p>
 * Этот контроллер предоставляет методы для получения всех комментариев по идентификатору задачи и автору,
 * создания нового комментария и удаления существующего комментария с проверкой прав доступа для администраторов.
 * Все операции взаимодействуют с сервисом {@link CommentService} и возвращают соответствующие ответы в формате JSON.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("api/v1/comment")
@RequiredArgsConstructor
@Tag(name = "Comment Controller", description = """
		Управляет комментариями, связанными с задачами. Он предоставляет методы для получения всех комментариев\s
		по идентификатору задачи и автору,создания нового комментария и удаления существующего комментария с проверкой\s
		прав доступа для администраторов. Все операции взаимодействуют с сервисом CommentService и возвращают\s
		соответствующие ответы в формате JSON.
	\t""")
public class CommentController {

	private final CommentService commentService;

	/**
	 * Получает список всех комментариев для задачи с указанным идентификатором.
	 *
	 * @param id идентификатор задачи, для которой необходимо получить комментарии.
	 * @return список объектов {@link CommentResponse}, представляющих комментарии для указанной задачи.
	 */
	@GetMapping("task/{id}/all")
	@Operation(description = "Retrieves a list of comments for the task with the specified identifier.")
	public List<CommentResponse> getAllByTask(@PathVariable Long id) {
		log.trace("All comments of task {} request", id);
		List<CommentResponse> comments = commentService.getByTask(id)
				.stream()
				.map(ResponseFactory::createComment)
				.toList();
		log.info("Retrieved {} comments for task {}", comments.size(), id);
		return comments;
	}

	/**
	 * Получает список всех комментариев, созданных автором с указанным идентификатором.
	 *
	 * @param id идентификатор автора, для которого необходимо получить комментарии.
	 * @return список объектов {@link CommentResponse}, представляющих комментарии, созданные указанным автором.
	 */
	@GetMapping("/author/{id}/all")
	public List<CommentResponse> getAllByAuthor(@PathVariable Long id) {
		log.trace("All comments of author {} request", id);
		List<CommentResponse> comments = commentService.getByAuthor(id)
				.stream()
				.map(ResponseFactory::createComment)
				.toList();
		log.info("Retrieved {} comments for author {}", comments.size(), id);
		return comments;
	}

	/**
	 * Добавляет комментарий к задаче с указанным идентификатором.
	 *
	 * @param id идентификатор задачи, к которой необходимо добавить комментарий.
	 * @param accessToken токен доступа, предоставленный в заголовке запроса.
	 * @param content содержимое комментария.
	 * @return ResponseEntity с сообщением о результате создания комментария.
	 */
	@PutMapping("/task/{id}/create")
	@Operation(description = "Adds a comment to the task with the specified identifier.")
	public ResponseEntity<String> create(@PathVariable Long id,
										 @RequestHeader(name = "Authorization") String accessToken,
										 @RequestBody String content) {
		log.trace("Comment creation request for task {} with content: {}", id, content);
		try {
			commentService.save(accessToken, id, content);
			log.info("Comment successfully created for task {}", id);
			return ResponseEntity.ok("Comment successfully created");
		}
		catch (Exception exception) {
			log.error("Error creating comment for task {}: {}", id, exception.getMessage());
			return ResponseEntity
					.status(HttpStatus.METHOD_NOT_ALLOWED)
					.body(exception.getMessage());
		}
	}

	/**
	 * Удаляет комментарий с указанным идентификатором.
	 * <p>
	 * Доступно только пользователям с ролью администратора.
	 * </p>
	 *
	 * @param id идентификатор комментария, который необходимо удалить.
	 * @return ResponseEntity с сообщением о результате удаления комментария.
	 */
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@Operation(description = "Deletes a comment with the specified identifier.")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		log.trace("Delete comment {} request", id);
		try {
			commentService.delete(id);
			log.info("Comment {} successfully deleted", id);
			return ResponseEntity.ok("Comment successfully deleted");
		}
		catch (Exception exception) {
			log.error("Error deleting comment {}: {}", id, exception.getMessage());
			return ResponseEntity.ok(
					"Comment wasn't deleted. Cause: %s".formatted(exception)
			);
		}
	}
}
