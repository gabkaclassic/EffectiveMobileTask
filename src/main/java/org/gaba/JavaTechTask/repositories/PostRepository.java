package org.gaba.JavaTechTask.repositories;

import org.gaba.JavaTechTask.entities.Post;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface PostRepository extends ReactiveCrudRepository<Post, UUID> {
}
