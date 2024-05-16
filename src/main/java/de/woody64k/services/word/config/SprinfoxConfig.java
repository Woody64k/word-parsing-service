package de.woody64k.services.word.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Profile("!prod")
@Configuration
public class SprinfoxConfig {
    @Bean
    OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Word Parser API").description("Parses Content From Word Documents.").version("v1.0.0").license(new License().name("Apache 2.0"))).security(Collections.singletonList(new SecurityRequirement().addList("apiKey")))
                .components(new Components().addSecuritySchemes("apiKey", new SecurityScheme().name("apiKey").type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER)));
    }
}
