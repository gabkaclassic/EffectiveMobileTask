package org.gaba.JavaTechTask.controllers;

import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.entities.Account;
import org.gaba.JavaTechTask.services.AccountService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/auth")
    public Mono<ResponseEntity<String>> auth(@RequestBody Account account) {

        return accountService.login(account)
                .map(token -> ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).body(""))
                .onErrorResume(e-> Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage())));
    }

    @PostMapping("/registration")
    public Mono<ResponseEntity<List<String>>> registration(@RequestBody Account account) {

        return accountService.registration(account)
                .map(result -> (result.isEmpty() ? ResponseEntity.ok() : ResponseEntity.status(HttpStatus.FORBIDDEN)).body(result));
    }
}
