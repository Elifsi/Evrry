"use client";

/**
 * useCurrentLocation — React hook wrapping the Evrry geolocation service.
 *
 * Usage:
 *   const { location, status, error, requestLocation } = useCurrentLocation();
 *
 * Calling `requestLocation()` triggers the browser permission prompt (if needed)
 * and populates `location` with the device's coordinates + a reverse-geocoded label.
 *
 * The hook does NOT auto-request on mount to avoid triggering unnecessary
 * permission prompts. Callers must invoke `requestLocation()` in response to
 * a user gesture (e.g. "Use my location" button tap).
 */
import { useState, useCallback } from "react";
import {
  getCurrentPosition,
  reverseGeocode,
  locationErrorMessage,
  ResolvedLocation,
} from "@/lib/location/geoService";

export type LocationStatus = "idle" | "requesting" | "geocoding" | "ready" | "error";

export interface UseCurrentLocationResult {
  /** The resolved location (coords + label) when status === "ready". */
  location: ResolvedLocation | null;
  status: LocationStatus;
  /** Human-readable error description when status === "error". */
  error: string | null;
  /**
   * Triggers a fresh geolocation request.
   * Safe to call multiple times; cancels any in-flight request.
   */
  requestLocation: () => void;
  /** Clear the current result and reset to idle. */
  clear: () => void;
}

export function useCurrentLocation(): UseCurrentLocationResult {
  const [location, setLocation] = useState<ResolvedLocation | null>(null);
  const [status, setStatus] = useState<LocationStatus>("idle");
  const [error, setError] = useState<string | null>(null);

  const requestLocation = useCallback(() => {
    setStatus("requesting");
    setError(null);

    getCurrentPosition({ timeoutMs: 10_000 })
      .then(async (position) => {
        const coords = {
          lat: position.coords.latitude,
          lng: position.coords.longitude,
          accuracyMeters: position.coords.accuracy,
        };
        setStatus("geocoding");
        const label = await reverseGeocode(coords);
        setLocation({ coords, label });
        setStatus("ready");
      })
      .catch((err) => {
        setError(locationErrorMessage(err));
        setStatus("error");
      });
  }, []);

  const clear = useCallback(() => {
    setLocation(null);
    setStatus("idle");
    setError(null);
  }, []);

  return { location, status, error, requestLocation, clear };
}
