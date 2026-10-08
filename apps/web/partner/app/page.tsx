"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import {
  UtensilsCrossed,
  ShoppingBag,
  Hotel,
  Home,
  Clock,
  CheckCircle2,
  TrendingUp,
  ArrowRight,
  Receipt,
  Bike,
  Sparkles,
} from "lucide-react";
import { PartnerHeader } from "@/components/PartnerHeader";
import {
  fetchActiveOrders,
  fetchPartnerStores,
} from "@/lib/partner/api";
import { KdsOrderTicket, PartnerStore } from "@/lib/partner/types";

export default function PartnerOverviewPage() {
  const [orders, setOrders] = useState<KdsOrderTicket[]>([]);
  const [stores, setStores] = useState<PartnerStore[]>([]);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const loadData = async () => {
    setIsRefreshing(true);
    const [fetchedOrders, fetchedStores] = await Promise.all([
      fetchActiveOrders(),
      fetchPartnerStores(),
    ]);
    setOrders(fetchedOrders);
    setStores(fetchedStores);
    setIsRefreshing(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  const pendingCount = orders.filter((o) => o.status === "PENDING").length;
  const preparingCount = orders.filter((o) => o.status === "PREPARING").length;
  const readyCount = orders.filter((o) => o.status === "READY_FOR_PICKUP").length;

  return (
    <div className="flex-1 flex flex-col h-screen overflow-y-auto bg-slate-50">
      <PartnerHeader
        title="Merchant Operations Dashboard"
        subtitle="Live Fulfillment Desks • Multi-Vertical Operations for Venue Partners"
        onRefresh={loadData}
        isRefreshing={isRefreshing}
      />

      <div className="p-6 md:p-8 space-y-6 max-w-[1500px] w-full mx-auto">
        {/* Banner Alert for Pending Kitchen Orders */}
        {pendingCount > 0 && (
          <div className="bg-amber-50 border border-amber-200 p-4 rounded-2xl flex items-center justify-between shadow-xs animate-in fade-in">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-amber-500 text-white flex items-center justify-center font-bold text-sm shrink-0 animate-pulse">
                {pendingCount}
              </div>
              <div>
                <p className="text-sm font-bold text-amber-900">
                  {pendingCount} New Incoming Order{pendingCount > 1 ? "s" : ""} Awaiting Acceptance!
                </p>
                <p className="text-xs text-amber-700">
                  Open Kitchen KDS to review items and confirm preparation time.
                </p>
              </div>
            </div>
            <Link
              href="/kds"
              className="px-4 py-2 bg-amber-600 hover:bg-amber-700 text-white text-xs font-bold rounded-xl transition-colors flex items-center gap-1.5 shadow-xs shrink-0"
            >
              <span>Open KDS</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        )}

        {/* Top 4 Metric KPI Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Active in Pipeline
              </span>
              <div className="p-2 bg-brand-50 rounded-xl text-brand-600">
                <UtensilsCrossed className="w-4 h-4" />
              </div>
            </div>
            <p className="text-2xl font-black text-slate-900 mt-2 font-mono">
              {orders.length} Orders
            </p>
            <div className="flex items-center gap-2 mt-1 text-[11px] text-slate-500">
              <span className="font-semibold text-amber-600">{preparingCount} cooking</span>
              <span>•</span>
              <span className="font-semibold text-emerald-600">{readyCount} ready for pickup</span>
            </div>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Today&apos;s Gross Sales
              </span>
              <div className="p-2 bg-emerald-50 rounded-xl text-emerald-600">
                <TrendingUp className="w-4 h-4" />
              </div>
            </div>
            <p className="text-2xl font-black text-slate-900 mt-2 font-mono">
              NPR 18,450
            </p>
            <p className="text-[11px] text-emerald-700 font-medium mt-1">
              +14.2% higher than yesterday
            </p>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Avg. Kitchen Prep
              </span>
              <div className="p-2 bg-purple-50 rounded-xl text-purple-600">
                <Clock className="w-4 h-4" />
              </div>
            </div>
            <p className="text-2xl font-black text-slate-900 mt-2 font-mono">
              16.4 mins
            </p>
            <p className="text-[11px] text-purple-700 font-medium mt-1">
              Well within the 20 min benchmark
            </p>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Fulfillment Score
              </span>
              <div className="p-2 bg-blue-50 rounded-xl text-blue-600">
                <CheckCircle2 className="w-4 h-4" />
              </div>
            </div>
            <p className="text-2xl font-black text-slate-900 mt-2 font-mono">
              98.8%
            </p>
            <p className="text-[11px] text-blue-700 font-medium mt-1">
              0 rejections in last 24h
            </p>
          </div>
        </div>

        {/* 4 Operations Desks Navigation Grid */}
        <div>
          <h2 className="text-sm font-bold text-slate-900 uppercase tracking-wider mb-3">
            Fulfillment Desks & Property Operations
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* 1. Dining KDS */}
            <Link
              href="/kds"
              className="group bg-white p-5 rounded-2xl border border-slate-200 hover:border-brand-300 shadow-xs hover:shadow-md transition-all flex flex-col justify-between"
            >
              <div className="space-y-3">
                <div className="w-10 h-10 rounded-xl bg-brand-50 text-brand-600 flex items-center justify-center group-hover:scale-105 transition-transform">
                  <UtensilsCrossed className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-slate-900 group-hover:text-brand-700 transition-colors">
                    Kitchen Display (KDS)
                  </h3>
                  <p className="text-xs text-slate-500 mt-1 line-clamp-2">
                    Real-time order tickets, prep countdowns, KOT thermal receipt printing, and rider handoffs.
                  </p>
                </div>
              </div>
              <div className="pt-4 border-t border-slate-100 flex items-center justify-between text-xs font-bold text-brand-600">
                <span>Open Kitchen Desk</span>
                <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />
              </div>
            </Link>

            {/* 2. Grocery Pick-and-Pack */}
            <Link
              href="/grocery"
              className="group bg-white p-5 rounded-2xl border border-slate-200 hover:border-emerald-300 shadow-xs hover:shadow-md transition-all flex flex-col justify-between"
            >
              <div className="space-y-3">
                <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center group-hover:scale-105 transition-transform">
                  <ShoppingBag className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-slate-900 group-hover:text-emerald-700 transition-colors">
                    Grocery Picking & POS
                  </h3>
                  <p className="text-xs text-slate-500 mt-1 line-clamp-2">
                    Aisle-by-aisle picklist checkboxes, barcode scans, out-of-stock replacements, and fast bag packing.
                  </p>
                </div>
              </div>
              <div className="pt-4 border-t border-slate-100 flex items-center justify-between text-xs font-bold text-emerald-600">
                <span>Open Grocery Desk</span>
                <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />
              </div>
            </Link>

            {/* 3. Hotel Front Desk */}
            <Link
              href="/hotel"
              className="group bg-white p-5 rounded-2xl border border-slate-200 hover:border-indigo-300 shadow-xs hover:shadow-md transition-all flex flex-col justify-between"
            >
              <div className="space-y-3">
                <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center group-hover:scale-105 transition-transform">
                  <Hotel className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-slate-900 group-hover:text-indigo-700 transition-colors">
                    Hotel Desk & Calendar
                  </h3>
                  <p className="text-xs text-slate-500 mt-1 line-clamp-2">
                    Today&apos;s check-ins, guest verification, room allocation, and multi-night booking calendars.
                  </p>
                </div>
              </div>
              <div className="pt-4 border-t border-slate-100 flex items-center justify-between text-xs font-bold text-indigo-600">
                <span>Open Hotel Desk</span>
                <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />
              </div>
            </Link>

            {/* 4. Room Rental Landlord */}
            <Link
              href="/rentals"
              className="group bg-white p-5 rounded-2xl border border-slate-200 hover:border-amber-300 shadow-xs hover:shadow-md transition-all flex flex-col justify-between"
            >
              <div className="space-y-3">
                <div className="w-10 h-10 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center group-hover:scale-105 transition-transform">
                  <Home className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-slate-900 group-hover:text-amber-700 transition-colors">
                    Landlord Stays & Flats
                  </h3>
                  <p className="text-xs text-slate-500 mt-1 line-clamp-2">
                    Manage 1BHK/2BHK flat listings, rent and deposit rates, and chat with verified prospective tenants.
                  </p>
                </div>
              </div>
              <div className="pt-4 border-t border-slate-100 flex items-center justify-between text-xs font-bold text-amber-600">
                <span>Open Landlord Hub</span>
                <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />
              </div>
            </Link>
          </div>
        </div>

        {/* Live Active Orders Preview */}
        <div className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs space-y-4">
          <div className="flex items-center justify-between border-b border-slate-100 pb-3">
            <div>
              <h3 className="text-sm font-bold text-slate-900">
                Live Kitchen & Fulfillment Queue
              </h3>
              <p className="text-xs text-slate-500">
                Most recent orders received across the EVRRY customer mobile app.
              </p>
            </div>
            <Link
              href="/kds"
              className="text-xs font-bold text-brand-600 hover:text-brand-800 flex items-center gap-1"
            >
              <span>Full Screen KDS</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {orders.slice(0, 3).map((o) => (
              <div
                key={o.id}
                className="p-4 rounded-xl border border-slate-200 bg-slate-50/50 space-y-3"
              >
                <div className="flex items-center justify-between">
                  <span className="font-mono text-xs font-bold text-slate-900">
                    {o.displayNumber}
                  </span>
                  <span
                    className={`text-[10px] font-bold uppercase px-2 py-0.5 rounded-full ${
                      o.status === "PENDING"
                        ? "bg-amber-100 text-amber-800 border border-amber-200"
                        : o.status === "PREPARING"
                        ? "bg-blue-100 text-blue-800 border border-blue-200"
                        : "bg-emerald-100 text-emerald-800 border border-emerald-200"
                    }`}
                  >
                    {o.status.replace(/_/g, " ")}
                  </span>
                </div>

                <div className="space-y-1">
                  {o.items.map((i) => (
                    <div key={i.id} className="flex justify-between text-xs text-slate-700">
                      <span>
                        {i.quantity}x {i.name}
                      </span>
                      <span className="font-mono font-medium">NPR {i.priceNpr * i.quantity}</span>
                    </div>
                  ))}
                </div>

                <div className="pt-2 border-t border-slate-200/60 flex items-center justify-between text-xs font-semibold text-slate-600">
                  <span>Total: NPR {o.totalNpr}</span>
                  <span className="font-mono text-[11px] text-slate-400">{o.createdAt}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
