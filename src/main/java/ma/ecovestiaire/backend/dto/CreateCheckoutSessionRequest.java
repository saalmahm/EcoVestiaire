package ma.ecovestiaire.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCheckoutSessionRequest {

    @NotNull
    private Long orderId;

    private String successUrl;
    private String cancelUrl;
}