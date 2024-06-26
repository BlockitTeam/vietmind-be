package com.vm.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.SessionCookieConfig;
import javax.servlet.ServletContext;

@Configuration
public class ServletConfig {

    @Bean
    public ServletContextInitializer cookieConfigurer() {
        return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) {
                SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
                sessionCookieConfig.setHttpOnly(true);
                sessionCookieConfig.setSecure(true);
                sessionCookieConfig.setPath("/");
                sessionCookieConfig.setMaxAge(7 * 24 * 60 * 60); // 7 days

                // Set SameSite=None
                sessionCookieConfig.setComment("SameSite=None; Secure");
            }
        };
    }
}
