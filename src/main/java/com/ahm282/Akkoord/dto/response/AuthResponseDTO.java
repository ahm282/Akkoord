package com.ahm282.Akkoord.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private UUID id;
    private String email;
    private String fullName;
    private List<String> roles;
}
