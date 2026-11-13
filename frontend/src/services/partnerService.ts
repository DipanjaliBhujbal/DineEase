import { apiRequest } from "./apiClient";

export interface PartnerOrderItem {
  menuItemId: number;
  menuItemName: string;
  quantity: number;
  unitPrice: number;
  lineTotal: number;
}

export interface PartnerOrder {
  orderId: number;
  customerName: string;
  phone: string;
  address: string;
  city: string;
  pincode: string;
  totalAmount: number;
  status: string;
  createdAt: string;
  items: PartnerOrderItem[];
}

function getPartnerHeaders(username: string, password: string) {
  return {
    Accept: "application/json",
    Authorization: `Basic ${btoa(`${username}:${password}`)}`,
    "Content-Type": "application/json",
  };
}

export const partnerService = {
  getOrders: (
    username: string,
    password: string,
  ): Promise<PartnerOrder[]> => {
    return apiRequest<PartnerOrder[]>("/api/partner/orders", {
      headers: getPartnerHeaders(username, password),
    });
  },

  updateOrderStatus: (
    orderId: number,
    status: string,
    username: string,
    password: string,
  ): Promise<PartnerOrder> => {
    return apiRequest<PartnerOrder>(
      `/api/partner/orders/${orderId}/status`,
      {
        method: "PATCH",
        headers: getPartnerHeaders(username, password),
        body: JSON.stringify({ status }),
      },
    );
  },
};
