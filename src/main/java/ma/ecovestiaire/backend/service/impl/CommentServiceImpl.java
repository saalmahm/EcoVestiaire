package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.CommentResponse;
import ma.ecovestiaire.backend.dto.CreateCommentRequest;
import ma.ecovestiaire.backend.entity.Comment;
import ma.ecovestiaire.backend.entity.Item;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.enums.NotificationType;
import ma.ecovestiaire.backend.repository.CommentRepository;
import ma.ecovestiaire.backend.repository.ItemRepository;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.CommentService;
import ma.ecovestiaire.backend.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public CommentServiceImpl(CommentRepository commentRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"
                ));
    }

    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article introuvable"
                ));
    }

    private CommentResponse toCommentResponse(Comment comment) {
        CommentResponse dto = new CommentResponse();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        User author = comment.getUser();
        dto.setAuthorId(author.getId());
        dto.setAuthorFirstName(author.getFirstName());
        dto.setAuthorLastName(author.getLastName());
        dto.setAuthorProfilePhotoUrl(author.getProfilePhotoUrl());
        return dto;
    }

    @Override
    public CommentResponse addCommentToItem(Long itemId, CreateCommentRequest request) {
        User user = getCurrentUser();
        Item item = getItemOrThrow(itemId);

        Comment comment = Comment.builder()
                .user(user)
                .item(item)
                .content(request.getContent())
                .build();

        Comment saved = commentRepository.save(comment);

        // Notification pour le vendeur de l'article
        String message = "Nouveau commentaire sur votre article " + item.getTitle();
        String link = "/items/" + item.getId();

        notificationService.createNotification(
                item.getSeller(),
                NotificationType.NEW_COMMENT,
                message,
                link
        );

        return toCommentResponse(saved);
    }

    @Override
    public List<CommentResponse> getCommentsForItem(Long itemId) {
        Item item = getItemOrThrow(itemId);
        return commentRepository.findByItemOrderByCreatedAtAsc(item)
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    @Override
    public void deleteComment(Long commentId) {
        User currentUser = getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Commentaire introuvable"
                ));

        boolean isAuthor = comment.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = isCurrentUserAdmin();

        if (!isAuthor && !isAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Vous ne pouvez supprimer que vos propres commentaires"
            );
        }

        commentRepository.delete(comment);
    }
}