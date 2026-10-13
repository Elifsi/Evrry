"use client";

/**
 * RideRequestPanel — Full ride booking flow with real geolocation and OSRM routing.
 *
 * Flow:
 *   1. pickup  — GPS auto-detects or user picks from LocationPickerSheet
 *   2. drop    — User searches / picks destination via LocationPickerSheet
 *   3. choose  — OSRM calculates road distance, user picks vehicle type and payment
 *   4. requestRide — Dispatches to the Zustand rideSlice
 *
 * Architecture:
 * - Coordinates (lat/lng from the browser GPS or Nominatim search) are the
 *   source of truth. Labels are derived for display only.
 * - MapPoint {x, y} percentage coordinates for the SVG placeholder map are
 *   derived from real lat/lng relative to the pickup as the map center — this
 *   makes the placeholder visually meaningful even without a Google Maps key.
 * - OSRM provides road distance; the lib/maps/routing.ts circuity model is the
 *   fallback when the OSRM endpoint is unavailable.
 */
import { useEffect, useState, useCallback } from "react";
import Link from "next/link";
import {
  ChevronLeft,
  Search,
  LocateFixed,
  Wallet,
  CreditCard,
  MapPin,
  Navigation,
  Loader,
  AlertCircle,
  RefreshCw,
} from "lucide-react";
import clsx from "clsx";
import { RIDE_TYPES, RIDE_TYPE_ICON } from "@/lib/data/rideTypes";
import { priceRide } from "@/lib/pricing";
import { BRAND_LABEL } from "@/lib/payments";
import { useAppStore } from "@/lib/store/useAppStore";
import type { PaymentSource } from "@/lib/store/useAppStore";
import MapPlaceholder from "@/components/MapPlaceholder";
import { LocationPin, VehicleMarker } from "@/components/MapPin";
import LocationPickerSheet from "@/components/location/LocationPickerSheet";
import { useCurrentLocation } from "@/lib/location/useCurrentLocation";
import { calculateRoute } from "@/lib/maps/routing";
import type { ResolvedLocation } from "@/lib/location/geoService";

/** OSRM public demo endpoint — swap for a self-hosted or production endpoint. */
const OSRM_ENDPOINT =
  process.env.NEXT_PUBLIC_OSRM_ENDPOINT ?? "https://router.project-osrm.org";

type Step = "pickup" | "drop" | "choose";

/**
 * Projects a lat/lng onto the SVG placeholder map as x/y percentages.
 * The pickup point is always centered at (50%, 54%).
 * Scale: approximately 0.3% per 100m (realistic city-block scale for Nepal valleys).
 */
const MAP_CENTER = { x: 50, y: 54 };
const DEG_TO_PERCENT = 350; // 1 degree lat/lng ≈ ~111km → 350% / 111km ≈ 3.15% per km

function latLngToMapPoint(
  origin: { lat: number; lng: number },
  target: { lat: number; lng: number }
) {
  const dx = (target.lng - origin.lng) * DEG_TO_PERCENT;
  const dy = -(target.lat - origin.lat) * DEG_TO_PERCENT; // Y axis inverted on screen
  return {
    x: Math.min(94, Math.max(6, MAP_CENTER.x + dx)),
    y: Math.min(90, Math.max(10, MAP_CENTER.y + dy)),
  };
}

function randomPoint(minRadius: number, maxRadius: number) {
  const angle = Math.random() * Math.PI * 2;
  const radius = minRadius + Math.random() * (maxRadius - minRadius);
  return {
    x: Math.min(92, Math.max(8, MAP_CENTER.x + radius * Math.cos(angle))),
    y: Math.min(88, Math.max(16, MAP_CENTER.y + radius * Math.sin(angle))),
  };
}

export default function RideRequestPanel() {
  const walletBalance = useAppStore((s) => s.walletBalance);
  const paymentMethods = useAppStore((s) => s.paymentMethods);
  const requestRide = useAppStore((s) => s.requestRide);
  const savedAddresses = useAppStore((s) => s.savedAddresses);

  // --- Location state ---
  const [step, setStep] = useState<Step>("pickup");
  const [pickup, setPickup] = useState<ResolvedLocation | null>(null);
  const [drop, setDrop] = useState<ResolvedLocation | null>(null);
  const [pickingPickup, setPickingPickup] = useState(false);
  const [pickingDrop, setPickingDrop] = useState(false);

  // --- OSRM routing state ---
  const [routeLoading, setRouteLoading] = useState(false);
  const [routeError, setRouteError] = useState<string | null>(null);
  const [distanceKm, setDistanceKm] = useState<number>(0);
  const [routeProvider, setRouteProvider] = useState<"osrm" | "circuity_model" | null>(null);

  // --- Vehicle and payment ---
  const [selectedTypeId, setSelectedTypeId] = useState(RIDE_TYPES[0].id);
  const [error, setError] = useState<string | null>(null);
  const [userSelectedSource, setSource] = useState<PaymentSource | undefined>(undefined);

  // --- Map visualization ---
  const dropPoint = drop && pickup ? latLngToMapPoint(pickup.coords, drop.coords) : null;

  // Auto-request GPS on mount (for the pickup step) without blocking render
  const { location: gpsLocation, status: gpsStatus, error: gpsError, requestLocation } =
    useCurrentLocation();

  useEffect(() => {
    // Auto-try GPS on mount. If it fails, the user will see the "Use my location"
    // button in the picker sheet or can tap "Detect" in the pickup bar.
    requestLocation();
    // Only on mount
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (gpsStatus === "ready" && gpsLocation && !pickup) {
      setPickup(gpsLocation);
    }
  }, [gpsStatus, gpsLocation, pickup]);

  // Ambient vehicles on placeholder map — generated after mount to avoid SSR mismatch
  const [ambientVehicles, setAmbientVehicles] = useState<
    { x: number; y: number; icon: (typeof RIDE_TYPE_ICON)[string] }[]
  >([]);
  useEffect(() => {
    setAmbientVehicles(
      Array.from({ length: 5 }).map((_, i) => ({
        ...randomPoint(10, 30),
        icon: RIDE_TYPE_ICON[RIDE_TYPES[i % RIDE_TYPES.length].id],
      }))
    );
  }, []);

  // --- Route calculation ---
  const calculateRideRoute = useCallback(
    async (pickupLoc: ResolvedLocation, dropLoc: ResolvedLocation) => {
      setRouteLoading(true);
      setRouteError(null);
      try {
        const result = await calculateRoute(
          { lat: pickupLoc.coords.lat, lng: pickupLoc.coords.lng },
          { lat: dropLoc.coords.lat, lng: dropLoc.coords.lng },
          OSRM_ENDPOINT
        );
        setDistanceKm(Math.max(0.5, result.distanceMeters / 1000));
        setRouteProvider(result.provider);
        setStep("choose");
      } catch {
        setRouteError("Could not calculate the route. Please check your connection and try again.");
        setRouteLoading(false);
      }
      setRouteLoading(false);
    },
    []
  );

  function handleDropSelected(loc: ResolvedLocation) {
    setDrop(loc);
    if (pickup) {
      calculateRideRoute(pickup, loc);
    } else {
      // If pickup was not yet resolved (rare), go to choose with circuity fallback
      setStep("choose");
    }
  }

  async function handleRequest() {
    if (!pickup || !drop) return;
    setError(null);

    const selectedType = RIDE_TYPES.find((rt) => rt.id === selectedTypeId) ?? RIDE_TYPES[0];
    const { total } = priceRide(selectedType, distanceKm);
    const defaultCard = paymentMethods.find((m) => m.isDefault) ?? paymentMethods[0];
    const source = userSelectedSource ?? (walletBalance >= total ? "wallet" : defaultCard?.id);
    if (!source) {
      setError("Please add a payment method to request a ride.");
      return;
    }

    const pickupPoint = { x: MAP_CENTER.x, y: MAP_CENTER.y };
    const dropPointVal = dropPoint ?? { x: MAP_CENTER.x + 15, y: MAP_CENTER.y - 10 };

    const result = requestRide({
      rideTypeId: selectedType.id,
      pickup: pickup.label,
      drop: drop.label,
      pickupPoint,
      dropPoint: dropPointVal,
      distanceKm: Math.round(distanceKm * 10) / 10,
      source,
    });

    if (!result.ok) {
      const msgs: Record<string, string> = {
        insufficient_balance: "Insufficient wallet balance. Please add a payment method.",
        no_payment_method: "Please add a payment method to request a ride.",
        ride_in_progress: "You already have an active ride.",
        invalid_ride_type: "This ride type is not available. Please select another.",
      };
      setError(msgs[result.reason] ?? "Something went wrong. Please try again.");
    }
  }

  const selectedType = RIDE_TYPES.find((rt) => rt.id === selectedTypeId) ?? RIDE_TYPES[0];
  const { total } = priceRide(selectedType, distanceKm);
  const defaultCard = paymentMethods.find((m) => m.isDefault) ?? paymentMethods[0];
  const source = userSelectedSource ?? (walletBalance >= total ? "wallet" : defaultCard?.id);

  return (
    <div className="flex flex-col h-full">
      {/* Top bar */}
      <div className="flex items-center gap-2 px-4 pt-4 pb-2">
        <Link
          href="/explore"
          className="flex h-8 w-8 items-center justify-center rounded-full bg-white shadow-sm text-ink/60 hover:text-ink shrink-0"
        >
          <ChevronLeft size={18} />
        </Link>
        <h1 className="text-base font-bold text-ink flex-1">Book a Ride</h1>
      </div>

      {/* Map placeholder */}
      <MapPlaceholder className="relative h-48 shrink-0">
        {/* Ambient nearby vehicles */}
        {step === "pickup" &&
          ambientVehicles.map((v, i) => {
            const Icon = v.icon;
            return <VehicleMarker key={i} x={v.x} y={v.y} icon={Icon} delayMs={i * 100} />;
          })}

        {/* Route line */}
        {dropPoint && (
          <svg className="pointer-events-none absolute inset-0 h-full w-full">
            <line
              x1={`${MAP_CENTER.x}%`}
              y1={`${MAP_CENTER.y}%`}
              x2={`${dropPoint.x}%`}
              y2={`${dropPoint.y}%`}
              stroke="#0B172A"
              strokeOpacity={0.35}
              strokeWidth={2}
              strokeDasharray="6 6"
            />
          </svg>
        )}

        {/* Pickup pin — always at center of map */}
        <LocationPin x={MAP_CENTER.x} y={MAP_CENTER.y} kind="pickup" />
        {dropPoint && <LocationPin x={dropPoint.x} y={dropPoint.y} kind="drop" animate />}

        {/* GPS status badge */}
        {(gpsStatus === "requesting" || gpsStatus === "geocoding") && (
          <div className="absolute bottom-2 left-1/2 -translate-x-1/2 flex items-center gap-1.5 rounded-full bg-white/90 px-3 py-1 text-[10px] font-medium text-ink shadow">
            <Loader size={10} className="animate-spin text-brand" />
            Detecting your location…
          </div>
        )}
      </MapPlaceholder>

      {/* Bottom sheet */}
      <div className="relative z-10 flex-1 overflow-y-auto rounded-t-[1.5rem] bg-paper px-5 pb-5 pt-4 shadow-[0_-8px_24px_rgba(0,0,0,0.06)]">
        {/* ─── STEP: PICKUP ────────────────────────────────────────── */}
        {step === "pickup" && (
          <>
            <p className="text-sm font-semibold text-ink">Set your pickup point</p>

            {/* Pickup row */}
            <button
              onClick={() => setPickingPickup(true)}
              className="mt-2 flex w-full items-center gap-2 rounded-xl border border-black/10 bg-white px-3 py-2.5 text-left"
            >
              <LocateFixed size={16} className="shrink-0 text-emerald-600" />
              {gpsStatus === "requesting" || gpsStatus === "geocoding" ? (
                <span className="flex-1 text-sm text-ink/40 flex items-center gap-1.5">
                  <Loader size={12} className="animate-spin text-brand" /> Detecting location…
                </span>
              ) : pickup ? (
                <span className="flex-1 truncate text-sm text-ink">{pickup.label}</span>
              ) : (
                <span className="flex-1 text-sm text-ink/40">Tap to set pickup</span>
              )}
              <Search size={14} className="shrink-0 text-ink/30" />
            </button>

            {gpsStatus === "error" && gpsError && (
              <div className="mt-2 flex items-start gap-2 rounded-lg bg-amber-50 px-3 py-2 text-xs text-amber-700">
                <AlertCircle size={13} className="mt-0.5 shrink-0" />
                <span>{gpsError}</span>
                <button onClick={requestLocation} className="ml-auto shrink-0 font-semibold">
                  Retry
                </button>
              </div>
            )}

            <button
              onClick={() => {
                if (pickup) setStep("drop");
                else setPickingPickup(true);
              }}
              disabled={gpsStatus === "requesting" || gpsStatus === "geocoding"}
              className="mt-3 flex w-full items-center justify-center rounded-full bg-brand py-3 text-sm font-semibold text-white transition hover:brightness-95 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {pickup ? "Confirm pickup" : "Pick a location"}
            </button>
          </>
        )}

        {/* ─── STEP: DROP ──────────────────────────────────────────── */}
        {step === "drop" && (
          <>
            <p className="text-sm font-semibold text-ink">Where to?</p>

            {/* Pickup summary */}
            <div className="mt-2 flex items-center gap-2 rounded-xl border border-emerald-200 bg-emerald-50 px-3 py-2">
              <MapPin size={14} className="shrink-0 text-emerald-600" />
              <span className="flex-1 truncate text-xs text-emerald-800">{pickup?.label}</span>
              <button
                onClick={() => setPickingPickup(true)}
                className="text-xs font-medium text-emerald-600"
              >
                Change
              </button>
            </div>

            {/* Drop search trigger */}
            <button
              onClick={() => setPickingDrop(true)}
              className="mt-2 flex w-full items-center gap-2 rounded-xl border border-black/10 bg-white px-3 py-2.5 text-left"
            >
              <Navigation size={16} className="shrink-0 text-red-500" />
              <span className="flex-1 text-sm text-ink/40">Search drop location…</span>
              <Search size={14} className="shrink-0 text-ink/30" />
            </button>

            {/* Popular presets with real Nepal coordinates */}
            <div className="mt-3 space-y-1.5">
              <p className="text-[10px] font-bold uppercase tracking-wider text-ink/40">
                Popular destinations
              </p>
              <div className="flex flex-wrap gap-1.5">
                {[
                  {
                    name: "Tribhuvan Int'l Airport (TIA)",
                    coords: { lat: 27.6966, lng: 85.3591 },
                  },
                  { name: "Thamel Tourist Center", coords: { lat: 27.7149, lng: 85.3121 } },
                  { name: "Patan Durbar Square", coords: { lat: 27.6711, lng: 85.3265 } },
                  { name: "Boudhanath Stupa", coords: { lat: 27.7215, lng: 85.3621 } },
                  { name: "New Baneshwor Chowk", coords: { lat: 27.6996, lng: 85.3384 } },
                ].map((preset) => (
                  <button
                    key={preset.name}
                    type="button"
                    onClick={() =>
                      handleDropSelected({
                        label: preset.name,
                        coords: { lat: preset.coords.lat, lng: preset.coords.lng },
                      })
                    }
                    className="text-[11px] px-2.5 py-1 rounded-lg bg-white border border-black/8 text-ink/70 hover:border-brand hover:text-brand transition-colors text-left font-medium"
                  >
                    📍 {preset.name}
                  </button>
                ))}
              </div>
            </div>
          </>
        )}

        {/* ─── STEP: CHOOSE VEHICLE ────────────────────────────────── */}
        {step === "choose" && (
          <>
            {routeLoading && (
              <div className="flex items-center justify-center gap-2 py-4 text-sm text-ink/60">
                <Loader size={16} className="animate-spin text-brand" />
                Calculating road distance…
              </div>
            )}

            {routeError && (
              <div className="mb-3 flex items-start gap-2 rounded-lg bg-red-50 px-3 py-2 text-xs text-red-600">
                <AlertCircle size={13} className="mt-0.5 shrink-0" />
                <span className="flex-1">{routeError}</span>
                <button
                  onClick={() => pickup && drop && calculateRideRoute(pickup, drop)}
                  className="ml-auto shrink-0 font-semibold"
                >
                  <RefreshCw size={12} />
                </button>
              </div>
            )}

            {!routeLoading && (
              <>
                <div className="flex items-center justify-between">
                  <p className="text-sm font-semibold text-ink">Choose a ride</p>
                  <span className="text-xs text-ink/40">
                    ~{distanceKm.toFixed(1)} km ·{" "}
                    {routeProvider === "osrm" ? (
                      <span className="text-emerald-600 font-medium">Road distance</span>
                    ) : (
                      <span className="text-amber-600">Est. distance</span>
                    )}
                  </span>
                </div>

                {/* Route summary */}
                <div className="mt-2 rounded-xl border border-black/5 bg-white p-3 text-xs text-ink/60 space-y-1">
                  <div className="flex items-center gap-2">
                    <MapPin size={12} className="text-emerald-600" />
                    <span className="truncate">{pickup?.label}</span>
                  </div>
                  <div className="ml-[5px] h-3 border-l border-dashed border-black/20" />
                  <div className="flex items-center gap-2">
                    <Navigation size={12} className="text-red-500" />
                    <span className="truncate">{drop?.label}</span>
                  </div>
                </div>

                <div className="mt-3 space-y-2">
                  {RIDE_TYPES.map((rt) => {
                    const Icon = RIDE_TYPE_ICON[rt.id];
                    const { total: rtTotal } = priceRide(rt, distanceKm);
                    const selected = rt.id === selectedTypeId;
                    return (
                      <button
                        key={rt.id}
                        onClick={() => setSelectedTypeId(rt.id)}
                        className={clsx(
                          "flex w-full items-center gap-3 rounded-xl border bg-white p-3 text-left shadow-sm transition",
                          selected
                            ? "border-brand ring-1 ring-brand/40"
                            : "border-black/5 hover:border-brand/40"
                        )}
                      >
                        <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-brandSoft text-brand">
                          <Icon size={20} strokeWidth={1.7} />
                        </span>
                        <div className="min-w-0 flex-1">
                          <p className="text-sm font-semibold text-ink">{rt.label}</p>
                          <p className="truncate text-xs text-ink/45">
                            {rt.subtitle} · {rt.etaMinutes} min away
                          </p>
                        </div>
                        <p className="text-base font-bold text-ink">
                          Rs {rtTotal.toLocaleString("en-IN")}
                        </p>
                      </button>
                    );
                  })}
                </div>

                <div className="mt-4">
                  <p className="mb-1.5 text-xs font-medium text-ink/50">Pay with</p>
                  <div className="flex flex-wrap gap-1.5">
                    <button
                      onClick={() => setSource("wallet")}
                      disabled={walletBalance < total}
                      className={clsx(
                        "inline-flex items-center gap-1 rounded-full border px-2.5 py-1.5 text-[11px] font-medium transition disabled:cursor-not-allowed disabled:opacity-40",
                        source === "wallet"
                          ? "border-ink bg-ink text-white"
                          : "border-black/15 bg-white text-ink/70 hover:border-ink/40"
                      )}
                    >
                      <Wallet size={11} /> Wallet Rs {walletBalance.toLocaleString("en-IN")}
                    </button>
                    {paymentMethods.map((m) => (
                      <button
                        key={m.id}
                        onClick={() => setSource(m.id)}
                        className={clsx(
                          "inline-flex items-center gap-1 rounded-full border px-2.5 py-1.5 text-[11px] font-medium transition",
                          source === m.id
                            ? "border-ink bg-ink text-white"
                            : "border-black/15 bg-white text-ink/70 hover:border-ink/40"
                        )}
                      >
                        <CreditCard size={11} /> {BRAND_LABEL[m.brand]} •••• {m.last4}
                      </button>
                    ))}
                  </div>
                </div>

                {error && <p className="mt-3 text-xs font-medium text-red-600">{error}</p>}

                <button
                  onClick={handleRequest}
                  disabled={!source || routeLoading}
                  className="mt-4 flex w-full items-center justify-center rounded-full bg-brand py-3 text-sm font-semibold text-white transition hover:brightness-95 disabled:cursor-not-allowed disabled:opacity-40"
                >
                  Request {selectedType.label} · Rs {total.toLocaleString("en-IN")}
                </button>

                <button
                  onClick={() => setStep("drop")}
                  className="mt-2 w-full text-center text-xs text-ink/40 hover:text-ink transition"
                >
                  ← Change destination
                </button>
              </>
            )}
          </>
        )}
      </div>

      {/* Pickup location picker sheet */}
      <LocationPickerSheet
        isOpen={pickingPickup}
        title="Set pickup point"
        onSelect={(loc) => {
          setPickup(loc);
          setPickingPickup(false);
          setStep("drop");
        }}
        onClose={() => setPickingPickup(false)}
      />

      {/* Drop location picker sheet — saved addresses shown as quick suggestions */}
      <LocationPickerSheet
        isOpen={pickingDrop}
        title="Where to?"
        onSelect={handleDropSelected}
        onClose={() => setPickingDrop(false)}
        suggestions={savedAddresses.map((a) => ({
          label: a.name,
          icon: a.tag === "home" ? "home" : a.tag === "office" ? "office" : "pin",
          location: { label: a.label, coords: { lat: a.lat, lng: a.lng } },
        }))}
      />
    </div>
  );
}
