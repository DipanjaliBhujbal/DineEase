package com.jobscheduler.dineease.menu.service;

import com.jobscheduler.dineease.menu.entity.MenuCategory;
import com.jobscheduler.dineease.menu.repository.MenuCategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuCategoryService {

    private final MenuCategoryRepository repository;

    public MenuCategoryService(MenuCategoryRepository repository) {
        this.repository = repository;
    }

    public List<MenuCategory> getAllCategories(Long restaurantId) {
        return repository.findByRestaurant_IdOrderByDisplayOrderAscIdAsc(restaurantId);
    }
}
