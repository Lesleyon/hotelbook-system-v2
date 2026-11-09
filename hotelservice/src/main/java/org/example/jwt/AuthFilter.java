package org.example.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class AuthFilter implements Filter {
    private final JWTservice jwtService;
    public AuthFilter(JWTservice jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        if (path.contains("/user/register") || path.contains("/user/auth") || path.contains("/api/rooms/")) {
            chain.doFilter(request, response);
            return;
        }
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.isValid(token)) {
                String username = jwtService.extractUsername(token);
                String role = jwtService.extractRole(token);
                req.setAttribute("username", username);
                req.setAttribute("role", role);
                chain.doFilter(request, response);
                return;
            }
        }
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}