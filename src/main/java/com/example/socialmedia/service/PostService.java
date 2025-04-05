package com.example.socialmedia.service;

import java.time.Instant;

import com.example.socialmedia.dto.CreatePostRequest;
import com.example.socialmedia.model.Post;
import com.example.socialmedia.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Flux<Post> getFeed() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public Mono<Post> createPost(String author, CreatePostRequest request) {
        Post post = Post.builder()
                .author(author)
                .content(request.getContent())
                .createdAt(Instant.now())
                .build();

        return postRepository.save(post);
    }
}
