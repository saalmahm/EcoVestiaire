package ma.ecovestiaire.backend.controller;

import jakarta.validation.Valid;
import ma.ecovestiaire.backend.dto.ConversationDetailResponse;
import ma.ecovestiaire.backend.dto.ConversationSummaryResponse;
import ma.ecovestiaire.backend.dto.CreateConversationRequest;
import ma.ecovestiaire.backend.dto.MessageResponse;
import ma.ecovestiaire.backend.dto.SendMessageRequest;
import ma.ecovestiaire.backend.service.ConversationService;
import ma.ecovestiaire.backend.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final MessageService messageService;

    public ConversationController(ConversationService conversationService,
                                  MessageService messageService) {
        this.conversationService = conversationService;
        this.messageService = messageService;
    }

    // POST /api/conversations
    @PostMapping
    public ResponseEntity<ConversationDetailResponse> createOrGetConversation(
            @Valid @RequestBody CreateConversationRequest request
    ) {
        ConversationDetailResponse response = conversationService.createOrGetConversation(request);
        return ResponseEntity.ok(response);
    }

    // GET /api/conversations
    @GetMapping
    public ResponseEntity<List<ConversationSummaryResponse>> getMyConversations() {
        List<ConversationSummaryResponse> conversations =
                conversationService.getMyConversations();
        return ResponseEntity.ok(conversations);
    }

    // GET /api/conversations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ConversationDetailResponse> getConversation(
            @PathVariable("id") Long conversationId
    ) {
        ConversationDetailResponse response =
                conversationService.getConversation(conversationId);
        return ResponseEntity.ok(response);
    }

    // POST /api/conversations/{id}/messages
    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable("id") Long conversationId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        MessageResponse response = messageService.sendMessage(conversationId, request);
        return ResponseEntity.ok(response);
    }

    // GET /api/conversations/{id}/messages?page=&size=
    @GetMapping("/{id}/messages")
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @PathVariable("id") Long conversationId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<MessageResponse> result = messageService.getMessages(conversationId, pageable);
        return ResponseEntity.ok(result);
    }
}