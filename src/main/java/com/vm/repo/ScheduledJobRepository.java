package com.vm.repo;

import com.vm.enums.JobStatus;
import com.vm.enums.JobType;
import com.vm.model.ScheduledJob;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {
    
    List<ScheduledJob> findByStatusAndTriggerTimeLessThanEqual(JobStatus status, LocalDateTime triggerTime);
    
    List<ScheduledJob> findByEntityTypeAndEntityId(String entityType, String entityId);
    
    List<ScheduledJob> findByEntityTypeAndEntityIdAndType(String entityType, String entityId, JobType type);
    
    @Query("SELECT j FROM ScheduledJob j WHERE j.status IN ('PENDING', 'SCHEDULED') AND j.triggerTime <= :currentTime")
    List<ScheduledJob> findJobsToExecute(@Param("currentTime") Instant currentTime);
    
    @Query("SELECT j FROM ScheduledJob j WHERE j.entityType = :entityType AND j.entityId = :entityId AND j.status IN ('PENDING', 'SCHEDULED')")
    List<ScheduledJob> findActiveJobsByEntity(@Param("entityType") String entityType, @Param("entityId") String entityId);
    
    Optional<ScheduledJob> findByName(String name);
    
    List<ScheduledJob> findByStatus(JobStatus status);
}
