package com.vm.request;

import com.vm.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorUserRequest {
    private String firstName;
    private String lastName;

    private Integer birthYear;
    private Gender gender;

    private String workplace;
    private String degree;
    private String specializations;
}
