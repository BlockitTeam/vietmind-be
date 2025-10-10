# Quartz Job Scheduling Integration

This document describes the Quartz integration for handling job scheduling in the VietMind application.

## Overview

The application now uses Quartz Scheduler instead of Spring's @Scheduled annotation for more powerful and flexible job scheduling capabilities.

## Architecture

### Components

1. **QuartzConfig**: Configuration for Quartz Scheduler
2. **AppointmentReminderJob**: Quartz Job implementation for appointment reminders
3. **QuartzJobManager**: Service for managing Quartz jobs
4. **JobSchedulerServiceImpl**: Updated to use Quartz instead of @Scheduled

### Key Features

- **Persistent Jobs**: Jobs survive server restarts
- **Dynamic Scheduling**: Create, update, and cancel jobs at runtime
- **Cron Support**: Support for cron expressions
- **Job Clustering**: Support for multiple server instances
- **Job Monitoring**: Track job execution status

## Usage

### 1. Automatic Job Creation

Jobs are automatically created when:
- A new appointment is created
- An appointment's start time is updated

### 2. Manual Job Management

```java
@Autowired
private QuartzJobManager quartzJobManager;

// Schedule a recurring job
quartzJobManager.scheduleRecurringJob("daily-cleanup", "0 0 2 * * ?");

// Cancel a specific job
quartzJobManager.cancelJob(jobId);

// Reschedule a job
quartzJobManager.rescheduleJob(jobId, newTriggerTime);
```

### 3. API Endpoints

- `POST /api/quartz/schedule-recurring` - Schedule recurring jobs
- `POST /api/quartz/start-scheduler` - Start the scheduler
- `POST /api/quartz/shutdown-scheduler` - Shutdown the scheduler

## Job Types

### Appointment Reminder Jobs

1. **24-hour Email Reminder**:
   - Job Type: `APPOINTMENT_MAIL_24H_REMINDER`
   - Trigger: 24 hours before appointment
   - Action: Send email notification

2. **1-hour Push Notification**:
   - Job Type: `APPOINTMENT_NOTIFICATION_1H`
   - Trigger: 1 hour before appointment
   - Action: Send push notification

## Configuration

### Quartz Properties

```yaml
spring:
  quartz:
    job-store-type: jdbc
    jdbc:
      initialize-schema: always
    properties:
      org.quartz.scheduler.instanceName: VietMindScheduler
      org.quartz.scheduler.instanceId: AUTO
      org.quartz.jobStore.class: org.quartz.impl.jdbcjobstore.JobStoreTX
      org.quartz.jobStore.driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
      org.quartz.jobStore.tablePrefix: QRTZ_
      org.quartz.jobStore.isClustered: true
      org.quartz.threadPool.threadCount: 10
```

### Database Tables

Quartz requires the following tables (auto-created):
- `qrtz_job_details`
- `qrtz_triggers`
- `qrtz_cron_triggers`
- `qrtz_simple_triggers`
- `qrtz_scheduler_state`

## Job Execution Flow

1. **Job Creation**: When appointment is created, jobs are scheduled in Quartz
2. **Job Storage**: Job details stored in database via Quartz
3. **Job Execution**: Quartz triggers job at scheduled time
4. **Job Processing**: AppointmentReminderJob executes the business logic
5. **Status Update**: Job status updated in ScheduledJob table

## Monitoring and Management

### Job Status Tracking

- **PENDING**: Job created, waiting to be scheduled
- **SCHEDULED**: Job scheduled in Quartz
- **EXECUTED**: Job completed successfully
- **FAILED**: Job failed (with retry capability)
- **CANCELLED**: Job cancelled

### Error Handling

- Automatic retry for failed jobs (max 3 retries)
- Detailed error logging
- Job status tracking
- Manual retry capability

## Benefits of Quartz Integration

### 1. **Persistence**
- Jobs survive server restarts
- Database-backed job storage
- Cluster-aware scheduling

### 2. **Flexibility**
- Dynamic job creation/updating
- Cron expression support
- Complex scheduling scenarios

### 3. **Reliability**
- Job clustering support
- Failover capabilities
- Transaction support

### 4. **Monitoring**
- Job execution tracking
- Performance metrics
- Error reporting

## Migration from @Scheduled

The system has been migrated from Spring's @Scheduled to Quartz:

### Before (Spring @Scheduled)
```java
@Scheduled(fixedRate = 60000)
public void executePendingJobs() {
    // Execute jobs every minute - POLLING approach
}
```

### After (Quartz)
```java
// Jobs are scheduled individually with specific trigger times - EVENT-DRIVEN approach
quartzJobManager.scheduleAppointmentReminder(scheduledJob);
// No need for polling - Quartz handles execution automatically
```

## Troubleshooting

### Common Issues

1. **Jobs not executing**: Check Quartz scheduler status
2. **Database connection**: Ensure Quartz tables are created
3. **Job conflicts**: Check for duplicate job keys
4. **Memory issues**: Monitor job queue size

### Debugging

```java
// Check scheduler status
scheduler.isStarted()

// List all jobs
scheduler.getJobGroupNames()

// Check specific job
scheduler.getJobDetail(jobKey)
```

## Performance Considerations

- **Thread Pool**: Configure appropriate thread count
- **Job Store**: Use JDBC job store for persistence
- **Clustering**: Enable clustering for high availability
- **Memory**: Monitor job queue and memory usage

## Security

- Jobs run with application context
- Database access through Spring Security
- No external job execution
- Audit logging for job operations

## Future Enhancements

- Web-based job management interface
- Job execution analytics
- Advanced scheduling rules
- Job dependency management
- Real-time job monitoring dashboard
