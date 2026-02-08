package ma.ecovestiaire.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "conversations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conversation_users_item",
                        columnNames = {"user1_id", "user2_id", "item_id"}
                )
        }
)
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // premier participant
    @ManyToOne(optional = false)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    // deuxième participant
    @ManyToOne(optional = false)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    // conversation éventuellement liée à un article (type Vinted)
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}