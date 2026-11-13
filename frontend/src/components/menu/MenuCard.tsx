import type { MenuItem } from "../../types/menu";

interface MenuCardProps {
  item: MenuItem;
  onAddToCart: (item: MenuItem) => void;
}

export default function MenuCard({ item, onAddToCart }: MenuCardProps) {
  return (
    <article className="menu-card">
      <div className="menu-card-image">
        {item.imageUrl ? (
          <img src={item.imageUrl} alt={item.name} />
        ) : (
          <div className="menu-image-placeholder" aria-hidden="true">
            🍽️
          </div>
        )}
      </div>

      <div className="menu-card-content">
        <p className="menu-card-category">{item.categoryName}</p>
        <h3>{item.name}</h3>
        <p className="menu-card-description">
          {item.description || "Freshly prepared and ready to enjoy."}
        </p>

        <div className="menu-card-footer">
          <strong>
            {item.price.toLocaleString("en-IN", {
              style: "currency",
              currency: "INR",
              maximumFractionDigits: 2,
            })}
          </strong>

          <button
            type="button"
            onClick={() => onAddToCart(item)}
            disabled={!item.available}
          >
            {item.available ? "Add to Cart" : "Unavailable"}
          </button>
        </div>
      </div>
    </article>
  );
}
