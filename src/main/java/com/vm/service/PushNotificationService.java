package com.vm.service;

import com.vm.model.Appointment;
import com.vm.model.User;

import java.util.Map;

public interface PushNotificationService {
    void sendAppointmentReminderNotification(User user, Appointment appointment, int hoursBefore);
    void sendPushNotification(String deviceToken, String title, String body, String data);
    void sendPushNotification(String deviceToken, String title, String body, Map<String, String> data);
}
