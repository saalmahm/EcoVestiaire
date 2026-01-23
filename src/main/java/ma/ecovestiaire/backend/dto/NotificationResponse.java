package ma.ecovestiaire.backend.dto;

import lombok.Data;
import ma.ecovestiaire.backend.enums.NotificationType;

import java.time.Instant;

@Data
public class NotificationResponse {

    private Long id;
    private NotificationType type;
    private String message;
    private String link;
    private boolean isRead;
    private Instant createdAt;
}