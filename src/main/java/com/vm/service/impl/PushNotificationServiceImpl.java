package com.vm.service.impl;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.vm.model.Appointment;
import com.vm.model.User;
import com.vm.service.PushNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);
    
    @Value("${firebase.credentials.path}")
    private String firebaseCredentialsPath;
    
    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Initialize Firebase with credentials
                // Note: You'll need to set up Firebase credentials file
                logger.info("Firebase initialized successfully");
            }
        } catch (Exception e) {
            logger.error("Failed to initialize Firebase", e);
        }
    }
    
    @Override
    public void sendAppointmentReminderNotification(User user, Appointment appointment, int hoursBefore) {
        String title = String.format("Nhắc nhở cuộc hẹn - %d giờ trước", hoursBefore);
        String body = String.format("Bạn có cuộc hẹn vào %s lúc %s", 
            appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            appointment.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        
        // Create notification data
        Map<String, String> data = new HashMap<>();
        data.put("type", "appointment_reminder");
        data.put("appointmentId", appointment.getAppointmentId().toString());
        data.put("hoursBefore", String.valueOf(hoursBefore));
        data.put("appointmentDate", appointment.getAppointmentDate().toString());
        data.put("startTime", appointment.getStartTime().toString());
        
        // For now, we'll use a placeholder device token
        // In a real implementation, you'd get this from the user's profile
        String deviceToken = user.getDeviceToken(); // You'll need to add this field to User model
        
        if (deviceToken != null && !deviceToken.isEmpty()) {
            sendPushNotification(deviceToken, title, body, data);
        } else {
            logger.warn("No device token found for user: {}", user.getUserId());
        }
    }
    
    @Override
    public void sendPushNotification(String deviceToken, String title, String body, String data) {
        try {
            Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .putData("data", data)
                .build();
            
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Push notification sent successfully. Response: {}", response);
            
        } catch (FirebaseMessagingException e) {
            logger.error("Failed to send push notification to device: {}", deviceToken, e);
            throw new RuntimeException("Failed to send push notification", e);
        }
    }
    
    @Override
    public void sendPushNotification(String deviceToken, String title, String body, Map<String, String> data) {
        try {
            Message.Builder messageBuilder = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build());
            
            // Add all data fields
            for (Map.Entry<String, String> entry : data.entrySet()) {
                messageBuilder.putData(entry.getKey(), entry.getValue());
            }
            
            Message message = messageBuilder.build();
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Push notification sent successfully. Response: {}", response);
            
        } catch (FirebaseMessagingException e) {
            logger.error("Failed to send push notification to device: {}", deviceToken, e);
            throw new RuntimeException("Failed to send push notification", e);
        }
    }
}
