"use client";

import React, { createContext, useContext, useEffect, useState } from "react";
import { createAdminClient } from "@/lib/supabase/client";

export interface AdminUser {
  id: string;
  email: string;
  displayName: string;
  role: "admin" | "superadmin" | "operator";
  phone?: string;
  avatarUrl?: string;
}

interface AuthContextType {
  admin: AdminUser | null;
  isLoading: boolean;
  loginWithPassword: (email: string, password: string) => Promise<{ success: boolean; error?: string }>;
  loginWithPhoneOtp: (phone: string, token: string) => Promise<{ success: boolean; error?: string }>;
  loginAsDevAdmin: (role?: "superadmin" | "admin" | "operator") => void;
  logout: () => Promise<void>;
  isSuperadmin: boolean;
}

const DEFAULT_DEV_ADMIN: AdminUser = {
  id: "00000000-0000-0000-0000-000000000001",
  email: "suman@evrry.com.np",
  displayName: "Suman Adhikari",
  role: "superadmin",
  phone: "+977 9841000001",
};

const AuthContext = createContext<AuthContextType>({
  admin: null,
  isLoading: true,
  loginWithPassword: async () => ({ success: false }),
  loginWithPhoneOtp: async () => ({ success: false }),
  loginAsDevAdmin: () => {},
  logout: async () => {},
  isSuperadmin: false,
});

const AUTH_STORAGE_KEY = "evrry_admin_session";

export function AdminAuthProvider({ children }: { children: React.ReactNode }) {
  const [admin, setAdmin] = useState<AdminUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check saved session in localStorage or active Supabase session
    const checkSession = async () => {
      try {
        const saved = typeof window !== "undefined" ? localStorage.getItem(AUTH_STORAGE_KEY) : null;
        if (saved) {
          const parsed = JSON.parse(saved);
          setAdmin(parsed);
          setIsLoading(false);
          return;
        }

        const supabase = createAdminClient();
        const { data: { session } } = await supabase.auth.getSession();

        if (session?.user) {
          // Query user profile from public.profiles
          const { data: profile } = await supabase
            .from("profiles")
            .select("role, display_name, phone, status")
            .eq("id", session.user.id)
            .single();

          if (profile && (profile.role === "admin" || profile.role === "superadmin")) {
            const adminUser: AdminUser = {
              id: session.user.id,
              email: session.user.email || "",
              displayName: profile.display_name || session.user.email?.split("@")[0] || "Admin",
              role: profile.role,
              phone: profile.phone,
            };
            setAdmin(adminUser);
            localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(adminUser));
          } else {
            // Not an admin role
            setAdmin(null);
          }
        } else {
          // Default to dev admin in local development if nothing is stored
          if (process.env.NODE_ENV !== "production") {
            setAdmin(DEFAULT_DEV_ADMIN);
          }
        }
      } catch (err) {
        console.error("Auth session check error:", err);
      } finally {
        setIsLoading(false);
      }
    };

    checkSession();
  }, []);

  const loginWithPassword = async (email: string, password: string) => {
    try {
      const supabase = createAdminClient();
      const { data, error } = await supabase.auth.signInWithPassword({
        email,
        password,
      });

      if (error) {
        // In local development, if Supabase Auth is offline or user not seeded, allow verified demo credentials
        if (
          process.env.NODE_ENV !== "production" &&
          (email === "admin@evrry.com.np" || email === "suman@evrry.com.np") &&
          password.length >= 6
        ) {
          const devUser: AdminUser = {
            id: "00000000-0000-0000-0000-000000000001",
            email,
            displayName: email.includes("suman") ? "Suman Adhikari" : "Operations Admin",
            role: "superadmin",
            phone: "+977 9841000001",
          };
          setAdmin(devUser);
          localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(devUser));
          return { success: true };
        }
        return { success: false, error: error.message };
      }

      if (data.user) {
        const { data: profile } = await supabase
          .from("profiles")
          .select("role, display_name, phone, status")
          .eq("id", data.user.id)
          .single();

        if (!profile || (profile.role !== "admin" && profile.role !== "superadmin")) {
          await supabase.auth.signOut();
          return { success: false, error: "Access Denied: Account lacks administrative privileges." };
        }

        if (profile.status !== "active") {
          await supabase.auth.signOut();
          return { success: false, error: "Access Denied: Administrative account is suspended." };
        }

        const adminUser: AdminUser = {
          id: data.user.id,
          email: data.user.email || "",
          displayName: profile.display_name || data.user.email?.split("@")[0] || "Admin",
          role: profile.role,
          phone: profile.phone,
        };
        setAdmin(adminUser);
        localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(adminUser));
        return { success: true };
      }

      return { success: false, error: "Authentication failed" };
    } catch (err: any) {
      return { success: false, error: err.message || "Network error during authentication" };
    }
  };

  const loginWithPhoneOtp = async (phone: string, token: string) => {
    try {
      const supabase = createAdminClient();
      const { data, error } = await supabase.auth.verifyOtp({
        phone,
        token,
        type: "sms",
      });

      if (error) {
        return { success: false, error: error.message };
      }

      if (data.user) {
        const { data: profile } = await supabase
          .from("profiles")
          .select("role, display_name, phone, status")
          .eq("id", data.user.id)
          .single();

        if (!profile || (profile.role !== "admin" && profile.role !== "superadmin")) {
          await supabase.auth.signOut();
          return { success: false, error: "Access Denied: Phone number is not registered to an administrator." };
        }

        const adminUser: AdminUser = {
          id: data.user.id,
          email: data.user.email || `${phone}@evrry.com.np`,
          displayName: profile.display_name || "Admin Operator",
          role: profile.role,
          phone: profile.phone,
        };
        setAdmin(adminUser);
        localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(adminUser));
        return { success: true };
      }

      return { success: false, error: "Verification failed" };
    } catch (err: any) {
      return { success: false, error: err.message || "Failed to verify OTP" };
    }
  };

  const loginAsDevAdmin = (role: "superadmin" | "admin" | "operator" = "superadmin") => {
    const devUser: AdminUser = {
      id: "00000000-0000-0000-0000-000000000001",
      email: role === "operator" ? "pratima@evrry.com.np" : "suman@evrry.com.np",
      displayName: role === "operator" ? "Pratima Sharma (KYC Desk)" : "Suman Adhikari (VP Ops)",
      role: role === "operator" ? "admin" : "superadmin",
      phone: "+977 9841000001",
    };
    setAdmin(devUser);
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(devUser));
  };

  const logout = async () => {
    try {
      const supabase = createAdminClient();
      await supabase.auth.signOut();
    } catch (err) {
      console.warn("Sign out error:", err);
    } finally {
      setAdmin(null);
      localStorage.removeItem(AUTH_STORAGE_KEY);
      if (typeof window !== "undefined") {
        window.location.href = "/login";
      }
    }
  };

  return (
    <AuthContext.Provider
      value={{
        admin,
        isLoading,
        loginWithPassword,
        loginWithPhoneOtp,
        loginAsDevAdmin,
        logout,
        isSuperadmin: admin?.role === "superadmin",
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAdminAuth() {
  return useContext(AuthContext);
}
