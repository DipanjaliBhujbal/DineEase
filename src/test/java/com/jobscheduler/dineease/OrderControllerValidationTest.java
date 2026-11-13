package com.jobscheduler.dineease;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "dineease.admin.password=test-only-password")
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void missingCustomerNameReturnsBadRequest() throws Exception {
        String request = """
                {
                  "restaurantId": 1,
                  "phone": "9876543210",
                  "address": "123 Test Street",
                  "city": "Pune",
                  "pincode": "411001",
                  "items": [
                    {
                      "menuItemId": 1,
                      "quantity": 1
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("customerName")));
    }

    @Test
    void zeroQuantityReturnsBadRequest() throws Exception {
        String request = """
                {
                  "restaurantId": 1,
                  "customerName": "Test Customer",
                  "phone": "9876543210",
                  "address": "123 Test Street",
                  "city": "Pune",
                  "pincode": "411001",
                  "items": [
                    {
                      "menuItemId": 1,
                      "quantity": 0
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("quantity")));
    }

    @Test
    void negativeRestaurantIdReturnsBadRequest() throws Exception {
        String request = """
                {
                  "restaurantId": -1,
                  "customerName": "Test Customer",
                  "phone": "9876543210",
                  "address": "123 Test Street",
                  "city": "Pune",
                  "pincode": "411001",
                  "items": [
                    {
                      "menuItemId": 1,
                      "quantity": 1
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("restaurantId")));
    }
}