package com.vm.service;

import com.vm.model.Appointment;
import com.vm.model.User;

import java.util.List;
import java.util.Map;

public interface PushNotificationService {
    void sendAppointmentReminderNotification(User user, Appointment appointment, int hoursBefore);
    void sendPushNotification(String deviceToken, String title, String body, String data);
    void sendPushNotification(String deviceToken, String title, String body, Map<String, String> data);
    void sendSimplePushNotification(String deviceToken, String title, String body);
    void sendPushNotificationToMultipleDevices(List<String> deviceTokens, String title, String body, Map<String, String> data);
}
