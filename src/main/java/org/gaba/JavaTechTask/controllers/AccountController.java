package org.gaba.JavaTechTask.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="Пользователи", description = "Взаимодействие пользователей друг с другом, получение выборки пользователей")
public class AccountController {

    private final AccountService accountService;
    private final JWTService jwtService;

    @GetMapping("/relations")
    @Operation(
            summary = "Подписчики",
            description = "Получение ников пользователей, находящихся в указанном типе связи с переданным пользователем " +
                    "(SUBSCRIBER - подписчики, FRIEND - друзья и подписчики, REQUEST - необработанные заявки)"
    )
    @SecurityRequirement(name = "JWT")
    public Flux<String> getSubscribers(
            @RequestParam("username") @Parameter(description = "Имя пользователя") String username,
            @RequestParam("relationType") @Parameter(description = "Имя пользователя") String relationType) {

        return accountService.getUsersByRelation(username, relationType);
    }

    @PostMapping("/auth")
    @Operation(
            summary = "Авторизация",
            description = "Вход в аккаунт с помощью логина и пароля, возвращает JWT Bearer токен, по которому производится дальнейшее взаимодействие "
    )
    public Mono<ResponseEntity<String>> auth(@RequestBody @Parameter(description = "JSON с полями username и password") Account account) {

        return accountService.login(account)
                .map(token -> ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).body(""))
                .onErrorResume(e-> Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage())));
    }

    @PostMapping("/registration")
    @Operation(
            summary = "Регистрация",
            description = "Создание нового аккаунта, возвращает список ошибок о невалидности входных данный"
    )
    public Mono<ResponseEntity<List<String>>> registration(@RequestBody @Parameter(description = "JSON с полями username, email и password") Account account) {

        return accountService.registration(account)
                .map(result -> (result.isEmpty() ? ResponseEntity.ok() : ResponseEntity.status(HttpStatus.FORBIDDEN)).body(result));
    }

    @PostMapping("/send_request")
    @Operation(
            summary = "Запрос на дружбу",
            description = "Отправка заявки на дружбу другому пользователю"
    )
    @SecurityRequirement(name = "JWT")
    public void sendRequest(
            @Parameter(description = "Токен авторизации") @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam("targetUsername") @Parameter(description = "Имя пользователя, которому отправляется заявка") String targetUsername) {

        accountService.sendRequest(jwtService.extractId(token), targetUsername);
    }

    @PostMapping("/reject_request")
    @Operation(
            summary = "Отклонение запроса",
            description = "Отказ от предложения дружбы"
    )
    @SecurityRequirement(name = "JWT")
    public void rejectRequest(
            @Parameter(description = "Токен авторизации") @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(description = "Имя пользователя, которому отказывают в дружбе") @RequestParam("username") String username) {

        accountService.rejectRequest(username, jwtService.extractId(token));
    }

    @PostMapping("/confirm_request")
    @Operation(
            summary = "Одобрение запроса",
            description = "Одобрение предложения дружбы"
    )
    @SecurityRequirement(name = "JWT")
    public void confirmRequest(
            @Parameter(description = "Токен авторизации") @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(description = "Имя пользователя, которому одобрили запрос дружбы") @RequestParam("username") String username) {

        accountService.confirmRequest(username, jwtService.extractId(token));
    }

    @PostMapping("/reject_friend")
    @Operation(
            summary = "Отписка",
            description = "Отказ от дружбы с пользователем"
    )
    @SecurityRequirement(name = "JWT")
    public void rejectFriend(
            @Parameter(description = "Токен авторизации") @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(description = "Имя пользователя, от которого отписываются") @RequestParam("targetUsername") String targetUsername) {

        accountService.rejectFriend(jwtService.extractId(token), targetUsername);
    }
}
