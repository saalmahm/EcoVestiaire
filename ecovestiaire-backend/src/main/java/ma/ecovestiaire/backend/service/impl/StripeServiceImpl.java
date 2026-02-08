package ma.ecovestiaire.backend.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import ma.ecovestiaire.backend.entity.Order;
import ma.ecovestiaire.backend.enums.ItemStatus;
import ma.ecovestiaire.backend.enums.OrderStatus;
import ma.ecovestiaire.backend.repository.OrderRepository;
import ma.ecovestiaire.backend.service.StripeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger log = LoggerFactory.getLogger(StripeServiceImpl.class);

    private final OrderRepository orderRepository;
    private final String webhookSecret;

    public StripeServiceImpl(
            OrderRepository orderRepository,
            @Value("${STRIPE_SECRET_KEY}") String secretKey,
            @Value("${STRIPE_WEBHOOK_SECRET}") String webhookSecret
    ) {
        this.orderRepository = orderRepository;
        Stripe.apiKey = secretKey;
        this.webhookSecret = webhookSecret;
    }

    @Override
    public Session createCheckoutSessionForOrder(Long orderId, String successUrl, String cancelUrl) throws StripeException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande introuvable"));

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(successUrl)
                        .setCancelUrl(cancelUrl)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("eur")
                                                        .setUnitAmount(
                                                                order.getAmount()
                                                                        .multiply(java.math.BigDecimal.valueOf(100))
                                                                        .longValue()
                                                        )
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName(order.getItem().getTitle())
                                                                        .setDescription(order.getItem().getDescription())
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        //stocke l'id de la commande dans les metadata
                        .putMetadata("orderId", order.getId().toString())
                        .build();

        Session session = Session.create(params);

        order.setStripeCheckoutSessionId(session.getId());
        orderRepository.save(order);

        return session;
    }

    @Override
    public void handleWebhookEvent(String payload, String signatureHeader) throws StripeException {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Stripe webhook signature verification failed", e);
            throw e;
        }

        log.info("Received Stripe webhook event: {}", event.getType());

        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        StripeObject dataObject = deserializer.getObject().orElse(null);

        if ("checkout.session.completed".equals(event.getType())) {
            Session session;

            if (dataObject instanceof Session) {
                session = (Session) dataObject;
            } else {
                try {
                    session = (Session) event.getData().getObject();
                } catch (ClassCastException ex) {
                    log.warn("Cannot cast event data object to Session for event {}", event.getId(), ex);
                    return;
                }
            }

            if (session == null) {
                log.warn("Stripe session object is null for event {}", event.getId());
                return;
            }

            String orderIdStr = session.getMetadata() != null
                    ? session.getMetadata().get("orderId")
                    : null;

            if (orderIdStr == null) {
                log.warn("Stripe session {} does not contain orderId metadata", session.getId());
                return;
            }

            Long orderId;
            try {
                orderId = Long.valueOf(orderIdStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid orderId metadata '{}' on Stripe session {}", orderIdStr, session.getId());
                return;
            }

            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                log.warn("Order with id {} not found for Stripe session {}", orderId, session.getId());
                return;
            }

            order.setStatus(OrderStatus.PAID);
            if (order.getItem() != null) {
                order.getItem().setStatus(ItemStatus.SOLD);
            }
            orderRepository.save(order);

            log.info("Marked order {} as PAID and item {} as SOLD",
                    order.getId(),
                    order.getItem() != null ? order.getItem().getId() : null);

            return;
        }

        log.info("Unhandled Stripe event type: {}", event.getType());
    }
}