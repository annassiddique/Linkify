package com.nyx.Linkify.feature.feed.repo;

import com.nyx.Linkify.feature.feed.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepo extends JpaRepository<Comment, Long> {
}
