package com.vm.service.impl;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.SendRawEmailResponse;
import com.vm.model.Appointment;
import com.vm.model.User;
import com.vm.service.EmailService;
import com.vm.service.EmailTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
        
        sendEmailWithImage(user.getEmail(), subject, htmlContent);
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
    
    /**
     * Send email with embedded image (header image)
     */
    private void sendEmailWithImage(String to, String subject, String htmlContent) {
        try (SesClient sesClient = SesClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build()) {

            // Create MIME message
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage mimeMessage = new MimeMessage(session);
            
            mimeMessage.setFrom(new InternetAddress(fromEmail, fromName != null ? fromName : ""));
            mimeMessage.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(to));
            mimeMessage.setSubject(subject, "UTF-8");

            // Create multipart message
            MimeMultipart multipart = new MimeMultipart("related");

            // HTML body part
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            // Image attachment part (header image)
            try {
                ClassPathResource imageResource = new ClassPathResource(
                        "templates/email/images/header.jpg");
                if (imageResource.exists()) {
                    MimeBodyPart imagePart = new MimeBodyPart();
                    InputStream imageInputStream = imageResource.getInputStream();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int nRead;
                    while ((nRead = imageInputStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    byte[] imageBytes = buffer.toByteArray();
                    imageInputStream.close();
                    
                    imagePart.setContent(imageBytes, "image/jpeg");
                    imagePart.setContentID("<headerImage>");
                    imagePart.setDisposition(MimeBodyPart.INLINE);
                    imagePart.setHeader("Content-ID", "<headerImage>");
                    multipart.addBodyPart(imagePart);
                } else {
                    logger.warn("Header image not found at templates/email/images/header.jpg");
                }
            } catch (IOException e) {
                logger.error("Failed to load header image", e);
            }

            mimeMessage.setContent(multipart);

            // Convert MimeMessage to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mimeMessage.writeTo(outputStream);
            byte[] rawMessageBytes = outputStream.toByteArray();

            // Send raw email
            RawMessage rawMessage = RawMessage.builder()
                    .data(SdkBytes.fromByteArray(rawMessageBytes))
                    .build();

            SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
                    .destinations(to)
                    .rawMessage(rawMessage)
                    .source(fromEmail)
                    .build();

            SendRawEmailResponse response = sesClient.sendRawEmail(rawEmailRequest);
            logger.info("Email with image sent successfully to: {}. SES MessageId: {}", to, response.messageId());

        } catch (SdkException | MessagingException | IOException e) {
            logger.error("Failed to send email with image via SES to: {}", to, e);
            // Fallback to regular email without image
            logger.info("Falling back to regular email without image");
            sendEmail(to, subject, htmlContent);
        }
    }
    
}
