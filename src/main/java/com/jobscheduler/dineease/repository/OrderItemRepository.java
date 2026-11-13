package com.jobscheduler.dineease.repository;

import com.jobscheduler.dineease.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {
}
