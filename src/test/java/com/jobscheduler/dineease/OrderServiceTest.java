package com.jobscheduler.dineease;

import com.jobscheduler.dineease.dto.CreateOrderRequest;
import com.jobscheduler.dineease.entity.CustomerOrder;
import com.jobscheduler.dineease.menu.entity.MenuItem;
import com.jobscheduler.dineease.menu.entity.Restaurant;
import com.jobscheduler.dineease.menu.repository.MenuItemRepository;
import com.jobscheduler.dineease.menu.repository.RestaurantRepository;
import com.jobscheduler.dineease.repository.CustomerOrderRepository;
import com.jobscheduler.dineease.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private CustomerOrderRepository orderRepository;
    private MenuItemRepository menuItemRepository;
    private RestaurantRepository restaurantRepository;
    private OrderService orderService;

    private Restaurant restaurant;
    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        orderRepository = mock(CustomerOrderRepository.class);
        menuItemRepository = mock(MenuItemRepository.class);
        restaurantRepository = mock(RestaurantRepository.class);

        orderService = new OrderService(
                orderRepository,
                menuItemRepository,
                restaurantRepository
        );

        restaurant = mock(Restaurant.class);
        when(restaurant.getId()).thenReturn(1L);
        when(restaurantRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(restaurant));

        menuItem = mock(MenuItem.class);
        when(menuItem.getId()).thenReturn(10L);
        when(menuItem.getName()).thenReturn("Paneer Tikka");
        when(menuItem.getPrice()).thenReturn(new BigDecimal("249.00"));
        when(menuItem.isAvailable()).thenReturn(true);
        when(menuItem.getRestaurant()).thenReturn(restaurant);
        when(menuItemRepository.findById(10L))
                .thenReturn(Optional.of(menuItem));

        when(orderRepository.save(any(CustomerOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private CreateOrderRequest request(
            Long restaurantId,
            List<CreateOrderRequest.OrderLineRequest> items) {
        return new CreateOrderRequest(
                restaurantId,
                " Test Customer ",
                "9876543210",
                " Test Address ",
                " Pune ",
                "411001",
                items
        );
    }

    private CreateOrderRequest.OrderLineRequest line(
            Long itemId, Integer quantity) {
        return new CreateOrderRequest.OrderLineRequest(itemId, quantity);
    }

    @Test
    void createsOrderUsingDatabasePriceAndCalculatesTotal() {
        CustomerOrder order = orderService.createOrder(
                request(1L, List.of(line(10L, 2)))
        );

        assertEquals("Test Customer", order.getCustomerName());
        assertEquals("Test Address", order.getAddress());
        assertEquals("Pune", order.getCity());
        assertEquals("PLACED", order.getStatus());
        assertEquals(new BigDecimal("498.00"), order.getTotalAmount());
        assertEquals(1, order.getItems().size());
        assertEquals("Paneer Tikka", order.getItems().get(0).getMenuItemName());
        assertEquals(new BigDecimal("249.00"), order.getItems().get(0).getUnitPrice());
        assertEquals(order, order.getItems().get(0).getCustomerOrder());

        verify(orderRepository).save(any(CustomerOrder.class));
    }

    @Test
    void rejectsOrderWhenRestaurantIsInactiveOrMissing() {
        when(restaurantRepository.findByIdAndActiveTrue(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalStateException.class,
                () -> orderService.createOrder(
                        request(99L, List.of(line(10L, 1)))
                )
        );

        verify(orderRepository, never()).save(any(CustomerOrder.class));
    }

    @Test
    void rejectsUnavailableMenuItem() {
        when(menuItem.isAvailable()).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(
                        request(1L, List.of(line(10L, 1)))
                )
        );

        assertTrue(exception.getMessage().contains("unavailable"));
        verify(orderRepository, never()).save(any(CustomerOrder.class));
    }

    @Test
    void rejectsMenuItemFromAnotherRestaurant() {
        Restaurant otherRestaurant = mock(Restaurant.class);
        when(otherRestaurant.getId()).thenReturn(2L);
        when(menuItem.getRestaurant()).thenReturn(otherRestaurant);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(
                        request(1L, List.of(line(10L, 1)))
                )
        );

        assertTrue(exception.getMessage().contains("same restaurant"));
        verify(orderRepository, never()).save(any(CustomerOrder.class));
    }

    @Test
    void rejectsDuplicateMenuItemIds() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(
                        request(1L, List.of(line(10L, 1), line(10L, 2)))
                )
        );

        assertTrue(exception.getMessage().contains("Duplicate menu item ID"));
        verify(orderRepository, never()).save(any(CustomerOrder.class));
    }
    @Test
    void rejectsDirectTransitionFromReadyToDelivered() {
        CustomerOrder order = mock(CustomerOrder.class);
        when(order.getStatus()).thenReturn("READY");
        when(order.getItems()).thenReturn(java.util.Collections.emptyList());
        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatus(
                        42L,
                        com.jobscheduler.dineease.entity.OrderStatus.DELIVERED
                )
        );

        verify(orderRepository, never()).save(any(CustomerOrder.class));
    }
}