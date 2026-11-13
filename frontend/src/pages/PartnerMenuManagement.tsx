import { useEffect, useState } from "react";
import { partnerMenuService } from "../services/partnerMenuService";
import type { MenuItem, MenuItemRequest } from "../types/menu";
import type { MenuCategory } from "../types/category";

interface Props {
  username: string;
  password: string;
}

const emptyForm: MenuItemRequest = {
  name: "",
  description: "",
  price: 0,
  imageUrl: "",
  available: true,
  categoryId: 0,
};

const inputStyle: React.CSSProperties = {
  width: "100%",
  boxSizing: "border-box",
  padding: "10px",
  marginTop: "6px",
  marginBottom: "14px",
  border: "1px solid #d5d7dc",
  borderRadius: "6px",
  fontSize: "14px",
};

const primaryButton: React.CSSProperties = {
  padding: "10px 16px",
  background: "#202124",
  color: "#fff",
  border: "none",
  borderRadius: "6px",
  cursor: "pointer",
};

const secondaryButton: React.CSSProperties = {
  padding: "8px 12px",
  background: "#fff",
  color: "#202124",
  border: "1px solid #d5d7dc",
  borderRadius: "6px",
  cursor: "pointer",
};

function formatPrice(price: number): string {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
  }).format(price);
}

export default function PartnerMenuManagement({
  username,
  password,
}: Props) {
  const [items, setItems] = useState<MenuItem[]>([]);
  const [categories, setCategories] = useState<MenuCategory[]>([]);
  const [form, setForm] = useState<MenuItemRequest>(emptyForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  async function loadMenu() {
    setLoading(true);
    setError("");

    try {
      const [loadedCategories, loadedItems] = await Promise.all([
        partnerMenuService.getCategories(username, password),
        partnerMenuService.getItems(username, password),
      ]);

      setCategories(loadedCategories);
      setItems(loadedItems);
    } catch (err: unknown) {
      setError(
        err instanceof Error
          ? err.message
          : "Could not load your menu. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadMenu();
  }, [username, password]);

  function resetForm() {
    setForm({
      ...emptyForm,
      categoryId: categories[0]?.id ?? 0,
    });
    setEditingId(null);
  }

  function startEditing(item: MenuItem) {
    setForm({
      name: item.name,
      description: item.description ?? "",
      price: item.price,
      imageUrl: item.imageUrl ?? "",
      available: item.available,
      categoryId: item.categoryId,
    });
    setEditingId(item.id);
    setError("");
    setMessage("");
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setMessage("");

    if (categories.length === 0) {
      setError("Create a menu category before adding dishes.");
      return;
    }

    if (!Number.isFinite(form.price) || form.price <= 0) {
      setError("Enter a price greater than zero.");
      return;
    }

    if (!categories.some((category) => category.id === Number(form.categoryId))) {
      setError("Please select a valid menu category.");
      return;
    }

    const request: MenuItemRequest = {
      ...form,
      name: form.name.trim(),
      description: form.description?.trim() || null,
      imageUrl: form.imageUrl?.trim() || null,
      price: Number(form.price),
      categoryId: Number(form.categoryId),
    };

    if (!request.name) {
      setError("Dish name is required.");
      return;
    }

    setSaving(true);

    try {
      if (editingId === null) {
        await partnerMenuService.createItem(request, username, password);
        setMessage("Dish added successfully.");
      } else {
        await partnerMenuService.updateItem(
          editingId,
          request,
          username,
          password,
        );
        setMessage("Dish updated successfully.");
      }

      resetForm();
      await loadMenu();
    } catch (err: unknown) {
      setError(
        err instanceof Error ? err.message : "Could not save this dish.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function toggleAvailability(item: MenuItem) {
    setError("");
    setMessage("");

    try {
      await partnerMenuService.updateItem(
        item.id,
        {
          name: item.name,
          description: item.description,
          price: item.price,
          imageUrl: item.imageUrl,
          available: !item.available,
          categoryId: item.categoryId,
        },
        username,
        password,
      );

      setMessage(`${item.name} is now ${item.available ? "unavailable" : "available"}.`);
      await loadMenu();
    } catch (err: unknown) {
      setError(
        err instanceof Error
          ? err.message
          : "Could not change dish availability.",
      );
    }
  }

  async function deleteItem(item: MenuItem) {
    const confirmed = window.confirm(
      `Delete "${item.name}" from your menu? This cannot be undone.`,
    );

    if (!confirmed) return;

    setError("");
    setMessage("");

    try {
      await partnerMenuService.deleteItem(item.id, username, password);
      if (editingId === item.id) resetForm();
      setItems((current) => current.filter((entry) => entry.id !== item.id));
      setMessage(`${item.name} was deleted.`);
    } catch (err: unknown) {
      setError(
        err instanceof Error ? err.message : "Could not delete this dish.",
      );
    }
  }

  return (
    <section
      style={{
        background: "#fff",
        padding: "24px",
        borderRadius: "12px",
        boxShadow: "0 2px 10px rgba(0,0,0,0.06)",
        marginBottom: "24px",
      }}
    >
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
          <h2 style={{ margin: "0 0 6px" }}>Menu Management</h2>
          <p style={{ color: "#666", margin: 0 }}>
            Add dishes, update prices, and control availability.
          </p>
        </div>
        <button
          type="button"
          style={secondaryButton}
          onClick={() => void loadMenu()}
          disabled={loading || saving}
        >
          {loading ? "Loading..." : "Refresh Menu"}
        </button>
      </div>

      {error && (
        <div role="alert" style={{ marginTop: "16px", color: "#a32121" }}>
          {error}
        </div>
      )}
      {message && (
        <div role="status" style={{ marginTop: "16px", color: "#216e39" }}>
          {message}
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        style={{
          marginTop: "24px",
          padding: "20px",
          background: "#f8f9fb",
          borderRadius: "8px",
        }}
      >
        <h3 style={{ marginTop: 0 }}>
          {editingId === null ? "Add a Dish" : `Edit Dish #${editingId}`}
        </h3>

        <label htmlFor="partner-dish-name">Dish name *</label>
        <input
          id="partner-dish-name"
          style={inputStyle}
          value={form.name}
          onChange={(event) =>
            setForm((current) => ({ ...current, name: event.target.value }))
          }
          maxLength={150}
          required
        />

        <label htmlFor="partner-dish-description">Description</label>
        <textarea
          id="partner-dish-description"
          style={{ ...inputStyle, minHeight: "80px", resize: "vertical" }}
          value={form.description ?? ""}
          onChange={(event) =>
            setForm((current) => ({
              ...current,
              description: event.target.value,
            }))
          }
          rows={3}
        />

        <label htmlFor="partner-dish-price">Price (₹) *</label>
        <input
          id="partner-dish-price"
          style={inputStyle}
          type="number"
          min="0.01"
          step="0.01"
          value={form.price || ""}
          onChange={(event) =>
            setForm((current) => ({
              ...current,
              price: event.target.value === "" ? 0 : Number(event.target.value),
            }))
          }
          required
        />

        <label htmlFor="partner-dish-category">Category *</label>
        <select
          id="partner-dish-category"
          style={inputStyle}
          value={form.categoryId || ""}
          onChange={(event) =>
            setForm((current) => ({
              ...current,
              categoryId: Number(event.target.value),
            }))
          }
          required
        >
          <option value="" disabled>Select a category</option>
          {categories.map((category) => (
            <option key={category.id} value={category.id}>
              {category.name}
            </option>
          ))}
        </select>

        <label htmlFor="partner-dish-image">Image URL (optional)</label>
        <input
          id="partner-dish-image"
          style={inputStyle}
          type="url"
          value={form.imageUrl ?? ""}
          onChange={(event) =>
            setForm((current) => ({
              ...current,
              imageUrl: event.target.value,
            }))
          }
          placeholder="https://example.com/dish.jpg"
        />

        <label
          style={{
            display: "flex",
            alignItems: "center",
            gap: "8px",
            marginBottom: "18px",
          }}
        >
          <input
            type="checkbox"
            checked={form.available}
            onChange={(event) =>
              setForm((current) => ({
                ...current,
                available: event.target.checked,
              }))
            }
          />
          Available for ordering
        </label>

        <div style={{ display: "flex", gap: "10px", flexWrap: "wrap" }}>
          <button type="submit" style={primaryButton} disabled={saving}>
            {saving
              ? "Saving..."
              : editingId === null
                ? "Add Dish"
                : "Save Changes"}
          </button>
          {editingId !== null && (
            <button
              type="button"
              style={secondaryButton}
              onClick={resetForm}
              disabled={saving}
            >
              Cancel Edit
            </button>
          )}
        </div>
      </form>

      <h3 style={{ marginTop: "28px" }}>Your Dishes ({items.length})</h3>

      {loading ? (
        <p>Loading menu...</p>
      ) : items.length === 0 ? (
        <p>No dishes found. Use the form above to add your first dish.</p>
      ) : (
        <div style={{ overflowX: "auto" }}>
          <table
            style={{
              width: "100%",
              minWidth: "650px",
              borderCollapse: "collapse",
              textAlign: "left",
            }}
          >
            <thead>
              <tr>
                {["Dish", "Category", "Price", "Availability", "Actions"].map(
                  (heading) => (
                    <th
                      key={heading}
                      style={{
                        padding: "12px 10px",
                        borderBottom: "1px solid #eee",
                        background: "#f8f9fb",
                      }}
                    >
                      {heading}
                    </th>
                  ),
                )}
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.id}>
                  <td style={{ padding: "12px 10px", borderBottom: "1px solid #eee" }}>
                    <strong>{item.name}</strong>
                    {item.description && (
                      <div style={{ color: "#666", fontSize: "13px", marginTop: "4px" }}>
                        {item.description}
                      </div>
                    )}
                  </td>
                  <td style={{ padding: "12px 10px", borderBottom: "1px solid #eee" }}>
                    {item.categoryName}
                  </td>
                  <td style={{ padding: "12px 10px", borderBottom: "1px solid #eee", whiteSpace: "nowrap" }}>
                    {formatPrice(item.price)}
                  </td>
                  <td style={{ padding: "12px 10px", borderBottom: "1px solid #eee" }}>
                    <button
                      type="button"
                      style={secondaryButton}
                      onClick={() => void toggleAvailability(item)}
                      disabled={saving}
                      aria-label={`Make ${item.name} ${item.available ? "unavailable" : "available"}`}
                    >
                      {item.available ? "Available" : "Unavailable"}
                    </button>
                  </td>
                  <td style={{ padding: "12px 10px", borderBottom: "1px solid #eee", whiteSpace: "nowrap" }}>
                    <button
                      type="button"
                      style={secondaryButton}
                      onClick={() => startEditing(item)}
                    >
                      Edit
                    </button>{" "}
                    <button
                      type="button"
                      style={{
                        ...secondaryButton,
                        color: "#a32121",
                      }}
                      onClick={() => void deleteItem(item)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
