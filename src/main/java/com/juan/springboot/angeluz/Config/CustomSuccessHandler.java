package com.juan.springboot.angeluz.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String rol = authentication.getAuthorities().iterator().next().getAuthority();

        if (rol.equals("ROLE_ADMIN")) {
            response.sendRedirect("/admin/home");
        } else if (rol.equals("ROLE_MODERATOR")) {
            response.sendRedirect("/moderador/home");
        } else {
            response.sendRedirect("/index");
        }
    }
}