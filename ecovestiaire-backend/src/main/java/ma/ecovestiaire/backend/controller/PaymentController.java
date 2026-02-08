package ma.ecovestiaire.backend.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import ma.ecovestiaire.backend.dto.CreateCheckoutSessionRequest;
import ma.ecovestiaire.backend.dto.CreateCheckoutSessionResponse;
import ma.ecovestiaire.backend.service.StripeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

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

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signatureHeader
    ) {
        try {
            stripeService.handleWebhookEvent(payload, signatureHeader);
            return ResponseEntity.ok("Webhook processed");
        } catch (SignatureVerificationException e) {
            log.warn("Received Stripe webhook with invalid signature", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (StripeException e) {
            log.error("Error while processing Stripe webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
        }
    }
}