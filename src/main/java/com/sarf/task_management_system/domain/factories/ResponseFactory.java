package com.sarf.task_management_system.domain.factories;


import com.sarf.task_management_system.domain.dto.response.CommentResponse;
import com.sarf.task_management_system.domain.dto.response.JwtResponse;
import com.sarf.task_management_system.domain.dto.response.TaskResponse;
import com.sarf.task_management_system.domain.dto.response.UserResponse;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.models.Comment;
import com.sarf.task_management_system.domain.models.Task;
import com.sarf.task_management_system.domain.security.JwtTokenProvider;
import com.sarf.task_management_system.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ResponseFactory {

    private JwtTokenProvider tokenProvider;
    private UserService userService;

    public Comment create(
            final String authorToken,
            final Task task,
            final String content
    ) {
        String email = tokenProvider.getEmail(authorToken);
        Comment comment = new Comment();
        comment.setAuthor(userService.getByEmail(email));
        comment.setContent(content);
        comment.setTask(task);

        return comment;
    }

    public static UserResponse createUser(ApplicationUser user) {
        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRoles(user.getRoles());

        return response;
    }

    public static TaskResponse createTask(Task task) {
        TaskResponse response = new TaskResponse();

        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setAuthor(
                createUser(task.getAuthor())
        );
        response.setComments(
                task.getComments()
                        .stream()
                        .map(ResponseFactory::createComment)
                        .toList()
        );
        response.setPriority(task.getPriority());
        response.setStatus(task.getStatus());

        return response;
    }

    public static CommentResponse createComment(Comment comment) {
        CommentResponse response = new CommentResponse();

        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setAuthor(
                createUser(comment.getAuthor())
        );

        return response;
    }
}
