package ma.ecovestiaire.backend.dto;

import lombok.Data;

@Data
public class UserSummaryResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String profilePhotoUrl;
    private String location;
}