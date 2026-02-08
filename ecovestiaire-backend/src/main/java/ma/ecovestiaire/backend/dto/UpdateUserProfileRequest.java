package ma.ecovestiaire.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Size(max = 500)
    private String bio;

    @Size(max = 150)
    private String location;

    @Size(max = 255)
    private String profilePhotoUrl;
}