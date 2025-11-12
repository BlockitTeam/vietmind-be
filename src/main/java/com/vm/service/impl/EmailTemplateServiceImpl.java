package com.vm.service.impl;

import com.vm.model.Appointment;
import com.vm.model.User;
import com.vm.service.EmailTemplateService;
import com.vm.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(EmailTemplateServiceImpl.class);
    private final UserService userService;


    @Override
    public String generateAppointmentReminderHtml(User user, Appointment appointment, int hoursBefore) {
        String templateName = hoursBefore == 24 ? "appointment-reminder-24h.html" : "appointment-reminder-1h.html";
        String template = loadTemplate(templateName);

        Map<String, String> variables = createTemplateVariables(user, appointment, hoursBefore);
        return processTemplate(template, variables);
    }

    public String generateGeneralNotificationHtml(String title, String message) {
        String template = loadTemplate("general-notification.html");

        Map<String, String> variables = new HashMap<>();
        variables.put("title", title);
        variables.put("message", message);

        return processTemplate(template, variables);
    }

    @Override
    public String loadTemplate(String templateName) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email/" + templateName);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Failed to load email template: {}", templateName, e);
            throw new RuntimeException("Failed to load email template: " + templateName, e);
        }
    }

    @Override
    public String processTemplate(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, entry.getValue() != null ? entry.getValue() : "");
        }
        return result;
    }

    private Map<String, String> createTemplateVariables(User user, Appointment appointment, int hoursBefore) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        var doctorDetails = userService.getDoctorById(appointment.getDoctorId()).orElse(null);

        Map<String, String> variables = new HashMap<>();
        variables.put("userName", user.getFullName() != null ? user.getFullName() : "Bạn");
        variables.put("appointmentDate", appointment.getAppointmentDate().format(dateFormatter));
        variables.put("startTime", appointment.getStartTime().format(timeFormatter));
        variables.put("endTime", appointment.getEndTime().format(timeFormatter));
        variables.put("appointmentContent", appointment.getContent() != null ? appointment.getContent() : "Không có nội dung chi tiết");
        variables.put("hoursBefore", String.valueOf(hoursBefore));
        variables.put("doctorName", String.valueOf(Objects.nonNull(doctorDetails) ? doctorDetails.getFullName() : "Doctor"));

        return variables;
    }
}
