import { Bike, CarFront, Car, CarTaxiFront, LucideIcon } from "lucide-react";

export interface RideType {
  id: string;
  label: string;
  subtitle: string;
  seats: number;
  baseFare: number;
  perKm: number;
  /** Pickup ETA in minutes, not trip duration. */
  etaMinutes: number;
}

// Fares are base + per-km, not looked up from the catalog (see lib/pricing.ts's
// priceRide) — modeled loosely on Rapido (bike, cheapest/fastest) and Uber/
// Ola-style cab tiers (standard/premium), per the researched UX.
export const RIDE_TYPES: RideType[] = [
  { id: "bike", label: "EVRRY Moto (Bike)", subtitle: "1 seat · fastest through city traffic", seats: 1, baseFare: 40, perKm: 18, etaMinutes: 3 },
  { id: "auto", label: "EV City Auto", subtitle: "3 seats · zero-emission ride", seats: 3, baseFare: 35, perKm: 16, etaMinutes: 4 },
  { id: "standard", label: "EVRRY Comfort Cab", subtitle: "4 seats · AC EV Sedan (BYD / Tata)", seats: 4, baseFare: 80, perKm: 32, etaMinutes: 5 },
  { id: "premium", label: "EVRRY Premier Sedan", subtitle: "4 seats · luxury chauffeur & top-rated", seats: 4, baseFare: 120, perKm: 45, etaMinutes: 5 },
];

export const RIDE_TYPE_ICON: Record<string, LucideIcon> = {
  bike: Bike,
  auto: CarFront,
  standard: Car,
  premium: CarTaxiFront,
};

export function findRideType(id: string): RideType | undefined {
  return RIDE_TYPES.find((r) => r.id === id);
}
