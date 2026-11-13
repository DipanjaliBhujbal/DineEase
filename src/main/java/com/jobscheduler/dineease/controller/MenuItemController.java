package com.jobscheduler.dineease.menu.controller;

import com.jobscheduler.dineease.menu.dto.MenuItemRequest;
import com.jobscheduler.dineease.menu.dto.MenuItemResponse;
import com.jobscheduler.dineease.menu.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu/items")
public class MenuItemController {

    private final MenuItemService service;

    public MenuItemController(MenuItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<MenuItemResponse> getMenuItems(
            @RequestParam Long restaurantId,
            @RequestParam(required = false) Long categoryId) {
        return service.getAvailableItems(restaurantId, categoryId);
    }

    @GetMapping("/{id}")
    public MenuItemResponse getMenuItem(@PathVariable Long id) {
        return service.getItemById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse createMenuItem(@Valid @RequestBody MenuItemRequest request) {
        return service.createItem(request);
    }

    @PutMapping("/{id}")
    public MenuItemResponse updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequest request) {
        return service.updateItem(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable Long id) {
        service.deleteItem(id);
    }
}
