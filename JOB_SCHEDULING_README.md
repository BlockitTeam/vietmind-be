# Job Scheduling System for Appointment Reminders

This document describes the job scheduling system implemented for appointment reminders in the VietMind application.

## Overview

The system provides automated email and push notification reminders for appointments:
- **Email reminder**: Sent 24 hours before appointment
- **Push notification**: Sent 1 hour before appointment

## Architecture

### Components

1. **ScheduledJob Entity**: Database model for storing job information
2. **JobSchedulerService**: Core service for managing job lifecycle
3. **EmailService**: SendGrid integration for email notifications
4. **PushNotificationService**: Firebase integration for push notifications
5. **AppointmentService Integration**: Automatic job creation/updates

### Database Schema

```sql
CREATE TABLE scheduled_jobs (
    job_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(255) NOT NULL,
    job_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    trigger_time DATETIME NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    job_data TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    executed_at DATETIME,
    error_message TEXT,
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3
);
```

## Job Types

- `appointment_mail_24h_reminder`: Email reminder 24 hours before
- `appointment_notification_1h`: Push notification 1 hour before

## Job Statuses

- `PENDING`: Job created, waiting to be scheduled
- `SCHEDULED`: Job scheduled and waiting for execution
- `EXECUTED`: Job completed successfully
- `FAILED`: Job failed (with retry capability)
- `CANCELLED`: Job cancelled

## Configuration

### Environment Variables

```bash
# SendGrid Configuration
SENDGRID_API_KEY=your-sendgrid-api-key
SENDGRID_FROM_EMAIL=noreply@vietmind.com
SENDGRID_FROM_NAME=VietMind

# Firebase Configuration
FIREBASE_CREDENTIALS_PATH=classpath:firebase-credentials.json
```

### Application Properties

```yaml
sendgrid:
  api:
    key: ${SENDGRID_API_KEY:your-sendgrid-api-key-here}
  from:
    email: ${SENDGRID_FROM_EMAIL:noreply@vietmind.com}
    name: ${SENDGRID_FROM_NAME:VietMind}

firebase:
  credentials:
    path: ${FIREBASE_CREDENTIALS_PATH:classpath:firebase-credentials.json}
```

## Usage

### Automatic Job Creation

Jobs are automatically created when:
1. A new appointment is created
2. An appointment's start time is updated

### Manual Job Management

```java
// Get jobs for an appointment
List<ScheduledJob> jobs = jobSchedulerService.getJobsByEntity("appointment", "123");

// Cancel all jobs for an appointment
jobSchedulerService.cancelJobsByEntity("appointment", "123");

// Update jobs when appointment time changes
LocalDateTime newTime = LocalDateTime.of(2024, 1, 15, 14, 0);
jobSchedulerService.updateJobsByEntity("appointment", "123", newTime);
```

### API Endpoints

- `GET /api/jobs/entity/{entityType}/{entityId}` - Get jobs for an entity
- `POST /api/jobs/retry-failed` - Retry failed jobs
- `POST /api/jobs/execute-pending` - Execute pending jobs manually

## Email Template

The system includes a beautiful HTML email template with:
- Responsive design
- VietMind branding
- Appointment details
- Important reminders
- Professional styling

## Push Notification Data

Push notifications include:
- Title and body text
- Appointment ID
- Hours before appointment
- Appointment date and time
- Custom data fields

## Error Handling

- Automatic retry for failed jobs (max 3 retries)
- Detailed error logging
- Job status tracking
- Manual retry capability

## Monitoring

The system runs a scheduled task every minute to:
1. Execute pending jobs
2. Retry failed jobs
3. Update job statuses

## Setup Instructions

1. **Add Dependencies**: Already added to `pom.xml`
2. **Database Migration**: Run migration `V11__Create_scheduled_jobs_table.sql`
3. **Configure Services**: Set environment variables
4. **Enable Scheduling**: `@EnableScheduling` annotation is configured
5. **Test Integration**: Create an appointment to test job creation

## Security Considerations

- API keys stored in environment variables
- Firebase credentials file should be secured
- Job data is stored as JSON in database
- User device tokens are encrypted

## Troubleshooting

### Common Issues

1. **Jobs not executing**: Check if scheduling is enabled
2. **Email not sending**: Verify SendGrid API key
3. **Push notifications failing**: Check Firebase configuration
4. **Database errors**: Ensure migration ran successfully

### Logs

Check application logs for:
- Job execution status
- Email sending results
- Push notification delivery
- Error messages and stack traces

## Future Enhancements

- Webhook support for job status updates
- Advanced retry strategies
- Job priority system
- Bulk operations
- Analytics and reporting dashboard
