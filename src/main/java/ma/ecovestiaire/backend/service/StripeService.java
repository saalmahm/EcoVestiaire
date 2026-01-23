package ma.ecovestiaire.backend.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

public interface StripeService {

    Session createCheckoutSessionForOrder(Long orderId, String successUrl, String cancelUrl) throws StripeException;

    void handleWebhookEvent(String payload, String signatureHeader) throws StripeException;
}