package com.harvesthall;

import com.harvesthall.dto.OrderRequest;
import com.harvesthall.dto.OrderResponse;
import com.harvesthall.model.MenuItem;
import com.harvesthall.model.Order;
import com.harvesthall.model.Order.OrderStatus;
import com.harvesthall.repository.OrderRepository;
import com.harvesthall.service.MenuService;
import com.harvesthall.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService.
 * Covers Stories 2, 3, and 4 (Release 2).
 *
 * XP Test-First: tests reflect acceptance criteria from user stories.
 */
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuService menuService;

    @InjectMocks
    private OrderService orderService;

    private MenuItem burger;
    private MenuItem drink;

    @BeforeEach
    void setUp() {
        burger = new MenuItem("All Beef Burger", "Burgers", 8.99, "Classic beef burger");
        burger.setId(1L);
        burger.setAvailable(true);

        drink = new MenuItem("Fountain Drink", "Drinks", 2.49, "Pepsi or OJ");
        drink.setId(2L);
        drink.setAvailable(true);
    }

    // Helper: build a simple one-item order request
    private OrderRequest buildRequest(Long menuItemId, int qty) {
        OrderRequest req = new OrderRequest();
        req.setCustomerName("Jane Student");
        req.setCustomerEmail("jane@brandonu.ca");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setMenuItemId(menuItemId);
        item.setQuantity(qty);
        req.setItems(List.of(item));
        return req;
    }

    // Helper: simulate a saved order returned by the repository
    private Order savedOrder(OrderRequest req, MenuItem item, int qty) {
        Order order = new Order();
        order.setId(101L);
        order.setCustomerName(req.getCustomerName());
        order.setCustomerEmail(req.getCustomerEmail());
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(item.getPrice() * qty);
        order.setOrderItems(List.of(new com.harvesthall.model.OrderItem(order, item, qty)));
        return order;
    }

    // ----------------------------------------------------------------
    // Test Case 2: Place Order
    // ----------------------------------------------------------------

    @Test
    @DisplayName("TC-02: placeOrder saves order with correct details")
    void testPlaceOrderSavesCorrectly() {
        OrderRequest req = buildRequest(1L, 1);
        when(menuService.findById(1L)).thenReturn(Optional.of(burger));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(101L);
            return o;
        });

        OrderResponse response = orderService.placeOrder(req);

        assertNotNull(response);
        assertEquals(101L, response.getOrderId());
        assertEquals("Jane Student", response.getCustomerName());
        assertEquals(8.99, response.getTotalAmount());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("TC-02b: placeOrder with multiple items calculates total correctly")
    void testPlaceOrderMultipleItems() {
        OrderRequest req = new OrderRequest();
        req.setCustomerName("Test User");
        req.setCustomerEmail("test@brandonu.ca");

        OrderRequest.OrderItemRequest i1 = new OrderRequest.OrderItemRequest();
        i1.setMenuItemId(1L); i1.setQuantity(2); // 2 x 8.99 = 17.98

        OrderRequest.OrderItemRequest i2 = new OrderRequest.OrderItemRequest();
        i2.setMenuItemId(2L); i2.setQuantity(1); // 1 x 2.49 = 2.49

        req.setItems(List.of(i1, i2));

        when(menuService.findById(1L)).thenReturn(Optional.of(burger));
        when(menuService.findById(2L)).thenReturn(Optional.of(drink));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(102L);
            return o;
        });

        OrderResponse response = orderService.placeOrder(req);

        assertEquals(20.47, response.getTotalAmount(), 0.001, "Total should be 17.98 + 2.49 = 20.47");
    }

    @Test
    @DisplayName("TC-02c: placeOrder throws if menu item not found")
    void testPlaceOrderItemNotFound() {
        OrderRequest req = buildRequest(99L, 1);
        when(menuService.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(req));
    }

    @Test
    @DisplayName("TC-02d: placeOrder throws if item is unavailable")
    void testPlaceOrderItemUnavailable() {
        burger.setAvailable(false);
        OrderRequest req = buildRequest(1L, 1);
        when(menuService.findById(1L)).thenReturn(Optional.of(burger));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> orderService.placeOrder(req));
        assertTrue(ex.getMessage().contains("unavailable"));
    }

    // ----------------------------------------------------------------
    // Test Case 3: Order Confirmation
    // ----------------------------------------------------------------

    @Test
    @DisplayName("TC-03: placeOrder response contains confirmation message with order ID")
    void testOrderConfirmationMessage() {
        OrderRequest req = buildRequest(1L, 1);
        when(menuService.findById(1L)).thenReturn(Optional.of(burger));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(55L);
            return o;
        });

        OrderResponse response = orderService.placeOrder(req);

        assertNotNull(response.getConfirmationMessage(), "Confirmation message must not be null");
        assertTrue(response.getConfirmationMessage().contains("55"),
            "Confirmation message should include the order ID");
        assertEquals("PENDING", response.getStatus());
    }

    // ----------------------------------------------------------------
    // Story 4 (Release 2): Kitchen staff order view & status update
    // ----------------------------------------------------------------

    @Test
    @DisplayName("TC-04a: getActiveOrders returns only active orders")
    void testGetActiveOrders() {
        Order pending = new Order(); pending.setStatus(OrderStatus.PENDING);
        Order preparing = new Order(); preparing.setStatus(OrderStatus.PREPARING);
        when(orderRepository.findByStatusIn(anyList())).thenReturn(List.of(pending, preparing));

        List<Order> result = orderService.getActiveOrders();

        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(o -> o.getStatus() == OrderStatus.COMPLETED));
    }

    @Test
    @DisplayName("TC-04b: updateOrderStatus changes status correctly")
    void testUpdateOrderStatus() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setCustomerName("Test");
        order.setCustomerEmail("t@t.com");
        order.setOrderTime(LocalDateTime.now());
        order.setTotalAmount(8.99);
        order.setOrderItems(List.of());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        OrderResponse response = orderService.updateOrderStatus(1L, "PREPARING");

        assertEquals("PREPARING", response.getStatus());
    }

    @Test
    @DisplayName("TC-04c: updateOrderStatus throws for invalid status")
    void testUpdateOrderStatusInvalid() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class,
            () -> orderService.updateOrderStatus(1L, "FLYING"));
    }
}
