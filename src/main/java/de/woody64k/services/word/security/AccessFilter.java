package de.woody64k.services.word.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1)
public class AccessFilter implements Filter {
    @Value("${app.security.clientId}")
    private String clientId;

    @Value("${app.security.clientSecret}")
    private String clientSecret;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        String path = httpReq.getRequestURI();
        if (path.contains("swagger") || path.contains("api-docs")) {
            chain.doFilter(request, response);
        } else {
            String barer = httpReq.getHeader("Authorization");
            if (barer != null && barer.startsWith("Basic ") && barer.length() > 6) {
                String authData = new String(Base64.getDecoder().decode(barer.substring(6)), StandardCharsets.UTF_8);
                String[] authInfo = authData.split(":");
                if (authInfo.length == 2 && clientId.contentEquals(authInfo[0]) && clientSecret.contentEquals(authInfo[1])) {
                    chain.doFilter(request, response);
                } else {
                    ((HttpServletResponse) response).sendError(403);
                }
            } else {
                ((HttpServletResponse) response).sendError(403);
            }
        }
    }
}
