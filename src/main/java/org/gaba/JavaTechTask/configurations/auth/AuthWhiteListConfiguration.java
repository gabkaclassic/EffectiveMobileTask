package org.gaba.JavaTechTask.configurations.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AuthWhiteListConfiguration {

    @Bean
    public List<String> authWhiteList() {
        return List.of(
                "/account/auth",
                "/account/logout",
                "/account/registration"
        );
    }

}
