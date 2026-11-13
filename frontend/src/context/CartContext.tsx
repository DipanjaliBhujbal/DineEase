import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import type { MenuItem } from "../types/menu";

export interface CartItem {
  menuItem: MenuItem;
  quantity: number;
}

interface CartContextType {
  items: CartItem[];
  addToCart: (item: MenuItem) => void;
  removeFromCart: (itemId: number) => void;
  updateQuantity: (itemId: number, quantity: number) => void;
  clearCart: () => void;
  totalItems: number;
  totalPrice: number;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export function CartProvider({ children }: { children: ReactNode }) {
  const [items, setItems] = useState<CartItem[]>(() => {
    try {
      const savedCart = localStorage.getItem("dineease-cart");

      if (savedCart) {
        return JSON.parse(savedCart) as CartItem[];
      }
    } catch {
      console.error("Unable to restore saved cart.");
    }

    return [];
  });

  useEffect(() => {
    try {
      localStorage.setItem("dineease-cart", JSON.stringify(items));
    } catch {
      console.error("Unable to save cart.");
    }
  }, [items]);

  const addToCart = (menuItem: MenuItem) => {
    setItems((currentItems) => {
      const existingItem = currentItems.find(
        (item) => item.menuItem.id === menuItem.id,
      );

      if (existingItem) {
        return currentItems.map((item) =>
          item.menuItem.id === menuItem.id
            ? { ...item, quantity: item.quantity + 1 }
            : item,
        );
      }

      return [...currentItems, { menuItem, quantity: 1 }];
    });
  };

  const removeFromCart = (itemId: number) => {
    setItems((currentItems) =>
      currentItems.filter((item) => item.menuItem.id !== itemId),
    );
  };

  const updateQuantity = (itemId: number, quantity: number) => {
    if (quantity <= 0) {
      removeFromCart(itemId);
      return;
    }

    setItems((currentItems) =>
      currentItems.map((item) =>
        item.menuItem.id === itemId ? { ...item, quantity } : item,
      ),
    );
  };

  const clearCart = () => setItems([]);

  const totalItems = items.reduce(
    (total, item) => total + item.quantity,
    0,
  );

  const totalPrice = items.reduce(
    (total, item) => total + item.menuItem.price * item.quantity,
    0,
  );

  return (
    <CartContext.Provider
      value={{
        items,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        totalItems,
        totalPrice,
      }}
    >
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  const context = useContext(CartContext);

  if (!context) {
    throw new Error("useCart must be used inside a CartProvider");
  }

  return context;
}
