package org.gaba.JavaTechTask.configurations;

import lombok.RequiredArgsConstructor;
import org.gaba.JavaTechTask.configurations.auth.AuthenticationEntryPoint;
import org.gaba.JavaTechTask.configurations.auth.JWTAuthFilter;
import org.gaba.JavaTechTask.services.JWTService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@Order(2)
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public JWTAuthFilter jwtAuthFilter(JWTService jwtService, List<String> authWhiteList) {
        return new JWTAuthFilter(jwtService, authWhiteList);
    }
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity config, JWTService jwtService, List<String> authWhiteList) {

        return config
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                .csrf(csrfSpec -> csrfSpec.disable())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//                .authorizeExchange(authorizeExchangeSpec ->
//                        authorizeExchangeSpec.pathMatchers("/account/auth", "/account/logout", "/account/registration").permitAll()
//                                .anyExchange().authenticated()
//                )
                .addFilterAt(jwtAuthFilter(jwtService, authWhiteList), SecurityWebFiltersOrder.AUTHORIZATION)
//                .exceptionHandling(exceptionHandlingSpec ->
//                        exceptionHandlingSpec.authenticationEntryPoint(authenticationEntryPoint)
//                                .accessDeniedHandler(
//                                        (exchange, denied) -> Mono.fromRunnable(
//                                                () -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)
//                                        )
//                                )
//                )
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        corsConfiguration.setAllowedMethods(List.of("GET", "PUT", "POST", "DELETE"));
        corsConfiguration.setAllowedOrigins(List.of("*"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
