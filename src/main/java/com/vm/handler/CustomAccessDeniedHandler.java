package com.vm.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // Clear the JSESSIONID cookie
        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", null)
                .path("/")
                .httpOnly(true)
                .maxAge(0)  // Set the max age to 0 to delete the cookie
//                .secure(true)  // Set this according to your needs, usually true for HTTPS
//                .sameSite("Lax")  // Set the same site policy as required
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        log.info("Clear COOKIES due to call API forbidden ");
        // Send the access denied response
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied base on custom rule");
    }
}
