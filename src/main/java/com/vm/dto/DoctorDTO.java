package com.vm.dto;

import com.vm.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DoctorDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private Integer birthYear;
    private Gender gender;
    private Integer conversationId;
}
