package org.gaba.JavaTechTask.controllers;

import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.controllers.dto.PostDTO;
import org.gaba.JavaTechTask.services.JWTService;
import org.gaba.JavaTechTask.services.PostService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JWTService jwtService;

    @PostMapping("/create")
    public Mono<List<String>> createPost(
            PostDTO data,
            BindingResult errors,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {


        return postService.create(
                data.subject(),
                data.content(),
                jwtService.extractId(token),
                Arrays.stream(data.files()).map(FilePart.class::cast).toList()
        );
    }

}
