package com.sarf.task_management_system.reositories;

import com.sarf.task_management_system.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAuthor_Id(Long authorId);
    List<Task> findByAssignee_Id(Long assigneeId);
}
