package com.jobscheduler.dineease.service;

import com.jobscheduler.dineease.dto.CreateOrderRequest;
import com.jobscheduler.dineease.entity.CustomerOrder;
import com.jobscheduler.dineease.entity.OrderItem;
import com.jobscheduler.dineease.entity.OrderStatus;
import com.jobscheduler.dineease.menu.entity.MenuItem;
import com.jobscheduler.dineease.menu.entity.Restaurant;
import com.jobscheduler.dineease.menu.repository.MenuItemRepository;
import com.jobscheduler.dineease.menu.repository.RestaurantRepository;
import com.jobscheduler.dineease.repository.CustomerOrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public OrderService(
            CustomerOrderRepository orderRepository,
            MenuItemRepository menuItemRepository,
            RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public CustomerOrder createOrder(CreateOrderRequest request) {
        Restaurant restaurant = restaurantRepository
                .findByIdAndActiveTrue(request.restaurantId())
                .orElseThrow(() -> new IllegalStateException(
                        "No active restaurant is configured."));

        CustomerOrder order = new CustomerOrder();
        order.setRestaurant(restaurant);
        order.setCustomerName(request.customerName().trim());
        order.setPhone(request.phone());
        order.setAddress(request.address().trim());
        order.setCity(request.city().trim());
        order.setPincode(request.pincode());

        BigDecimal total = BigDecimal.ZERO;
        Set<Long> processedMenuItemIds = new HashSet<>();

        for (CreateOrderRequest.OrderLineRequest line : request.items()) {
            Long menuItemId = line.menuItemId();
            Integer quantity = line.quantity();

            if (!processedMenuItemIds.add(menuItemId)) {
                throw new IllegalArgumentException(
                        "Duplicate menu item ID: " + menuItemId);
            }

            MenuItem menuItem = menuItemRepository.findById(menuItemId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Menu item not found: " + menuItemId));

            if (menuItem.getRestaurant() == null
                    || !restaurant.getId().equals(
                    menuItem.getRestaurant().getId())) {
                throw new IllegalArgumentException(
                        "All items in an order must belong to the same restaurant.");
            }

            if (!menuItem.isAvailable()) {
                throw new IllegalArgumentException(
                        "Menu item is unavailable: " + menuItem.getName());
            }

            BigDecimal unitPrice = menuItem.getPrice();
            BigDecimal lineTotal =
                    unitPrice.multiply(BigDecimal.valueOf(quantity));

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItemId(menuItem.getId());
            orderItem.setMenuItemName(menuItem.getName());
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(unitPrice);

            order.addItem(orderItem);
            total = total.add(lineTotal);
        }

        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PLACED.name());

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<CustomerOrder> getAllOrders() {
        List<CustomerOrder> orders =
                orderRepository.findAllByOrderByCreatedAtDescIdDesc();

        orders.forEach(order -> order.getItems().size());
        return orders;
    }

    @Transactional(readOnly = true)
    public CustomerOrder getOrderById(Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + orderId));

        order.getItems().size();
        return order;
    }

    @Transactional(readOnly = true)
    public List<CustomerOrder> getOrdersByPhone(String phone) {
        List<CustomerOrder> orders =
                orderRepository.findAllByPhoneOrderByCreatedAtDescIdDesc(
                        phone);

        orders.forEach(order -> order.getItems().size());
        return orders;
    }

    @Transactional
    public CustomerOrder updateOrderStatus(
            Long orderId,
            OrderStatus newStatus) {

        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + orderId));

        order.getItems().size();

        OrderStatus currentStatus;
        try {
            currentStatus = OrderStatus.valueOf(order.getStatus());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "The order has an unrecognized current status.");
        }

        if (currentStatus == newStatus) {
            return order;
        }

        if (!isAllowedTransition(currentStatus, newStatus)) {
            throw new IllegalArgumentException(
                    "Cannot change order status from "
                            + currentStatus + " to " + newStatus);
        }

        order.setStatus(newStatus.name());
        return orderRepository.save(order);
    }

    private boolean isAllowedTransition(
            OrderStatus current,
            OrderStatus next) {

        return switch (current) {
            case PLACED ->
                    next == OrderStatus.CONFIRMED
                            || next == OrderStatus.CANCELLED;

            case CONFIRMED ->
                    next == OrderStatus.PREPARING
                            || next == OrderStatus.CANCELLED;

            case PREPARING ->
                    next == OrderStatus.READY
                            || next == OrderStatus.CANCELLED;

            case READY ->
                    next == OrderStatus.OUT_FOR_DELIVERY;

            case OUT_FOR_DELIVERY ->
                    next == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED -> false;
        };
    }

    // Partner-specific methods

    @Transactional(readOnly = true)
    public List<CustomerOrder> getOrdersForRestaurant(Long restaurantId) {
        List<CustomerOrder> orders =
                orderRepository
                        .findAllByRestaurant_IdOrderByCreatedAtDescIdDesc(
                                restaurantId);

        orders.forEach(order -> order.getItems().size());
        return orders;
    }

    @Transactional
    public CustomerOrder updatePartnerOrderStatus(
            Long orderId,
            Long restaurantId,
            OrderStatus newStatus) {

        CustomerOrder order = orderRepository.findById(orderId)
                .filter(existingOrder ->
                        existingOrder.getRestaurant() != null
                                && restaurantId.equals(
                                existingOrder.getRestaurant().getId()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found for this restaurant."));

        order.getItems().size();

        OrderStatus currentStatus;
        try {
            currentStatus = OrderStatus.valueOf(order.getStatus());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "The order has an unrecognized current status.");
        }

        if (currentStatus == newStatus) {
            return order;
        }

        if (!isAllowedPartnerTransition(currentStatus, newStatus)) {
            throw new IllegalArgumentException(
                    "Restaurant partners cannot change order status from "
                            + currentStatus + " to " + newStatus);
        }

        order.setStatus(newStatus.name());
        return orderRepository.save(order);
    }

    private boolean isAllowedPartnerTransition(
            OrderStatus current,
            OrderStatus next) {

        return switch (current) {
            case PLACED ->
                    next == OrderStatus.CONFIRMED
                            || next == OrderStatus.CANCELLED;

            case CONFIRMED ->
                    next == OrderStatus.PREPARING
                            || next == OrderStatus.CANCELLED;

            case PREPARING ->
                    next == OrderStatus.READY;

            case READY, OUT_FOR_DELIVERY, DELIVERED, CANCELLED -> false;
        };
    }
}
