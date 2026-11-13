import { useState } from "react";
import type { FormEvent } from "react";
import { customerService } from "../services/customerService";
import type { OrderResponse } from "../services/orderService";

export default function MyOrdersPage() {
  const [phone, setPhone] = useState("");
  const [otp, setOtp] = useState("");
  const [otpRequested, setOtpRequested] = useState(false);
  const [verified, setVerified] = useState(false);
  const [orders, setOrders] = useState<OrderResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  function resetToVerification() {
    setVerified(false);
    setOtpRequested(false);
    setOtp("");
    setOrders([]);
    setMessage("Your session has expired. Please verify your phone again.");
  }

  function isSessionError(err: unknown) {
    const text = err instanceof Error ? err.message : String(err);
    return /401|unauthorized|session expired|invalid customer session/i.test(text);
  }

  async function requestOtp(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setMessage("");
    setLoading(true);

    try {
      const result = await customerService.requestOtp(phone.trim());
      setOtpRequested(true);
      setMessage(
        typeof result === "string"
          ? result
          : result.message || "OTP requested. Check your configured delivery channel.",
      );
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to request OTP.");
    } finally {
      setLoading(false);
    }
  }

  async function verifyOtp(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setMessage("");
    setLoading(true);

    try {
      await customerService.verifyOtp(phone.trim(), otp.trim());
      const result = await customerService.getOrders();
      setOrders(result);
      setVerified(true);
      setMessage("Phone verified successfully.");
    } catch (err) {
      if (isSessionError(err)) {
        await customerService.logout().catch(() => undefined);
        resetToVerification();
      } else {
        setError(err instanceof Error ? err.message : "OTP verification failed.");
      }
    } finally {
      setLoading(false);
    }
  }

  async function refreshOrders() {
    setError("");
    setLoading(true);

    try {
      setOrders(await customerService.getOrders());
    } catch (err) {
      if (isSessionError(err)) {
        await customerService.logout().catch(() => undefined);
        resetToVerification();
      } else {
        setError(err instanceof Error ? err.message : "Unable to load orders.");
      }
    } finally {
      setLoading(false);
    }
  }

  async function logout() {
    setError("");
    setMessage("");
    setLoading(true);

    try {
      await customerService.logout();
      setVerified(false);
      setOtpRequested(false);
      setPhone("");
      setOtp("");
      setOrders([]);
      setMessage("You have logged out.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to log out.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main style={{ maxWidth: 850, margin: "36px auto", padding: "0 20px" }}>
      <h1>My Orders</h1>
      <p>Verify your phone number to view orders associated with it.</p>

      {!verified ? (
        <>
          <form
            onSubmit={requestOtp}
            style={{ display: "flex", gap: 12, flexWrap: "wrap" }}
          >
            <input
              type="tel"
              aria-label="Phone number"
              autoComplete="tel"
              placeholder="Registered phone number"
              value={phone}
              onChange={(event) => setPhone(event.target.value)}
              required
              disabled={loading}
              style={{ flex: "1 1 220px", padding: 12 }}
            />
            <button type="submit" disabled={loading}>
              {loading ? "Please wait..." : "Request OTP"}
            </button>
          </form>

          {otpRequested && (
            <form
              onSubmit={verifyOtp}
              style={{
                display: "flex",
                gap: 12,
                flexWrap: "wrap",
                marginTop: 16,
              }}
            >
              <input
                type="text"
                inputMode="numeric"
                autoComplete="one-time-code"
                aria-label="One-time password"
                placeholder="Enter OTP"
                value={otp}
                onChange={(event) => setOtp(event.target.value)}
                required
                disabled={loading}
                style={{ flex: "1 1 180px", padding: 12 }}
              />
              <button type="submit" disabled={loading}>
                {loading ? "Verifying..." : "Verify & View Orders"}
              </button>
            </form>
          )}
        </>
      ) : (
        <button onClick={() => void refreshOrders()} disabled={loading}>
          {loading ? "Refreshing..." : "Refresh Orders"}
        </button>
      )}

      {message && <p role="status">{message}</p>}
      {error && (
        <p role="alert" style={{ color: "#b42318" }}>
          {error}
        </p>
      )}

      {verified && (
        <section style={{ marginTop: 28 }}>
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              flexWrap: "wrap",
              gap: 12,
            }}
          >
            <h2>Your order history</h2>
            <button onClick={() => void logout()} disabled={loading}>
              {loading ? "Please wait..." : "Log out"}
            </button>
          </div>

          {orders.length === 0 ? (
            <p>No orders found for this verified phone number.</p>
          ) : (
            orders.map((order) => (
              <article
                key={order.orderId}
                style={{
                  border: "1px solid #e5e7eb",
                  borderRadius: 12,
                  padding: 20,
                  marginBottom: 16,
                }}
              >
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    gap: 12,
                    flexWrap: "wrap",
                  }}
                >
                  <h3 style={{ marginTop: 0 }}>Order #{order.orderId}</h3>
                  <strong>{order.status.replaceAll("_", " ")}</strong>
                </div>

                <p>Placed: {new Date(order.createdAt).toLocaleString()}</p>

                {order.items.map((item, index) => (
                  <div
                    key={`${order.orderId}-${item.menuItemId}-${index}`}
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      gap: 12,
                      padding: "8px 0",
                    }}
                  >
                    <span>
                      {item.menuItemName} × {item.quantity}
                    </span>
                    <span>₹{Number(item.lineTotal).toFixed(2)}</span>
                  </div>
                ))}

                <hr />
                <p style={{ textAlign: "right", fontWeight: 700 }}>
                  Total: ₹{Number(order.totalAmount).toFixed(2)}
                </p>
              </article>
            ))
          )}
        </section>
      )}
    </main>
  );
}
