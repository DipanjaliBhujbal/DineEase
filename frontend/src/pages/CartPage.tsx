import { Link } from "react-router-dom";
import { useCart } from "../context/CartContext";

export default function CartPage() {
  const {
    items,
    removeFromCart,
    updateQuantity,
    clearCart,
    totalItems,
    totalPrice,
  } = useCart();

  const formatPrice = (price: number) =>
    price.toLocaleString("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 2,
    });

  // Display an empty-cart message when there are no items.
  if (items.length === 0) {
    return (
      <main className="cart-page">
        <h1>Your Cart</h1>

        <div className="empty-menu">
          <span aria-hidden="true">🛒</span>
          <h2>Your cart is empty</h2>
          <p>Add something delicious from our menu.</p>
          <Link to="/">Browse Menu</Link>
        </div>
      </main>
    );
  }

  return (
    <main className="cart-page">
      <div className="cart-page-heading">
        <div>
          <p className="section-eyebrow">YOUR ORDER</p>
          <h1>Your Cart</h1>
          <p>{totalItems} items in your cart</p>
        </div>

        <button
          type="button"
          className="clear-cart-button"
          onClick={clearCart}
        >
          Clear Cart
        </button>
      </div>

      <div className="cart-layout">
        <section
          className="cart-items-list"
          aria-label="Cart items"
        >
          {items.map(({ menuItem, quantity }) => (
            <article
              className="cart-line-item"
              key={menuItem.id}
            >
              <div className="cart-line-image">
                {menuItem.imageUrl ? (
                  <img
                    src={menuItem.imageUrl}
                    alt={menuItem.name}
                  />
                ) : (
                  <span aria-hidden="true">🍽️</span>
                )}
              </div>

              <div className="cart-line-details">
                <p className="menu-card-category">
                  {menuItem.categoryName}
                </p>

                <h2>{menuItem.name}</h2>

                <p>{formatPrice(menuItem.price)} each</p>

                <div className="quantity-controls">
                  <button
                    type="button"
                    aria-label={`Decrease ${menuItem.name} quantity`}
                    onClick={() =>
                      updateQuantity(menuItem.id, quantity - 1)
                    }
                  >
                    −
                  </button>

                  <span>{quantity}</span>

                  <button
                    type="button"
                    aria-label={`Increase ${menuItem.name} quantity`}
                    onClick={() =>
                      updateQuantity(menuItem.id, quantity + 1)
                    }
                  >
                    +
                  </button>
                </div>
              </div>

              <div className="cart-line-total">
                <strong>
                  {formatPrice(menuItem.price * quantity)}
                </strong>

                <button
                  type="button"
                  className="remove-item-button"
                  onClick={() => removeFromCart(menuItem.id)}
                >
                  Remove
                </button>
              </div>
            </article>
          ))}
        </section>

        <aside className="order-summary">
          <h2>Order Summary</h2>

          <div>
            <span>Items ({totalItems})</span>
            <strong>{formatPrice(totalPrice)}</strong>
          </div>

          <div>
            <span>Delivery</span>
            <strong>Calculated at checkout</strong>
          </div>

          <hr />

          <div className="order-total">
            <span>Subtotal</span>
            <strong>{formatPrice(totalPrice)}</strong>
          </div>

          {/* Navigate to the checkout page. */}
          <Link
            to="/checkout"
            className="checkout-button checkout-link"
          >
            Proceed to Checkout
          </Link>

          <Link to="/" className="continue-shopping-link">
            Continue Shopping
          </Link>
        </aside>
      </div>
    </main>
  );
}
