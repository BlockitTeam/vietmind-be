package com.vm.dto;

import com.vm.enums.Gender;
import com.vm.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserDTO {
    private UUID id;
    private String username;

    private boolean enabled;
    private boolean surveyCompleted;
    private Integer surveyDetail;

    private String firstName;
    private String lastName;

    private Integer birthYear;
    private Gender gender;
    private Set<Role> roles;
}
