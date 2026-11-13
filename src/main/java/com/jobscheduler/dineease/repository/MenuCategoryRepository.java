package com.jobscheduler.dineease.menu.repository;

import com.jobscheduler.dineease.menu.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    List<MenuCategory> findByRestaurant_IdOrderByDisplayOrderAscIdAsc(Long restaurantId);

    Optional<MenuCategory> findByIdAndRestaurant_Id(Long id, Long restaurantId);
}
