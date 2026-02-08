package ma.ecovestiaire.backend.controller;

import jakarta.validation.Valid;
import ma.ecovestiaire.backend.dto.CreateOrderRequest;
import ma.ecovestiaire.backend.dto.OrderResponse;
import ma.ecovestiaire.backend.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        List<OrderResponse> orders = orderService.getMyOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/my-purchases")
    public ResponseEntity<Page<OrderResponse>> getMyPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        Page<OrderResponse> result = orderService.getMyPurchases(page, size, sortDir);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-sales")
    public ResponseEntity<Page<OrderResponse>> getMySales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        Page<OrderResponse> result = orderService.getMySales(page, size, sortDir);
        return ResponseEntity.ok(result);
    }
}