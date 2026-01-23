package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.CreateOrderRequest;
import ma.ecovestiaire.backend.dto.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    List<OrderResponse> getMyOrders();

    Page<OrderResponse> getMyPurchases(int page, int size, String sortDir);

    Page<OrderResponse> getMySales(int page, int size, String sortDir);
}