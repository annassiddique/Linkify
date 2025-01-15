package com.nyx.Linkify.feature.feed.service;

import com.nyx.Linkify.feature.authentication.model.AuthenticationUser;
import com.nyx.Linkify.feature.authentication.repo.AuthenticateUserRepo;
import com.nyx.Linkify.feature.feed.dto.CommentDto;
import com.nyx.Linkify.feature.feed.dto.PostDto;
import com.nyx.Linkify.feature.feed.model.Comment;
import com.nyx.Linkify.feature.feed.model.Post;
import com.nyx.Linkify.feature.feed.repo.CommentRepo;
import com.nyx.Linkify.feature.feed.repo.PostRepo;
import org.springframework.boot.autoconfigure.web.servlet.error.DefaultErrorViewResolver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
    private final PostRepo postRepo;
    private final AuthenticateUserRepo userRepo;
    private final CommentRepo commentRepo;
    private final DefaultErrorViewResolver conventionErrorViewResolver;

    public FeedService(PostRepo postRepo, AuthenticateUserRepo userRepo, CommentRepo commentRepo, DefaultErrorViewResolver conventionErrorViewResolver) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.commentRepo = commentRepo;
        this.conventionErrorViewResolver = conventionErrorViewResolver;
    }

    public Post createPost(PostDto postDto, Long authorId) {
        AuthenticationUser author = userRepo.findById(authorId).orElseThrow(() -> new IllegalStateException("Author not found"));
        Post post = new Post(postDto.getContent(), author);
        post.setPicture(postDto.getPicture());
        return postRepo.save(post);
    }

    public Post editPost(Long postId, Long userId, PostDto postDto) {
        Post post = postRepo.findById(postId).orElseThrow(() -> new IllegalStateException("Post not found"));
        AuthenticationUser user = userRepo.findById(userId).orElseThrow(() -> new IllegalStateException("User not found"));

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalStateException("You are not allowed to edit this post");
        }
        post.setContent(postDto.getContent());
        post.setPicture(postDto.getPicture());
        return postRepo.save(post);
    }

    public List<Post> getFeedPosts(Long id) {
        return postRepo.findByAuthorIdNotOrderByCreationDateDesc(id);
    }

    public List<Post> getAllPosts() {
        return postRepo.findAllByOrderByCreationDateDesc();
    }

    public Post getPost(Long postId) {
        return postRepo.findById(postId).orElseThrow(() -> new IllegalStateException("Post not found"));
    }

    public void deletePost(Long postId, Long id) {
        Post post = postRepo.findById(postId).orElseThrow(() -> new IllegalStateException("Post not found"));
        AuthenticationUser user = userRepo.findById(id).orElseThrow(() -> new IllegalStateException("User not found"));

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalStateException("You are not allowed to delete this post");
        }

        postRepo.delete(post);
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepo.findByAuthorId(userId);
    }

    public Post likePost(Long postId, Long userid) {
        Post post = postRepo.findById(postId).orElseThrow(() -> new IllegalStateException("Post not found"));
        AuthenticationUser user = userRepo.findById(userid).orElseThrow(() -> new IllegalStateException("User not found"));
        if (post.getLikes().contains(user)) {
            post.getLikes().remove(user);
        } else {
            post.getLikes().add(user);
        }
        return postRepo.save(post);

    }

    public Comment addComment(Long postId, Long id, String content) {
        Post post = postRepo.findById(postId).orElseThrow(() -> new IllegalStateException("Post not found"));
        AuthenticationUser user = userRepo.findById(id).orElseThrow(() -> new IllegalStateException("User not found"));
        Comment comment = new Comment(post, user, content);
        return commentRepo.save(comment);
    }

    public void deleteComment(Long commentId, Long id) {
        Comment comment = commentRepo.findById(commentId).orElseThrow(() -> new IllegalStateException("Comment not found"));
        AuthenticationUser user = userRepo.findById(id).orElseThrow(() -> new IllegalStateException("User not found"));
        if(!comment.getAuthor().equals(user)) {
            throw new IllegalStateException("You are not allowed to delete this comment");
        }
        commentRepo.delete(comment);
    }

    public Comment editComment(Long commentId, Long id, String content) {
        Comment comment = commentRepo.findById(commentId).orElseThrow(() -> new IllegalStateException("Comment not found"));
        AuthenticationUser user = userRepo.findById(id).orElseThrow(() -> new IllegalStateException("User not found"));
        if(!comment.getAuthor().equals(user)) {
            throw new IllegalStateException("You are not allowed to edit this comment");
        }
        comment.setContent(content);
        return commentRepo.save(comment);
    }
}
