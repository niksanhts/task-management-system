package com.sarf.task_management_system.domain.models;

import com.sarf.task_management_system.domain.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "users")
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String email;

    private String name;

    private String hashPassword;

    private List<Role> roles;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasksCreatedByUser;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasksAssignedToUser;
}

