package com.MediConnect.MediConnect.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.MediConnect.MediConnect.Model.User;
import com.MediConnect.MediConnect.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Skip filter for non-protected user endpoints
        if (requestURI.equals("/api/user/register") || requestURI.equals("/api/user/login") || requestURI.startsWith("/api/doctor/") || requestURI.startsWith("/api/admin/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = request.getHeader("token");
            if (token == null || token.isEmpty()) {
                sendErrorResponse(response, "Not Authorized Login Again", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String userId = jwtUtil.validateToken(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid token: User not found"));


            request.setAttribute("userId", userId);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
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