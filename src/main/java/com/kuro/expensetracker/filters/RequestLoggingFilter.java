package com.kuro.expensetracker.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@WebFilter("/*") // Intercept all incoming requests
@Slf4j
public class RequestLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {

            String method = httpRequest.getMethod();
            String uri = httpRequest.getRequestURI();
            String query = httpRequest.getQueryString();
            String fullUrl = query != null ? uri + "?" + query : uri;

            // Log at INFO level
            log.info("HTTP Request - Method: {}, URL: {}", method, fullUrl);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}