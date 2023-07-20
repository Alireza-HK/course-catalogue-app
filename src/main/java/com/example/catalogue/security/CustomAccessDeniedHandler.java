package com.example.catalogue.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());

        authentication.ifPresent(auth -> log.warn("User: {} attempted to access the protected URL: {} [Method: {}]",
                auth.getName(), request.getRequestURI(), request.getMethod()));


        if (isRestApiRequest(request)) {
            writeJsonResponse(response, HttpServletResponse.SC_FORBIDDEN, "Access denied");
        } else {
            response.sendRedirect(request.getContextPath() + "/accessDenied");
        }
    }

    private boolean isRestApiRequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains("application/json");
    }

    private void writeJsonResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        String jsonResponse = """
                {
                    "error": "Forbidden",
                    "message": "%s"
                }
                """.formatted(message);
        response.getWriter().write(jsonResponse);
    }
}
