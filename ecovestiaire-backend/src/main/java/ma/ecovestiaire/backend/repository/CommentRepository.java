package ma.ecovestiaire.backend.repository;

import ma.ecovestiaire.backend.entity.Comment;
import ma.ecovestiaire.backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemOrderByCreatedAtAsc(Item item);
}