package com.jobscheduler.dineease.repository;

import com.jobscheduler.dineease.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerOrderRepository
        extends JpaRepository<CustomerOrder, Long> {

    // Existing query used by the admin dashboard.
    List<CustomerOrder> findAllByOrderByCreatedAtDescIdDesc();

    // Retrieve orders for a customer, newest first.
    List<CustomerOrder> findAllByPhoneOrderByCreatedAtDescIdDesc(
            String phone);

    // Retrieve orders belonging to one restaurant, newest first.
    List<CustomerOrder> findAllByRestaurant_IdOrderByCreatedAtDescIdDesc(
            Long restaurantId);
}