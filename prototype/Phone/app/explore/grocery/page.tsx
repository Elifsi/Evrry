"use client";

import { useMemo, useState } from "react";
import Link from "next/link";
import {
  ChevronLeft,
  Search,
  Sparkles,
  Zap,
  MapPin,
  Clock,
  ShieldCheck,
  Percent,
} from "lucide-react";
import { GROCERY_CATEGORIES, GROCERY_CATEGORY_ICON } from "@/lib/data/groceryCategories";
import { byCategory } from "@/lib/data/catalog";
import ProductCard from "@/components/ProductCard";
import CartFloatingBar from "@/components/CartFloatingBar";

export default function GroceryQuickMartPage() {
  const [query, setQuery] = useState("");
  const [selectedCategoryId, setSelectedCategoryId] = useState<string>("all");

  const allGroceryItems = useMemo(() => byCategory("grocery"), []);

  const filteredItems = useMemo(() => {
    let items = allGroceryItems;
    if (selectedCategoryId !== "all") {
      items = items.filter((i) => i.groceryCategoryId === selectedCategoryId);
    }
    if (query.trim()) {
      const q = query.toLowerCase();
      items = items.filter(
        (i) =>
          i.title.toLowerCase().includes(q) ||
          (i.subtitle && i.subtitle.toLowerCase().includes(q))
      );
    }
    return items;
  }, [allGroceryItems, selectedCategoryId, query]);

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
        <div className="inline-flex items-center gap-1 bg-emerald-100 text-emerald-800 px-2.5 py-0.5 rounded-full text-xs font-black">
          <Zap size={11} className="fill-emerald-800" /> 10-15 min delivery
        </div>
      </div>

      {/* Location & Title */}
      <div>
        <div className="flex items-center gap-1.5 text-xs text-ink/70">
          <MapPin size={13} className="text-emerald-600 shrink-0" />
          <span className="font-bold text-ink">Blinkit Quick Mart • Jhamsikhel Hub</span>
        </div>
        <h1 className="mt-1 text-2xl font-black tracking-tight text-ink">Groceries & Essentials</h1>
        <p className="text-xs text-ink/50">
          Fresh milk, bakery, veggies & pantry essentials delivered to your doorstep in minutes
        </p>
      </div>

      {/* Quick Mart Super Saver Banner */}
      <div className="bg-gradient-to-r from-emerald-600 to-teal-700 rounded-2xl p-4 text-white shadow-sm flex items-center justify-between">
        <div className="space-y-1">
          <div className="flex items-center gap-1 text-[10px] font-black uppercase tracking-wider bg-white/20 px-2 py-0.5 rounded-full w-fit">
            <Percent size={11} /> Fresh Morning Stock
          </div>
          <h3 className="text-sm font-bold">DDC Milk & Bakery Staples</h3>
          <p className="text-[11px] text-white/80">Packed directly from local Kathmandu cold store</p>
        </div>
        <div className="w-12 h-12 rounded-xl bg-white/10 backdrop-blur-xs flex items-center justify-center text-2xl shrink-0">
          🥛
        </div>
      </div>

      {/* Search Input */}
      <div className="relative">
        <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink/35" />
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search milk, atta, wai wai, biscuits, oil..."
          className="w-full pl-10 pr-4 py-2.5 bg-white border border-black/10 rounded-2xl text-xs text-ink placeholder:text-ink/35 shadow-2xs focus:outline-none focus:border-emerald-600"
        />
      </div>

      {/* Blinkit-Style Aisles / Categories Horizontal Carousel */}
      <div className="space-y-2">
        <h2 className="text-xs font-bold text-ink/70 uppercase tracking-wider">
          Explore Grocery Aisles
        </h2>
        <div className="flex gap-2 overflow-x-auto pb-1 scrollbar-none">
          <button
            type="button"
            onClick={() => setSelectedCategoryId("all")}
            className={`px-3 py-2 rounded-xl border shrink-0 text-xs font-bold transition-all ${
              selectedCategoryId === "all"
                ? "bg-emerald-600 text-white border-emerald-600 shadow-xs"
                : "bg-white text-ink/70 border-black/10 hover:border-black/20"
            }`}
          >
            All Products ({allGroceryItems.length})
          </button>
          {GROCERY_CATEGORIES.map((cat) => {
            const Icon = GROCERY_CATEGORY_ICON[cat.id];
            const isSelected = selectedCategoryId === cat.id;
            return (
              <button
                key={cat.id}
                type="button"
                onClick={() => setSelectedCategoryId(cat.id)}
                className={`flex items-center gap-1.5 px-3 py-2 rounded-xl border shrink-0 text-xs font-bold transition-all ${
                  isSelected
                    ? "bg-emerald-600 text-white border-emerald-600 shadow-xs"
                    : "bg-white text-ink/70 border-black/10 hover:border-black/20"
                }`}
              >
                {Icon && <Icon size={14} />}
                <span>{cat.label}</span>
              </button>
            );
          })}
        </div>
      </div>

      {/* 2-Column Product Grid */}
      <div className="space-y-3 pt-1">
        <div className="flex items-center justify-between">
          <h2 className="text-xs font-black uppercase tracking-wider text-ink/60">
            Available on Shelves ({filteredItems.length})
          </h2>
          <span className="text-[11px] text-ink/40 font-mono">10 min dispatch</span>
        </div>

        {filteredItems.length === 0 ? (
          <div className="text-center py-12 space-y-2 bg-white rounded-2xl border border-black/5 p-6">
            <p className="text-sm font-semibold text-ink">No groceries found</p>
            <p className="text-xs text-ink/50">Try checking another aisle or clearing your search.</p>
            <button
              type="button"
              onClick={() => {
                setQuery("");
                setSelectedCategoryId("all");
              }}
              className="mt-2 px-4 py-1.5 bg-emerald-600 text-white text-xs font-bold rounded-xl"
            >
              Show All Items
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-3">
            {filteredItems.map((item) => (
              <ProductCard key={item.id} item={item} />
            ))}
          </div>
        )}
      </div>

      {/* Real-Time Floating Cart */}
      <CartFloatingBar />
    </div>
  );
}
