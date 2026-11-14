package com.MediConnect.MediConnect.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/admin/login") ||
                requestURI.startsWith("/api/doctor/") ||
                requestURI.startsWith("/api/user/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = request.getHeader("atoken");
            if (token == null || token.isEmpty()) {
                sendErrorResponse(response, "Missing atoken header", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String subject = jwtUtil.validateToken(token);
            if (!subject.equals(adminEmail + adminPassword)) {
                sendErrorResponse(response, "Invalid token: subject does not match", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            sendErrorResponse(response, "Token validation failed: " + e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}