package com.vm.dto;

import com.vm.model.Availability;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class AvailabilityDTO {
    private Long id;
    private String userId;
    private Integer dayOfWeek;
    private Integer shiftNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private String date;

    // Constructors, Getters, Setters
    public AvailabilityDTO(Availability availability, String dateStr) {
        this.id = availability.getId();
        this.userId = availability.getUserId();
        this.dayOfWeek = availability.getDayOfWeek();
        this.shiftNumber = availability.getShiftNumber();
        this.startTime = availability.getStartTime();
        this.endTime = availability.getEndTime();
        this.date = dateStr;
    }
}