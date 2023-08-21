package org.gaba.JavaTechTask.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.entities.Chat;
import org.gaba.JavaTechTask.services.ChatService;
import org.gaba.JavaTechTask.services.JWTService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name="Чаты", description = "Создание и получение чатов")
public class ChatController {

    private final ChatService chatService;
    private final JWTService jwtService;

    @PostMapping
    @Operation(
            summary = "Создание",
            description = "Запрос на общение с другом для создания нового чата, возвращает ID созданного чата"
    )
    @SecurityRequirement(name = "JWT")
    public Mono<String> createChat(
            @Parameter(description = "JSON с полями creator (создатель чата) и participant (второй участник)") @RequestBody Chat chat,
            @Parameter(description = "Токен авторизации") @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        return chatService.create(jwtService.extractId(token), chat);
    }

    @GetMapping
    @Operation(
            summary = "Получение чата",
            description = "Получение чата по его ID"
    )
    @SecurityRequirement(name = "JWT")
    public Mono<Chat> getChat(
            @Parameter(description = "Токен авторизации") @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(description = "ID чата") @RequestParam("chatId") String chatId) {

        return chatService.getChat(jwtService.extractId(token), chatId);
    }

}
