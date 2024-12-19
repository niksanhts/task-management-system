package com.sarf.task_management_system.web.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private UserResponse author;
}
