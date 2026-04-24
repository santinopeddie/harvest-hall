package com.harvesthall.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

// ---- Request DTOs ----

/** Payload sent by the frontend when placing an order */
public class OrderRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequest> items;

    // Release 2: optional pickup time
    private LocalDateTime pickupTime;

    private String specialInstructions;

    // --- Getters & Setters ---
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public LocalDateTime getPickupTime() { return pickupTime; }
    public void setPickupTime(LocalDateTime pickupTime) { this.pickupTime = pickupTime; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    // Inner class for each line item in the request
    public static class OrderItemRequest {
        @NotNull(message = "Menu item ID is required")
        private Long menuItemId;

        @NotNull
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        public Long getMenuItemId() { return menuItemId; }
        public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
