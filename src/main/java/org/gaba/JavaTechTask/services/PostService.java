package org.gaba.JavaTechTask.services;

import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.entities.Post;
import org.gaba.JavaTechTask.repositories.PostRepository;
import org.gaba.JavaTechTask.validators.FilesValidator;
import org.gaba.JavaTechTask.validators.PostValidator;
import org.springframework.data.domain.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AWSService awsService;
    private final PostValidator postValidator;
    private final FilesValidator filesValidator;
    private final AccountService accountService;

    public Mono<List<String>> create(String subject, String content, String authorId, List<FilePart> files) {

        return accountService.existsById(authorId)
                .map(exists -> {

                    if(!exists)
                        return List.of("Invalid author");

                    var post = Post.builder()
                            .subject(subject)
                            .content(content)
                            .build();

                    var errors = postValidator.validatePost(post);

                    if(!errors.isEmpty())
                        return errors;

                    errors = filesValidator.checkFiles(files, AWSService.IMAGES_TYPE);

                    if(!errors.isEmpty())
                        return errors;

                    awsService.saveImages(files).doOnNext(filenames -> {
                        post.setFiles(filenames);
                        post.setAuthor(authorId);
                        postRepository.save(post).subscribe();
                    }).subscribe();

                    return errors;
        });
    }

    public Mono<Boolean> delete(String author, String postId) {

        return postRepository.findById(postId)
                .map(post -> {
                    if(!post.getAuthor().equals(author))
                        return false;
                    postRepository.deleteById(postId).subscribe();
                    return true;
                });
    }

    public Flux<Post> getFeed(String accountId, int pageSize, int pageNumber, boolean orderDesc) {

        return accountService.getSubscriptions(accountId)
                .flatMap(postRepository::findByAuthor)
                .sort(orderDesc ? Comparator.comparing(Post::getCreationTime).reversed() : Comparator.comparing(Post::getCreationTime))
                .skip((long) pageSize * pageNumber).take(pageSize);
    }
}
