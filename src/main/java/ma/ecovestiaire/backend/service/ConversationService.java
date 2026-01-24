package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.ConversationDetailResponse;
import ma.ecovestiaire.backend.dto.ConversationSummaryResponse;
import ma.ecovestiaire.backend.dto.CreateConversationRequest;

import java.util.List;

public interface ConversationService {

    ConversationDetailResponse createOrGetConversation(CreateConversationRequest request);

    List<ConversationSummaryResponse> getMyConversations();

    ConversationDetailResponse getConversation(Long conversationId);
}