package org.gaba.JavaTechTask.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.controllers.dto.PostDTO;
import org.gaba.JavaTechTask.entities.Post;
import org.gaba.JavaTechTask.services.JWTService;
import org.gaba.JavaTechTask.services.PostService;
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
@Tag(name="Посты", description = "Взаимодействие с лентой активности")
public class PostController {

    private final PostService postService;
    private final JWTService jwtService;

    @PostMapping
    @Operation(
            summary = "Создание",
            description = "Создание поста с текстом и (опционально) с фото. Возвращает список ошибок о невалидности входных данных"
    )
    @SecurityRequirement(name = "JWT")
    public Mono<List<String>> createPost(
            @Parameter(description = "Параметры в формате form-data (subject - тема поста, content - текст поста, files - прилагаемые файлы (опционально))")
            PostDTO data,
            @Parameter(description = "Токен авторизации") @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        return postService.create(
                data.subject(),
                data.content(),
                jwtService.extractId(token),
                Arrays.stream(data.files()).map(FilePart.class::cast).toList()
        );
    }

    @GetMapping("/feed")
    @Operation(
            summary = "Лента активности",
            description = "Получение постов пользователей, на которых подписан текущий пользователь"
    )
    @SecurityRequirement(name = "JWT")
    public Flux<Post> getActivityFeed(
            @Parameter(description = "Токен авторизации") @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(description = "Порядок сортировеи по дате (true - обратный порядок") @RequestParam(value = "orderDesc", defaultValue = "true") Boolean orderDesc,
            @Parameter(description = "Размер страницы (по умолчанию - 3)") @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
            @Parameter(description = "Номер страницы (по умолчанию - 0)") @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber
    ) {

        return postService.getFeed(jwtService.extractId(token), pageSize, pageNumber, orderDesc);
    }

    @DeleteMapping
    @Operation(
            summary = "Удаление",
            description = "Удаление поста его создателем"
    )
    @SecurityRequirement(name = "JWT")
    public Mono<Boolean> deletePost(
            @Parameter(description = "ID поста") @RequestParam("postId") String postId,
            @Parameter(description = "Токен авторизации") @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        return postService.delete(
                jwtService.extractId(token),
                postId
        );
    }

}
