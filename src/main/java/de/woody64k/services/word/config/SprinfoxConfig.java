package de.woody64k.services.word.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SprinfoxConfig {

    @Bean
    OpenAPI springShopOpenAPI() {
        return new OpenAPI().info(new Info().title("Word Parser API").description("Parses Content From Word Documents.").version("v1.0.0").license(new License().name("Apache 2.0")));
    }
}
