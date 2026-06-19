package com.SmartHealthRemoteSystem.SHSR.WebConfiguration;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class SuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String redirectUrl = null;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String authority = grantedAuthority.getAuthority();
            if (authority.equals("ROLE_DOCTOR")) {
                redirectUrl = "/doctor";
                break;
            } else if (authority.equals("ROLE_PATIENT")) {
                redirectUrl = "/patient";
                break;
            } else if (authority.equals("ROLE_PHARMACIST")) {
                redirectUrl = "/pharmacist";
                break;
            } else if (authority.equals("ROLE_CLINIC_ASSISTANT")) {
                redirectUrl = "/clinicassistant";
                break;
            } else if (authority.equals("ROLE_RADIOGRAPHER")) {
                redirectUrl = "/radiographer";
                break;
            } else if (authority.equals("ROLE_RADIOLOGIST")) {
                redirectUrl = "/radiologist";
                break;
            } else if (authority.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin";
                break;
            }
        }
        if (redirectUrl == null) {
            throw new IllegalStateException();
        }
        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
