import { apiRequest } from "./apiClient";
import type { Restaurant } from "../types/restaurant";

export const restaurantService = {
  getAll: (): Promise<Restaurant[]> => {
    return apiRequest<Restaurant[]>("/api/restaurants");
  },
};
