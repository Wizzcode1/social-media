package com.example.socialmedia.repository;

import com.example.socialmedia.model.Post;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface PostRepository extends ReactiveMongoRepository<Post, String> {
    Flux<Post> findAllByOrderByCreatedAtDesc();
}
