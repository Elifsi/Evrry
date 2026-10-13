/**
 * Shared geolocation service for Evrry.
 *
 * Provides:
 * - Real browser Geolocation API integration with permission handling.
 * - OSRM-based reverse geocoding via Nominatim (no Google key required for text lookup).
 * - Structured results used by RideRequestPanel, LocationPicker, and the Address Book.
 *
 * Architecture note:
 * Coordinates are the source of truth. A formatted address label is derived for
 * display only and must never replace lat/lng in routing, persistence, or matching.
 */

export interface GeoCoords {
  lat: number;
  lng: number;
  /** Accuracy radius in metres as reported by the device; undefined if unavailable. */
  accuracyMeters?: number;
}

export interface ResolvedLocation {
  coords: GeoCoords;
  /** Human-readable label derived from reverse geocoding or user input. */
  label: string;
}

export type LocationPermission = "unknown" | "granted" | "denied" | "prompt";

/**
 * Returns the current Geolocation API permission state.
 * Falls back to "unknown" when the Permissions API is not supported.
 */
export async function getLocationPermissionState(): Promise<LocationPermission> {
  if (typeof navigator === "undefined" || !navigator.permissions) return "unknown";
  try {
    const result = await navigator.permissions.query({ name: "geolocation" });
    return result.state as LocationPermission;
  } catch {
    return "unknown";
  }
}

/**
 * Obtains the device's current geographic position.
 *
 * Requests HIGH_ACCURACY first. If the browser/device cannot provide a fix
 * within `timeoutMs`, the promise rejects with a GeolocationPositionError.
 *
 * This function does NOT fall back silently — callers must handle
 * GeolocationPositionError and distinguish between:
 *   code 1 — PERMISSION_DENIED
 *   code 2 — POSITION_UNAVAILABLE
 *   code 3 — TIMEOUT
 */
export function getCurrentPosition(
  opts: { timeoutMs?: number; maximumAgeMs?: number } = {}
): Promise<GeolocationPosition> {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error("Geolocation is not supported by this browser."));
      return;
    }
    navigator.geolocation.getCurrentPosition(resolve, reject, {
      enableHighAccuracy: true,
      timeout: opts.timeoutMs ?? 10_000,
      maximumAge: opts.maximumAgeMs ?? 0,
    });
  });
}

/**
 * Converts a GeolocationPositionError code into a user-readable message.
 */
export function locationErrorMessage(err: unknown): string {
  if (err && typeof err === "object" && "code" in err) {
    const code = (err as GeolocationPositionError).code;
    if (code === 1)
      return "Location access was denied. Please allow location permission in your browser settings and try again.";
    if (code === 2)
      return "Your device could not determine its position. Make sure location services are enabled.";
    if (code === 3) return "Location request timed out. Please try again or select a location manually.";
  }
  if (err instanceof Error) return err.message;
  return "An unknown location error occurred.";
}

/**
 * Reverse-geocodes a lat/lng to a human-readable label using the OpenStreetMap
 * Nominatim API (free, no key required for prototype use).
 *
 * Returns a short street-level description when available, falling back
 * to a coordinate string on failure.
 *
 * IMPORTANT: Nominatim has a 1 req/sec rate limit. Do not hammer this endpoint;
 * the prototype calls it only when the user confirms a location or releases a
 * drag pin — not on every map move.
 */
export async function reverseGeocode(coords: GeoCoords): Promise<string> {
  try {
    const url = `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${coords.lat}&lon=${coords.lng}&zoom=16&addressdetails=1`;
    const res = await fetch(url, {
      headers: { "Accept-Language": "en", "User-Agent": "Evrry/1.0 (elifsi.com)" },
      signal: AbortSignal.timeout(5_000),
    });
    if (!res.ok) throw new Error(`Nominatim ${res.status}`);
    const data = await res.json();
    // Build a concise label: road + suburb/city
    const a = data.address ?? {};
    const parts = [
      a.road ?? a.pedestrian ?? a.path,
      a.suburb ?? a.neighbourhood ?? a.quarter ?? a.village ?? a.town,
      a.city ?? a.county,
    ].filter(Boolean);
    if (parts.length) return parts.join(", ");
    // Fall back to the display name Nominatim provides
    if (data.display_name) return data.display_name.split(",").slice(0, 2).join(",").trim();
    throw new Error("empty");
  } catch {
    // Return coordinate string as last resort — transparent to the user,
    // who can still adjust the pin if the label isn't helpful.
    return `${coords.lat.toFixed(5)}, ${coords.lng.toFixed(5)}`;
  }
}

/**
 * Searches places by text using the Nominatim API.
 * Returns up to `limit` matching places with coordinates and display labels.
 * Biased toward Nepal (viewbox covering Nepal).
 */
export async function searchPlaces(
  query: string,
  limit = 5
): Promise<ResolvedLocation[]> {
  if (!query.trim()) return [];
  try {
    // Nepal bounding box: roughly 80.0°E–88.2°E, 26.3°N–30.5°N
    const viewbox = "80.0,30.5,88.2,26.3";
    const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(query)}&format=jsonv2&limit=${limit}&viewbox=${viewbox}&bounded=0&addressdetails=1&accept-language=en`;
    const res = await fetch(url, {
      headers: { "User-Agent": "Evrry/1.0 (elifsi.com)" },
      signal: AbortSignal.timeout(5_000),
    });
    if (!res.ok) throw new Error(`Nominatim search ${res.status}`);
    const results = await res.json();
    return (results as Record<string, unknown>[]).map((r) => {
      const a = (r.address as Record<string, string>) ?? {};
      const parts = [
        a.road ?? a.pedestrian ?? a.amenity ?? a.tourism ?? a.leisure,
        a.suburb ?? a.neighbourhood ?? a.quarter ?? a.village ?? a.town,
        a.city ?? a.county,
      ].filter(Boolean);
      const label = parts.length
        ? parts.join(", ")
        : String(r.display_name ?? "").split(",").slice(0, 2).join(",").trim();
      return {
        coords: {
          lat: parseFloat(String(r.lat)),
          lng: parseFloat(String(r.lon)),
        },
        label,
      };
    });
  } catch {
    return [];
  }
}
