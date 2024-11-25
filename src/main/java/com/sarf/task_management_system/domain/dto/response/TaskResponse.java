package com.sarf.task_management_system.domain.dto.response;

import com.sarf.task_management_system.domain.enums.Priority;
import com.sarf.task_management_system.domain.enums.Status;
import com.sarf.task_management_system.domain.models.ApplicationUser;
import com.sarf.task_management_system.domain.models.Comment;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private List<CommentResponse> comments = new ArrayList<>();
    private UserResponse author;
    private UserResponse assignee;
}
