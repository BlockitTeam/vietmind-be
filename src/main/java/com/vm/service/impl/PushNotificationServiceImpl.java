package com.vm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.vm.model.Appointment;
import com.vm.model.User;
import com.vm.service.PushNotificationService;
import com.vm.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Service
public class PushNotificationServiceImpl implements PushNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public void sendAppointmentReminderNotification(User user, Appointment appointment, int hoursBefore) {
        String title = hoursBefore <= 0 ? "Bạn vừa có 1 cuộc hẹn mới!" : String.format("Cuộc hẹn sẽ diễn ra trong %d giờ tới", hoursBefore);
        String body = String.format("Bạn có cuộc hẹn vào %s lúc %s", 
            appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            appointment.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        );

        var doctorDetail = userService.getDoctorById(appointment.getDoctorId()).get();
        
        // Create doctor detail map
        Map<String, Object> doctorInfo = new HashMap<>();
        doctorInfo.put("doctorId", doctorDetail.getId().toString());
        doctorInfo.put("doctorName", doctorDetail.getFullName());
        if (doctorDetail.getWorkplace() != null) {
            doctorInfo.put("workplace", doctorDetail.getWorkplace());
        }
        if (doctorDetail.getDegree() != null) {
            doctorInfo.put("degree", doctorDetail.getDegree());
        }
        if (doctorDetail.getSpecializations() != null) {
            doctorInfo.put("specializations", doctorDetail.getSpecializations());
        }
        
        // Convert doctor info to JSON string
        String doctorDetailJson;
        try {
            doctorDetailJson = objectMapper.writeValueAsString(doctorInfo);
        } catch (Exception e) {
            logger.error("Failed to serialize doctor detail to JSON", e);
            doctorDetailJson = "{}";
        }
        
        // Create notification data
        Map<String, String> data = new HashMap<>();
        data.put("type", "appointment_reminder");
        data.put("appointmentId", appointment.getAppointmentId().toString());
        data.put("appointmentDate", appointment.getAppointmentDate().toString());
        data.put("doctorId", appointment.getDoctorId());
        data.put("startTime", appointment.getStartTime().toString());
        data.put("hoursBefore", String.valueOf(hoursBefore));
        data.put("doctorDetail", doctorDetailJson);
        
        // Get device token from user
        String deviceToken = user.getDeviceToken();
        
        if (deviceToken != null && !deviceToken.isEmpty()) {
            sendPushNotification(deviceToken, title, body, data);
            logger.info("Appointment reminder notification sent to user: {} for appointment: {}", 
                user.getUserId(), appointment.getAppointmentId());
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
            if (data != null) {
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    messageBuilder.putData(entry.getKey(), entry.getValue());
                }
            }
            
            Message message = messageBuilder.build();
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Push notification sent successfully to device: {}. Response: {}", deviceToken, response);
            
        } catch (FirebaseMessagingException e) {
            logger.error("Failed to send push notification to device: {}", deviceToken, e);
            throw new RuntimeException("Failed to send push notification", e);
        }
    }
    
    /**
     * Send a simple push notification without data
     */
    public void sendSimplePushNotification(String deviceToken, String title, String body) {
        try {
            Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .build();
            
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Simple push notification sent successfully to device: {}. Response: {}", deviceToken, response);
            
        } catch (FirebaseMessagingException e) {
            logger.error("Failed to send simple push notification to device: {}", deviceToken, e);
            throw new RuntimeException("Failed to send push notification", e);
        }
    }
    
    /**
     * Send push notification to multiple devices
     */
    public void sendPushNotificationToMultipleDevices(java.util.List<String> deviceTokens, String title, String body, Map<String, String> data) {
        if (deviceTokens == null || deviceTokens.isEmpty()) {
            logger.warn("No device tokens provided for bulk notification");
            return;
        }
        
        for (String deviceToken : deviceTokens) {
            try {
                sendPushNotification(deviceToken, title, body, data);
            } catch (Exception e) {
                logger.error("Failed to send notification to device: {}", deviceToken, e);
                // Continue with other devices even if one fails
            }
        }
    }
}
