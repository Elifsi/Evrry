"use client";

import { useMemo, useState } from "react";
import Link from "next/link";
import {
  ChevronLeft,
  Sparkles,
  Users,
  Minus,
  Plus,
  Star,
  MapPin,
  Calendar,
  CheckCircle2,
  Hotel as HotelIcon,
  Filter,
} from "lucide-react";
import { HOTELS } from "@/lib/data/hotels";
import { roomsByHotel } from "@/lib/data/catalog";
import { addDays, todayIso } from "@/lib/dates";
import HotelCard from "@/components/HotelCard";

const DESTINATION_PRESETS = [
  { id: "all", label: "All Nepal" },
  { id: "pokhara", label: "Pokhara Lakeside", filter: "Pokhara" },
  { id: "thamel", label: "Thamel, Kathmandu", filter: "Thamel" },
  { id: "battisputali", label: "Battisputali Heritage", filter: "Battisputali" },
];

export default function BookingComHotelsPage() {
  const [destination, setDestination] = useState<string>("all");
  const [checkIn, setCheckIn] = useState(() => addDays(todayIso(), 1));
  const [checkOut, setCheckOut] = useState(() => addDays(todayIso(), 3));
  const [guests, setGuests] = useState(2);
  const [starFilter, setStarFilter] = useState<number | null>(null);

  function handleCheckInChange(value: string) {
    setCheckIn(value);
    if (value >= checkOut) setCheckOut(addDays(value, 1));
  }

  const results = useMemo(() => {
    return HOTELS.filter((hotel) => {
      if (destination !== "all") {
        const dest = DESTINATION_PRESETS.find((d) => d.id === destination);
        if (dest?.filter && !hotel.area.toLowerCase().includes(dest.filter.toLowerCase())) {
          return false;
        }
      }
      if (starFilter !== null && hotel.starRating < starFilter) {
        return false;
      }
      return true;
    })
      .map((hotel) => {
        const rooms = roomsByHotel(hotel.id).filter((r) => (r.maxGuests ?? 99) >= guests);
        return { hotel, fromPrice: rooms.length > 0 ? rooms[0].price : null };
      })
      .filter((r): r is { hotel: (typeof HOTELS)[number]; fromPrice: number } => r.fromPrice !== null)
      .sort((a, b) => a.fromPrice - b.fromPrice);
  }, [destination, starFilter, guests]);

  return (
    <div className="px-5 pt-6 pb-28 space-y-4">
      {/* Top Navigation */}
      <div className="flex items-center justify-between">
        <Link
          href="/explore"
          className="inline-flex items-center gap-1 text-xs font-semibold text-ink/60 hover:text-ink transition-colors"
        >
          <ChevronLeft size={16} /> Explore Verticals
        </Link>
        <div className="text-xs font-semibold text-amber-700 bg-amber-50 px-2.5 py-0.5 rounded-full border border-amber-200">
          Booking.com Partner Network
        </div>
      </div>

      {/* Header */}
      <div>
        <h1 className="text-2xl font-black tracking-tight text-ink">Hotels & Stays</h1>
        <p className="text-xs text-ink/50">
          Curated luxury resorts, heritage hotels & boutique stays across Nepal
        </p>
      </div>

      {/* Booking Search Form Card */}
      <div className="bg-white rounded-2xl border border-black/10 p-4 shadow-sm space-y-3">
        {/* Destination Selector */}
        <div className="space-y-1">
          <label className="text-[10px] font-bold uppercase tracking-wider text-ink/50 block">
            Popular Destinations
          </label>
          <div className="flex gap-1.5 overflow-x-auto pb-1 scrollbar-none">
            {DESTINATION_PRESETS.map((dest) => (
              <button
                key={dest.id}
                type="button"
                onClick={() => setDestination(dest.id)}
                className={`px-3 py-1.5 rounded-xl border text-xs font-bold shrink-0 transition-all ${
                  destination === dest.id
                    ? "bg-amber-600 text-white border-amber-600 shadow-xs"
                    : "bg-slate-50 text-ink/70 border-black/8 hover:bg-slate-100"
                }`}
              >
                {dest.label}
              </button>
            ))}
          </div>
        </div>

        {/* Date Selectors */}
        <div className="grid grid-cols-2 gap-2.5 pt-1">
          <label className="block p-2.5 rounded-xl bg-slate-50 border border-black/8">
            <span className="text-[10px] font-bold uppercase tracking-wider text-ink/40 flex items-center gap-1">
              <Calendar size={11} /> Check-In
            </span>
            <input
              type="date"
              min={todayIso()}
              value={checkIn}
              onChange={(e) => handleCheckInChange(e.target.value)}
              className="mt-1 w-full bg-transparent text-xs font-bold text-ink outline-none"
            />
          </label>

          <label className="block p-2.5 rounded-xl bg-slate-50 border border-black/8">
            <span className="text-[10px] font-bold uppercase tracking-wider text-ink/40 flex items-center gap-1">
              <Calendar size={11} /> Check-Out
            </span>
            <input
              type="date"
              min={addDays(checkIn, 1)}
              value={checkOut}
              onChange={(e) => setCheckOut(e.target.value)}
              className="mt-1 w-full bg-transparent text-xs font-bold text-ink outline-none"
            />
          </label>
        </div>

        {/* Guests Counter & Star Filter */}
        <div className="flex items-center justify-between pt-1 border-t border-black/5">
          <div className="flex items-center gap-2">
            <span className="text-xs font-semibold text-ink/70 flex items-center gap-1">
              <Users size={13} className="text-amber-600" />
              <span>Guests:</span>
            </span>
            <div className="flex items-center gap-2 bg-slate-100 rounded-lg p-1">
              <button
                type="button"
                disabled={guests <= 1}
                onClick={() => setGuests((g) => Math.max(1, g - 1))}
                className="w-5 h-5 rounded bg-white flex items-center justify-center text-ink disabled:opacity-30 shadow-2xs"
              >
                <Minus size={11} />
              </button>
              <span className="text-xs font-bold font-mono px-1">{guests}</span>
              <button
                type="button"
                disabled={guests >= 6}
                onClick={() => setGuests((g) => Math.min(6, g + 1))}
                className="w-5 h-5 rounded bg-white flex items-center justify-center text-ink disabled:opacity-30 shadow-2xs"
              >
                <Plus size={11} />
              </button>
            </div>
          </div>

          <div className="flex items-center gap-1">
            {[4, 5].map((stars) => (
              <button
                key={stars}
                type="button"
                onClick={() => setStarFilter(starFilter === stars ? null : stars)}
                className={`text-[11px] font-bold px-2 py-1 rounded-lg border transition-all ${
                  starFilter === stars
                    ? "bg-amber-600 text-white border-amber-600"
                    : "bg-white text-ink/70 border-black/10"
                }`}
              >
                {stars}★+
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Hotel Cards Feed */}
      <div className="space-y-3 pt-1">
        <div className="flex items-center justify-between">
          <h2 className="text-xs font-black uppercase tracking-wider text-ink/60">
            Available Hotels ({results.length})
          </h2>
          <span className="text-[11px] text-ink/40">Verified Stays Only</span>
        </div>

        {results.length === 0 ? (
          <div className="text-center py-12 space-y-2 bg-white rounded-2xl border border-black/5 p-6">
            <p className="text-sm font-semibold text-ink">No hotels available</p>
            <p className="text-xs text-ink/50">
              Try adjusting your guest count, dates, or clearing destination filters.
            </p>
          </div>
        ) : (
          <div className="space-y-3">
            {results.map(({ hotel, fromPrice }) => (
              <HotelCard
                key={hotel.id}
                hotel={hotel}
                fromPrice={fromPrice}
                checkIn={checkIn}
                checkOut={checkOut}
                guests={guests}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
