package com.jobscheduler.dineease.customer.controller;

import com.jobscheduler.dineease.dto.OrderResponse;
import com.jobscheduler.dineease.entity.CustomerOrder;
import com.jobscheduler.dineease.entity.OrderItem;
import com.jobscheduler.dineease.service.CustomerSessionService;
import com.jobscheduler.dineease.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerOrderTrackingControllerTest {

    private OrderService orderService;
    private CustomerSessionService sessionService;
    private CustomerOrderTrackingController controller;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        sessionService = mock(CustomerSessionService.class);
        controller = new CustomerOrderTrackingController(
                orderService, sessionService);
    }

    @Test
    void rejectsMissingSession() {
        when(sessionService.getVerifiedPhone(null)).thenReturn(null);

        ResponseEntity<?> response = controller.getOrderById(1L, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verifyNoInteractions(orderService);
    }

    @Test
    void rejectsInvalidSession() {
        when(sessionService.getVerifiedPhone("invalid-token")).thenReturn(null);

        ResponseEntity<?> response =
                controller.getOrderById(1L, "invalid-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verifyNoInteractions(orderService);
    }

    @Test
    void doesNotExposeAnotherCustomersOrder() {
        when(sessionService.getVerifiedPhone("valid-token"))
                .thenReturn("9876543210");

        CustomerOrder order = mock(CustomerOrder.class);
        when(orderService.getOrderById(12L)).thenReturn(order);
        when(order.getPhone()).thenReturn("9999999999");

        ResponseEntity<?> response =
                controller.getOrderById(12L, "valid-token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService).getOrderById(12L);
    }

    @Test
    void returnsNotFoundForMissingOrder() {
        when(sessionService.getVerifiedPhone("valid-token"))
                .thenReturn("9876543210");
        when(orderService.getOrderById(999L))
                .thenThrow(new IllegalArgumentException("Order not found"));

        ResponseEntity<?> response =
                controller.getOrderById(999L, "valid-token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void returnsOrderToVerifiedOwner() {
        when(sessionService.getVerifiedPhone("valid-token"))
                .thenReturn("9876543210");

        OrderItem item = mock(OrderItem.class);
        when(item.getMenuItemId()).thenReturn(10L);
        when(item.getMenuItemName()).thenReturn("Paneer Tikka");
        when(item.getQuantity()).thenReturn(2);
        when(item.getUnitPrice()).thenReturn(new BigDecimal("249.00"));

        CustomerOrder order = mock(CustomerOrder.class);
        when(orderService.getOrderById(12L)).thenReturn(order);
        when(order.getPhone()).thenReturn("9876543210");
        when(order.getItems()).thenReturn(List.of(item));
        when(order.getId()).thenReturn(12L);
        when(order.getCustomerName()).thenReturn("Test Customer");
        when(order.getStatus()).thenReturn("PLACED");
        when(order.getTotalAmount()).thenReturn(new BigDecimal("498.00"));

        ResponseEntity<?> response =
                controller.getOrderById(12L, "valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(OrderResponse.class, response.getBody());

        OrderResponse body = (OrderResponse) response.getBody();
        assertEquals(12L, body.orderId());
        assertEquals("Test Customer", body.customerName());
        assertEquals(1, body.items().size());
        assertEquals(new BigDecimal("498.00"),
                body.items().get(0).lineTotal());
    }
}