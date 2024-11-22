package de.woody64k.services.document.security;

import java.io.IOException;
import java.util.Set;

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

@Component
@Order(1)
public class AccessFilter implements Filter {
    @Value("${app.security.apiKeys}")
    private Set<String> apiKeys;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        if (SwaggerExcluder.isSwaggerUi(httpReq)) {
            chain.doFilter(request, response);
        } else {
            String apiKey = httpReq.getHeader("apiKey");
            if (apiKey != null && apiKeys.contains(apiKey)) {
                chain.doFilter(request, response);
            } else {
                ((HttpServletResponse) response).sendError(403);
            }
        }
    }
}
