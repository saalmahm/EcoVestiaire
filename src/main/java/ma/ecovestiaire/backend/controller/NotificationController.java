package ma.ecovestiaire.backend.controller;

import ma.ecovestiaire.backend.dto.NotificationResponse;
import ma.ecovestiaire.backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET /notifications?markAsRead=true|false
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @RequestParam(name = "markAsRead", defaultValue = "false") boolean markAsRead
    ) {
        List<NotificationResponse> notifications =
                notificationService.getMyNotifications(markAsRead);
        return ResponseEntity.ok(notifications);
    }
}