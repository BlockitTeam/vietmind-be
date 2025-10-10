package com.vm.service;

import com.vm.model.User;
import com.vm.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PushNotificationServiceTest {

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a test user
        testUser = new User();
        testUser.setUsername("test@example.com");
        testUser.setPassword("password");
        testUser.setEnabled(true);
        testUser.setDeviceToken("test-device-token-123");
        testUser = userRepository.save(testUser);
    }

    @Test
    void testSendSimplePushNotification() {
        // This test will fail in actual execution without a real device token
        // but it tests the service method structure
        assertDoesNotThrow(() -> {
            pushNotificationService.sendSimplePushNotification(
                "test-device-token-123",
                "Test Title",
                "Test Body"
            );
        });
    }

    @Test
    void testSendPushNotificationWithData() {
        Map<String, String> data = new HashMap<>();
        data.put("type", "test");
        data.put("userId", testUser.getId().toString());

        // This test will fail in actual execution without a real device token
        // but it tests the service method structure
        assertDoesNotThrow(() -> {
            pushNotificationService.sendPushNotification(
                "test-device-token-123",
                "Test Title",
                "Test Body",
                data
            );
        });
    }

    @Test
    void testSendPushNotificationToMultipleDevices() {
        java.util.List<String> deviceTokens = java.util.Arrays.asList(
            "test-device-token-1",
            "test-device-token-2"
        );

        Map<String, String> data = new HashMap<>();
        data.put("type", "bulk_test");

        // This test will fail in actual execution without real device tokens
        // but it tests the service method structure
        assertDoesNotThrow(() -> {
            pushNotificationService.sendPushNotificationToMultipleDevices(
                deviceTokens,
                "Bulk Test Title",
                "Bulk Test Body",
                data
            );
        });
    }
}
