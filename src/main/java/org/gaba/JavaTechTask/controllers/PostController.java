package org.gaba.JavaTechTask.controllers;

import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.entities.PostData;
import org.gaba.JavaTechTask.services.JWTService;
import org.gaba.JavaTechTask.services.PostService;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JWTService jwtService;

    @PostMapping("/create")
    public Mono<List<String>> createPost(
            PostData data,
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
