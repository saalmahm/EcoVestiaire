package ma.ecovestiaire.backend.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import ma.ecovestiaire.backend.entity.Order;
import ma.ecovestiaire.backend.repository.OrderRepository;
import ma.ecovestiaire.backend.service.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StripeServiceImpl implements StripeService {

    private final OrderRepository orderRepository;

    public StripeServiceImpl(
            OrderRepository orderRepository,
            @Value("${STRIPE_SECRET_KEY}") String secretKey
    ) {
        this.orderRepository = orderRepository;
        Stripe.apiKey = secretKey;
    }

    @Override
    public Session createCheckoutSessionForOrder(Long orderId, String successUrl, String cancelUrl) throws StripeException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande introuvable"));

        // Construire la session Checkout
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
                                                        .setUnitAmount(order.getAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue()) // en centimes
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
                        .putMetadata("orderId", order.getId().toString())
                        .build();

        Session session = Session.create(params);

        // Sauvegarder l'id de session Stripe dans l'Order
        order.setStripeCheckoutSessionId(session.getId());
        orderRepository.save(order);

        return session;
    }
}