package org.gaba.JavaTechTask.configurations.auth;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.gaba.JavaTechTask.services.JWTService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class JWTAuthFilter implements WebFilter {

    private final JWTService jwtService;

    private final List<String> authWhiteList;
    private static final List<String> documentationUrls = List.of(
            "/swagger-ui",
            "/webjars/swagger-ui/",
            "/v3/api-docs"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        var path = exchange.getRequest().getPath().toString();

        if(authWhiteList.stream().anyMatch(p -> p.equals(path))
                || documentationUrls.stream().anyMatch(path::startsWith))
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
