package com.harvesthall.repository;

import com.harvesthall.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // Find all available (not deleted/hidden) menu items
    List<MenuItem> findByAvailableTrue();

    // Find items by category (e.g., "Burgers", "Drinks")
    List<MenuItem> findByCategoryAndAvailableTrue(String category);

    // Find by name containing keyword (for admin search)
    List<MenuItem> findByNameContainingIgnoreCase(String keyword);
}
