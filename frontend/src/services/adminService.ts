import { apiRequest } from "./apiClient";

export interface AdminOrderItem {
  menuItemId: number;
  menuItemName: string;
  quantity: number;
  unitPrice: number;
  lineTotal: number;
}

export interface AdminOrder {
  orderId: number;
  customerName: string;
  phone: string;
  address: string;
  city: string;
  pincode: string;
  totalAmount: number;
  status: string;
  createdAt: string;
  items: AdminOrderItem[];
}

function getAdminHeaders(username: string, password: string) {
  return {
    Accept: "application/json",
    Authorization: `Basic ${btoa(`${username}:${password}`)}`,
    "Content-Type": "application/json",
  };
}

export const adminService = {
  getOrders: (
    username: string,
    password: string,
  ): Promise<AdminOrder[]> => {
    return apiRequest<AdminOrder[]>("/api/admin/orders", {
      headers: getAdminHeaders(username, password),
    });
  },

  getOrderById: (
    orderId: number,
    username: string,
    password: string,
  ): Promise<AdminOrder> => {
    return apiRequest<AdminOrder>(`/api/admin/orders/${orderId}`, {
      headers: getAdminHeaders(username, password),
    });
  },

  updateOrderStatus: (
    orderId: number,
    status: string,
    username: string,
    password: string,
  ): Promise<AdminOrder> => {
    return apiRequest<AdminOrder>(
      `/api/admin/orders/${orderId}/status`,
      {
        method: "PATCH",
        headers: getAdminHeaders(username, password),
        body: JSON.stringify({ status }),
      },
    );
  },
};
