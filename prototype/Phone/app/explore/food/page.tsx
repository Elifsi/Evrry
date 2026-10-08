"use client";

import { useMemo, useState } from "react";
import Link from "next/link";
import {
  ChevronLeft,
  Search,
  Sparkles,
  Star,
  Clock,
  MapPin,
  Flame,
  Percent,
  CheckCircle2,
  Filter,
} from "lucide-react";
import clsx from "clsx";
import { RESTAURANTS } from "@/lib/data/restaurants";
import RestaurantCard from "@/components/RestaurantCard";

const CUISINES = [
  { id: "all", label: "All", emoji: "🍽️" },
  { id: "momo", label: "Momo & Newari", emoji: "🥟", filterMatch: "Momo" },
  { id: "thakali", label: "Thakali Thali", emoji: "🍲", filterMatch: "Thakali" },
  { id: "pizza", label: "Woodfired Pizza", emoji: "🍕", filterMatch: "Pizza" },
  { id: "burgers", label: "Burgers & CFC", emoji: "🍔", filterMatch: "Burgers" },
  { id: "japanese", label: "Japanese & Sushi", emoji: "🍱", filterMatch: "Japanese" },
  { id: "bakery", label: "Bakery & Desserts", emoji: "🥐", filterMatch: "Bakery" },
  { id: "pure_veg", label: "Pure Veg", emoji: "🥗", isVegOnly: true },
];

const FILTERS = [
  { id: "rating", label: "Rating 4.5+ ★" },
  { id: "veg", label: "Pure Veg 🟢" },
  { id: "fast", label: "Under 30 mins ⚡" },
  { id: "offers", label: "Special Offers 🏷️" },
] as const;

export default function FoodDeliveryPage() {
  const [query, setQuery] = useState("");
  const [selectedCuisine, setSelectedCuisine] = useState("all");
  const [activeFilters, setActiveFilters] = useState<Set<string>>(new Set());

  function toggleFilter(id: string) {
    setActiveFilters((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  }

  const restaurants = useMemo(() => {
    return RESTAURANTS.filter((r) => {
      // Text search
      if (
        query &&
        !`${r.name} ${r.cuisines.join(" ")} ${r.area}`.toLowerCase().includes(query.toLowerCase())
      ) {
        return false;
      }

      // Cuisine pill
      if (selectedCuisine !== "all") {
        const c = CUISINES.find((item) => item.id === selectedCuisine);
        if (c?.isVegOnly && !r.isPureVeg) return false;
        if (c?.filterMatch && !r.cuisines.some((cuisine) => cuisine.toLowerCase().includes(c.filterMatch!.toLowerCase()))) {
          return false;
        }
      }

      // Filter chips
      if (activeFilters.has("rating") && r.rating < 4.5) return false;
      if (activeFilters.has("veg") && !r.isPureVeg) return false;
      if (activeFilters.has("fast") && r.deliveryEtaMinutes > 30) return false;
      if (activeFilters.has("offers") && !r.offer) return false;

      return true;
    });
  }, [query, selectedCuisine, activeFilters]);

  return (
    <div className="px-5 pt-6 pb-28 space-y-4">
      {/* Pinned Top Navigation */}
      <div className="flex items-center justify-between">
        <Link
          href="/explore"
          className="inline-flex items-center gap-1 text-xs font-semibold text-ink/60 hover:text-ink transition-colors"
        >
          <ChevronLeft size={16} /> Explore Verticals
        </Link>
        <div className="flex items-center gap-1 text-xs font-semibold text-brand">
          <Clock size={12} />
          <span>25-35 min delivery</span>
        </div>
      </div>

      {/* Header & Location */}
      <div>
        <div className="flex items-center gap-1.5 text-xs text-ink/70">
          <MapPin size={13} className="text-brand shrink-0" />
          <span className="font-bold text-ink">Delivering to: Jhamsikhel, Lalitpur</span>
        </div>
        <h1 className="mt-1 text-2xl font-black tracking-tight text-ink">Food Delivery</h1>
        <p className="text-xs text-ink/50">
          Order from Kathmandu & Lalitpur&apos;s top kitchens • DoorDash & Zomato style experience
        </p>
      </div>

      {/* Promotional Perks Carousel */}
      <div className="bg-gradient-to-r from-orange-500 to-amber-600 rounded-2xl p-4 text-white shadow-sm flex items-center justify-between">
        <div className="space-y-1">
          <div className="flex items-center gap-1.5 text-[10px] font-black uppercase tracking-wider bg-white/20 px-2 py-0.5 rounded-full w-fit">
            <Percent size={11} /> EVRRY Food Fest
          </div>
          <h3 className="text-sm font-bold">20% OFF on Roadhouse & Jimbu Thakali</h3>
          <p className="text-[11px] text-white/80">Use code: NEPAL20 • Free delivery on Rs 1,000+</p>
        </div>
        <div className="w-12 h-12 rounded-xl bg-white/10 backdrop-blur-xs flex items-center justify-center text-2xl shrink-0">
          🍕
        </div>
      </div>

      {/* Search Input */}
      <div className="relative">
        <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink/35" />
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search restaurants, cuisines, momo, pizza..."
          className="w-full pl-10 pr-4 py-2.5 bg-white border border-black/10 rounded-2xl text-xs text-ink placeholder:text-ink/35 shadow-2xs focus:outline-none focus:border-brand"
        />
      </div>

      {/* Cuisine Horizontal Scroll Carousel (DoorDash / Zomato style) */}
      <div className="space-y-2">
        <h2 className="text-xs font-bold text-ink/70 uppercase tracking-wider">
          What are you craving?
        </h2>
        <div className="flex gap-2.5 overflow-x-auto pb-1 scrollbar-none">
          {CUISINES.map((c) => {
            const isSelected = selectedCuisine === c.id;
            return (
              <button
                key={c.id}
                type="button"
                onClick={() => setSelectedCuisine(c.id)}
                className={`flex flex-col items-center gap-1 px-3 py-2 rounded-2xl border shrink-0 transition-all ${
                  isSelected
                    ? "bg-brand text-white border-brand shadow-xs"
                    : "bg-white text-ink/80 border-black/8 hover:border-black/20"
                }`}
              >
                <span className="text-xl">{c.emoji}</span>
                <span className="text-[11px] font-semibold whitespace-nowrap">{c.label}</span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Filter Chips */}
      <div className="flex flex-wrap gap-1.5 pt-1">
        {FILTERS.map((f) => {
          const isActive = activeFilters.has(f.id);
          return (
            <button
              key={f.id}
              type="button"
              onClick={() => toggleFilter(f.id)}
              className={clsx(
                "rounded-full border px-3 py-1 text-xs font-semibold transition-all shadow-2xs",
                isActive
                  ? "border-brand bg-brandSoft text-brand"
                  : "border-black/10 bg-white text-ink/70 hover:border-black/20"
              )}
            >
              {f.label}
            </button>
          );
        })}
      </div>

      {/* Restaurant List */}
      <div className="space-y-3 pt-1">
        <div className="flex items-center justify-between">
          <h2 className="text-xs font-black uppercase tracking-wider text-ink/60">
            Open Restaurants ({restaurants.length})
          </h2>
          <span className="text-[11px] text-ink/40">Sorted by Rating & Distance</span>
        </div>

        {restaurants.length === 0 ? (
          <div className="text-center py-12 space-y-2 bg-white rounded-2xl border border-black/5 p-6">
            <p className="text-sm font-semibold text-ink">No kitchens match your filter</p>
            <p className="text-xs text-ink/50">
              Try searching for &quot;Momo&quot;, &quot;Thakali&quot;, or reset cuisine filters.
            </p>
            <button
              type="button"
              onClick={() => {
                setQuery("");
                setSelectedCuisine("all");
                setActiveFilters(new Set());
              }}
              className="mt-2 px-4 py-1.5 bg-brand text-white text-xs font-bold rounded-xl"
            >
              Reset Filters
            </button>
          </div>
        ) : (
          <div className="space-y-3">
            {restaurants.map((r) => (
              <RestaurantCard key={r.id} restaurant={r} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
