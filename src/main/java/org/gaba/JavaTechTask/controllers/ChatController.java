package org.gaba.JavaTechTask.controllers;

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
public class ChatController {

    private final ChatService chatService;
    private final JWTService jwtService;

    @PostMapping
    public Mono<String> createChat(@RequestBody Chat chat, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        return chatService.create(jwtService.extractId(token), chat);
    }

    @GetMapping
    public Mono<Chat> getChat(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam("chatId") String chatId) {

        return chatService.getChat(jwtService.extractId(token), chatId);
    }

}
