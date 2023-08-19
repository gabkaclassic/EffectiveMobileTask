package org.gaba.JavaTechTask.configurations.auth;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.gaba.JavaTechTask.services.JWTService;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter implements WebFilter {

    private final JWTService jwtService;

    private final List<String> authWhiteList;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        var path = exchange.getRequest().getPath().toString();

        if(authWhiteList.stream().anyMatch(p -> p.equals(path)))
            return chain.filter(exchange);

        var token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(!jwtService.validateToken(token)) {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatusCode.valueOf(403));
            return response.setComplete().onErrorStop();
        }
        return chain.filter(exchange);
    }
}
