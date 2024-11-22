package com.sarf.task_management_system.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(unique = true)
    private String email;

    @NonNull
    private String hashPassword;

    @NonNull
    private String roles;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasksCreatedByUser = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasksAssignedToUser = new HashSet<>();
}

