package com.sarf.task_management_system.web.dto.response;

import com.sarf.task_management_system.domain.enums.Priority;
import com.sarf.task_management_system.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private UserResponse author;
    private UserResponse assignee;
}
