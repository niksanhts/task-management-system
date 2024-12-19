package com.sarf.task_management_system.domain.services;

import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.models.Comment;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final TaskService taskService;
	private final UserService userService;
	private final CommentRepository commentRepository;

	public List<Comment> getByTask(final Long taskId) {
		return commentRepository.findByTaskId(taskId);
	}

	public List<Comment> getByAuthor(final Long authorId) {
		return commentRepository.findByAuthorId(authorId);
	}

	public void save(final String authorEmail,
					 final Long taskId,
					 final String content) throws AccessDeniedException {

		Task task = taskService.getById(taskId);
		ApplicationUser author = userService.getByEmail(authorEmail);

		if (!author.getTasksAssignedToUser().contains(task) ||
				!author.getTasksCreatedByUser().contains(task)) {
			throw new AccessDeniedException("Only creator and assignee can comment task");
		}

		Comment comment = new Comment(1, author, task, content);
		commentRepository.save(comment);
	}

	public void delete(final Long id) throws NoSuchElementException {
		if (!commentRepository.existsById(id))
			throw new NoSuchElementException("Trying to delete comment what does not exist");
		commentRepository.deleteById(id);
	}
}
