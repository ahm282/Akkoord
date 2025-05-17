package com.ahm282.Akkoord.dto.response;

import com.ahm282.Akkoord.model.enums.UserAccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private String fullName;
    private String email;
    private UserAccessLevel accessLevel;
//    private UUID departmentId;
//    private String departmentName;
}