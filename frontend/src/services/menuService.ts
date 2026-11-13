import { apiRequest } from "./apiClient";
import type { MenuItem, MenuItemRequest } from "../types/menu";

export const menuService = {
  getAll: (restaurantId: number, categoryId?: number): Promise<MenuItem[]> => {
    const params = new URLSearchParams({ restaurantId: String(restaurantId) });
    if (categoryId) params.set("categoryId", String(categoryId));
    return apiRequest<MenuItem[]>(`/api/menu/items?${params.toString()}`);
  },

  getById: (id: number): Promise<MenuItem> => {
    return apiRequest<MenuItem>(`/api/menu/items/${id}`);
  },

  create: (item: MenuItemRequest): Promise<MenuItem> => {
    return apiRequest<MenuItem>("/api/menu/items", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(item),
    });
  },

  update: (id: number, item: MenuItemRequest): Promise<MenuItem> => {
    return apiRequest<MenuItem>(`/api/menu/items/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(item),
    });
  },

  delete: (id: number): Promise<void> => {
    return apiRequest<void>(`/api/menu/items/${id}`, {
      method: "DELETE",
    });
  },
};
