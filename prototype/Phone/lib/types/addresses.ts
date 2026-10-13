/**
 * SavedAddress — User address book entry.
 *
 * Coordinates are the source of truth. The `label` field is the human-readable
 * address string (from reverse geocoding or user input). The `tag` distinguishes
 * special addresses (home, office) from custom ones.
 *
 * This data is persisted via the Zustand persist middleware (localStorage) for
 * the prototype. In production, each user's addresses would be stored in the
 * Supabase `user_addresses` table (with PostGIS geography columns) subject to
 * the existing RLS policy `addresses_owner`.
 */

export interface SavedAddress {
  id: string;
  tag: "home" | "office" | "custom";
  /** User-facing name for custom addresses (e.g. "Gym", "Mom's house"). */
  name: string;
  /** Reverse-geocoded or user-confirmed address label. Display only. */
  label: string;
  lat: number;
  lng: number;
  createdAt: number;
  updatedAt: number;
}
