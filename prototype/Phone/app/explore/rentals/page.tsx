"use client";

import { useMemo, useState } from "react";
import Link from "next/link";
import {
  ChevronLeft,
  Search,
  Home,
  CheckCircle2,
  MapPin,
  Calendar,
  Phone,
  MessageSquare,
  ShieldCheck,
  Droplets,
  Car,
  Bed,
  Bath,
  ArrowRight,
  Filter,
} from "lucide-react";
import { RENTAL_PROPERTIES } from "@/lib/data/rentals";
import { RentalProperty } from "@/lib/types";

const PROPERTY_TYPES = ["All", "1BHK", "2BHK", "3BHK", "Studio"];
const FURNISHING_OPTIONS = ["All", "Fully Furnished", "Semi-Furnished"];

export default function RoomRentalsPage() {
  const [query, setQuery] = useState("");
  const [selectedType, setSelectedType] = useState("All");
  const [selectedFurnishing, setSelectedFurnishing] = useState("All");
  const [visitProperty, setVisitProperty] = useState<RentalProperty | null>(null);
  const [visitDate, setVisitDate] = useState("");
  const [visitSlot, setVisitSlot] = useState("Morning (10 AM - 12 PM)");
  const [visitSubmitted, setVisitSubmitted] = useState(false);

  const filteredProperties = useMemo(() => {
    return RENTAL_PROPERTIES.filter((p) => {
      if (selectedType !== "All" && p.propertyType !== selectedType) return false;
      if (selectedFurnishing !== "All" && p.furnishing !== selectedFurnishing) return false;
      if (
        query &&
        !`${p.title} ${p.area} ${p.address} ${p.landlordName}`.toLowerCase().includes(query.toLowerCase())
      ) {
        return false;
      }
      return true;
    });
  }, [selectedType, selectedFurnishing, query]);

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
        <div className="text-xs font-semibold text-rose-700 bg-rose-50 px-2.5 py-0.5 rounded-full border border-rose-200">
          Airbnb & Hamrobazar Stays
        </div>
      </div>

      {/* Header */}
      <div>
        <h1 className="text-2xl font-black tracking-tight text-ink">Room & Flat Rentals</h1>
        <p className="text-xs text-ink/50">
          Verified 1BHK, 2BHK, 3BHK & studio apartments with direct landlord contact
        </p>
      </div>

      {/* Landlord Verification Guarantee Banner */}
      <div className="bg-gradient-to-r from-rose-600 to-pink-700 rounded-2xl p-4 text-white shadow-sm flex items-center justify-between">
        <div className="space-y-1">
          <div className="flex items-center gap-1 text-[10px] font-black uppercase tracking-wider bg-white/20 px-2 py-0.5 rounded-full w-fit">
            <ShieldCheck size={11} /> 100% Lalpurja Verified
          </div>
          <h3 className="text-sm font-bold">Zero Brokerage • Direct Landlord</h3>
          <p className="text-[11px] text-white/80">Schedule visits and sign digital tenancy contracts</p>
        </div>
        <div className="w-12 h-12 rounded-xl bg-white/10 backdrop-blur-xs flex items-center justify-center text-2xl shrink-0">
          🏡
        </div>
      </div>

      {/* Search Input */}
      <div className="relative">
        <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink/35" />
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search by area (Jhamsikhel, Baneshwor, Sanepa...)"
          className="w-full pl-10 pr-4 py-2.5 bg-white border border-black/10 rounded-2xl text-xs text-ink placeholder:text-ink/35 shadow-2xs focus:outline-none focus:border-rose-500"
        />
      </div>

      {/* Filter Chips: Property Types */}
      <div className="space-y-2">
        <div className="flex items-center justify-between">
          <span className="text-[10px] font-bold uppercase tracking-wider text-ink/50">
            Property Configuration
          </span>
        </div>
        <div className="flex gap-1.5 overflow-x-auto pb-1 scrollbar-none">
          {PROPERTY_TYPES.map((type) => (
            <button
              key={type}
              type="button"
              onClick={() => setSelectedType(type)}
              className={`px-3 py-1.5 rounded-xl border text-xs font-bold shrink-0 transition-all ${
                selectedType === type
                  ? "bg-rose-600 text-white border-rose-600 shadow-xs"
                  : "bg-white text-ink/70 border-black/10 hover:border-black/20"
              }`}
            >
              {type}
            </button>
          ))}
        </div>
      </div>

      {/* Furnishing Filter */}
      <div className="flex gap-1.5">
        {FURNISHING_OPTIONS.map((f) => (
          <button
            key={f}
            type="button"
            onClick={() => setSelectedFurnishing(f)}
            className={`px-2.5 py-1 rounded-lg border text-[11px] font-semibold transition-all ${
              selectedFurnishing === f
                ? "bg-rose-50 text-rose-700 border-rose-200"
                : "bg-white text-ink/60 border-black/8 hover:bg-slate-50"
            }`}
          >
            {f}
          </button>
        ))}
      </div>

      {/* Listings Feed */}
      <div className="space-y-3 pt-1">
        <div className="flex items-center justify-between">
          <h2 className="text-xs font-black uppercase tracking-wider text-ink/60">
            Available Rental Properties ({filteredProperties.length})
          </h2>
          <span className="text-[11px] text-ink/40">Kathmandu & Pokhara</span>
        </div>

        {filteredProperties.length === 0 ? (
          <div className="text-center py-12 space-y-2 bg-white rounded-2xl border border-black/5 p-6">
            <p className="text-sm font-semibold text-ink">No properties found</p>
            <p className="text-xs text-ink/50">Try clearing your filters or changing the locality search.</p>
            <button
              type="button"
              onClick={() => {
                setQuery("");
                setSelectedType("All");
                setSelectedFurnishing("All");
              }}
              className="mt-2 px-4 py-1.5 bg-rose-600 text-white text-xs font-bold rounded-xl"
            >
              Reset Filters
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {filteredProperties.map((property) => (
              <div
                key={property.id}
                className="overflow-hidden rounded-2xl border border-black/8 bg-white shadow-sm transition hover:border-rose-400"
              >
                {/* Banner Gradient Card */}
                <div
                  className="relative flex h-28 w-full items-end p-3.5"
                  style={{
                    background: `linear-gradient(135deg, ${property.gradientFrom}, ${property.gradientTo})`,
                  }}
                >
                  <div className="absolute top-2.5 left-2.5 flex items-center gap-1 bg-white/95 px-2 py-0.5 rounded-full text-[10px] font-bold text-rose-700 shadow-2xs">
                    <ShieldCheck size={11} /> Verified Landlord
                  </div>

                  <span className="rounded-full bg-black/60 backdrop-blur-xs text-white px-2.5 py-0.5 text-[10px] font-bold">
                    {property.propertyType} • {property.furnishing}
                  </span>
                </div>

                {/* Content Details */}
                <div className="p-3.5 space-y-2.5">
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <Link
                        href={`/explore/rentals/${property.id}`}
                        className="text-sm font-bold text-ink hover:text-rose-600 transition-colors line-clamp-1"
                      >
                        {property.title}
                      </Link>
                      <p className="text-xs text-ink/60 flex items-center gap-1 mt-0.5">
                        <MapPin size={11} className="text-rose-600 shrink-0" />
                        <span className="truncate">{property.area}</span>
                      </p>
                    </div>

                    <div className="text-right shrink-0">
                      <p className="text-base font-black text-ink">
                        Rs {property.monthlyRentNpr.toLocaleString()}
                      </p>
                      <p className="text-[10px] text-ink/40 font-medium">per month</p>
                    </div>
                  </div>

                  {/* Specs Pill Row */}
                  <div className="flex flex-wrap items-center gap-2 text-[11px] text-ink/70 bg-slate-50 p-2 rounded-xl">
                    <span className="flex items-center gap-1">
                      <Bed size={12} className="text-ink/40" />
                      {property.bedrooms} Bed
                    </span>
                    <span>•</span>
                    <span className="flex items-center gap-1">
                      <Bath size={12} className="text-ink/40" />
                      {property.bathrooms} Bath
                    </span>
                    <span>•</span>
                    <span>{property.floor}</span>
                    <span>•</span>
                    <span className="truncate font-medium">{property.parking}</span>
                  </div>

                  {/* Water Supply & Amenities */}
                  <div className="flex items-center gap-1 text-[11px] text-emerald-700 font-medium">
                    <Droplets size={12} className="shrink-0" />
                    <span className="truncate">{property.waterSupply}</span>
                  </div>

                  {/* Action Buttons */}
                  <div className="pt-2 border-t border-black/5 flex items-center gap-2">
                    <button
                      type="button"
                      onClick={() => {
                        setVisitProperty(property);
                        setVisitSubmitted(false);
                      }}
                      className="flex-1 py-2 bg-rose-600 hover:bg-rose-700 text-white rounded-xl text-xs font-bold transition-all shadow-xs flex items-center justify-center gap-1"
                    >
                      <Calendar size={13} />
                      <span>Schedule Visit</span>
                    </button>

                    <Link
                      href={`/chat`}
                      className="px-3 py-2 border border-black/10 hover:bg-slate-50 text-ink/80 rounded-xl text-xs font-bold transition-colors flex items-center gap-1"
                    >
                      <MessageSquare size={13} />
                      <span>Chat</span>
                    </Link>

                    <Link
                      href={`/explore/rentals/${property.id}`}
                      className="px-2.5 py-2 border border-black/10 hover:bg-slate-50 text-ink/80 rounded-xl text-xs font-bold transition-colors"
                      title="View Details"
                    >
                      <ArrowRight size={13} />
                    </Link>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Schedule Physical Visit Modal */}
      {visitProperty && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-black/10 max-w-sm w-full p-5 shadow-2xl space-y-4 animate-in fade-in zoom-in-95">
            {visitSubmitted ? (
              <div className="text-center py-4 space-y-2">
                <div className="w-12 h-12 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center mx-auto">
                  <CheckCircle2 size={24} />
                </div>
                <h3 className="text-base font-bold text-ink">Visit Scheduled!</h3>
                <p className="text-xs text-ink/60">
                  Landlord {visitProperty.landlordName} ({visitProperty.landlordPhone}) has been notified for your visit on {visitDate || "Tomorrow"} ({visitSlot}).
                </p>
                <button
                  type="button"
                  onClick={() => setVisitProperty(null)}
                  className="mt-3 w-full py-2 bg-rose-600 text-white rounded-xl text-xs font-bold"
                >
                  Done
                </button>
              </div>
            ) : (
              <>
                <div className="flex items-center justify-between border-b border-black/5 pb-2">
                  <div>
                    <h3 className="text-sm font-bold text-ink">Schedule Property Visit</h3>
                    <p className="text-xs text-ink/50 truncate max-w-[240px]">
                      {visitProperty.title}
                    </p>
                  </div>
                  <button
                    type="button"
                    onClick={() => setVisitProperty(null)}
                    className="text-xs text-ink/40 hover:text-ink"
                  >
                    ✕
                  </button>
                </div>

                <div className="space-y-3">
                  <div>
                    <label className="text-[10px] font-bold uppercase tracking-wider text-ink/50 block mb-1">
                      Preferred Date
                    </label>
                    <input
                      type="date"
                      value={visitDate}
                      onChange={(e) => setVisitDate(e.target.value)}
                      className="w-full text-xs p-2 bg-slate-50 border border-black/10 rounded-xl font-medium outline-none"
                    />
                  </div>

                  <div>
                    <label className="text-[10px] font-bold uppercase tracking-wider text-ink/50 block mb-1">
                      Time Slot
                    </label>
                    <select
                      value={visitSlot}
                      onChange={(e) => setVisitSlot(e.target.value)}
                      className="w-full text-xs p-2 bg-slate-50 border border-black/10 rounded-xl font-medium outline-none"
                    >
                      <option>Morning (10:00 AM - 12:00 PM)</option>
                      <option>Afternoon (2:00 PM - 4:00 PM)</option>
                      <option>Evening (4:30 PM - 6:30 PM)</option>
                    </select>
                  </div>

                  <div className="p-2.5 rounded-xl bg-slate-50 border border-black/5 text-xs text-ink/70 space-y-1">
                    <p className="font-semibold text-ink">Landlord Contact:</p>
                    <p>{visitProperty.landlordName} • {visitProperty.landlordPhone}</p>
                    <p className="text-[11px] text-ink/50">{visitProperty.address}</p>
                  </div>
                </div>

                <div className="flex items-center gap-2 pt-2">
                  <button
                    type="button"
                    onClick={() => setVisitSubmitted(true)}
                    className="flex-1 py-2.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl text-xs font-bold transition-all shadow-xs"
                  >
                    Confirm Visit Request
                  </button>
                  <button
                    type="button"
                    onClick={() => setVisitProperty(null)}
                    className="px-4 py-2.5 border border-black/10 hover:bg-slate-50 text-ink/70 rounded-xl text-xs font-semibold"
                  >
                    Cancel
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
