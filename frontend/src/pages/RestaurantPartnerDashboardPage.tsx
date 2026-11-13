import { useState } from "react";
import PartnerMenuManagement from "./PartnerMenuManagement";
import {
  partnerService,
  type PartnerOrder,
} from "../services/partnerService";

const statusOptions: Record<string, string[]> = {
  PLACED: ["CONFIRMED", "CANCELLED"],
  CONFIRMED: ["PREPARING", "CANCELLED"],
  PREPARING: ["READY"],
  READY: [],
  OUT_FOR_DELIVERY: [],
  DELIVERED: [],
  CANCELLED: [],
};

const styles: Record<string, React.CSSProperties> = {
  page: {
    minHeight: "100vh",
    background: "#f5f6fa",
    padding: "32px 20px",
    color: "#202124",
  },
  container: { maxWidth: "1400px", margin: "0 auto" },
  heading: { fontSize: "30px", marginBottom: "8px" },
  subtitle: { color: "#666", marginBottom: "28px" },
  card: {
    background: "#fff",
    padding: "24px",
    borderRadius: "12px",
    boxShadow: "0 2px 10px rgba(0,0,0,0.06)",
    marginBottom: "24px",
  },
  input: {
    display: "block",
    width: "100%",
    boxSizing: "border-box",
    padding: "12px",
    marginTop: "6px",
    marginBottom: "16px",
    border: "1px solid #d5d7dc",
    borderRadius: "6px",
    fontSize: "15px",
  },
  button: {
    padding: "11px 18px",
    background: "#202124",
    color: "#fff",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
  },
  secondaryButton: {
    padding: "8px 12px",
    background: "#fff",
    color: "#202124",
    border: "1px solid #d5d7dc",
    borderRadius: "6px",
    cursor: "pointer",
  },
  tableWrapper: { overflowX: "auto", width: "100%" },
  table: {
    width: "100%",
    minWidth: "1050px",
    borderCollapse: "collapse",
    textAlign: "left",
    fontSize: "14px",
  },
  cell: {
    padding: "14px 12px",
    borderBottom: "1px solid #eee",
    verticalAlign: "top",
  },
  select: {
    padding: "9px 12px",
    border: "1px solid #d5d7dc",
    borderRadius: "6px",
    maxWidth: "200px",
    background: "#fff",
    fontSize: "14px",
  },
  error: {
    background: "#fff0f0",
    color: "#a32121",
    padding: "12px",
    borderRadius: "6px",
    marginBottom: "16px",
    overflowWrap: "anywhere",
  },
  success: {
    background: "#edf8ef",
    color: "#216e39",
    padding: "12px",
    borderRadius: "6px",
    marginBottom: "16px",
  },
};

function formatAmount(amount: number): string {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(amount);
}

function formatStatus(status: string): string {
  return status.replaceAll("_", " ");
}

export default function RestaurantPartnerDashboardPage() {
  const [username, setUsername] = useState("partner_test");
  const [password, setPassword] = useState("");
  const [authenticated, setAuthenticated] = useState(false);
  const [orders, setOrders] = useState<PartnerOrder[]>([]);
  const [loading, setLoading] = useState(false);
  const [updatingOrderId, setUpdatingOrderId] = useState<number | null>(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  const filteredOrders =
    statusFilter === "ALL"
      ? orders
      : orders.filter((order) => order.status === statusFilter);

  const activeOrders = orders.filter((order) =>
    ["PLACED", "CONFIRMED", "PREPARING"].includes(order.status),
  ).length;

  const readyOrders = orders.filter(
    (order) => order.status === "READY",
  ).length;

  async function handleLogin(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);

    try {
      const result = await partnerService.getOrders(username, password);
      setOrders(result);
      setAuthenticated(true);
      setSuccess("Partner login successful.");
    } catch {
      setAuthenticated(false);
      setError(
        "Login failed or orders could not be loaded. Check your credentials and backend server.",
      );
    } finally {
      setLoading(false);
    }
  }

  async function refreshOrders() {
    setError("");
    setSuccess("");
    setLoading(true);

    try {
      const result = await partnerService.getOrders(username, password);
      setOrders(result);
      setSuccess("Orders refreshed.");
    } catch {
      setError("Unable to refresh orders. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  async function handleStatusUpdate(orderId: number, newStatus: string) {
    setError("");
    setSuccess("");
    setUpdatingOrderId(orderId);

    try {
      const updatedOrder = await partnerService.updateOrderStatus(
        orderId,
        newStatus,
        username,
        password,
      );

      setOrders((currentOrders) =>
        currentOrders.map((order) =>
          order.orderId === orderId ? updatedOrder : order,
        ),
      );

      setSuccess(
        `Order #${orderId} updated to ${formatStatus(newStatus)}.`,
      );
    } catch (err: unknown) {
      const message =
        err instanceof Error ? err.message : "An unexpected error occurred.";
      setError(`Could not update order #${orderId}: ${message}`);
    } finally {
      setUpdatingOrderId(null);
    }
  }

  function handleLogout() {
    setAuthenticated(false);
    setUsername("partner_test");
    setPassword("");
    setOrders([]);
    setStatusFilter("ALL");
    setError("");
    setSuccess("");
  }

  return (
    <main style={styles.page}>
      <div style={styles.container}>
        <h1 style={styles.heading}>Restaurant Partner Dashboard</h1>
        <p style={styles.subtitle}>
          Review your restaurant orders and manage preparation status.
        </p>

        {error && <div role="alert" style={styles.error}>{error}</div>}
        {success && <div role="status" style={styles.success}>{success}</div>}

        {!authenticated ? (
          <section style={{ ...styles.card, maxWidth: "420px" }}>
            <h2 style={{ marginTop: 0 }}>Partner Login</h2>
            <p style={{ color: "#666", fontSize: "14px" }}>
              Sign in with your restaurant partner account.
            </p>

            <form onSubmit={handleLogin}>
              <label htmlFor="partner-username">Username</label>
              <input
                id="partner-username"
                style={styles.input}
                value={username}
                onChange={(event) => setUsername(event.target.value)}
                autoComplete="username"
                required
              />

              <label htmlFor="partner-password">Password</label>
              <input
                id="partner-password"
                style={styles.input}
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                autoComplete="current-password"
                required
              />

              <button type="submit" style={styles.button} disabled={loading}>
                {loading ? "Signing in..." : "Sign In"}
              </button>
            </form>
          </section>
        ) : (
          <>
            <section
              style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
                gap: "16px",
                marginBottom: "24px",
              }}
            >
              {[
                { title: "Restaurant Orders", value: orders.length },
                { title: "Active Preparation", value: activeOrders },
                { title: "Ready for Pickup", value: readyOrders },
              ].map((metric) => (
                <div
                  key={metric.title}
                  style={{ ...styles.card, marginBottom: 0 }}
                >
                  <p style={{ color: "#666", marginTop: 0 }}>{metric.title}</p>
                  <h2 style={{ fontSize: "28px", marginBottom: 0 }}>
                    {metric.value}
                  </h2>
                </div>
              ))}
            </section>

            <PartnerMenuManagement username={username} password={password} />

            <section style={styles.card}>
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  flexWrap: "wrap",
                  gap: "12px",
                }}
              >
                <div>
                  <h2 style={{ margin: "0 0 6px" }}>Restaurant Orders</h2>
                  <p style={{ color: "#666", margin: 0 }}>
                    {orders.length} order{orders.length === 1 ? "" : "s"} found
                  </p>
                </div>

                <div style={{ display: "flex", gap: "10px" }}>
                  <button
                    type="button"
                    style={styles.secondaryButton}
                    onClick={refreshOrders}
                    disabled={loading}
                  >
                    {loading ? "Refreshing..." : "Refresh"}
                  </button>
                  <button
                    type="button"
                    style={styles.secondaryButton}
                    onClick={handleLogout}
                  >
                    Logout
                  </button>
                </div>
              </div>
            </section>

            <section style={styles.card}>
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  flexWrap: "wrap",
                  gap: "12px",
                  marginBottom: "20px",
                }}
              >
                <label htmlFor="partner-status-filter">Filter by status:</label>
                <select
                  id="partner-status-filter"
                  style={styles.select}
                  value={statusFilter}
                  onChange={(event) => setStatusFilter(event.target.value)}
                >
                  <option value="ALL">All Orders</option>
                  <option value="PLACED">Placed</option>
                  <option value="CONFIRMED">Confirmed</option>
                  <option value="PREPARING">Preparing</option>
                  <option value="READY">Ready</option>
                  <option value="OUT_FOR_DELIVERY">Out for Delivery</option>
                  <option value="DELIVERED">Delivered</option>
                  <option value="CANCELLED">Cancelled</option>
                </select>
                <span style={{ color: "#666", fontSize: "14px" }}>
                  Showing {filteredOrders.length} of {orders.length} orders
                </span>
              </div>

              {filteredOrders.length === 0 ? (
                <p>
                  {orders.length === 0
                    ? "No orders have been placed for this restaurant."
                    : "No orders match the selected status."}
                </p>
              ) : (
                <div style={styles.tableWrapper}>
                  <table style={styles.table}>
                    <thead>
                      <tr>
                        {[
                          "Order",
                          "Date & Time",
                          "Customer",
                          "Contact",
                          "Delivery Address",
                          "Items",
                          "Total",
                          "Status",
                          "Update Status",
                        ].map((heading) => (
                          <th
                            key={heading}
                            style={{
                              ...styles.cell,
                              background: "#f8f9fb",
                              whiteSpace: "nowrap",
                            }}
                          >
                            {heading}
                          </th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {filteredOrders.map((order) => {
                        const allowedStatuses = statusOptions[order.status] ?? [];

                        return (
                          <tr key={order.orderId}>
                            <td style={styles.cell}><strong>#{order.orderId}</strong></td>
                            <td style={{ ...styles.cell, minWidth: "150px" }}>
                              {new Date(order.createdAt).toLocaleString("en-IN")}
                            </td>
                            <td style={{ ...styles.cell, minWidth: "140px" }}>
                              {order.customerName}
                            </td>
                            <td style={{ ...styles.cell, whiteSpace: "nowrap" }}>
                              {order.phone}
                            </td>
                            <td style={{ ...styles.cell, minWidth: "190px" }}>
                              {order.address}, {order.city} - {order.pincode}
                            </td>
                            <td style={{ ...styles.cell, minWidth: "160px" }}>
                              {order.items.map((item, index) => (
                                <div key={`${item.menuItemId}-${index}`} style={{ marginBottom: "8px" }}>
                                  {item.menuItemName} × {item.quantity}
                                  <div style={{ color: "#666" }}>
                                    {formatAmount(item.lineTotal)}
                                  </div>
                                </div>
                              ))}
                            </td>
                            <td style={{ ...styles.cell, whiteSpace: "nowrap" }}>
                              <strong>{formatAmount(order.totalAmount)}</strong>
                            </td>
                            <td style={styles.cell}>
                              <span
                                style={{
                                  display: "inline-block",
                                  background: "#eef0f4",
                                  padding: "6px 9px",
                                  borderRadius: "6px",
                                  fontSize: "12px",
                                  fontWeight: 600,
                                  whiteSpace: "nowrap",
                                }}
                              >
                                {formatStatus(order.status)}
                              </span>
                            </td>
                            <td style={styles.cell}>
                              {allowedStatuses.length === 0 ? (
                                <span style={{ color: "#777" }}>No action</span>
                              ) : (
                                <select
                                  aria-label={`Update status for order ${order.orderId}`}
                                  style={styles.select}
                                  value=""
                                  disabled={updatingOrderId === order.orderId}
                                  onChange={(event) => {
                                    const value = event.target.value;
                                    if (value) {
                                      void handleStatusUpdate(order.orderId, value);
                                    }
                                  }}
                                >
                                  <option value="">
                                    {updatingOrderId === order.orderId
                                      ? "Updating..."
                                      : "Choose status"}
                                  </option>
                                  {allowedStatuses.map((status) => (
                                    <option key={status} value={status}>
                                      {formatStatus(status)}
                                    </option>
                                  ))}
                                </select>
                              )}
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              )}
            </section>
          </>
        )}
      </div>
    </main>
  );
}
