package com.sarf.task_management_system.models;

import com.sarf.task_management_system.enums.*;
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

    @NonNull
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany()
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    private ApplicationUser author;

    @ManyToOne
    private ApplicationUser assignee;
}


