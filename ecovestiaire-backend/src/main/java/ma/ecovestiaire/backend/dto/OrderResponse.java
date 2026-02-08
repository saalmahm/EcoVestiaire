package ma.ecovestiaire.backend.dto;

import lombok.Data;
import ma.ecovestiaire.backend.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class OrderResponse {

    private Long id;
    private Long itemId;
    private String itemTitle;
    private BigDecimal amount;
    private OrderStatus status;
    private String stripePaymentIntentId;
    private String stripePaymentId;
    private Instant createdAt;
}