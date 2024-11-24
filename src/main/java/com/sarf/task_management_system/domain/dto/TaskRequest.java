package com.sarf.task_management_system.domain.dto;

import com.sarf.task_management_system.domain.enums.Priority;
import com.sarf.task_management_system.domain.enums.Status;
import lombok.Data;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private String authorEmail;
    private String assigneeEmail;
}
