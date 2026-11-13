import { customerService } from "./customerService";
import { apiRequest } from "./apiClient";

export interface CreateOrderRequest {
  restaurantId: number;
  customerName: string;
  phone: string;
  address: string;
  city: string;
  pincode: string;
  items: {
    menuItemId: number;
    quantity: number;
  }[];
}

export interface OrderResponse {
  orderId: number;
  customerName: string;
  status: string;
  totalAmount: number;
  createdAt: string;
  items: {
    menuItemId: number;
    menuItemName: string;
    quantity: number;
    unitPrice: number;
    lineTotal: number;
  }[];
}

export const orderService = {
  create: (order: CreateOrderRequest): Promise<OrderResponse> => {
    return apiRequest<OrderResponse>("/api/orders", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(order),
    });
  },

  getById: (orderId: number): Promise<OrderResponse> => {
    const token = customerService.getSessionToken();
    if (!token) {
      throw new Error("Please verify your phone number before tracking an order.");
    }

    return apiRequest<OrderResponse>(`/api/customer/orders/${orderId}`, {
      method: "GET",
      headers: { "X-Customer-Session": token },
    });
  },
};
