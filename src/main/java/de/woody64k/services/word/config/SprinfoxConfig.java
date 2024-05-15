package de.woody64k.services.word.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SprinfoxConfig {
    @Bean
    OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Word Parser API")
                        .description("Parses Content From Word Documents.")
                        .version("v1.0.0")
                        .license(new License()
                                .name("Apache 2.0")))
                .security(Collections
                        .singletonList(new SecurityRequirement()
                                .addList("OAuth")))
                .components(new Components()
                        .addSecuritySchemes("OAuth", new SecurityScheme()
                                .name("OAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Basic")))
                .externalDocs(new ExternalDocumentation());
    }
}
