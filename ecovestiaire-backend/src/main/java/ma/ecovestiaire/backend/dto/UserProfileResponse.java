package ma.ecovestiaire.backend.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private String profilePhotoUrl;
    private String location;
    private String role;
}