package com.harvesthall;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harvesthall.dto.OrderRequest;
import com.harvesthall.dto.OrderResponse;
import com.harvesthall.model.MenuItem;
import com.harvesthall.service.MenuService;
import com.harvesthall.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for REST controllers using MockMvc.
 * Verifies HTTP status codes, response shape, and JSON content.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuService menuService;

    @MockBean
    private OrderService orderService;

    // ----------------------------------------------------------------
    // Menu Controller Tests
    // ----------------------------------------------------------------

    @Test
    @DisplayName("GET /api/menu returns 200 with menu list")
    void testGetMenuReturns200() throws Exception {
        MenuItem burger = new MenuItem("All Beef Burger", "Burgers", 8.99, "Classic");
        burger.setId(1L);
        when(menuService.getAvailableMenuItems()).thenReturn(List.of(burger));

        mockMvc.perform(get("/api/menu"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value("All Beef Burger"))
            .andExpect(jsonPath("$[0].price").value(8.99));
    }

    @Test
    @DisplayName("GET /api/menu?category=Burgers filters correctly")
    void testGetMenuByCategory() throws Exception {
        MenuItem burger = new MenuItem("All Beef Burger", "Burgers", 8.99, "Classic");
        when(menuService.getMenuItemsByCategory("Burgers")).thenReturn(List.of(burger));

        mockMvc.perform(get("/api/menu").param("category", "Burgers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].category").value("Burgers"));
    }

    @Test
    @DisplayName("POST /api/menu returns 201 for valid item")
    void testAddMenuItemReturns201() throws Exception {
        MenuItem item = new MenuItem("New Item", "Sides", 4.99, "A new side dish");
        item.setId(10L);
        when(menuService.addMenuItem(any(MenuItem.class))).thenReturn(item);

        mockMvc.perform(post("/api/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("New Item"));
    }

    // ----------------------------------------------------------------
    // Order Controller Tests
    // ----------------------------------------------------------------

    @Test
    @DisplayName("POST /api/orders returns 201 with confirmation")
    void testPlaceOrderReturns201() throws Exception {
        OrderRequest req = new OrderRequest();
        req.setCustomerName("Jane Student");
        req.setCustomerEmail("jane@brandonu.ca");
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setMenuItemId(1L);
        item.setQuantity(1);
        req.setItems(List.of(item));

        OrderResponse resp = new OrderResponse();
        resp.setOrderId(1L);
        resp.setCustomerName("Jane Student");
        resp.setStatus("PENDING");
        resp.setTotalAmount(8.99);
        resp.setConfirmationMessage("Order #1 confirmed! Your food will be ready for pickup at Harvest Hall.");

        when(orderService.placeOrder(any(OrderRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderId").value(1))
            .andExpect(jsonPath("$.confirmationMessage").exists())
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/orders returns 400 for missing customer name")
    void testPlaceOrderValidationError() throws Exception {
        OrderRequest req = new OrderRequest();
        // customerName intentionally blank — should fail validation
        req.setCustomerEmail("jane@brandonu.ca");
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setMenuItemId(1L);
        item.setQuantity(1);
        req.setItems(List.of(item));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/orders returns 400 for invalid email")
    void testPlaceOrderInvalidEmail() throws Exception {
        OrderRequest req = new OrderRequest();
        req.setCustomerName("Jane");
        req.setCustomerEmail("not-an-email");
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setMenuItemId(1L);
        item.setQuantity(1);
        req.setItems(List.of(item));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }
}
