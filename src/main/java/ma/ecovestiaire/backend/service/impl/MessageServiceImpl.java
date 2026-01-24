package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.MessageResponse;
import ma.ecovestiaire.backend.dto.SendMessageRequest;
import ma.ecovestiaire.backend.entity.Conversation;
import ma.ecovestiaire.backend.entity.Message;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.enums.NotificationType;
import ma.ecovestiaire.backend.repository.ConversationRepository;
import ma.ecovestiaire.backend.repository.MessageRepository;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.MessageService;
import ma.ecovestiaire.backend.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public MessageServiceImpl(MessageRepository messageRepository,
                              ConversationRepository conversationRepository,
                              UserRepository userRepository,
                              SimpMessagingTemplate messagingTemplate,
                              NotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"
                ));
    }

    private Conversation getConversationForCurrentUserOrThrow(Long conversationId) {
        User current = getCurrentUser();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conversation introuvable"
                ));

        if (!conversation.getUser1().getId().equals(current.getId())
                && !conversation.getUser2().getId().equals(current.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Vous n'êtes pas participant de cette conversation"
            );
        }

        return conversation;
    }

    private MessageResponse toResponse(Message message) {
        MessageResponse dto = new MessageResponse();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());

        User sender = message.getSender();
        dto.setSenderId(sender.getId());
        dto.setSenderFirstName(sender.getFirstName());
        dto.setSenderLastName(sender.getLastName());
        dto.setSenderProfilePhotoUrl(sender.getProfilePhotoUrl());

        dto.setContent(message.getContent());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setRead(message.isRead());

        return dto;
    }

    @Override
    public MessageResponse sendMessage(Long conversationId, SendMessageRequest request) {
        Conversation conversation = getConversationForCurrentUserOrThrow(conversationId);
        User current = getCurrentUser();

        Message message = Message.builder()
                .conversation(conversation)
                .sender(current)
                .content(request.getContent())
                .build();

        Message saved = messageRepository.save(message);

        MessageResponse payload = toResponse(saved);

        // déterminer l'autre participant
        User other = conversation.getUser1().getId().equals(current.getId())
                ? conversation.getUser2()
                : conversation.getUser1();

        // 1) Envoi temps réel via WebSocket au destinataire
        String userDest = other.getEmail(); // comme pour les notifications
        messagingTemplate.convertAndSendToUser(
                userDest,
                "/queue/chat",
                payload
        );

        // 2) Notification NEW_MESSAGE
        String notifMessage = "Nouveau message de " + current.getFirstName();
        String link = "/conversations/" + conversation.getId();

        notificationService.createNotification(
                other,
                NotificationType.NEW_MESSAGE,
                notifMessage,
                link
        );

        return payload;
    }

    @Override
    public Page<MessageResponse> getMessages(Long conversationId, Pageable pageable) {
        Conversation conversation = getConversationForCurrentUserOrThrow(conversationId);

        return messageRepository
                .findByConversationOrderByCreatedAtAsc(conversation, pageable)
                .map(this::toResponse);
    }
}