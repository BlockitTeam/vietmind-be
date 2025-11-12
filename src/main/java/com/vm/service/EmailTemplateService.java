package com.vm.service;

import com.vm.model.Appointment;
import com.vm.model.User;

import java.util.Map;

public interface EmailTemplateService {
    String generateAppointmentReminderHtml(User user, Appointment appointment, int hoursBefore);
    String generateGeneralNotificationHtml(String title, String message);
    String loadTemplate(String templateName);
    String processTemplate(String template, Map<String, String> variables);
}
