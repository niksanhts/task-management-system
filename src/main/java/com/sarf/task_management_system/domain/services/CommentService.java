package com.sarf.task_management_system.domain.services;

import com.sarf.task_management_system.domain.exceptions.ResourceNotFoundException;
import com.sarf.task_management_system.domain.exceptions.AccessDeniedException;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.models.Comment;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Сервисный класс для управления комментариями в приложении.
 * Этот класс предоставляет методы для выполнения операций с комментариями,
 * такими как получение комментариев по задаче или автору, а также сохранение и удаление комментариев.
 *
 * <p>
 * Класс аннотирован {@link Service}, чтобы указать, что он является компонентом сервиса в контексте Spring.
 * Он использует Lombok's {@link Slf4j} для логирования и {@link RequiredArgsConstructor} для инъекции зависимостей через конструктор.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

	private final TaskService taskService;
	private final UserService userService;
	private final CommentRepository commentRepository;

	/**
	 * Получает список комментариев для задачи с указанным идентификатором.
	 *
	 * @param taskId идентификатор задачи.
	 * @return список комментариев для указанной задачи.
	 */
	public List<Comment> getByTask(final Long taskId) {
		log.debug("Fetching comments for task with id: {}", taskId);
		List<Comment> comments = commentRepository.findByTaskId(taskId);
		log.debug("Fetched {} comments for task with id: {}", comments.size(), taskId);
		return comments;
	}

	/**
	 * Получает список комментариев, созданных автором с указанным идентификатором.
	 *
	 * @param authorId идентификатор автора.
	 * @return список комментариев, созданных автором.
	 */
	public List<Comment> getByAuthor(final Long authorId) {
		log.debug("Fetching comments for author with id: {}", authorId);
		List<Comment> comments = commentRepository.findByAuthorId(authorId);
		log.debug("Fetched {} comments for author with id: {}", comments.size(), authorId);
		return comments;
	}

	/**
	 * Сохраняет новый комментарий для задачи.
	 *
	 * @param token токен пользователя, создающего комментарий.
	 * @param taskId идентификатор задачи, к которой добавляется комментарий.
	 * @param content содержимое комментария.
	 * @throws AccessDeniedException если пользователь не имеет прав на добавление комментария к задаче.
	 */
	public void save(final String token,
					 final Long taskId,
					 final String content) throws AccessDeniedException {

		log.debug("Attempting to save a comment for task id: {} with content: {}", taskId, content);
		ApplicationUser author = userService.getByToken(token);

		log.debug("Retrieved author with id: {}", author.getId());
		Task task = taskService.getById(taskId);

		log.debug("Retrieved task with id: {}", task.getId());

		if (author.getTasksAssignedToUser().contains(task) ||
				author.getTasksCreatedByUser().contains(task)) {
			Comment comment = new Comment(1, author, task, content);
			commentRepository.save(comment);
			log.info("Comment saved successfully for task id: {} by author id: {}", taskId, author.getId());

		} else {
			log.warn("Access denied: User with id {} is not authorized to comment on task id: {}", author.getId(), taskId);
			throw new AccessDeniedException("Only creator, assignee or admin can comment task");
		}
	}

	/**
	 * Удаляет комментарий по его идентификатору.
	 *
	 * @param id идентификатор комментария, который необходимо удалить.
	 * @throws NoSuchElementException если комментарий с указанным идентификатором не найден.
	 */
	public void delete(final Long id) throws NoSuchElementException {
		log.debug("Attempting to delete comment with id: {}", id);
		if (!commentRepository.existsById(id)) {
			log.warn("Deletion failed: Comment with id {} does not exist", id);
			throw new ResourceNotFoundException("Trying to delete comment what does not exist");
		}
		commentRepository.deleteById(id);
		log.info("Comment with id {} deleted successfully", id);
	}
}
