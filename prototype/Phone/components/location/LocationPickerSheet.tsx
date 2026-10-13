"use client";

/**
 * LocationPickerSheet — Reusable location-selection bottom sheet.
 *
 * Provides:
 * 1. "Use my current location" — real device GPS via the browser Geolocation API.
 * 2. Place search — Nominatim-powered text search (biased toward Nepal).
 * 3. A search result list with a map-compatible coordinate payload on selection.
 *
 * The caller receives `onSelect(ResolvedLocation)` with both coordinates and a
 * formatted address label. Coordinates are the canonical source of truth; the
 * label is display-only.
 *
 * This component intentionally does NOT embed a live map — the parent ride panel
 * already renders the map viewport. On a future iteration with a real Google Maps
 * API key, the interactive pin-drop can be added here.
 *
 * Usage:
 *   <LocationPickerSheet
 *     isOpen={pickingPickup}
 *     title="Pick a pickup point"
 *     onSelect={(loc) => setPickup(loc)}
 *     onClose={() => setPickingPickup(false)}
 *   />
 */
import { useState, useCallback, useEffect, useRef } from "react";
import { X, LocateFixed, Search, Loader, AlertCircle, MapPin, Navigation } from "lucide-react";
import clsx from "clsx";
import { useCurrentLocation } from "@/lib/location/useCurrentLocation";
import { searchPlaces, ResolvedLocation } from "@/lib/location/geoService";

interface LocationPickerSheetProps {
  isOpen: boolean;
  title: string;
  onSelect: (location: ResolvedLocation) => void;
  onClose: () => void;
  /** Pre-populate the search input (e.g. from a previously typed address). */
  initialQuery?: string;
  /** Suggestions to show before the user types (e.g. saved addresses). */
  suggestions?: { label: string; icon?: "home" | "office" | "pin"; location: ResolvedLocation }[];
}

export default function LocationPickerSheet({
  isOpen,
  title,
  onSelect,
  onClose,
  initialQuery = "",
  suggestions = [],
}: LocationPickerSheetProps) {
  const [query, setQuery] = useState(initialQuery);
  const [results, setResults] = useState<ResolvedLocation[]>([]);
  const [searching, setSearching] = useState(false);
  const [searchError, setSearchError] = useState<string | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const { location: gpsLocation, status: gpsStatus, error: gpsError, requestLocation } =
    useCurrentLocation();

  // Reset state when sheet opens
  useEffect(() => {
    if (isOpen) {
      setQuery(initialQuery);
      setResults([]);
      setSearchError(null);
      setTimeout(() => inputRef.current?.focus(), 100);
    }
  }, [isOpen, initialQuery]);

  // Debounced place search
  const handleQueryChange = useCallback((value: string) => {
    setQuery(value);
    if (debounceRef.current) clearTimeout(debounceRef.current);
    if (!value.trim()) {
      setResults([]);
      setSearching(false);
      return;
    }
    setSearching(true);
    setSearchError(null);
    debounceRef.current = setTimeout(async () => {
      try {
        const res = await searchPlaces(value, 6);
        setResults(res);
      } catch {
        setSearchError("Search failed. Please try again.");
      } finally {
        setSearching(false);
      }
    }, 500);
  }, []);

  // When GPS resolves, propagate to caller
  useEffect(() => {
    if (gpsStatus === "ready" && gpsLocation) {
      onSelect(gpsLocation);
      onClose();
    }
  }, [gpsStatus, gpsLocation, onSelect, onClose]);

  if (!isOpen) return null;

  function handleSelect(loc: ResolvedLocation) {
    onSelect(loc);
    onClose();
  }

  return (
    <div className="fixed inset-0 z-50 flex items-end">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/40 backdrop-blur-sm"
        onClick={onClose}
      />

      {/* Sheet */}
      <div className="relative w-full rounded-t-3xl bg-paper shadow-2xl flex flex-col max-h-[90vh]">
        {/* Handle */}
        <div className="flex justify-center pt-3 pb-1">
          <div className="h-1 w-10 rounded-full bg-black/15" />
        </div>

        {/* Header */}
        <div className="flex items-center justify-between px-5 pb-3">
          <h2 className="text-base font-bold text-ink">{title}</h2>
          <button
            onClick={onClose}
            className="flex h-8 w-8 items-center justify-center rounded-full bg-black/5 text-ink/60 hover:bg-black/10"
          >
            <X size={16} />
          </button>
        </div>

        {/* Search bar */}
        <div className="px-5 pb-3">
          <div className="relative flex items-center">
            <Search size={16} className="absolute left-3 text-ink/35 shrink-0" />
            <input
              ref={inputRef}
              value={query}
              onChange={(e) => handleQueryChange(e.target.value)}
              placeholder="Search address or landmark…"
              className="w-full pl-9 pr-10 py-2.5 rounded-xl border border-black/10 bg-white text-sm text-ink placeholder:text-ink/35 focus:outline-none focus:border-brand"
            />
            {(searching) && (
              <Loader size={14} className="absolute right-3 text-brand animate-spin" />
            )}
            {query && !searching && (
              <button
                onClick={() => handleQueryChange("")}
                className="absolute right-3 text-ink/40 hover:text-ink"
              >
                <X size={14} />
              </button>
            )}
          </div>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto px-5 pb-8 space-y-2">
          {/* GPS option */}
          <button
            onClick={requestLocation}
            disabled={gpsStatus === "requesting" || gpsStatus === "geocoding"}
            className={clsx(
              "flex w-full items-center gap-3 rounded-xl border p-3 text-left transition",
              gpsStatus === "error"
                ? "border-red-200 bg-red-50"
                : "border-emerald-200 bg-emerald-50 hover:bg-emerald-100"
            )}
          >
            <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-emerald-600 text-white">
              {gpsStatus === "requesting" || gpsStatus === "geocoding" ? (
                <Loader size={16} className="animate-spin" />
              ) : (
                <LocateFixed size={16} />
              )}
            </span>
            <div className="min-w-0 flex-1">
              <p className="text-sm font-semibold text-emerald-800">
                {gpsStatus === "requesting"
                  ? "Requesting location…"
                  : gpsStatus === "geocoding"
                  ? "Resolving address…"
                  : "Use my current location"}
              </p>
              {gpsStatus === "error" && gpsError ? (
                <p className="mt-0.5 text-xs text-red-600 leading-tight">{gpsError}</p>
              ) : (
                <p className="mt-0.5 text-xs text-emerald-600">GPS · accurate to your device</p>
              )}
            </div>
          </button>

          {/* Search error */}
          {searchError && (
            <div className="flex items-center gap-2 rounded-lg bg-red-50 px-3 py-2 text-xs text-red-600">
              <AlertCircle size={13} />
              {searchError}
            </div>
          )}

          {/* Search results */}
          {results.length > 0 && (
            <div className="space-y-1">
              <p className="text-[10px] font-bold uppercase tracking-wider text-ink/40 pb-1">
                Search results
              </p>
              {results.map((r, i) => (
                <button
                  key={i}
                  onClick={() => handleSelect(r)}
                  className="flex w-full items-center gap-3 rounded-xl border border-black/5 bg-white px-3 py-2.5 text-left hover:border-brand hover:bg-brandSoft transition"
                >
                  <MapPin size={14} className="shrink-0 text-ink/40" />
                  <div className="min-w-0 flex-1">
                    <p className="text-sm font-medium text-ink truncate">{r.label}</p>
                    <p className="text-[10px] text-ink/40">
                      {r.coords.lat.toFixed(4)}, {r.coords.lng.toFixed(4)}
                    </p>
                  </div>
                </button>
              ))}
            </div>
          )}

          {/* Suggestions (saved places, presets) — shown when query is empty */}
          {!query && suggestions.length > 0 && (
            <div className="space-y-1">
              <p className="text-[10px] font-bold uppercase tracking-wider text-ink/40 pb-1">
                Saved places
              </p>
              {suggestions.map((s, i) => {
                const Icon = s.icon === "home" ? MapPin : s.icon === "office" ? Navigation : MapPin;
                return (
                  <button
                    key={i}
                    onClick={() => handleSelect(s.location)}
                    className="flex w-full items-center gap-3 rounded-xl border border-black/5 bg-white px-3 py-2.5 text-left hover:border-brand hover:bg-brandSoft transition"
                  >
                    <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-brandSoft text-brand">
                      <Icon size={14} />
                    </span>
                    <div className="min-w-0 flex-1">
                      <p className="text-sm font-medium text-ink truncate">{s.label}</p>
                      <p className="text-[10px] text-ink/40 truncate">{s.location.label}</p>
                    </div>
                  </button>
                );
              })}
            </div>
          )}

          {/* Empty state when typing but no results */}
          {query && !searching && results.length === 0 && !searchError && (
            <div className="py-6 text-center text-sm text-ink/40">
              No places found for &quot;{query}&quot;
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
