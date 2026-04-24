package com.harvesthall.controller;

import com.harvesthall.dto.OrderRequest;
import com.harvesthall.dto.OrderResponse;
import com.harvesthall.model.Order;
import com.harvesthall.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for order operations.
 *
 * Endpoints:
 *   POST /api/orders                      – Story 2+3: place order
 *   GET  /api/orders/{id}                 – get single order
 *   GET  /api/orders?email=x@y.com        – customer looks up their orders
 *   GET  /api/orders/active               – Story 4 (R2): kitchen staff view
 *   PATCH /api/orders/{id}/status?status= – Story 4 (R2): update status
 *   GET  /api/orders/all                  – admin: all orders
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** POST /api/orders — place a new order and receive confirmation */
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** GET /api/orders/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /** GET /api/orders?email=student@brandonu.ca */
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByEmail(
            @RequestParam(required = false) String email) {
        if (email != null && !email.isBlank()) {
            return ResponseEntity.ok(orderService.getOrdersByEmail(email));
        }
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /** GET /api/orders/active — kitchen dashboard (Release 2) */
    @GetMapping("/active")
    public ResponseEntity<List<Order>> getActiveOrders() {
        return ResponseEntity.ok(orderService.getActiveOrders());
    }

    /** PATCH /api/orders/{id}/status?status=PREPARING — kitchen updates order (Release 2) */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    /** GET /api/orders/all — admin view */
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
