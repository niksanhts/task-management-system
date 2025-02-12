package com.sarf.task_management_system.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private UserResponse author;
}
