package org.gaba.JavaTechTask.controllers;

import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.entities.Account;
import org.gaba.JavaTechTask.services.AccountService;
import org.gaba.JavaTechTask.services.JWTService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final JWTService jwtService;

    @GetMapping("/relations")
    public Flux<String> getSubscribers(@RequestParam("username") String username, @RequestParam("relationType") String relationType) {

        return accountService.getUsersByRelation(username, relationType);
    }

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

    @PostMapping("/send_request")
    public void sendRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam("targetUsername") String targetUsername) {

        accountService.sendRequest(jwtService.extractId(token), targetUsername);
    }

    @PostMapping("/reject_request")
    public void rejectRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam("username") String username) {

        accountService.rejectRequest(username, jwtService.extractId(token));
    }

    @PostMapping("/confirm_request")
    public void confirmRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam("username") String username) {

        accountService.confirmRequest(username, jwtService.extractId(token));
    }

    @PostMapping("/reject_friend")
    public void rejectFriend(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam("targetUsername") String targetUsername) {

        accountService.rejectFriend(jwtService.extractId(token), targetUsername);
    }
}
