import { apiRequest } from "./apiClient";
import type { MenuItem, MenuItemRequest } from "../types/menu";
import type { MenuCategory } from "../types/category";

function getPartnerHeaders(username: string, password: string) {
  return {
    Accept: "application/json",
    Authorization: `Basic ${btoa(`${username}:${password}`)}`,
    "Content-Type": "application/json",
  };
}

export const partnerMenuService = {
  getCategories: (
    username: string,
    password: string,
  ): Promise<MenuCategory[]> =>
    apiRequest<MenuCategory[]>("/api/partner/menu/categories", {
      headers: getPartnerHeaders(username, password),
    }),

  getItems: (
    username: string,
    password: string,
  ): Promise<MenuItem[]> =>
    apiRequest<MenuItem[]>("/api/partner/menu/items", {
      headers: getPartnerHeaders(username, password),
    }),

  createItem: (
    item: MenuItemRequest,
    username: string,
    password: string,
  ): Promise<MenuItem> =>
    apiRequest<MenuItem>("/api/partner/menu/items", {
      method: "POST",
      headers: getPartnerHeaders(username, password),
      body: JSON.stringify(item),
    }),

  updateItem: (
    id: number,
    item: MenuItemRequest,
    username: string,
    password: string,
  ): Promise<MenuItem> =>
    apiRequest<MenuItem>(`/api/partner/menu/items/${id}`, {
      method: "PUT",
      headers: getPartnerHeaders(username, password),
      body: JSON.stringify(item),
    }),

  deleteItem: (
    id: number,
    username: string,
    password: string,
  ): Promise<void> =>
    apiRequest<void>(`/api/partner/menu/items/${id}`, {
      method: "DELETE",
      headers: getPartnerHeaders(username, password),
    }),
};
