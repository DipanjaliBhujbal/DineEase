package com.jobscheduler.dineease.menu.controller;

import com.jobscheduler.dineease.entity.AppUser;
import com.jobscheduler.dineease.repository.AppUserRepository;
import com.jobscheduler.dineease.security.AuthenticatedAppUser;
import com.jobscheduler.dineease.menu.service.MenuCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/partner/menu/categories")
public class RestaurantPartnerCategoryController {

    private final MenuCategoryService categoryService;
    private final AppUserRepository appUserRepository;

    public RestaurantPartnerCategoryController(
            MenuCategoryService categoryService,
            AppUserRepository appUserRepository) {
        this.categoryService = categoryService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<MenuCategoryController.CategoryResponse> getCategories(
            @AuthenticationPrincipal AuthenticatedAppUser principal) {

        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Authentication required.");
        }

        AppUser user = appUserRepository.findById(principal.getAppUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Partner account not found."));

        if (user.getRestaurant() == null || user.getRestaurant().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No restaurant is assigned to this partner.");
        }

        return categoryService
                .getAllCategories(user.getRestaurant().getId())
                .stream()
                .map(MenuCategoryController.CategoryResponse::from)
                .toList();
    }
}