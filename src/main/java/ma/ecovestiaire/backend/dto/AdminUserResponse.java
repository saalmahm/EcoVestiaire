package ma.ecovestiaire.backend.dto;

import lombok.Data;
import ma.ecovestiaire.backend.enums.UserStatus;

import java.time.Instant;
import java.util.Set;

@Data
public class AdminUserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean deleted;
    private UserStatus status;
    private Set<String> roles;
    private Instant createdAt;
}