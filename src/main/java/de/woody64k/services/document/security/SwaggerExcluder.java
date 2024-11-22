package de.woody64k.services.document.security;

import jakarta.servlet.http.HttpServletRequest;

public class SwaggerExcluder {
    public static boolean isSwaggerUi(HttpServletRequest httpReq) {
        String path = httpReq.getRequestURI();
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }
}
