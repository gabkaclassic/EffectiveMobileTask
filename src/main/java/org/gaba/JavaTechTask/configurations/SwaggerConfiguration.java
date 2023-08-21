package org.gaba.JavaTechTask.configurations;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.Optional;

@Configuration
@EnableWebFlux
@EnableSwagger2
@OpenAPIDefinition
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfiguration {
    @Bean
    public Docket apiDocket() {

        return new Docket(DocumentationType.OAS_30)
                .select()
////                .apis(RequestHandlerSelectors.any())
//                .apis(Predicate.not(RequestHandlerSelectors.basePackage("springfox.documentation.swagger.web.UiConfiguration")))
//                .apis(Predicate.not(RequestHandlerSelectors.basePackage("springfox.documentation.swagger.web.SwaggerResource")))
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .build()
                .apiInfo(getApiInfo())
                .genericModelSubstitutes(Optional.class);
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "REST API documentation",
                "For technical task Effective Mobile",
                "1.0",
                "TERMS OF SERVICE URL",
                new Contact("Kuzmin Rodion", "https://github.com/gabkaclassic/EffectiveMobileTask", "kukzminrd44@gmail.com"),
                "",
                "",
                Collections.emptyList());
    }
}
