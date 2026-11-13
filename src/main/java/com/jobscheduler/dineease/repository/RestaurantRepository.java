package com.jobscheduler.dineease.menu.repository;

import com.jobscheduler.dineease.menu.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByIdAndActiveTrue(Long id);

    List<Restaurant> findByActiveTrueOrderByNameAsc();
}
