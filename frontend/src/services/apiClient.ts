const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export async function apiRequest<T>(
  endpoint: string,
  options: RequestInit = {},
): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      Accept: "application/json",
      ...options.headers,
    },
  });

  if (!response.ok) {
    const responseText = await response.text();
    let message = "";

    if (responseText) {
      try {
        const body: unknown = JSON.parse(responseText);

        if (
          typeof body === "object" &&
          body !== null &&
          "message" in body &&
          typeof body.message === "string"
        ) {
          message = body.message;
        } else if (
          typeof body === "object" &&
          body !== null &&
          "error" in body &&
          typeof body.error === "string"
        ) {
          message = body.error;
        } else if (typeof body === "string") {
          message = body;
        }
      } catch {
        message = responseText;
      }
    }

    throw new Error(
      message || `Request failed with status ${response.status}`,
    );
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();

  try {
    return JSON.parse(text) as T;
  } catch {
    return text as T;
  }
}