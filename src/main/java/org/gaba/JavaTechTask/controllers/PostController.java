package org.gaba.JavaTechTask.controllers;

import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.services.PostService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

}
