package ma.ecovestiaire.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationDetailResponse {

    private Long id;

    private Long user1Id;
    private String user1FirstName;
    private String user1LastName;

    private Long user2Id;
    private String user2FirstName;
    private String user2LastName;

    private Long itemId;
    private String itemTitle;
}