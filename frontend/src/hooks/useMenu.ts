import { useCallback, useEffect, useRef, useState } from "react";
import { menuService } from "../services/menuService";
import { categoryService } from "../services/categoryService";
import { restaurantService } from "../services/restaurantService";
import type { MenuItem } from "../types/menu";
import type { MenuCategory } from "../types/category";
import type { Restaurant } from "../types/restaurant";

const RESTAURANT_STORAGE_KEY = "dineease.selectedRestaurantId";

export function useMenu() {
  const [items, setItems] = useState<MenuItem[]>([]);
  const [categories, setCategories] = useState<MenuCategory[]>([]);
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);
  const [selectedRestaurantId, setSelectedRestaurantIdState] = useState<number | null>(() => {
    const savedId = localStorage.getItem(RESTAURANT_STORAGE_KEY);
    return savedId && Number.isInteger(Number(savedId)) && Number(savedId) > 0
      ? Number(savedId)
      : null;
  });
  const [selectedCategory, setSelectedCategory] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const requestIdRef = useRef(0);

  const setSelectedRestaurantId = useCallback((id: number) => {
    if (!Number.isInteger(id) || id <= 0) return;

    localStorage.setItem(RESTAURANT_STORAGE_KEY, String(id));
    setSelectedRestaurantIdState(id);
    setSelectedCategory(null);
  }, []);

  useEffect(() => {
    let cancelled = false;

    restaurantService.getAll()
      .then((result) => {
        if (cancelled) return;

        setRestaurants(result);

        if (result.length === 0) {
          setItems([]);
          setCategories([]);
          setError("No restaurants are currently available.");
          setLoading(false);
          return;
        }

        const savedId = localStorage.getItem(RESTAURANT_STORAGE_KEY);
        const savedRestaurant = result.find(
          (restaurant) => restaurant.id === Number(savedId)
        );
        const restaurant = savedRestaurant ?? result[0];

        if (!savedRestaurant) {
          localStorage.setItem(RESTAURANT_STORAGE_KEY, String(restaurant.id));
        }

        setSelectedRestaurantIdState((currentId) =>
          currentId === restaurant.id ? currentId : restaurant.id
        );
      })
      .catch((err: unknown) => {
        if (cancelled) return;
        setError(err instanceof Error ? err.message : "Unable to load restaurants.");
        setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, []);

  const loadMenu = useCallback(async () => {
    if (selectedRestaurantId === null) return;

    const requestId = ++requestIdRef.current;
    setLoading(true);
    setError(null);

    try {
      const [menuItems, menuCategories] = await Promise.all([
        menuService.getAll(selectedRestaurantId, selectedCategory ?? undefined),
        categoryService.getAll(selectedRestaurantId),
      ]);

      if (requestId !== requestIdRef.current) return;

      setItems(menuItems);
      setCategories(menuCategories);
    } catch (err: unknown) {
      if (requestId !== requestIdRef.current) return;

      setError(
        err instanceof Error
          ? err.message
          : "Unable to load the menu. Please try again."
      );
    } finally {
      if (requestId === requestIdRef.current) {
        setLoading(false);
      }
    }
  }, [selectedRestaurantId, selectedCategory]);

  useEffect(() => {
    void loadMenu();

    return () => {
      requestIdRef.current += 1;
    };
  }, [loadMenu]);

  return {
    items,
    categories,
    restaurants,
    selectedRestaurantId,
    setSelectedRestaurantId,
    selectedCategory,
    setSelectedCategory,
    loading,
    error,
    reloadMenu: loadMenu,
  };
}