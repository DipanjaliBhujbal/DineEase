import { apiRequest } from "./apiClient";
import type { OrderResponse } from "./orderService";

interface OtpRequestResponse {
  message: string;
}

interface VerifyOtpResponse {
  message: string;
  token: string;
  expiresInSeconds: number;
}

const SESSION_KEY = "dineease_customer_session";

export const customerService = {
  requestOtp(phone: string): Promise<OtpRequestResponse> {
    return apiRequest<OtpRequestResponse>("/api/customer/auth/request-otp", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ phone }),
    });
  },

  async verifyOtp(phone: string, otp: string): Promise<void> {
    const result = await apiRequest<VerifyOtpResponse>(
      "/api/customer/auth/verify-otp",
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ phone, otp }),
      },
    );

    sessionStorage.setItem(SESSION_KEY, result.token);
  },

  getOrders(): Promise<OrderResponse[]> {
    const token = sessionStorage.getItem(SESSION_KEY);

    if (!token) {
      throw new Error("Please verify your phone number first.");
    }

    return apiRequest<OrderResponse[]>("/api/customer/orders", {
      method: "GET",
      headers: { "X-Customer-Session": token },
    });
  },

  async logout(): Promise<void> {
    const token = sessionStorage.getItem(SESSION_KEY);

    try {
      if (token) {
        await apiRequest<{ message: string }>("/api/customer/auth/logout", {
          method: "POST",
          headers: { "X-Customer-Session": token },
        });
      }
    } finally {
      sessionStorage.removeItem(SESSION_KEY);
    }
  },


  getSessionToken(): string | null {
    return sessionStorage.getItem(SESSION_KEY);
  },
  hasSession(): boolean {
    return Boolean(sessionStorage.getItem(SESSION_KEY));
  },
};
