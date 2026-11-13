package com.jobscheduler.dineease.menu.service;

import com.jobscheduler.dineease.menu.dto.MenuItemRequest;
import com.jobscheduler.dineease.menu.dto.MenuItemResponse;
import com.jobscheduler.dineease.menu.entity.MenuCategory;
import com.jobscheduler.dineease.menu.entity.MenuItem;
import com.jobscheduler.dineease.menu.repository.MenuCategoryRepository;
import com.jobscheduler.dineease.menu.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuItemService {

    private final MenuItemRepository repository;
    private final MenuCategoryRepository categoryRepository;

    public MenuItemService(
            MenuItemRepository repository,
            MenuCategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAvailableItems(Long restaurantId, Long categoryId) {
        List<MenuItem> items = categoryId == null
                ? repository.findByRestaurantIdAndAvailableTrue(restaurantId)
                : repository.findByRestaurantIdAndCategoryIdAndAvailableTrue(restaurantId, categoryId);

        return items.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MenuItemResponse getItemById(Long id) {
        MenuItem item = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + id));
        return toResponse(item);
    }

    @Transactional
    public MenuItemResponse createItem(MenuItemRequest request) {
        MenuCategory category = findCategory(request.categoryId());
        MenuItem item = new MenuItem();
        item.setRestaurant(category.getRestaurant());
        applyRequest(item, request, category);
        return toResponse(repository.save(item));
    }

    @Transactional
    public MenuItemResponse updateItem(Long id, MenuItemRequest request) {
        MenuItem item = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + id));
        MenuCategory category = findCategory(request.categoryId());

        if (!item.getRestaurant().getId().equals(category.getRestaurant().getId())) {
            throw new IllegalArgumentException(
                    "The selected category does not belong to this menu item's restaurant");
        }

        applyRequest(item, request, category);
        return toResponse(repository.save(item));
    }

    @Transactional
    public void deleteItem(Long id) {
        MenuItem item = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + id));
        repository.delete(item);
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getPartnerItems(Long restaurantId) {
        return repository.findByRestaurantId(restaurantId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public MenuItemResponse createPartnerItem(Long restaurantId, MenuItemRequest request) {
        MenuCategory category = findCategoryForRestaurant(request.categoryId(), restaurantId);
        MenuItem item = new MenuItem();
        item.setRestaurant(category.getRestaurant());
        applyRequest(item, request, category);
        return toResponse(repository.save(item));
    }

    @Transactional
    public MenuItemResponse updatePartnerItem(
            Long restaurantId, Long id, MenuItemRequest request) {
        MenuItem item = repository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Menu item not found for this restaurant: " + id));
        MenuCategory category = findCategoryForRestaurant(request.categoryId(), restaurantId);
        applyRequest(item, request, category);
        return toResponse(repository.save(item));
    }

    @Transactional
    public void deletePartnerItem(Long restaurantId, Long id) {
        MenuItem item = repository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Menu item not found for this restaurant: " + id));
        repository.delete(item);
    }

    private MenuCategory findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
    }

    private MenuCategory findCategoryForRestaurant(Long categoryId, Long restaurantId) {
        return categoryRepository.findByIdAndRestaurant_Id(categoryId, restaurantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found for this restaurant: " + categoryId));
    }

    private void applyRequest(MenuItem item, MenuItemRequest request, MenuCategory category) {
        item.setName(request.name().trim());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setImageUrl(request.imageUrl());
        item.setAvailable(request.available() == null || request.available());
        item.setCategory(category);
    }

    private MenuItemResponse toResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getRestaurant().getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getImageUrl(),
                item.isAvailable(),
                item.getCategory().getId(),
                item.getCategory().getName()
        );
    }
}
