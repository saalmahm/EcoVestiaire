package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.ConversationDetailResponse;
import ma.ecovestiaire.backend.dto.ConversationSummaryResponse;
import ma.ecovestiaire.backend.dto.CreateConversationRequest;
import ma.ecovestiaire.backend.entity.Conversation;
import ma.ecovestiaire.backend.entity.Item;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.repository.ConversationRepository;
import ma.ecovestiaire.backend.repository.ItemRepository;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.ConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ConversationServiceImpl(ConversationRepository conversationRepository,
                                   UserRepository userRepository,
                                   ItemRepository itemRepository) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"
                ));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utilisateur introuvable"
                ));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article introuvable"
                ));
    }

    private ConversationSummaryResponse toSummary(Conversation conversation, User current) {
        ConversationSummaryResponse dto = new ConversationSummaryResponse();
        dto.setId(conversation.getId());

        // déterminer l'autre utilisateur
        User other = conversation.getUser1().getId().equals(current.getId())
                ? conversation.getUser2()
                : conversation.getUser1();

        dto.setOtherUserId(other.getId());
        dto.setOtherUserFirstName(other.getFirstName());
        dto.setOtherUserLastName(other.getLastName());
        dto.setOtherUserProfilePhotoUrl(other.getProfilePhotoUrl());

        if (conversation.getItem() != null) {
            dto.setItemId(conversation.getItem().getId());
            dto.setItemTitle(conversation.getItem().getTitle());
        }

        return dto;
    }

    private ConversationDetailResponse toDetail(Conversation conversation) {
        ConversationDetailResponse dto = new ConversationDetailResponse();
        dto.setId(conversation.getId());

        User u1 = conversation.getUser1();
        User u2 = conversation.getUser2();

        dto.setUser1Id(u1.getId());
        dto.setUser1FirstName(u1.getFirstName());
        dto.setUser1LastName(u1.getLastName());

        dto.setUser2Id(u2.getId());
        dto.setUser2FirstName(u2.getFirstName());
        dto.setUser2LastName(u2.getLastName());

        if (conversation.getItem() != null) {
            dto.setItemId(conversation.getItem().getId());
            dto.setItemTitle(conversation.getItem().getTitle());
        }

        return dto;
    }

    @Override
    public ConversationDetailResponse createOrGetConversation(CreateConversationRequest request) {
        User current = getCurrentUser();
        User target = getUserOrThrow(request.getTargetUserId());

        if (current.getId().equals(target.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vous ne pouvez pas démarrer une conversation avec vous-même"
            );
        }

        // ordonner les participants pour éviter les doublons (user1.id < user2.id)
        User user1 = current.getId() < target.getId() ? current : target;
        User user2 = current.getId() < target.getId() ? target : current;

        Conversation conversation;
        if (request.getItemId() != null) {
            Item item = getItemOrThrow(request.getItemId());

            conversation = conversationRepository
                    .findByUser1AndUser2AndItem(user1, user2, item)
                    .orElseGet(() -> {
                        Conversation c = Conversation.builder()
                                .user1(user1)
                                .user2(user2)
                                .item(item)
                                .build();
                        return conversationRepository.save(c);
                    });
        } else {
            conversation = conversationRepository
                    .findByUser1AndUser2AndItemIsNull(user1, user2)
                    .orElseGet(() -> {
                        Conversation c = Conversation.builder()
                                .user1(user1)
                                .user2(user2)
                                .build();
                        return conversationRepository.save(c);
                    });
        }

        return toDetail(conversation);
    }

    @Override
    public List<ConversationSummaryResponse> getMyConversations() {
        User current = getCurrentUser();
        return conversationRepository.findByUser1OrUser2(current, current)
                .stream()
                .map(c -> toSummary(c, current))
                .toList();
    }

    @Override
    public ConversationDetailResponse getConversation(Long conversationId) {
        User current = getCurrentUser();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conversation introuvable"
                ));

        // sécurité : l'utilisateur doit être participant
        if (!conversation.getUser1().getId().equals(current.getId())
                && !conversation.getUser2().getId().equals(current.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Vous n'êtes pas participant de cette conversation"
            );
        }

        return toDetail(conversation);
    }
}