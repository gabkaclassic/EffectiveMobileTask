package org.gaba.JavaTechTask.services;

import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.repositories.PostRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
}
