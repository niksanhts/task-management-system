package com.sarf.task_management_system.domain.models;

import com.sarf.task_management_system.domain.enums.Priority;
import com.sarf.task_management_system.domain.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    private ApplicationUser author;

    @ManyToOne
    private ApplicationUser assignee;

    @OneToMany
    private List<Comment> comments = new ArrayList<>();
}


