import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { orderService, type OrderResponse } from "../services/orderService";

const stages = [
  "PLACED",
  "CONFIRMED",
  "PREPARING",
  "READY",
  "OUT_FOR_DELIVERY",
  "DELIVERED",
];

function formatStatus(status: string) {
  return status.replaceAll("_", " ").toLowerCase().replace(/\b\w/g, (c) =>
    c.toUpperCase()
  );
}

export default function OrderTrackingPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [orderId, setOrderId] = useState(searchParams.get("orderId") ?? "");
  const [order, setOrder] = useState<OrderResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function trackOrder(id = orderId) {
    const parsedId = Number(id.trim());

    if (!id.trim() || !Number.isInteger(parsedId) || parsedId <= 0) {
      setError("Enter a valid order ID.");
      setOrder(null);
      return;
    }

    setLoading(true);
    setError("");
    setOrder(null);

    try {
      const result = await orderService.getById(parsedId);
      setOrder(result);
      setOrderId(String(parsedId));
      setSearchParams({ orderId: String(parsedId) });
    } catch {
      setError("We couldn't find that order. Check the order ID and try again.");
    } finally {
      setLoading(false);
    }
  }

  // Automatically load the order when orderId is present in the URL.
  useEffect(() => {
    const idFromUrl = searchParams.get("orderId");

    if (idFromUrl) {
      void trackOrder(idFromUrl);
    }
    // Run on initial page load only.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Refresh the order every 10 seconds while it is in progress.
  useEffect(() => {
    if (
      !order ||
      order.status === "DELIVERED" ||
      order.status === "CANCELLED"
    ) {
      return;
    }

    const intervalId = window.setInterval(async () => {
      try {
        const updatedOrder = await orderService.getById(order.orderId);
        setOrder(updatedOrder);
      } catch {
        // Keep the last successfully loaded order if a refresh fails.
      }
    }, 10000);

    return () => window.clearInterval(intervalId);
  }, [order?.orderId, order?.status]);

  const cancelled = order?.status === "CANCELLED";
  const currentStage = order ? stages.indexOf(order.status) : -1;

  return (
    <main style={{ maxWidth: 760, margin: "36px auto", padding: "0 20px" }}>
      <h1>Track Your Order</h1>
      <p>Enter your order ID to check its latest status.</p>

      <form
        onSubmit={(event) => {
          event.preventDefault();
          void trackOrder();
        }}
        style={{
          display: "flex",
          gap: 12,
          flexWrap: "wrap",
          margin: "24px 0",
        }}
      >
        <input
          aria-label="Order ID"
          type="number"
          min="1"
          value={orderId}
          onChange={(event) => setOrderId(event.target.value)}
          placeholder="e.g. 2"
          style={{
            flex: "1 1 200px",
            padding: 12,
            border: "1px solid #ccc",
            borderRadius: 8,
          }}
        />

        <button
          type="submit"
          disabled={loading}
          style={{
            padding: "12px 22px",
            borderRadius: 8,
            cursor: "pointer",
          }}
        >
          {loading ? "Checking..." : "Track Order"}
        </button>
      </form>

      {error && (
        <p role="alert" style={{ color: "#b42318" }}>
          {error}
        </p>
      )}

      {order && (
        <section
          aria-live="polite"
          style={{
            border: "1px solid #e5e7eb",
            borderRadius: 14,
            padding: 24,
            boxShadow: "0 4px 18px rgba(0,0,0,0.05)",
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
            <div>
              <h2 style={{ marginBottom: 6 }}>Order #{order.orderId}</h2>
              <p style={{ marginTop: 0 }}>For {order.customerName}</p>
            </div>
            <strong>{formatStatus(order.status)}</strong>
          </div>

          {cancelled ? (
            <p role="status">This order has been cancelled.</p>
          ) : currentStage >= 0 ? (
            <div style={{ margin: "28px 0" }}>
              {stages.map((stage, index) => {
                const complete = index <= currentStage;

                return (
                  <div
                    key={stage}
                    style={{
                      display: "flex",
                      alignItems: "center",
                      gap: 12,
                      marginBottom: 16,
                      color: complete ? "#15803d" : "#6b7280",
                    }}
                  >
                    <span aria-hidden="true">{complete ? "✓" : "○"}</span>
                    <span>{formatStatus(stage)}</span>
                    {index === currentStage && <small>Current status</small>}
                  </div>
                );
              })}
            </div>
          ) : (
            <p>Status update: {formatStatus(order.status)}</p>
          )}

          <h3>Order items</h3>

          {order.items.map((item) => (
            <div
              key={item.menuItemId}
              style={{
                display: "flex",
                justifyContent: "space-between",
                gap: 16,
                padding: "10px 0",
                borderBottom: "1px solid #eee",
              }}
            >
              <span>
                {item.menuItemName} × {item.quantity}
              </span>
              <span>₹{Number(item.lineTotal).toFixed(2)}</span>
            </div>
          ))}

          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              marginTop: 20,
              fontSize: 18,
              fontWeight: 700,
            }}
          >
            <span>Total</span>
            <span>₹{Number(order.totalAmount).toFixed(2)}</span>
          </div>

          <p style={{ color: "#6b7280", fontSize: 13 }}>
            Order status refreshes automatically every 10 seconds.
          </p>
        </section>
      )}
    </main>
  );
}
