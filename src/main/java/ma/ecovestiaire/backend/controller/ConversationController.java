package ma.ecovestiaire.backend.controller;

import jakarta.validation.Valid;
import ma.ecovestiaire.backend.dto.ConversationDetailResponse;
import ma.ecovestiaire.backend.dto.ConversationSummaryResponse;
import ma.ecovestiaire.backend.dto.CreateConversationRequest;
import ma.ecovestiaire.backend.service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    // POST /api/conversations
    // Body : { "targetUserId": ..., "itemId": ... (optionnel) }
    @PostMapping
    public ResponseEntity<ConversationDetailResponse> createOrGetConversation(
            @Valid @RequestBody CreateConversationRequest request
    ) {
        ConversationDetailResponse response = conversationService.createOrGetConversation(request);
        return ResponseEntity.ok(response);
    }

    // GET /api/conversations
    // Liste des conversations de l'utilisateur connecté
    @GetMapping
    public ResponseEntity<List<ConversationSummaryResponse>> getMyConversations() {
        List<ConversationSummaryResponse> conversations =
                conversationService.getMyConversations();
        return ResponseEntity.ok(conversations);
    }

    // GET /api/conversations/{id}
    // Détail d'une conversation (vérifie que l'utilisateur est participant)
    @GetMapping("/{id}")
    public ResponseEntity<ConversationDetailResponse> getConversation(
            @PathVariable("id") Long conversationId
    ) {
        ConversationDetailResponse response =
                conversationService.getConversation(conversationId);
        return ResponseEntity.ok(response);
    }
}