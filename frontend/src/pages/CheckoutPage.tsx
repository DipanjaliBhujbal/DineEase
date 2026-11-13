import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { orderService } from "../services/orderService";

function CheckoutPage() {
  const { items, totalPrice, clearCart } = useCart();
  const navigate = useNavigate();

  const [customerName, setCustomerName] = useState("");
  const [phone, setPhone] = useState("");
  const [address, setAddress] = useState("");
  const [city, setCity] = useState("");
  const [pincode, setPincode] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");

    if (items.length === 0) {
      setError("Your cart is empty. Please add items before checkout.");
      return;
    }

    setLoading(true);

    try {
      const restaurantId = Number(localStorage.getItem("dineease.selectedRestaurantId"));

      if (!restaurantId) {
        throw new Error("Please select a restaurant before placing your order.");
      }

      const order = await orderService.create({
        restaurantId,
        customerName: customerName.trim(),
        phone: phone.trim(),
        address: address.trim(),
        city: city.trim(),
        pincode: pincode.trim(),
        items: items.map(({ menuItem, quantity }) => ({
          menuItemId: menuItem.id,
          quantity,
        })),
      });

      clearCart();

      navigate(`/track-order?orderId=${order.orderId}`);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Unable to place your order. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  }

  if (items.length === 0) {
    return (
      <main className="checkout-page">
        <section className="checkout-card">
          <h1>Your cart is empty</h1>
          <p>Add some delicious dishes before checking out.</p>
          <Link to="/" className="checkout-button checkout-link">
            Browse Menu
          </Link>
        </section>
      </main>
    );
  }

  return (
    <main className="checkout-page">
      <section className="checkout-card">
        <div className="checkout-heading">
          <p className="eyebrow">Almost there</p>
          <h1>Checkout</h1>
          <p>Enter your delivery details to place your order.</p>
        </div>

        <div className="checkout-summary">
          <h3>Order summary</h3>

          {items.map(({ menuItem, quantity }) => (
            <div className="summary-row" key={menuItem.id}>
              <span>
                {menuItem.name} × {quantity}
              </span>
              <span>₹{(menuItem.price * quantity).toFixed(2)}</span>
            </div>
          ))}

          <div className="summary-total">
            <span>Estimated total</span>
            <strong>₹{totalPrice.toFixed(2)}</strong>
          </div>
        </div>

        <form className="checkout-form" onSubmit={handleSubmit}>
          <label htmlFor="customerName">Full name</label>
          <input
            id="customerName"
            type="text"
            value={customerName}
            onChange={(event) => setCustomerName(event.target.value)}
            maxLength={100}
            autoComplete="name"
            required
          />

          <label htmlFor="phone">Mobile number</label>
          <input
            id="phone"
            type="tel"
            value={phone}
            onChange={(event) => setPhone(event.target.value)}
            pattern="[6-9][0-9]{9}"
            title="Enter a valid 10-digit Indian mobile number"
            maxLength={10}
            autoComplete="tel"
            required
          />

          <label htmlFor="address">Delivery address</label>
          <textarea
            id="address"
            value={address}
            onChange={(event) => setAddress(event.target.value)}
            maxLength={500}
            autoComplete="street-address"
            rows={3}
            required
          />

          <label htmlFor="city">City</label>
          <input
            id="city"
            type="text"
            value={city}
            onChange={(event) => setCity(event.target.value)}
            maxLength={100}
            autoComplete="address-level2"
            required
          />

          <label htmlFor="pincode">PIN code</label>
          <input
            id="pincode"
            type="text"
            inputMode="numeric"
            value={pincode}
            onChange={(event) => setPincode(event.target.value)}
            pattern="[0-9]{6}"
            title="Enter a valid 6-digit PIN code"
            maxLength={6}
            autoComplete="postal-code"
            required
          />

          {error && (
            <div className="checkout-error" role="alert">
              {error}
            </div>
          )}

          <button
            type="submit"
            className="checkout-button"
            disabled={loading}
          >
            {loading
              ? "Placing order..."
              : `Place Order · ₹${totalPrice.toFixed(2)}`}
          </button>

          <Link to="/cart" className="continue-shopping">
            Back to Cart
          </Link>
        </form>
      </section>
    </main>
  );
}

export default CheckoutPage;
