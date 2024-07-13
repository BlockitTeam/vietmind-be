package com.vm.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // Clear the JSESSIONID cookie
        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", null)
                .path("/")
                .httpOnly(true)
                .maxAge(0)  // Set the max age to 0 to delete the cookie
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        log.info("Clear COOKIES due to call with cookies invalid");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
