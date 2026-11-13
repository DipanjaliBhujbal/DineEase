
export interface MenuItem {
  id: number;
  restaurantId: number;
  name: string;
  description: string | null;
  price: number;
  imageUrl: string | null;
  available: boolean;
  categoryId: number;
  categoryName: string;
}

export interface MenuItemRequest {
  name: string;
  description: string | null;
  price: number;
  imageUrl: string | null;
  available: boolean;
  categoryId: number;
}
