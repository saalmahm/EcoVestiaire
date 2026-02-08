package ma.ecovestiaire.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

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

    // informations sur le dernier message
    private String lastMessageContent;
    private Instant lastMessageAt;
    private boolean lastMessageFromMe;

    // nombre de messages non lus pour l’utilisateur courant
    private long unreadCount;
}