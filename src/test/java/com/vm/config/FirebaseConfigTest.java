package com.vm.config;

import com.google.firebase.FirebaseApp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FirebaseConfigTest {

    @Test
    void testFirebaseInitialization() {
        // Check if Firebase is initialized
        assertFalse(FirebaseApp.getApps().isEmpty(), "Firebase should be initialized");
        
        // Get the default app
        FirebaseApp app = FirebaseApp.getInstance();
        assertNotNull(app, "Firebase app should not be null");
        
        // Check if the app name is correct
        assertEquals("[DEFAULT]", app.getName(), "Firebase app name should be [DEFAULT]");
    }
}
