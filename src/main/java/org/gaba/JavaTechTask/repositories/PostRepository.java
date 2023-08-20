package org.gaba.JavaTechTask.repositories;

import org.gaba.JavaTechTask.entities.Post;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;


@Repository
public interface PostRepository extends ReactiveCrudRepository<Post, String>, ReactiveSortingRepository<Post, String> {
    Flux<Post> findByAuthor(String author);
}
