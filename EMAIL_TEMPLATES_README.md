# Email Template System

This document describes the email template system implemented for the VietMind application.

## Overview

The email template system provides a flexible and maintainable way to generate HTML emails using external template files instead of hardcoded HTML strings.

## Template Structure

### Template Location
All email templates are stored in: `src/main/resources/templates/email/`

### Available Templates

1. **appointment-reminder-24h.html** - 24-hour appointment reminder
2. **appointment-reminder-1h.html** - 1-hour appointment reminder  
3. **general-notification.html** - General notification emails

## Template Variables

### Appointment Reminder Templates

| Variable | Description | Example |
|----------|-------------|---------|
| `{{userName}}` | User's full name | "Nguyễn Văn A" |
| `{{appointmentDate}}` | Appointment date | "15/01/2024" |
| `{{startTime}}` | Start time | "14:00" |
| `{{endTime}}` | End time | "15:00" |
| `{{appointmentContent}}` | Appointment content | "Khám tổng quát" |
| `{{hoursBefore}}` | Hours before appointment | "24" or "1" |

### General Notification Template

| Variable | Description | Example |
|----------|-------------|---------|
| `{{title}}` | Email title | "Thông báo quan trọng" |
| `{{message}}` | Email message | "Cuộc hẹn đã được xác nhận" |

## Services

### EmailTemplateService

**Interface**: `com.vm.service.EmailTemplateService`
**Implementation**: `com.vm.service.impl.EmailTemplateServiceImpl`

#### Methods

```java
// Generate appointment reminder HTML
String generateAppointmentReminderHtml(User user, Appointment appointment, int hoursBefore);

// Load template from file
String loadTemplate(String templateName);

// Process template with variables
String processTemplate(String template, Map<String, String> variables);

// Generate general notification HTML
String generateGeneralNotificationHtml(String title, String message);
```

### EmailService

**Interface**: `com.vm.service.EmailService`
**Implementation**: `com.vm.service.impl.EmailServiceImpl`

#### Methods

```java
// Send appointment reminder email
void sendAppointmentReminderEmail(User user, Appointment appointment, int hoursBefore);

// Send general notification email
void sendGeneralNotificationEmail(String to, String subject, String title, String message);

// Send custom HTML email
void sendEmail(String to, String subject, String htmlContent);
```

## Usage Examples

### Sending Appointment Reminder

```java
@Autowired
private EmailService emailService;

// Send 24-hour reminder
emailService.sendAppointmentReminderEmail(user, appointment, 24);

// Send 1-hour reminder
emailService.sendAppointmentReminderEmail(user, appointment, 1);
```

### Sending General Notification

```java
@Autowired
private EmailService emailService;

emailService.sendGeneralNotificationEmail(
    "user@example.com",
    "Thông báo cuộc hẹn",
    "Cuộc hẹn đã được xác nhận",
    "Cuộc hẹn của bạn vào ngày 15/01/2024 đã được xác nhận thành công."
);
```

### Using Template Service Directly

```java
@Autowired
private EmailTemplateService emailTemplateService;

// Load a template
String template = emailTemplateService.loadTemplate("general-notification.html");

// Process with variables
Map<String, String> variables = new HashMap<>();
variables.put("title", "Test Title");
variables.put("message", "Test Message");
String html = emailTemplateService.processTemplate(template, variables);
```

## Template Development

### Creating New Templates

1. Create HTML file in `src/main/resources/templates/email/`
2. Use `{{variableName}}` syntax for placeholders
3. Follow existing template structure and styling
4. Test with different variable values

### Template Best Practices

- Use responsive CSS for mobile compatibility
- Include VietMind branding and colors
- Keep templates clean and maintainable
- Use semantic HTML structure
- Include proper fallbacks for missing variables

### Styling Guidelines

- **Primary Color**: #4CAF50 (Green)
- **Secondary Color**: #FF5722 (Orange/Red for urgent)
- **Background**: #f4f4f4
- **Container**: White with rounded corners and shadow
- **Typography**: Segoe UI font family
- **Responsive**: Mobile-first approach

## Error Handling

The template system includes comprehensive error handling:

- **Template Loading Errors**: Logged and wrapped in RuntimeException
- **Variable Processing**: Null values are replaced with empty strings
- **File Not Found**: Clear error messages with template name

## Configuration

No additional configuration is required. Templates are automatically loaded from the classpath.

## Testing

### Unit Testing Templates

```java
@Test
public void testAppointmentReminderTemplate() {
    // Create test data
    User user = new User();
    user.setFullName("Test User");
    
    Appointment appointment = new Appointment();
    appointment.setAppointmentDate(LocalDate.of(2024, 1, 15));
    appointment.setStartTime(LocalTime.of(14, 0));
    appointment.setEndTime(LocalTime.of(15, 0));
    appointment.setContent("Test appointment");
    
    // Generate HTML
    String html = emailTemplateService.generateAppointmentReminderHtml(user, appointment, 24);
    
    // Assertions
    assertThat(html).contains("Test User");
    assertThat(html).contains("15/01/2024");
    assertThat(html).contains("14:00");
}
```

## Maintenance

### Adding New Variables

1. Update template files with new `{{variableName}}` placeholders
2. Update `createTemplateVariables()` method in `EmailTemplateServiceImpl`
3. Update documentation
4. Add unit tests

### Modifying Existing Templates

1. Edit HTML files directly
2. Test with different data scenarios
3. Ensure backward compatibility
4. Update documentation if needed

## Performance Considerations

- Templates are loaded once and cached by Spring
- Variable processing is lightweight
- No database queries during template processing
- Efficient string replacement using StringBuilder internally

## Security Considerations

- All user input is properly escaped in templates
- No script injection vulnerabilities
- Safe HTML generation
- Template files are read-only from classpath
