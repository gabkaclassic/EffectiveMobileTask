package org.gaba.JavaTechTask.controllers;

import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.controllers.dto.PostDTO;
import org.gaba.JavaTechTask.entities.Post;
import org.gaba.JavaTechTask.services.JWTService;
import org.gaba.JavaTechTask.services.PostService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JWTService jwtService;

    @PostMapping
    public Mono<List<String>> createPost(
            PostDTO data,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        return postService.create(
                data.subject(),
                data.content(),
                jwtService.extractId(token),
                Arrays.stream(data.files()).map(FilePart.class::cast).toList()
        );
    }

    @GetMapping("/feed")
    public Flux<Post> getActivityFeed(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam(value = "orderDesc", defaultValue = "true") Boolean orderDesc,
            @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber
    ) {


        return postService.getFeed(jwtService.extractId(token), pageSize, pageNumber, orderDesc);
    }

    @DeleteMapping
    public Mono<Boolean> deletePost(
            @RequestParam("postId") String postId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {


        return postService.delete(
                jwtService.extractId(token),
                postId
        );
    }

}
