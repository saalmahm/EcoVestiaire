package ma.ecovestiaire.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationSummaryResponse {

    private Long id;

    // informations sur l'autre participant
    private Long otherUserId;
    private String otherUserFirstName;
    private String otherUserLastName;
    private String otherUserProfilePhotoUrl;

    // informations sur l'article lié 
    private Long itemId;
    private String itemTitle;
}