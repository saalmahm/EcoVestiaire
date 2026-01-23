package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.NotificationResponse;
import ma.ecovestiaire.backend.entity.Notification;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.enums.NotificationType;
import ma.ecovestiaire.backend.repository.NotificationRepository;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"
                ));
    }

    private NotificationResponse toResponse(Notification notification) {
        NotificationResponse dto = new NotificationResponse();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setLink(notification.getLink());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    @Override
    public List<NotificationResponse> getMyNotifications(boolean markAsRead) {
        User currentUser = getCurrentUser();
        List<Notification> notifications =
                notificationRepository.findByUserOrderByCreatedAtDesc(currentUser);

        if (markAsRead && !notifications.isEmpty()) {
            notifications.forEach(n -> n.setRead(true));
            notificationRepository.saveAll(notifications);
        }

        return notifications.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void createNotification(User targetUser, NotificationType type, String message, String link) {
        Notification notification = Notification.builder()
                .user(targetUser)
                .type(type)
                .message(message)
                .link(link)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        // Envoi temps réel sur WebSocket si le user est connecté
        NotificationResponse payload = toResponse(saved);

        String userDest = targetUser.getEmail(); 

        messagingTemplate.convertAndSendToUser(
                userDest,
                "/queue/notifications",
                payload
        );
    }
}