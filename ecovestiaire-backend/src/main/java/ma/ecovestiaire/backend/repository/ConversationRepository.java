package ma.ecovestiaire.backend.repository;

import ma.ecovestiaire.backend.entity.Conversation;
import ma.ecovestiaire.backend.entity.Item;
import ma.ecovestiaire.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // Lister toutes les conversations d’un utilisateur (il est soit user1, soit user2)
    List<Conversation> findByUser1OrUser2(User user1, User user2);

    // Trouver une conversation précise entre deux users (optionnellement liée à un item)
    Optional<Conversation> findByUser1AndUser2AndItem(User user1, User user2, Item item);

    Optional<Conversation> findByUser1AndUser2AndItemIsNull(User user1, User user2);
}