package de.woody64k.services.word.security;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.annotation.PostConstruct;
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
@Order(2)
public class ThrottleFilter implements Filter {
    @Value("${app.security.throttle.limit}")
    private int throttleLimit;
    @Value("${app.security.throttle.period}")
    private int throttlePeriod;
    private RateLimiterRegistry rateLimiterRegistry;

    @PostConstruct
    private void postConstruct() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMillis(throttlePeriod))
                .limitForPeriod(throttleLimit)
                .timeoutDuration(Duration.ofMillis(0))
                .build();

        // Create registry
        rateLimiterRegistry = RateLimiterRegistry.of(config);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        String path = httpReq.getRequestURI();
        if (path.contains("swagger") || path.contains("api-docs")) {
            chain.doFilter(request, response);
        } else {
            String apiKey = httpReq.getHeader("apiKey");
            RateLimiter limiter = rateLimiterRegistry.rateLimiter(apiKey);
            if (limiter.acquirePermission()) {
                StopWatch watch = new StopWatch();
                watch.start();
                chain.doFilter(request, response);
                watch.stop();
                log.info(String.format("Access from apiKey '%s' to '%s' (duration: %s seconds)", apiKey, path, watch.getTotalTimeSeconds()));
            } else {
                log.info(String.format("Limit Reached for API key '%s'. (Tried to access: '%s')", apiKey, path));
                ((HttpServletResponse) response).sendError(429);
            }
        }
    }
}
