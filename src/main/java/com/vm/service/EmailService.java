package com.vm.service;

import com.vm.model.Appointment;
import com.vm.model.User;

public interface EmailService {
    void sendAppointmentReminderEmail(User user, Appointment appointment, int hoursBefore);
    void sendGeneralNotificationEmail(String to, String subject, String title, String message);
    void sendEmail(String to, String subject, String htmlContent);
}
