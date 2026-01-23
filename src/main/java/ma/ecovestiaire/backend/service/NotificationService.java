package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.NotificationResponse;
import ma.ecovestiaire.backend.enums.NotificationType;
import ma.ecovestiaire.backend.entity.User;

import java.util.List;

public interface NotificationService {

    // utilisé par /notifications pour l'utilisateur courant
    List<NotificationResponse> getMyNotifications(boolean markAsRead);

    // méthodes à appeler depuis les autres services (commande, like, etc.)
    void createNotification(User targetUser, NotificationType type, String message, String link);
}