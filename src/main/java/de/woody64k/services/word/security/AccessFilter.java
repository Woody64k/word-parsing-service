package de.woody64k.services.word.security;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

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
    @Value("${app.security.apiKeys}")
    private Set<String> apiKeys;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        String path = httpReq.getRequestURI();
        if (path.contains("swagger") || path.contains("api-docs")) {
            chain.doFilter(request, response);
        } else {
            String apiKey = httpReq.getHeader("apiKey");
            if (apiKey != null && apiKeys.contains(apiKey)) {
                StopWatch watch = new StopWatch();
                watch.start();
                chain.doFilter(request, response);
                watch.stop();
                log.info(String.format("Access from apiKey '%s' to '%s' (duration: %s seconds)", apiKey, path, watch.getTotalTimeSeconds()));
            } else {
                ((HttpServletResponse) response).sendError(403);
            }
        }
    }
}
