package com.jobscheduler.dineease.menu.controller;

import com.jobscheduler.dineease.menu.entity.MenuCategory;
import com.jobscheduler.dineease.menu.service.MenuCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menu/categories")
public class MenuCategoryController {

    private final MenuCategoryService service;

    public MenuCategoryController(MenuCategoryService service) {
        this.service = service;
    }

    public record CategoryResponse(Long id, String name, Integer displayOrder) {
        public static CategoryResponse from(MenuCategory category) {
            return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDisplayOrder()
            );
        }
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories(@RequestParam Long restaurantId) {
        return service.getAllCategories(restaurantId)
            .stream()
            .map(CategoryResponse::from)
            .toList();
    }
}
