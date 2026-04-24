package com.harvesthall.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double priceAtOrder; // snapshot price at time of order

    // --- Constructors ---
    public OrderItem() {}

    public OrderItem(Order order, MenuItem menuItem, Integer quantity) {
        this.order = order;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.priceAtOrder = menuItem.getPrice();
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPriceAtOrder() { return priceAtOrder; }
    public void setPriceAtOrder(Double priceAtOrder) { this.priceAtOrder = priceAtOrder; }
}
