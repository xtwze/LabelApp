package com.example.LabelApp.repositories;

import com.example.LabelApp.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTrackId(Long trackId);
}