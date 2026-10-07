"use client";

import { useState } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import {
  Percent,
  CheckCircle2,
  Sparkles,
  Truck,
  Bike,
  Shield,
  Clock,
  CloudRain,
  DollarSign,
  Save,
  RotateCcw,
} from "lucide-react";
import { initialPricingSettings } from "@/lib/admin/mockData";
import { PlatformPricingSettings } from "@/lib/admin/types";

export default function PricingControlPage() {
  const [settings, setSettings] = useState<PlatformPricingSettings>(initialPricingSettings);
  const [savedSuccess, setSavedSuccess] = useState(false);
  const [adminNote, setAdminNote] = useState("");

  const handleSave = () => {
    // In production, invokes Supabase RPC / platform_settings mutation
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 4000);
  };

  const handleReset = () => {
    setSettings(initialPricingSettings);
  };

  // Sample simulation calculation for 5km delivery
  const extraKm = Math.max(0, 5 - settings.baseDeliveryRadiusMeters / 1000);
  const extraFeePaisa = extraKm * settings.extraDistanceFeePerKmPaisa;
  const totalDeliveryPaisa = settings.baseDeliveryFeePaisa + extraFeePaisa;
  const driverExtraCutPaisa = (extraFeePaisa * settings.driverExtraDistanceShareBps) / 10000;
  const totalDriverPayoutPaisa = settings.driverBaseSharePaisa + driverExtraCutPaisa;
  const platformDeliveryMarginPaisa = totalDeliveryPaisa - totalDriverPayoutPaisa;

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="Dynamic Fee & Pricing Control Matrix"
        subtitle="Zero-SQL Live Governance • Instant Client-Side Reactive Updates"
      />

      <div className="p-8 space-y-8 flex-1 max-w-6xl">
        {/* Success Alert */}
        {savedSuccess && (
          <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-xl text-emerald-800 text-xs flex items-center justify-between">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600" />
              <span className="font-semibold">
                Pricing updated successfully! Cache invalidated across all mobile and web clients.
              </span>
            </div>
            <span className="text-[11px] font-mono text-emerald-600">Audit Log #LOG-2026-PRC</span>
          </div>
        )}

        {/* Action Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
          <div>
            <h2 className="text-base font-bold text-slate-900 tracking-tight flex items-center gap-2">
              <Percent className="w-5 h-5 text-emerald-600" />
              Live Pricing Parameters (All Verticals)
            </h2>
            <p className="text-xs text-slate-500 mt-1">
              Changes take effect platform-wide immediately. Every update is permanently logged in{" "}
              <code className="text-emerald-700 bg-emerald-50 px-1 py-0.5 rounded font-mono">
                public.admin_audit_logs
              </code>
              .
            </p>
          </div>
          <div className="flex items-center gap-3">
            <button
              onClick={handleReset}
              className="px-4 py-2 border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-semibold rounded-lg transition-colors flex items-center gap-1.5"
            >
              <RotateCcw className="w-3.5 h-3.5" />
              Reset Defaults
            </button>
            <button
              onClick={handleSave}
              className="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold rounded-lg transition-colors flex items-center gap-1.5 shadow-md shadow-emerald-900/20"
            >
              <Save className="w-3.5 h-3.5" />
              Save & Apply Parameters
            </button>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left Column: Form Controls */}
          <div className="lg:col-span-2 space-y-6">
            {/* 1. Platform Fee Controls */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
              <h3 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2 border-b border-slate-100 pb-3">
                <DollarSign className="w-4 h-4 text-emerald-600" />
                1. Platform Fee Settings
              </h3>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Standard Order Platform Fee (NPR)
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-2.5 text-xs text-slate-400 font-semibold">
                      Rs
                    </span>
                    <input
                      type="number"
                      step="1"
                      value={settings.platformFeePaisa / 100}
                      onChange={(e) =>
                        setSettings({
                          ...settings,
                          platformFeePaisa: Math.round(Number(e.target.value) * 100),
                        })
                      }
                      className="w-full pl-9 pr-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 font-semibold text-slate-900"
                    />
                  </div>
                  <p className="text-[11px] text-slate-400 mt-1">
                    Charged on every Food, Grocery Mart, and Parcel checkout.
                  </p>
                </div>

                <div className="flex flex-col justify-between">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Promotional Platform Fee Waiver
                    </label>
                    <p className="text-[11px] text-slate-400">
                      Instantly waives the Rs 10 fee for all customers during festivals.
                    </p>
                  </div>
                  <div className="pt-2">
                    <button
                      type="button"
                      onClick={() =>
                        setSettings({
                          ...settings,
                          platformFeeWaiver: !settings.platformFeeWaiver,
                        })
                      }
                      className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                        settings.platformFeeWaiver ? "bg-emerald-600" : "bg-slate-200"
                      }`}
                    >
                      <span
                        className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                          settings.platformFeeWaiver ? "translate-x-5" : "translate-x-0"
                        }`}
                      />
                    </button>
                    <span className="ml-3 text-xs font-semibold text-slate-700">
                      {settings.platformFeeWaiver ? "Waiver Active (Rs 0 Fee)" : "Standard Fee Active"}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            {/* 2. Delivery Band & Radius Rules */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
              <h3 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2 border-b border-slate-100 pb-3">
                <Truck className="w-4 h-4 text-emerald-600" />
                2. Delivery Distance Bands & Free Delivery
              </h3>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Free Delivery Basket Threshold (NPR)
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-2.5 text-xs text-slate-400 font-semibold">
                      Rs
                    </span>
                    <input
                      type="number"
                      step="50"
                      value={settings.freeDeliveryThresholdPaisa / 100}
                      onChange={(e) =>
                        setSettings({
                          ...settings,
                          freeDeliveryThresholdPaisa: Math.round(Number(e.target.value) * 100),
                        })
                      }
                      className="w-full pl-9 pr-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 font-semibold text-slate-900"
                    />
                  </div>
                  <p className="text-[11px] text-slate-400 mt-1">
                    Orders at or above this value receive 100% Free Delivery.
                  </p>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Base Delivery Radius (Kilometers)
                  </label>
                  <input
                    type="number"
                    step="0.5"
                    value={settings.baseDeliveryRadiusMeters / 1000}
                    onChange={(e) =>
                      setSettings({
                        ...settings,
                        baseDeliveryRadiusMeters: Math.round(Number(e.target.value) * 1000),
                      })
                    }
                    className="w-full px-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 font-semibold text-slate-900"
                  />
                  <p className="text-[11px] text-slate-400 mt-1">
                    Standard flat rate applies within this radius (Default: 3 km).
                  </p>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Base Delivery Fee (NPR within radius)
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-2.5 text-xs text-slate-400 font-semibold">
                      Rs
                    </span>
                    <input
                      type="number"
                      step="5"
                      value={settings.baseDeliveryFeePaisa / 100}
                      onChange={(e) =>
                        setSettings({
                          ...settings,
                          baseDeliveryFeePaisa: Math.round(Number(e.target.value) * 100),
                        })
                      }
                      className="w-full pl-9 pr-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 font-semibold text-slate-900"
                    />
                  </div>
                  <p className="text-[11px] text-slate-400 mt-1">
                    Customer charge for deliveries &lt; 3 km (Default: Rs 50).
                  </p>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Extra Distance Fee (NPR per km)
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-2.5 text-xs text-slate-400 font-semibold">
                      Rs
                    </span>
                    <input
                      type="number"
                      step="1"
                      value={settings.extraDistanceFeePerKmPaisa / 100}
                      onChange={(e) =>
                        setSettings({
                          ...settings,
                          extraDistanceFeePerKmPaisa: Math.round(Number(e.target.value) * 100),
                        })
                      }
                      className="w-full pl-9 pr-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 font-semibold text-slate-900"
                    />
                  </div>
                  <p className="text-[11px] text-slate-400 mt-1">
                    Added per km beyond the 3 km base (Default: Rs 15/km).
                  </p>
                </div>
              </div>
            </div>

            {/* 3. Driver Earnings Model */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
              <h3 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2 border-b border-slate-100 pb-3">
                <Bike className="w-4 h-4 text-emerald-600" />
                3. Rider & Driver Delivery Share Rules
              </h3>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Base Driver Payout (0–3 km)
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-2.5 text-xs text-slate-400 font-semibold">
                      Rs
                    </span>
                    <input
                      type="number"
                      step="5"
                      value={settings.driverBaseSharePaisa / 100}
                      onChange={(e) =>
                        setSettings({
                          ...settings,
                          driverBaseSharePaisa: Math.round(Number(e.target.value) * 100),
                        })
                      }
                      className="w-full pl-9 pr-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 font-semibold text-slate-900"
                    />
                  </div>
                  <p className="text-[11px] text-slate-400 mt-1">
                    Guaranteed rider base pay per completed delivery (Default: Rs 40).
                  </p>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Rider Share of Extra Distance Fee (%)
                  </label>
                  <div className="relative">
                    <input
                      type="number"
                      step="5"
                      min="50"
                      max="100"
                      value={settings.driverExtraDistanceShareBps / 100}
                      onChange={(e) =>
                        setSettings({
                          ...settings,
                          driverExtraDistanceShareBps: Math.round(Number(e.target.value) * 100),
                        })
                      }
                      className="w-full px-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 font-semibold text-slate-900"
                    />
                    <span className="absolute right-3 top-2.5 text-xs text-slate-400 font-semibold">
                      %
                    </span>
                  </div>
                  <p className="text-[11px] text-slate-400 mt-1">
                    Share of extra distance fee passed directly to rider (Default: 80%).
                  </p>
                </div>
              </div>
            </div>

            {/* 4. Vertical Take-Rate Sliders */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
              <h3 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2 border-b border-slate-100 pb-3">
                <Sparkles className="w-4 h-4 text-emerald-600" />
                4. Default Vertical Commission Rates (Take-Rates)
              </h3>

              <div className="space-y-4 pt-2">
                {[
                  {
                    name: "Restaurants & Dining",
                    key: "foodBps",
                    val: settings.commissions.foodBps,
                    max: 3000,
                  },
                  {
                    name: "Grocery & Kirana Marts",
                    key: "groceryBps",
                    val: settings.commissions.groceryBps,
                    max: 2000,
                  },
                  {
                    name: "Bike-Taxi Rides",
                    key: "bikeTaxiBps",
                    val: settings.commissions.bikeTaxiBps,
                    max: 2500,
                  },
                  {
                    name: "Cab & Car Taxi Rides",
                    key: "cabTaxiBps",
                    val: settings.commissions.cabTaxiBps,
                    max: 2500,
                  },
                  {
                    name: "Hotels & Stays",
                    key: "hotelBps",
                    val: settings.commissions.hotelBps,
                    max: 2500,
                  },
                  {
                    name: "Room Rentals (Long-term)",
                    key: "rentalBps",
                    val: settings.commissions.rentalBps,
                    max: 1500,
                  },
                ].map((item) => (
                  <div key={item.key} className="space-y-1">
                    <div className="flex items-center justify-between text-xs font-semibold">
                      <span className="text-slate-800">{item.name}</span>
                      <span className="text-emerald-700 font-mono text-sm">
                        {(item.val / 100).toFixed(1)}% ({item.val} bps)
                      </span>
                    </div>
                    <input
                      type="range"
                      min="0"
                      max={item.max}
                      step="50"
                      value={item.val}
                      onChange={(e) =>
                        setSettings({
                          ...settings,
                          commissions: {
                            ...settings.commissions,
                            [item.key]: Number(e.target.value),
                          },
                        })
                      }
                      className="w-full accent-emerald-600 h-1.5 bg-slate-200 rounded-lg cursor-pointer"
                    />
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Right Column: Instant Live Simulation */}
          <div className="space-y-6">
            <div className="bg-gradient-to-b from-slate-900 to-slate-800 text-white p-6 rounded-2xl shadow-lg border border-slate-700/60 space-y-5 sticky top-24">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold uppercase tracking-wider text-emerald-400">
                  Live Financial Simulation
                </span>
                <span className="text-[10px] bg-slate-700 px-2 py-0.5 rounded font-mono text-slate-300">
                  5 km Delivery
                </span>
              </div>

              <div className="space-y-3 text-xs">
                <div className="flex justify-between py-1.5 border-b border-slate-700/80">
                  <span className="text-slate-300">Standard Platform Fee</span>
                  <span className="font-semibold text-white">
                    Rs {(settings.platformFeeWaiver ? 0 : settings.platformFeePaisa / 100).toFixed(2)}
                  </span>
                </div>

                <div className="flex justify-between py-1.5 border-b border-slate-700/80">
                  <span className="text-slate-300">Base Delivery (&le; 3 km)</span>
                  <span className="font-semibold text-white">
                    Rs {(settings.baseDeliveryFeePaisa / 100).toFixed(2)}
                  </span>
                </div>

                <div className="flex justify-between py-1.5 border-b border-slate-700/80">
                  <span className="text-slate-300">Extra Distance (2 km @ Rs 15)</span>
                  <span className="font-semibold text-white">
                    Rs {(extraFeePaisa / 100).toFixed(2)}
                  </span>
                </div>

                <div className="flex justify-between py-2 bg-slate-800/80 px-3 rounded-lg font-semibold">
                  <span className="text-slate-200">Customer Delivery Total</span>
                  <span className="text-emerald-400 text-sm">
                    Rs {(totalDeliveryPaisa / 100).toFixed(2)}
                  </span>
                </div>
              </div>

              {/* Rider Breakdown */}
              <div className="pt-2 border-t border-slate-700/80 space-y-2 text-xs">
                <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">
                  Driver Earnings Breakdown
                </div>
                <div className="flex justify-between">
                  <span className="text-slate-300">Base Pay:</span>
                  <span className="text-slate-200 font-mono">
                    Rs {(settings.driverBaseSharePaisa / 100).toFixed(2)}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-slate-300">
                    Extra Share ({settings.driverExtraDistanceShareBps / 100}%):
                  </span>
                  <span className="text-slate-200 font-mono">
                    Rs {(driverExtraCutPaisa / 100).toFixed(2)}
                  </span>
                </div>
                <div className="flex justify-between font-bold text-emerald-400 text-sm pt-1">
                  <span>Driver Takes Home:</span>
                  <span>Rs {(totalDriverPayoutPaisa / 100).toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-[11px] text-slate-400 pt-1">
                  <span>Platform Delivery Margin:</span>
                  <span className="text-slate-300">
                    Rs {(platformDeliveryMarginPaisa / 100).toFixed(2)}
                  </span>
                </div>
              </div>

              {/* Approval Note Input */}
              <div className="pt-3 border-t border-slate-700/80">
                <label className="block text-[11px] font-semibold text-slate-300 mb-1">
                  Reason / Approval Reference (for Audit Log)
                </label>
                <input
                  type="text"
                  placeholder="e.g. Festival promo approved by VP Operations"
                  value={adminNote}
                  onChange={(e) => setAdminNote(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3 py-1.5 text-xs text-white focus:outline-none focus:ring-1 focus:ring-emerald-500 placeholder-slate-500"
                />
              </div>

              <button
                onClick={handleSave}
                className="w-full py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-xs rounded-xl shadow-md transition-all flex items-center justify-center gap-2"
              >
                <Save className="w-4 h-4" />
                Commit Changes to Database
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
