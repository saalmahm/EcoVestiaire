package ma.ecovestiaire.backend.controller;

import ma.ecovestiaire.backend.repository.CommentRepository;
import ma.ecovestiaire.backend.repository.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminModerationController {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    public AdminModerationController(ItemRepository itemRepository,
                                     CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
    }

    // DELETE /admin/items/{id}
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            return ResponseEntity.notFound().build();
        }
        itemRepository.deleteById(itemId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /admin/comments/{id}
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            return ResponseEntity.notFound().build();
        }
        commentRepository.deleteById(commentId);
        return ResponseEntity.noContent().build();
    }
}