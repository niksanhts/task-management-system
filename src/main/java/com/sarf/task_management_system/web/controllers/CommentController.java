package com.sarf.task_management_system.web.controllers;

import com.sarf.task_management_system.domain.factories.ResponseFactory;
import com.sarf.task_management_system.domain.services.CommentService;
import com.sarf.task_management_system.web.dto.response.CommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/comment")
@RequiredArgsConstructor
@Tag(name = "Comment Controller",
		description = """
		CommentController is a Spring MVC controller that handles HTTP requests related to comments
		associated with tasks in the task management system. It provides endpoints for retrieving,
		creating, and deleting comments, allowing users to interact with comments in a structured way.""")
public class CommentController {

	private final CommentService commentService;

	@GetMapping("task/{id}/all")
	@Operation(description = "Retrieves a list of comments for the task with the specified identifier.")
	public List<CommentResponse> getAllByTask(@PathVariable Long id) {
		return commentService.getByTask(id)
				.stream()
				.map(ResponseFactory::createComment)
				.toList();
	}

	@GetMapping("/author/{id}/all")
	public List<CommentResponse> getAllByAuthor(@PathVariable Long id) {
		return commentService.getByAuthor(id)
				.stream()
				.map(ResponseFactory::createComment)
				.toList();
	}

	@PostMapping("/task/{taskId}/create")
	@Operation(description = "Adds a comment to the task with the specified identifier.")
	public ResponseEntity<String> create(@PathVariable Long taskId,
										 @RequestBody String authorEmail,
										 @RequestBody String content) {
		try {
			commentService.save(authorEmail, taskId, content);
			return ResponseEntity.ok("Comment successfully created");
		}
		catch (Exception exception) {
			return ResponseEntity
					.status(HttpStatus.METHOD_NOT_ALLOWED)
					.body(exception.getMessage());
		}

	}

	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@Operation(description = "Adds a comment to the task with the specified identifier.")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		try {
			commentService.delete(id);
			return ResponseEntity.ok("Comment successfully created");
		}
		catch (Exception exception) {
			return ResponseEntity.ok(
					"Comment wasn't created. Course: %s".formatted(exception)
			);
		}



	}




}
