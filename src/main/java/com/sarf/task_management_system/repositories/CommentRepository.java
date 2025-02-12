package com.sarf.task_management_system.repositories;

import com.sarf.task_management_system.domain.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByAuthorId(Long authorId);
	List<Comment> findByTaskId(Long taskId);
}
