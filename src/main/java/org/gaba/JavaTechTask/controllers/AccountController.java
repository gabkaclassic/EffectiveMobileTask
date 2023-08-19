package org.gaba.JavaTechTask.controllers;

import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.entities.Account;
import org.gaba.JavaTechTask.entities.Credentials;
import org.gaba.JavaTechTask.services.AccountService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/auth")
    public Mono<ResponseEntity<Object>> auth(@RequestBody Credentials credentials) {

        return accountService.login(credentials)
                .map(token -> ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).build())
                .onErrorReturn(InvalidPasswordException.class, ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PostMapping("/registration")
    public Mono<ResponseEntity<Object>> registration(@RequestBody Account account) {

        return accountService.registration(account)
                .map(result -> (result ? ResponseEntity.ok() : ResponseEntity.status(HttpStatus.FORBIDDEN)).build());
    }

    @PostMapping("/test")
    public Mono<Account> registration(@RequestBody Credentials credentials) {

        return accountService.test(credentials);
    }
}
