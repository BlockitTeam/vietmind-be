package com.vm.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vm.model.User;
import com.vm.repo.UserRepository;
import com.vm.request.DeviceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class DeviceControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testRegisterDeviceToken() throws Exception {
        // Create a test user first
        User user = new User();
        user.setUsername("test@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        user = userRepository.save(user);

        DeviceRequest request = new DeviceRequest();
        request.setDeviceToken("test-firebase-token-123");

        mockMvc.perform(post("/api/v1/device/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Device token registered successfully"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetDeviceToken() throws Exception {
        // Create a test user with device token
        User user = new User();
        user.setUsername("test@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        user.setDeviceToken("test-firebase-token-123");
        user = userRepository.save(user);

        mockMvc.perform(get("/api/v1/device/token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("test-firebase-token-123"))
                .andExpect(jsonPath("$.message").value("Device token retrieved successfully"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testRemoveDeviceToken() throws Exception {
        // Create a test user with device token
        User user = new User();
        user.setUsername("test@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        user.setDeviceToken("test-firebase-token-123");
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/v1/device/token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Device token removed successfully"));
    }
}
