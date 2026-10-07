"use client";

import { useState, useEffect } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import {
  Percent,
  CheckCircle2,
  Sparkles,
  Truck,
  Bike,
  Save,
  RotateCcw,
  DollarSign,
  TrendingUp,
} from "lucide-react";
import { fetchPricingSettings, updatePricingSettings } from "@/lib/admin/api";
import { PlatformPricingSettings } from "@/lib/admin/types";
import { initialPricingSettings } from "@/lib/admin/mockData";

export default function PricingControlPage() {
  const [settings, setSettings] = useState<PlatformPricingSettings>(initialPricingSettings);
  const [savedSuccess, setSavedSuccess] = useState(false);
  const [savedMessage, setSavedMessage] = useState("");
  const [adminNote, setAdminNote] = useState("");
  const [isRefreshing, setIsRefreshing] = useState(false);

  const loadSettings = async () => {
    setIsRefreshing(true);
    const data = await fetchPricingSettings();
    setSettings(data);
    setIsRefreshing(false);
  };

  useEffect(() => {
    loadSettings();
  }, []);

  const handleSave = async () => {
    const res = await updatePricingSettings(settings, "suman@evrry.com.np");
    setSavedMessage(res.message);
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 5000);
  };

  const handleReset = () => {
    setSettings(initialPricingSettings);
  };

  // Interactive Live Financial Simulation State
  const [simDistanceKm, setSimDistanceKm] = useState(5);
  const [simOrderAmountNpr, setSimOrderAmountNpr] = useState(800);

  // Dynamic Economic Calculations
  const baseRadiusKm = settings.baseDeliveryRadiusMeters / 1000;
  const extraKm = Math.max(0, simDistanceKm - baseRadiusKm);
  const extraFeePaisa = extraKm * settings.extraDistanceFeePerKmPaisa;
  const standardDeliveryPaisa = settings.baseDeliveryFeePaisa + extraFeePaisa;
  const isFreeDelivery = simOrderAmountNpr * 100 >= settings.freeDeliveryThresholdPaisa;
  const customerChargedDeliveryPaisa = isFreeDelivery ? 0 : standardDeliveryPaisa;
  const driverExtraCutPaisa = (extraFeePaisa * settings.driverExtraDistanceShareBps) / 10000;
  const totalDriverPayoutPaisa = settings.driverBaseSharePaisa + driverExtraCutPaisa;
  const platformFeePaisa = settings.platformFeeWaiver ? 0 : settings.platformFeePaisa;
  const merchantCommissionPaisa = (simOrderAmountNpr * 100 * settings.commissions.foodBps) / 10000;
  const platformDeliveryMarginPaisa = customerChargedDeliveryPaisa - totalDriverPayoutPaisa;
  const totalPlatformNetRevenuePaisa = platformFeePaisa + merchantCommissionPaisa + platformDeliveryMarginPaisa;

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="Dynamic Fee & Pricing Control Matrix"
        subtitle="Zero-SQL Live Governance • Instant Client-Side Reactive Updates"
        onRefresh={loadSettings}
        isRefreshing={isRefreshing}
      />

      <div className="p-8 space-y-8 flex-1 max-w-6xl">
        {/* Success Alert */}
        {savedSuccess && (
          <div className="bg-brand-50 border border-brand-200 p-4 rounded-xl text-brand-900 text-xs flex items-center justify-between shadow-sm">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-brand-600" />
              <span className="font-semibold">{savedMessage}</span>
            </div>
            <span className="text-[11px] font-mono text-brand-600 bg-white px-2 py-0.5 rounded border border-brand-200">
              Postgres platform_settings
            </span>
          </div>
        )}

        {/* Action Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-2xl border border-border shadow-card">
          <div>
            <h2 className="text-base font-bold text-ink tracking-tight flex items-center gap-2">
              <Percent className="w-5 h-5 text-brand-600" />
              Live Pricing Parameters (Kathmandu Valley & Nepal)
            </h2>
            <p className="text-xs text-ink-muted mt-1">
              Changes take effect platform-wide immediately. Every update is permanently logged in{" "}
              <code className="text-brand-700 bg-brand-50 px-1.5 py-0.5 rounded font-mono border border-brand-200">
                public.admin_audit_logs
              </code>
              .
            </p>
          </div>
          <div className="flex items-center gap-3">
            <button
              onClick={handleReset}
              className="px-4 py-2 border border-border hover:bg-slate-50 text-ink-muted hover:text-ink text-xs font-semibold rounded-lg transition-colors flex items-center gap-1.5"
            >
              <RotateCcw className="w-3.5 h-3.5" />
              Reset Defaults
            </button>
            <button
              onClick={handleSave}
              className="px-5 py-2 bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold rounded-lg transition-all flex items-center gap-1.5 shadow-md shadow-brand-900/30"
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
            <div className="bg-white p-6 rounded-2xl border border-border shadow-card space-y-4">
              <h3 className="text-sm font-bold text-ink tracking-tight flex items-center gap-2 border-b border-border pb-3">
                <DollarSign className="w-4 h-4 text-brand-600" />
                1. Platform Fee Settings
              </h3>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
                <div>
                  <label className="block text-xs font-semibold text-ink mb-1">
                    Standard Order Platform Fee (NPR)
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-2.5 text-xs text-ink-muted font-semibold">
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
                      className="w-full pl-9 pr-3 py-2 text-sm border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-600 font-semibold text-ink"
                    />
                  </div>
                  <p className="text-[11px] text-ink-muted mt-1">
                    Charged on every Food, Grocery Mart, and Parcel checkout.
                  </p>
                </div>

                <div className="flex flex-col justify-between">
                  <div>
                    <label className="block text-xs font-semibold text-ink mb-1">
                      Promotional Platform Fee Waiver
                    </label>
                    <p className="text-[11px] text-ink-muted">
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
                        settings.platformFeeWaiver ? "bg-brand-600" : "bg-slate-200"
                      }`}
                    >
                      <span
                        className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                          settings.platformFeeWaiver ? "translate-x-5" : "translate-x-0"
                        }`}
                      />
                    </button>
                    <span className="ml-3 text-xs font-semibold text-ink">
                      {settings.platformFeeWaiver ? "Waiver Active (Rs 0 Fee)" : "Standard Fee Active"}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            {/* 2. Delivery Band & Radius Rules */}
            <div className="bg-white p-6 rounded-2xl border border-border shadow-card space-y-4">
              <h3 className="text-sm font-bold text-ink tracking-tight flex items-center gap-2 border-b border-border pb-3">
                <Truck className="w-4 h-4 text-brand-600" />
                2. Delivery Distance Bands & Free Delivery
              </h3>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
                <div>
                  <label className="block text-xs font-semibold text-ink mb-1">
                    Free Delivery Basket Threshold (NPR)
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-2.5 text-xs text-ink-muted font-semibold">
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
                      className="w-full pl-9 pr-3 py-2 text-sm border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-600 font-semibold text-ink"
                    />
                  </div>
                  <p className="text-[11px] text-ink-muted mt-1">
                    Orders at or above this value receive 100% Free Delivery.
                  </p>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-ink mb-1">
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
                    className="w-full px-3 py-2 text-sm border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-600 font-semibold text-ink"
                  />
                  <p className="text-[11px] text-ink-muted mt-1">
                    Standard flat rate applies within this radius (Default: 3 km).
                  </p>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-ink mb-1">
                    Base Delivery Fee (NPR within radius)
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-2.5 text-xs text-ink-muted font-semibold">
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
                      className="w-full pl-9 pr-3 py-2 text-sm border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-600 font-semibold text-ink"
                    />
                  </div>
                  <p className="text-[11px] text-ink-muted mt-1">
                    Customer charge for deliveries &le; 3 km (Default: Rs 50).
                  </p>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-ink mb-1">
                    Extra Distance Fee (NPR per km)
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-2.5 text-xs text-ink-muted font-semibold">
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
                      className="w-full pl-9 pr-3 py-2 text-sm border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-600 font-semibold text-ink"
                    />
                  </div>
                  <p className="text-[11px] text-ink-muted mt-1">
                    Added per km beyond the 3 km base (Default: Rs 15/km).
                  </p>
                </div>
              </div>
            </div>

            {/* 3. Driver Earnings Model */}
            <div className="bg-white p-6 rounded-2xl border border-border shadow-card space-y-4">
              <h3 className="text-sm font-bold text-ink tracking-tight flex items-center gap-2 border-b border-border pb-3">
                <Bike className="w-4 h-4 text-brand-600" />
                3. Rider & Driver Delivery Share Rules
              </h3>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
                <div>
                  <label className="block text-xs font-semibold text-ink mb-1">
                    Base Driver Payout (0–3 km)
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-2.5 text-xs text-ink-muted font-semibold">
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
                      className="w-full pl-9 pr-3 py-2 text-sm border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-600 font-semibold text-ink"
                    />
                  </div>
                  <p className="text-[11px] text-ink-muted mt-1">
                    Guaranteed rider base pay per completed delivery (Default: Rs 40).
                  </p>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-ink mb-1">
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
                      className="w-full px-3 py-2 text-sm border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-600 font-semibold text-ink"
                    />
                    <span className="absolute right-3 top-2.5 text-xs text-ink-muted font-semibold">
                      %
                    </span>
                  </div>
                  <p className="text-[11px] text-ink-muted mt-1">
                    Share of extra distance fee passed directly to rider (Default: 80%).
                  </p>
                </div>
              </div>
            </div>

            {/* 4. Vertical Take-Rate Sliders */}
            <div className="bg-white p-6 rounded-2xl border border-border shadow-card space-y-4">
              <h3 className="text-sm font-bold text-ink tracking-tight flex items-center gap-2 border-b border-border pb-3">
                <Sparkles className="w-4 h-4 text-brand-600" />
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
                      <span className="text-ink">{item.name}</span>
                      <span className="text-brand-600 font-mono text-sm font-bold">
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
                      className="w-full accent-brand-600 h-1.5 bg-slate-200 rounded-lg cursor-pointer"
                    />
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Right Column: Instant Live Simulation */}
          <div className="space-y-6">
            <div className="bg-gradient-to-b from-slate-900 to-brand-950 text-white p-6 rounded-2xl shadow-xl border border-slate-800 space-y-5 sticky top-24">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold uppercase tracking-wider text-brand-300">
                  Live Financial Simulation
                </span>
                <span className="text-[10px] bg-slate-800 px-2 py-0.5 rounded font-mono text-slate-300 border border-slate-700">
                  Pre-flight Validator
                </span>
              </div>

              {/* Interactive Scenario Controls */}
              <div className="space-y-2 pt-1 border-t border-slate-800 text-xs">
                <div>
                  <div className="flex justify-between text-[11px] text-slate-400 mb-1">
                    <span>Simulated Distance:</span>
                    <span className="font-mono text-white font-bold">{simDistanceKm} km</span>
                  </div>
                  <div className="grid grid-cols-4 gap-1.5">
                    {[3, 5, 8, 12].map((km) => (
                      <button
                        key={km}
                        type="button"
                        onClick={() => setSimDistanceKm(km)}
                        className={`py-1 rounded text-[10px] font-semibold transition-all border ${
                          simDistanceKm === km
                            ? "bg-brand-600 text-white border-brand-500 shadow-sm"
                            : "bg-slate-800/80 text-slate-400 border-slate-700 hover:text-white"
                        }`}
                      >
                        {km} km
                      </button>
                    ))}
                  </div>
                </div>

                <div className="pt-2">
                  <div className="flex justify-between text-[11px] text-slate-400 mb-1">
                    <span>Order Basket (Food / Mart):</span>
                    <span className="font-mono text-white font-bold">Rs {simOrderAmountNpr}</span>
                  </div>
                  <div className="grid grid-cols-2 gap-1.5">
                    {[
                      { label: "Rs 600 (Normal)", val: 600 },
                      { label: "Rs 1,200 (Free Deliv)", val: 1200 },
                    ].map((opt) => (
                      <button
                        key={opt.val}
                        type="button"
                        onClick={() => setSimOrderAmountNpr(opt.val)}
                        className={`py-1 px-2 rounded text-[10px] font-semibold transition-all border text-center ${
                          simOrderAmountNpr === opt.val
                            ? "bg-brand-600 text-white border-brand-500 shadow-sm"
                            : "bg-slate-800/80 text-slate-400 border-slate-700 hover:text-white"
                        }`}
                      >
                        {opt.label}
                      </button>
                    ))}
                  </div>
                </div>
              </div>

              {/* Financial Calculation Breakdown */}
              <div className="space-y-2.5 text-xs pt-1 border-t border-slate-800">
                <div className="flex justify-between py-1 border-b border-slate-800/70">
                  <span className="text-slate-300">Standard Platform Fee:</span>
                  <span className="font-semibold text-white">
                    Rs {(platformFeePaisa / 100).toFixed(2)}
                  </span>
                </div>

                <div className="flex justify-between py-1 border-b border-slate-800/70">
                  <span className="text-slate-300">
                    Base Delivery (&le; {baseRadiusKm} km):
                  </span>
                  <span className="font-semibold text-white">
                    Rs {(settings.baseDeliveryFeePaisa / 100).toFixed(2)}
                  </span>
                </div>

                {extraKm > 0 && (
                  <div className="flex justify-between py-1 border-b border-slate-800/70">
                    <span className="text-slate-300">
                      Extra Distance ({extraKm} km @ Rs {settings.extraDistanceFeePerKmPaisa / 100}):
                    </span>
                    <span className="font-semibold text-white">
                      Rs {(extraFeePaisa / 100).toFixed(2)}
                    </span>
                  </div>
                )}

                <div className="flex justify-between py-2 bg-slate-800/80 px-3 rounded-lg font-semibold items-center">
                  <div>
                    <div className="text-slate-200">Customer Delivery Charged</div>
                    {isFreeDelivery && (
                      <span className="text-[10px] text-emerald-400 font-normal">
                        Waived via Free Delivery Threshold (&ge; Rs {settings.freeDeliveryThresholdPaisa / 100})
                      </span>
                    )}
                  </div>
                  <span className={`text-sm font-bold ${isFreeDelivery ? "text-emerald-400 line-through text-xs" : "text-brand-300"}`}>
                    Rs {(customerChargedDeliveryPaisa / 100).toFixed(2)}
                  </span>
                </div>
              </div>

              {/* Rider Take-Home Breakdown */}
              <div className="pt-2 border-t border-slate-800 space-y-1.5 text-xs">
                <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">
                  Driver Earnings Breakdown
                </div>
                <div className="flex justify-between">
                  <span className="text-slate-300">Base Pay:</span>
                  <span className="text-slate-200 font-mono">
                    Rs {(settings.driverBaseSharePaisa / 100).toFixed(2)}
                  </span>
                </div>
                {extraKm > 0 && (
                  <div className="flex justify-between">
                    <span className="text-slate-300">
                      Extra Distance Share ({settings.driverExtraDistanceShareBps / 100}%):
                    </span>
                    <span className="text-slate-200 font-mono">
                      Rs {(driverExtraCutPaisa / 100).toFixed(2)}
                    </span>
                  </div>
                )}
                <div className="flex justify-between font-bold text-emerald-400 text-sm pt-1 border-t border-slate-800/50">
                  <span>Driver Takes Home:</span>
                  <span>Rs {(totalDriverPayoutPaisa / 100).toFixed(2)}</span>
                </div>
              </div>

              {/* Platform Net Unit Economics */}
              <div className="pt-2 border-t border-slate-800 space-y-2 text-xs">
                <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">
                  Platform Net Unit Economics
                </div>
                <div className="flex justify-between text-slate-300">
                  <span>Merchant Take-Rate ({settings.commissions.foodBps / 100}%):</span>
                  <span className="font-mono text-white">
                    +Rs {(merchantCommissionPaisa / 100).toFixed(2)}
                  </span>
                </div>
                <div className="flex justify-between text-slate-300">
                  <span>Delivery Margin (Fee - Payout):</span>
                  <span className={`font-mono ${platformDeliveryMarginPaisa >= 0 ? "text-emerald-400" : "text-rose-400"}`}>
                    {platformDeliveryMarginPaisa >= 0 ? "+" : ""}Rs {(platformDeliveryMarginPaisa / 100).toFixed(2)}
                  </span>
                </div>
                <div className="p-2.5 rounded-xl border bg-slate-950/70 flex items-center justify-between border-slate-800">
                  <div>
                    <div className="text-[10px] text-slate-400 uppercase font-semibold">Total Net EVRRY Revenue</div>
                    <div className="text-[10px] text-slate-400">Platform fee + commission + margin</div>
                  </div>
                  <div className={`text-sm font-bold font-mono ${totalPlatformNetRevenuePaisa >= 0 ? "text-emerald-400" : "text-rose-400"}`}>
                    Rs {(totalPlatformNetRevenuePaisa / 100).toFixed(2)}
                  </div>
                </div>
              </div>

              {/* Approval Note Input */}
              <div className="pt-3 border-t border-slate-800">
                <label className="block text-[11px] font-semibold text-slate-300 mb-1">
                  Reason / Approval Reference (for Audit Log)
                </label>
                <input
                  type="text"
                  placeholder="e.g. Festival promo approved by VP Operations"
                  value={adminNote}
                  onChange={(e) => setAdminNote(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-700 rounded-lg px-3 py-1.5 text-xs text-white focus:outline-none focus:ring-1 focus:ring-brand-500 placeholder-slate-500"
                />
              </div>

              <button
                onClick={handleSave}
                className="w-full py-2.5 bg-brand-600 hover:bg-brand-500 text-white font-bold text-xs rounded-xl shadow-lg shadow-brand-900/40 transition-all flex items-center justify-center gap-2"
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
