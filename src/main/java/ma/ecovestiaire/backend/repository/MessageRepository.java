package ma.ecovestiaire.backend.repository;

import ma.ecovestiaire.backend.entity.Conversation;
import ma.ecovestiaire.backend.entity.Message;
import ma.ecovestiaire.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Historique des messages d’une conversation, triés par date croissante
    Page<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation, Pageable pageable);

    // Compter les messages non lus pour un user dans une conversation
    long countByConversationAndSenderNotAndReadIsFalse(Conversation conversation, User sender);

    // Dernier message d’une conversation
    Message findTopByConversationOrderByCreatedAtDesc(Conversation conversation);

    // Messages non lus pour un user dans une conversation (pour les marquer comme lus)
    List<Message> findByConversationAndSenderNotAndReadIsFalse(Conversation conversation, User sender);
}