package ma.ecovestiaire.backend.repository;

import ma.ecovestiaire.backend.entity.Notification;
import ma.ecovestiaire.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}