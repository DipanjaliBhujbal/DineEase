import {
  BrowserRouter,
  NavLink,
  Link,
  Route,
  Routes,
} from "react-router-dom";

import { CartProvider, useCart } from "./context/CartContext";
import HomePage from "./pages/HomePage";
import CartPage from "./pages/CartPage";
import CheckoutPage from "./pages/CheckoutPage";
import AdminDashboardPage from "./pages/AdminDashboardPage";
import OrderTrackingPage from "./pages/OrderTrackingPage";
import MyOrdersPage from "./pages/MyOrdersPage";
import RestaurantPartnerDashboardPage from "./pages/RestaurantPartnerDashboardPage";

function Navbar() {
  const { totalItems } = useCart();

  const navItems = [
    { label: "Menu", to: "/" },
    { label: "Cart", to: "/cart" },
    { label: "Track Order", to: "/track-order" },
    { label: "My Orders", to: "/my-orders" },
    { label: "Admin", to: "/admin" },
    { label: "Restaurant Partner", to: "/partner" },
  ];

  return (
    <header className="site-header">
      <nav className="site-nav" aria-label="Main navigation">
        <Link to="/" className="site-brand">
          <span className="brand-mark" aria-hidden="true">D</span>
          <span>DineEase</span>
        </Link>

        <div className="nav-links">
          {navItems.map(({ label, to }) => (
            <NavLink
              key={to}
              to={to}
              end={to === "/"}
              className={({ isActive }) =>
                `nav-link${isActive ? " active" : ""}`
              }
            >
              <span>{label}</span>
              {label === "Cart" && totalItems > 0 && (
                <span className="nav-cart-count" aria-label={`${totalItems} items`}>
                  {totalItems > 99 ? "99+" : totalItems}
                </span>
              )}
            </NavLink>
          ))}
        </div>
      </nav>
    </header>
  );
}

export default function App() {
  return (
    <CartProvider>
      <BrowserRouter>
        <Navbar />

        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/admin" element={<AdminDashboardPage />} />
          <Route path="/partner" element={<RestaurantPartnerDashboardPage />} />
          <Route path="/track-order" element={<OrderTrackingPage />} />
          <Route path="/my-orders" element={<MyOrdersPage />} />
        </Routes>
      </BrowserRouter>
    </CartProvider>
  );
}

