package com.vm.job;

import com.vm.model.ScheduledJob;
import com.vm.service.JobSchedulerService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentReminderJob implements Job {
    
    private static final Logger logger = LoggerFactory.getLogger(AppointmentReminderJob.class);
    
    @Autowired
    private JobSchedulerService jobSchedulerService;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            logger.info("Executing AppointmentReminderJob at: {}", context.getFireTime());
            
            // Get job data from context
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            Long jobId = jobDataMap.getLongValue("jobId");
            
            if (jobId != null) {
                // Execute the specific job
                jobSchedulerService.executeJobById(jobId);
            } else {
                // No need to execute all pending jobs - Quartz handles this automatically
                logger.info("No specific job ID provided, Quartz will handle execution automatically");
            }
            
            logger.info("AppointmentReminderJob completed successfully");
            
        } catch (Exception e) {
            logger.error("Error executing AppointmentReminderJob", e);
            throw new JobExecutionException("Failed to execute appointment reminder job", e);
        }
    }
}
