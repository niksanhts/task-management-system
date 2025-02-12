package com.sarf.task_management_system.domain.factories;

import com.sarf.task_management_system.domain.security.JwtTokenProvider;
import com.sarf.task_management_system.web.dto.response.CommentResponse;
import com.sarf.task_management_system.web.dto.response.JwtResponse;
import com.sarf.task_management_system.web.dto.response.TaskResponse;
import com.sarf.task_management_system.web.dto.response.UserResponse;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.models.Comment;
import com.sarf.task_management_system.domain.models.Task;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseFactory {

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

    public static JwtResponse createJWTResponse(@Valid final ApplicationUser user, final JwtTokenProvider jwtTokenProvider) {
        if(user == null)
            throw new NullPointerException();

        Long id = user.getId();
        String email = user.getEmail();
        String accessToken = jwtTokenProvider.createAccessToken(id, email, user.getRoles());
        String refreshToken = jwtTokenProvider.createRefreshToken(id, email);

        return new JwtResponse(
                id,
                email,
                accessToken,
                refreshToken
        );
    }
}
