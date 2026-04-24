package com.harvesthall.controller;

import com.harvesthall.model.MenuItem;
import com.harvesthall.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for menu operations.
 *
 * Endpoints:
 *   GET  /api/menu              – Story 1: view available menu
 *   GET  /api/menu?category=X   – filter by category
 *   POST /api/menu              – Story 5 (R2): admin adds item
 *   PUT  /api/menu/{id}         – Story 5 (R2): admin updates item
 *   DELETE /api/menu/{id}       – Story 5 (R2): admin soft-deletes item
 */
@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*") // allow the HTML frontend to call this API
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /** GET /api/menu  — or  GET /api/menu?category=Burgers */
    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenu(
            @RequestParam(required = false) String category) {
        List<MenuItem> items = (category != null && !category.isBlank())
            ? menuService.getMenuItemsByCategory(category)
            : menuService.getAvailableMenuItems();
        return ResponseEntity.ok(items);
    }

    /** POST /api/menu — admin creates new menu item */
    @PostMapping
    public ResponseEntity<MenuItem> addMenuItem(@Valid @RequestBody MenuItem item) {
        MenuItem created = menuService.addMenuItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/menu/{id} — admin updates menu item */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItem item) {
        MenuItem updated = menuService.updateMenuItem(id, item);
        return ResponseEntity.ok(updated);
    }

    /** DELETE /api/menu/{id} — soft-delete (marks unavailable) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMenuItem(@PathVariable Long id) {
        menuService.removeMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
