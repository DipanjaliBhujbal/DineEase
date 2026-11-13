package com.jobscheduler.dineease.menu.controller;

import com.jobscheduler.dineease.dto.AdminOrderResponse;
import com.jobscheduler.dineease.entity.AppUser;
import com.jobscheduler.dineease.menu.dto.MenuItemRequest;
import com.jobscheduler.dineease.menu.dto.MenuItemResponse;
import com.jobscheduler.dineease.menu.service.MenuItemService;
import com.jobscheduler.dineease.repository.AppUserRepository;
import com.jobscheduler.dineease.security.AuthenticatedAppUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/partner/menu/items")
public class RestaurantPartnerMenuController {

    private final MenuItemService menuItemService;
    private final AppUserRepository appUserRepository;

    public RestaurantPartnerMenuController(
            MenuItemService menuItemService,
            AppUserRepository appUserRepository) {
        this.menuItemService = menuItemService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getItems(
            @AuthenticationPrincipal AuthenticatedAppUser principal) {
        return menuItemService.getPartnerItems(getAssignedRestaurantId(principal));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse createItem(
            @Valid @RequestBody MenuItemRequest request,
            @AuthenticationPrincipal AuthenticatedAppUser principal) {
        return menuItemService.createPartnerItem(getAssignedRestaurantId(principal), request);
    }

    @PutMapping("/{id}")
    public MenuItemResponse updateItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequest request,
            @AuthenticationPrincipal AuthenticatedAppUser principal) {
        return menuItemService.updatePartnerItem(getAssignedRestaurantId(principal), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedAppUser principal) {
        menuItemService.deletePartnerItem(getAssignedRestaurantId(principal), id);
    }

    private Long getAssignedRestaurantId(AuthenticatedAppUser principal) {
        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Authentication required.");
        }

        AppUser user = appUserRepository.findById(principal.getAppUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Partner account not found."));

        if (user.getRestaurant() == null || user.getRestaurant().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No restaurant is assigned to this partner.");
        }

        return user.getRestaurant().getId();
    }
}
