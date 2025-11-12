package com.vm.service.impl;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SesException;
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
    
    @Value("${aws.ses.region}")
    private String awsRegion;

    @Value("${aws.ses.from.email}")
    private String fromEmail;

    @Value("${aws.ses.from.name}")
    private String fromName;

    @Value("${aws.ses.accessKeyId:}")
    private String accessKeyId;

    @Value("${aws.ses.secretAccessKey:}")
    private String secretAccessKey;
    
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
        try (SesClient sesClient = SesClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build()) {

            Destination destination = Destination.builder()
                    .toAddresses(to)
                    .build();

            Content subj = Content.builder().data(subject).build();
            Content html = Content.builder().data(htmlContent).build();
            Body body = Body.builder().html(html).build();
            Message message = Message.builder().subject(subj).body(body).build();

            // If you want a friendly name, use the format: "Name <email@domain>"
            String source = fromName != null && !fromName.isEmpty() ? (fromName + " <" + fromEmail + ">") : fromEmail;

            SendEmailRequest request = SendEmailRequest.builder()
                    .destination(destination)
                    .message(message)
                    .source(source)
                    .build();

            SendEmailResponse response = sesClient.sendEmail(request);
            logger.info("Email sent successfully to: {}. SES MessageId: {}", to, response.messageId());

        } catch (SdkException e) {
            logger.error("Failed to send email via SES to: {}", to, e);
            throw new RuntimeException("Failed to send email via SES", e);
        }
    }
    
}
