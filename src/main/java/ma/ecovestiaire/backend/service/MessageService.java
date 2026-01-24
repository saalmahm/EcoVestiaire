package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.MessageResponse;
import ma.ecovestiaire.backend.dto.SendMessageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    MessageResponse sendMessage(Long conversationId, SendMessageRequest request);

    Page<MessageResponse> getMessages(Long conversationId, Pageable pageable);
}