import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { useMenu } from "../hooks/useMenu";
import CategoryFilter from "../components/menu/CategoryFilter";
import MenuGrid from "../components/menu/MenuGrid";
import { useCart } from "../context/CartContext";

export default function HomePage() {
  const {
    items,
    categories,
    restaurants,
    selectedRestaurantId,
    setSelectedRestaurantId,
    selectedCategory,
    setSelectedCategory,
    loading,
    error,
    reloadMenu,
  } = useMenu();

  const { addToCart, clearCart, totalItems, totalPrice } = useCart();
  const [searchQuery, setSearchQuery] = useState("");

  const selectedRestaurant = restaurants.find(
    (restaurant) => restaurant.id === selectedRestaurantId
  );

  const filteredItems = useMemo(() => {
    const query = searchQuery.trim().toLowerCase();

    if (!query) return items;

    return items.filter((item) =>
      [item.name, item.description ?? "", item.categoryName]
        .some((value) => value.toLowerCase().includes(query))
    );
  }, [items, searchQuery]);

  return (
    <main className="home-page">
      <section className="hero-section">
        <p className="hero-eyebrow">GOOD FOOD, GOOD MOOD</p>
        <h1>Discover your next favourite meal.</h1>
        <p>
          Explore our menu of freshly prepared dishes, made to make your day
          delicious.
        </p>
      </section>

      <section className="menu-section">
        <div className="section-heading">
          <div>
            <p className="section-eyebrow">OUR MENU</p>
            <h2>Explore the Menu</h2>
          </div>
          <span>
            {loading ? "Loading..." : `${filteredItems.length} ${filteredItems.length === 1 ? "item" : "items"}`}
          </span>
        </div>

        <div className="restaurant-selector">
          <label htmlFor="restaurant-select">Choose a restaurant</label>
          <select
            id="restaurant-select"
            value={selectedRestaurantId ?? ""}
            onChange={(event) => {
              const nextRestaurantId = Number(event.target.value);

              if (nextRestaurantId === selectedRestaurantId) return;

              if (
                totalItems > 0 &&
                !window.confirm(
                  "Switching restaurants will clear your current cart. Continue?"
                )
              ) {
                return;
              }

              clearCart();
              setSelectedRestaurantId(nextRestaurantId);
              setSearchQuery("");
            }}
            disabled={restaurants.length === 0}
          >
            {restaurants.length === 0 ? (
              <option value="">No restaurants available</option>
            ) : (
              restaurants.map((restaurant) => (
                <option key={restaurant.id} value={restaurant.id}>
                  {restaurant.name}
                </option>
              ))
            )}
          </select>
        </div>

        {selectedRestaurant?.description && (
          <p>{selectedRestaurant.description}</p>
        )}

        <div className="menu-search">
          <label htmlFor="dish-search">Search dishes</label>
          <input
            id="dish-search"
            type="search"
            value={searchQuery}
            onChange={(event) => setSearchQuery(event.target.value)}
            placeholder="Search by dish name or description..."
            aria-label="Search dishes by name, description, or category"
          />
          {searchQuery && (
            <button
              type="button"
              onClick={() => setSearchQuery("")}
              aria-label="Clear dish search"
            >
              Clear
            </button>
          )}
        </div>

        <CategoryFilter
          categories={categories}
          selectedCategory={selectedCategory}
          onSelect={setSelectedCategory}
        />

        {loading ? (
          <p className="status-message">Loading delicious food...</p>
        ) : error ? (
          <div className="status-message error-message">
            <p>{error}</p>
            <button type="button" onClick={() => void reloadMenu()}>
              Try Again
            </button>
          </div>
        ) : filteredItems.length > 0 ? (
          <MenuGrid items={filteredItems} onAddToCart={addToCart} />
        ) : (
          <div className="status-message">
            <p>
              {searchQuery.trim()
                ? `No dishes found for "${searchQuery.trim()}".`
                : "No dishes are available in this category."}
            </p>
            {searchQuery && (
              <button type="button" onClick={() => setSearchQuery("")}>
                Clear Search
              </button>
            )}
          </div>
        )}
      </section>

      {totalItems > 0 && (
        <aside className="cart-summary">
          <div>
            <strong>
              Cart · {totalItems} {totalItems === 1 ? "item" : "items"}
            </strong>
            <p>
              Total:{" "}
              {totalPrice.toLocaleString("en-IN", {
                style: "currency",
                currency: "INR",
                maximumFractionDigits: 2,
              })}
            </p>
          </div>

          <Link to="/cart" className="view-cart-link">
            View Cart
          </Link>
        </aside>
      )}
    </main>
  );
}