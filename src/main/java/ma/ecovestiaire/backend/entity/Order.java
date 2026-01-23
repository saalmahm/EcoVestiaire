package ma.ecovestiaire.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.ecovestiaire.backend.enums.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // acheteur
    @ManyToOne(optional = false)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    // article concerné
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    // montant à payer (copié depuis Item.price au moment du commande)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // Références Stripe (remplis plus tard après création PaymentIntent, etc.)
    @Column(length = 100)
    private String stripePaymentIntentId;

    @Column(length = 100)
    private String stripePaymentId;

    // ID de la session de paiement Stripe Checkout
    @Column(length = 255)
    private String stripeCheckoutSessionId;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}