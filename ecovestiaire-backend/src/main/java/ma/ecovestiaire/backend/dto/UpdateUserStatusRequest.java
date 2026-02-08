package ma.ecovestiaire.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ma.ecovestiaire.backend.enums.UserStatus;

@Data
public class UpdateUserStatusRequest {

    @NotNull
    private UserStatus status;
}