import { Hotel } from "@/lib/types";

// id matches the `providerId` used by this hotel's room CatalogItems in
// lib/data/catalog.ts. No real photos in this prototype — gradientFrom/To
// drive a placeholder banner tile instead (see HotelCard).
export const HOTELS: Hotel[] = [
  {
    id: "hotel-lakeview",
    name: "Temple Tree Resort & Spa",
    starRating: 4,
    reviewScore: 9.2,
    reviewCount: 1840,
    area: "Lakeside, Pokhara",
    address: "Gaurighat, Central Lakeside, Pokhara",
    amenities: ["Free WiFi", "Infinity Pool", "Spa & Sauna", "Organic Breakfast", "Airport Shuttle"],
    description: "Boutique heritage resort blending Western Himalayan architecture with landscaped gardens, minutes from Phewa Lake.",
    gradientFrom: "#0ea5e9",
    gradientTo: "#0c4a6e",
  },
  {
    id: "hotel-parkgrand",
    name: "Hotel Mulberry",
    starRating: 5,
    reviewScore: 9.3,
    reviewCount: 3260,
    area: "Thamel, Kathmandu",
    address: "Jyatha Marg, Thamel, Kathmandu",
    amenities: ["Free WiFi", "Rooftop Infinity Pool", "Cocoon Spa", "24/7 Gym", "Breakfast Buffet", "Valet Parking"],
    description: "A luxury haven in central Thamel featuring a rooftop infinity pool and bar offering panoramic Himalayan sunrise views.",
    gradientFrom: "#a855f7",
    gradientTo: "#4c1d95",
  },
  {
    id: "hotel-urbannest",
    name: "Kathmandu Guest House",
    starRating: 4,
    reviewScore: 8.9,
    reviewCount: 4520,
    area: "Thamel, Kathmandu",
    address: "Chaksibari Marg, Thamel, Kathmandu",
    amenities: ["Free WiFi", "Heritage Gardens", "Courtyard Restaurant", "24-Hour Front Desk", "Travel Desk"],
    description: "The legendary heritage property of Thamel with private botanical gardens, historic art galleries, and peaceful courtyards.",
    gradientFrom: "#64748b",
    gradientTo: "#1e293b",
  },
  {
    id: "hotel-grandmeridian",
    name: "Dwarika's Heritage Hotel",
    starRating: 5,
    reviewScore: 9.8,
    reviewCount: 4890,
    area: "Battisputali, Kathmandu",
    address: "Battisputali Road, Kathmandu",
    amenities: ["Free WiFi", "Pool", "Panchakarma Spa", "Krishnarpan Fine Dining", "Airport Limousine", "Curated Art Collection"],
    description: "Living museum of 14th-century Newari wood carvings, royal courtyards, and world-class traditional Nepali hospitality.",
    gradientFrom: "#f59e0b",
    gradientTo: "#78350f",
  },
];

export function findHotel(id: string): Hotel | undefined {
  return HOTELS.find((h) => h.id === id);
}

export function reviewLabel(score: number): string {
  if (score >= 9.5) return "Exceptional";
  if (score >= 9.0) return "Wonderful";
  if (score >= 8.5) return "Excellent";
  if (score >= 8.0) return "Very Good";
  if (score >= 7.0) return "Good";
  return "Pleasant";
}
