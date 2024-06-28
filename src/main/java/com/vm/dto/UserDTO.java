package com.vm.dto;

import com.vm.model.Gender;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDTO {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private Integer birthYear;
    private Gender gender;
    private Integer conversationId;
}
