package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.CreateOrderRequest;
import ma.ecovestiaire.backend.dto.OrderResponse;
import ma.ecovestiaire.backend.entity.Item;
import ma.ecovestiaire.backend.entity.Order;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.enums.ItemStatus;
import ma.ecovestiaire.backend.enums.OrderStatus;
import ma.ecovestiaire.backend.repository.ItemRepository;
import ma.ecovestiaire.backend.repository.OrderRepository;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ItemRepository itemRepository,
                            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));
    }

    private OrderResponse toDto(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setItemId(order.getItem().getId());
        dto.setItemTitle(order.getItem().getTitle());
        dto.setAmount(order.getAmount());
        dto.setStatus(order.getStatus());
        dto.setStripePaymentIntentId(order.getStripePaymentIntentId());
        dto.setStripePaymentId(order.getStripePaymentId());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User buyer = getCurrentUser();

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article introuvable"
                ));

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cet article n'est pas disponible à l'achat"
            );
        }

        // Créer la commande en PENDING_PAYMENT
        Order order = Order.builder()
                .buyer(buyer)
                .item(item)
                .status(OrderStatus.PENDING_PAYMENT)
                .amount(item.getPrice())
                .build();

        // Réserver l'article
        item.setStatus(ItemStatus.RESERVED);

        // Sauvegarder dans la même transaction
        Order savedOrder = orderRepository.save(order);
        itemRepository.save(item);

        return toDto(savedOrder);
    }

    @Override
    public List<OrderResponse> getMyOrders() {
        User buyer = getCurrentUser();
        return orderRepository.findByBuyer(buyer)
                .stream()
                .map(this::toDto)
                .toList();
    }
}