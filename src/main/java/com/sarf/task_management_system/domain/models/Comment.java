package com.sarf.task_management_system.domain.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

@Entity
@Data
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private ApplicationUser author;

    @ManyToOne
    private Task task;

    @NonNull
    private String content;
}
