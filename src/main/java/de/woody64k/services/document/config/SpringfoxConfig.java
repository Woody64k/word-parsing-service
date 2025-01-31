package de.woody64k.services.document.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringfoxConfig {
    @Value("${build.version}")
    private String version;

    @Value("${build.time}")
    private String time;

    @Bean
    OpenAPI springShopOpenAPI() {
        return new OpenAPI().info(new Info().title("Document Parser API")
                .description(String.format("Parses Content From Word- and PDF-Documents. (last update: %s)", time))
                .version(version)
                .license(new License().name("Apache 2.0")))
                .security(Collections.singletonList(new SecurityRequirement().addList("apiKey")))
                .components(new Components().addSecuritySchemes("apiKey", new SecurityScheme().name("apiKey")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)));
    }
}
