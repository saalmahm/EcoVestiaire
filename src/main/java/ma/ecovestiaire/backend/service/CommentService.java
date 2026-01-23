package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.CommentResponse;
import ma.ecovestiaire.backend.dto.CreateCommentRequest;

import java.util.List;

public interface CommentService {

    CommentResponse addCommentToItem(Long itemId, CreateCommentRequest request);

    List<CommentResponse> getCommentsForItem(Long itemId);

    void deleteComment(Long commentId);
}