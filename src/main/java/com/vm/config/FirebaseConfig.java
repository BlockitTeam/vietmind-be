package com.vm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    
    @Value("${firebase.credentials.path:classpath:firebase-credentials.json}")
    private String firebaseCredentialsPath;
    
    @PostConstruct
    public void initializeFirebase() {
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                logger.info("Initializing Firebase...");
                
                InputStream serviceAccount;
                
                // Try to load from classpath first, then from file system
                if (firebaseCredentialsPath.startsWith("classpath:")) {
                    String resourcePath = firebaseCredentialsPath.substring("classpath:".length());
                    serviceAccount = getClass().getClassLoader().getResourceAsStream(resourcePath);
                    if (serviceAccount == null) {
                        throw new IOException("Firebase service account file not found in classpath: " + resourcePath);
                    }
                } else {
                    serviceAccount = new FileInputStream(firebaseCredentialsPath);
                }
                
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
                
                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully");
                
                serviceAccount.close();
            } else {
                logger.info("Firebase already initialized");
            }
            
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Firebase", e);
        } catch (Exception e) {
            logger.error("Unexpected error during Firebase initialization: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}
