package com.nyx.Linkify.feature.feed.repo;

import com.nyx.Linkify.feature.feed.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepo extends JpaRepository<Post, Long> {

    List<Post> findByAuthorIdNotOrderByCreationDateDesc(Long id);

    List<Post> findAllByOrderByCreationDateDesc();

    List<Post> findByAuthorId(Long userId);
}
