package com.harvesthall.service;

import com.harvesthall.dto.OrderRequest;
import com.harvesthall.dto.OrderResponse;
import com.harvesthall.model.MenuItem;
import com.harvesthall.model.Order;
import com.harvesthall.model.Order.OrderStatus;
import com.harvesthall.model.OrderItem;
import com.harvesthall.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuService menuService;

    @Autowired
    public OrderService(OrderRepository orderRepository, MenuService menuService) {
        this.orderRepository = orderRepository;
        this.menuService = menuService;
    }

    /**
     * Story 2 + 3: Place an order and return a confirmation response.
     * Validates item availability, computes total, persists order and items.
     */
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setOrderTime(LocalDateTime.now());
        order.setPickupTime(request.getPickupTime());
        order.setSpecialInstructions(request.getSpecialInstructions());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> items = new ArrayList<>();
        double total = 0.0;

        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem = menuService.findById(itemReq.getMenuItemId())
                .orElseThrow(() -> new RuntimeException(
                    "Menu item not found: " + itemReq.getMenuItemId()));

            if (!menuItem.getAvailable()) {
                throw new RuntimeException(menuItem.getName() + " is currently unavailable.");
            }

            OrderItem orderItem = new OrderItem(order, menuItem, itemReq.getQuantity());
            items.add(orderItem);
            total += menuItem.getPrice() * itemReq.getQuantity();
        }

        order.setOrderItems(items);
        order.setTotalAmount(Math.round(total * 100.0) / 100.0);

        Order saved = orderRepository.save(order);
        return OrderResponse.from(saved);
    }

    /**
     * Story 4 (Release 2): Kitchen staff view — returns active orders.
     */
    public List<Order> getActiveOrders() {
        return orderRepository.findByStatusIn(
            List.of(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.PREPARING)
        );
    }

    /**
     * Story 4 (Release 2): Kitchen staff updates order status.
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String statusStr) {
        OrderStatus newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(newStatus);
        return OrderResponse.from(orderRepository.save(order));
    }

    /** Get all orders (admin view) */
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderTimeDesc();
    }

    /** Customer looks up their orders by email */
    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByCustomerEmailOrderByOrderTimeDesc(email);
    }

    /** Get single order */
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }
}
