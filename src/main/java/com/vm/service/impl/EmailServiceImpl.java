package com.vm.service.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.vm.model.Appointment;
import com.vm.model.User;
import com.vm.service.EmailService;
import com.vm.service.EmailTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;
    
    @Value("${sendgrid.from.email}")
    private String fromEmail;
    
    @Value("${sendgrid.from.name}")
    private String fromName;
    
    @Autowired
    private EmailTemplateService emailTemplateService;
    
    @Override
    public void sendAppointmentReminderEmail(User user, Appointment appointment, int hoursBefore) {
        String subject = String.format("Nhắc nhở cuộc hẹn - %d giờ trước", hoursBefore);
        String htmlContent = emailTemplateService.generateAppointmentReminderHtml(user, appointment, hoursBefore);
        
        sendEmail(user.getEmail(), subject, htmlContent);
    }
    
    @Override
    public void sendGeneralNotificationEmail(String to, String subject, String title, String message) {
        String htmlContent = emailTemplateService.generateGeneralNotificationHtml(title, message);
        sendEmail(to, subject, htmlContent);
    }
    
    @Override
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            Email from = new Email(fromEmail, fromName);
            Email toEmail = new Email(to);
            Content content = new Content("text/html", htmlContent);
            Mail mail = new Mail(from, subject, toEmail, content);
            
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                logger.info("Email sent successfully to: {}", to);
            } else {
                logger.error("Failed to send email to: {}, Status: {}, Body: {}", 
                    to, response.getStatusCode(), response.getBody());
            }
            
        } catch (Exception e) {
            logger.error("Error sending email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
}
