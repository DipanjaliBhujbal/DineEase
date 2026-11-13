import type { MenuItem } from "../../types/menu";
import MenuCard from "./MenuCard";

interface MenuGridProps {
  items: MenuItem[];
  onAddToCart: (item: MenuItem) => void;
}

export default function MenuGrid({ items, onAddToCart }: MenuGridProps) {
  if (items.length === 0) {
    return (
      <div className="empty-menu">
        <span aria-hidden="true">🍽️</span>
        <h3>No menu items found</h3>
        <p>Try selecting another category.</p>
      </div>
    );
  }

  return (
    <div className="menu-grid">
      {items.map((item) => (
        <MenuCard
          key={item.id}
          item={item}
          onAddToCart={onAddToCart}
        />
      ))}
    </div>
  );
}
