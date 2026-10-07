import { createBrowserClient } from "@supabase/ssr";

/**
 * Creates a browser-side Supabase client for Admin UI interactions.
 * Safely falls back to anonymous/mock operations if credentials are not yet set.
 */
export function createAdminClient() {
  const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL || "http://127.0.0.1:54321";
  const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY || "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.e30.mock";

  return createBrowserClient(supabaseUrl, supabaseAnonKey);
}
