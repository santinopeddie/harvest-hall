package com.harvesthall.service;

import com.harvesthall.model.MenuItem;
import com.harvesthall.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    /** Story 1: Return all available menu items */
    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findByAvailableTrue();
    }

    /** Return items filtered by category */
    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategoryAndAvailableTrue(category);
    }

    /** Story 5 (Release 2): Admin adds a new menu item */
    public MenuItem addMenuItem(MenuItem item) {
        return menuItemRepository.save(item);
    }

    /** Story 5 (Release 2): Admin updates an existing menu item */
    public MenuItem updateMenuItem(Long id, MenuItem updated) {
        MenuItem existing = menuItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));
        existing.setName(updated.getName());
        existing.setCategory(updated.getCategory());
        existing.setPrice(updated.getPrice());
        existing.setDescription(updated.getDescription());
        existing.setAvailable(updated.getAvailable());
        return menuItemRepository.save(existing);
    }

    /** Soft-delete: mark item as unavailable instead of deleting */
    public void removeMenuItem(Long id) {
        MenuItem item = menuItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));
        item.setAvailable(false);
        menuItemRepository.save(item);
    }

    public Optional<MenuItem> findById(Long id) {
        return menuItemRepository.findById(id);
    }
}
