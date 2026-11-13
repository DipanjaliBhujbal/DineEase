import { apiRequest } from "./apiClient";
import type { MenuCategory } from "../types/category";

export const categoryService = {
  getAll: (restaurantId: number): Promise<MenuCategory[]> => {
    return apiRequest<MenuCategory[]>(`/api/menu/categories?restaurantId=${restaurantId}`);
  },
};
