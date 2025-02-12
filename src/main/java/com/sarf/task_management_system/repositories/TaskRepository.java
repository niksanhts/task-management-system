package com.sarf.task_management_system.repositories;

import com.sarf.task_management_system.domain.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAuthor_Id(Long authorId);
    List<Task> findByAssignee_Id(Long assigneeId);
}
