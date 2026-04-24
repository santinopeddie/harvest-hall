package com.harvesthall.repository;

import com.harvesthall.model.Order;
import com.harvesthall.model.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Kitchen staff view: fetch orders by status
    List<Order> findByStatusIn(List<OrderStatus> statuses);

    // Customer can look up their own orders
    List<Order> findByCustomerEmailOrderByOrderTimeDesc(String email);

    // Admin: all orders ordered by time
    List<Order> findAllByOrderByOrderTimeDesc();
}
