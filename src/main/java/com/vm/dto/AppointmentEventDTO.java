package com.vm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentEventDTO {
    private String id;
    private String title;
    private String start; // Định dạng "YYYY-MM-DDTHH:mm"
    private String end;   // Định dạng "YYYY-MM-DDTHH:mm"
}
