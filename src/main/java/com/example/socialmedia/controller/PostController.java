package com.example.socialmedia.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.socialmedia.dto.CreatePostRequest;
import com.example.socialmedia.model.Post;
import com.example.socialmedia.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/feed")
    public Mono<List<Post>> getFeed() {
        return postService.getFeed().collectList();
    }

    @PostMapping("/create")
    public Mono<Post> createPost(
            @RequestBody CreatePostRequest request,
            Authentication authentication) {

        String author = authentication.getName();
        return postService.createPost(author, request);
    }
}
