import { ServiceCategory } from "@/lib/types";

export const CATEGORY_META: { id: ServiceCategory; label: string; tagline?: string }[] = [
  { id: "food", label: "Food Delivery", tagline: "DoorDash & Zomato style" },
  { id: "grocery", label: "Grocery Marts", tagline: "10-15 min quick commerce" },
  { id: "rides", label: "City Rides", tagline: "Bike & cab on-demand" },
  { id: "hotels", label: "Hotels & Stays", tagline: "Booking.com style reservations" },
  { id: "rentals", label: "Room & Flat Rentals", tagline: "Airbnb style verified stays" },
  { id: "electronics", label: "Electronics", tagline: "Laptops, phones & tech" },
  { id: "fashion", label: "Fashion / Retail", tagline: "Clothing & lifestyle" },
  { id: "services", label: "Home Services", tagline: "Repairs, cleaning & salons" },
];
