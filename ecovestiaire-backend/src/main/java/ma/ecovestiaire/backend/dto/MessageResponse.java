package ma.ecovestiaire.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class MessageResponse {

    private Long id;
    private Long conversationId;

    private Long senderId;
    private String senderFirstName;
    private String senderLastName;
    private String senderProfilePhotoUrl;

    private String content;
    private Instant createdAt;
    private boolean read;
}