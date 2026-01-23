package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.CreateOrderRequest;
import ma.ecovestiaire.backend.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    List<OrderResponse> getMyOrders();
}