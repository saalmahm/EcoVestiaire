package ma.ecovestiaire.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateConversationRequest {

    // utilisateur avec qui je veux discuter
    @NotNull
    private Long targetUserId;

    // discussion autour d’un article précis
    private Long itemId;
}