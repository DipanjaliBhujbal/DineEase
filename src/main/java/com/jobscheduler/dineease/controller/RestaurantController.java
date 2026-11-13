package com.jobscheduler.dineease.menu.controller;

import com.jobscheduler.dineease.menu.entity.Restaurant;
import com.jobscheduler.dineease.menu.repository.RestaurantRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public record RestaurantResponse(
            Long id,
            String name,
            String description
    ) {
        public static RestaurantResponse from(Restaurant restaurant) {
            return new RestaurantResponse(
                    restaurant.getId(),
                    restaurant.getName(),
                    restaurant.getDescription()
            );
        }
    }

    @GetMapping
    public List<RestaurantResponse> getActiveRestaurants() {
        return restaurantRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(RestaurantResponse::from)
                .toList();
    }
}
