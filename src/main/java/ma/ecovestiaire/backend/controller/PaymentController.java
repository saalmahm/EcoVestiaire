package ma.ecovestiaire.backend.controller;

import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import ma.ecovestiaire.backend.dto.CreateCheckoutSessionRequest;
import ma.ecovestiaire.backend.dto.CreateCheckoutSessionResponse;
import ma.ecovestiaire.backend.service.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final StripeService stripeService;

    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<CreateCheckoutSessionResponse> createCheckoutSession(
            @Valid @RequestBody CreateCheckoutSessionRequest request
    ) throws StripeException {

        String successUrl = request.getSuccessUrl();
        String cancelUrl = request.getCancelUrl();

        if (successUrl == null || successUrl.isBlank()) {
            successUrl = "http://localhost:4200/payment-success"; 
        }
        if (cancelUrl == null || cancelUrl.isBlank()) {
            cancelUrl = "http://localhost:4200/payment-cancel";
        }

        var session = stripeService.createCheckoutSessionForOrder(
                request.getOrderId(),
                successUrl,
                cancelUrl
        );

        CreateCheckoutSessionResponse response = new CreateCheckoutSessionResponse();
        response.setCheckoutUrl(session.getUrl());

        return ResponseEntity.ok(response);
    }
}