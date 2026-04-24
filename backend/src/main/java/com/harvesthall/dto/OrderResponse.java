package com.harvesthall.dto;

import com.harvesthall.model.Order;
import com.harvesthall.model.OrderItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/** Confirmation payload sent back to the client after a successful order */
public class OrderResponse {

    private Long orderId;
    private String customerName;
    private String customerEmail;
    private String status;
    private LocalDateTime orderTime;
    private LocalDateTime pickupTime;
    private Double totalAmount;
    private String specialInstructions;
    private List<OrderItemResponse> items;
    private String confirmationMessage;

    public static OrderResponse from(Order order) {
        OrderResponse r = new OrderResponse();
        r.setOrderId(order.getId());
        r.setCustomerName(order.getCustomerName());
        r.setCustomerEmail(order.getCustomerEmail());
        r.setStatus(order.getStatus().name());
        r.setOrderTime(order.getOrderTime());
        r.setPickupTime(order.getPickupTime());
        r.setTotalAmount(order.getTotalAmount());
        r.setSpecialInstructions(order.getSpecialInstructions());
        r.setConfirmationMessage(
            "Order #" + order.getId() + " confirmed! Your food will be ready for pickup at Harvest Hall."
        );
        if (order.getOrderItems() != null) {
            r.setItems(order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList()));
        }
        return r;
    }

    // --- Getters & Setters ---
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }

    public LocalDateTime getPickupTime() { return pickupTime; }
    public void setPickupTime(LocalDateTime pickupTime) { this.pickupTime = pickupTime; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }

    public String getConfirmationMessage() { return confirmationMessage; }
    public void setConfirmationMessage(String confirmationMessage) { this.confirmationMessage = confirmationMessage; }

    // ---------- Inner DTO ----------
    public static class OrderItemResponse {
        private String itemName;
        private Integer quantity;
        private Double priceAtOrder;
        private Double lineTotal;

        public static OrderItemResponse from(OrderItem item) {
            OrderItemResponse r = new OrderItemResponse();
            r.setItemName(item.getMenuItem().getName());
            r.setQuantity(item.getQuantity());
            r.setPriceAtOrder(item.getPriceAtOrder());
            r.setLineTotal(item.getQuantity() * item.getPriceAtOrder());
            return r;
        }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public Double getPriceAtOrder() { return priceAtOrder; }
        public void setPriceAtOrder(Double priceAtOrder) { this.priceAtOrder = priceAtOrder; }

        public Double getLineTotal() { return lineTotal; }
        public void setLineTotal(Double lineTotal) { this.lineTotal = lineTotal; }
    }
}
