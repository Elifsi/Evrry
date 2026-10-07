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
  Receipt,
  HelpCircle,
  Layers,
} from "lucide-react";
import { fetchPricingSettings, updatePricingSettings } from "@/lib/admin/api";
import { PlatformPricingSettings } from "@/lib/admin/types";
import { initialPricingSettings } from "@/lib/admin/mockData";

type PricingCategoryTab = "delivery" | "platform" | "rider" | "commissions";

export default function PricingControlPage() {
  const [settings, setSettings] = useState<PlatformPricingSettings>(initialPricingSettings);
  const [activeTab, setActiveTab] = useState<PricingCategoryTab>("delivery");
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

      <div className="p-6 md:p-8 space-y-6 flex-1 max-w-[1500px] w-full mx-auto">
        {/* Success Alert */}
        {savedSuccess && (
          <div className="bg-brand-50 border border-brand-200 p-4 rounded-2xl text-brand-900 text-xs flex items-center justify-between shadow-sm">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-brand-600 shrink-0" />
              <span className="font-semibold">{savedMessage}</span>
            </div>
            <span className="text-[11px] font-mono text-brand-700 bg-white px-2.5 py-1 rounded-md border border-brand-200">
              Database: platform_settings (Live Synced)
            </span>
          </div>
        )}

        {/* Action Header Card */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-5 rounded-2xl border border-border shadow-sm">
          <div>
            <h2 className="text-base font-bold text-ink tracking-tight flex items-center gap-2">
              <Percent className="w-5 h-5 text-brand-600" />
              Platform Pricing & Take-Rate Governance
            </h2>
            <p className="text-xs text-ink-muted mt-0.5">
              Parameters apply dynamically across Kathmandu Valley and nationwide operations. Updates logged to audit trail.
            </p>
          </div>
          <div className="flex items-center gap-2.5 shrink-0">
            <button
              onClick={handleReset}
              className="px-4 py-2 border border-border hover:bg-slate-50 text-ink-muted hover:text-ink text-xs font-semibold rounded-xl transition-colors flex items-center gap-1.5"
            >
              <RotateCcw className="w-3.5 h-3.5" />
              Reset Defaults
            </button>
            <button
              onClick={handleSave}
              className="px-5 py-2 bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold rounded-xl transition-all flex items-center gap-1.5 shadow-md shadow-brand-900/20"
            >
              <Save className="w-3.5 h-3.5" />
              Save & Apply Parameters
            </button>
          </div>
        </div>

        {/* Main Grid: Parameters Workspace (Left) & Simulator Desk (Right) */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          {/* Left Column: Segmented Parameters Form Desk (7 cols) */}
          <div className="lg:col-span-7 space-y-4">
            {/* Segmented Category Navigation */}
            <div className="bg-white p-2 rounded-2xl border border-border shadow-sm flex items-center gap-1.5 overflow-x-auto">
              {[
                {
                  id: "delivery" as PricingCategoryTab,
                  label: "Delivery Distance",
                  icon: Truck,
                },
                {
                  id: "platform" as PricingCategoryTab,
                  label: "Platform Fees & VAT",
                  icon: DollarSign,
                },
                {
                  id: "rider" as PricingCategoryTab,
                  label: "Rider Payout Share",
                  icon: Bike,
                },
                {
                  id: "commissions" as PricingCategoryTab,
                  label: "Vertical Take-Rates",
                  icon: Sparkles,
                },
              ].map((tab) => {
                const Icon = tab.icon;
                const isActive = activeTab === tab.id;
                return (
                  <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id)}
                    className={`flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold transition-all whitespace-nowrap shrink-0 ${
                      isActive
                        ? "bg-brand-600 text-white shadow-sm"
                        : "bg-surface-subtle text-ink-muted hover:text-ink hover:bg-slate-100"
                    }`}
                  >
                    <Icon className="w-4 h-4" />
                    <span>{tab.label}</span>
                  </button>
                );
              })}
            </div>

            {/* TAB CONTENT 1: Delivery Distance & Free Delivery */}
            {activeTab === "delivery" && (
              <div className="bg-white p-6 rounded-2xl border border-border shadow-sm space-y-5 animate-fade-in">
                <div className="border-b border-border pb-3">
                  <h3 className="text-sm font-bold text-ink flex items-center gap-2">
                    <Truck className="w-4 h-4 text-brand-600" />
                    Delivery Distance Bands & Basket Thresholds
                  </h3>
                  <p className="text-xs text-ink-muted mt-0.5">
                    Controls customer delivery tariffs, base service radius, and free delivery qualification.
                  </p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div className="p-4 bg-surface-subtle rounded-xl border border-border">
                    <label className="block text-xs font-bold text-ink mb-1">
                      Base Delivery Fee (NPR)
                    </label>
                    <div className="relative mt-1">
                      <span className="absolute left-3 top-2.5 text-xs text-ink-muted font-bold">
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
                        className="w-full pl-9 pr-3 py-2 text-sm bg-white border border-border rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-600 font-bold text-ink"
                      />
                    </div>
                    <p className="text-[11px] text-ink-muted mt-1.5">
                      Standard delivery flat rate within the initial base radius.
                    </p>
                  </div>

                  <div className="p-4 bg-surface-subtle rounded-xl border border-border">
                    <label className="block text-xs font-bold text-ink mb-1">
                      Base Delivery Radius (Kilometers)
                    </label>
                    <div className="relative mt-1">
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
                        className="w-full px-3 py-2 text-sm bg-white border border-border rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-600 font-bold text-ink"
                      />
                      <span className="absolute right-3 top-2.5 text-xs text-ink-muted font-bold">
                        km
                      </span>
                    </div>
                    <p className="text-[11px] text-ink-muted mt-1.5">
                      Distance covered by base fee (Default: 3.0 km).
                    </p>
                  </div>

                  <div className="p-4 bg-surface-subtle rounded-xl border border-border">
                    <label className="block text-xs font-bold text-ink mb-1">
                      Extra Distance Tariff (NPR per km)
                    </label>
                    <div className="relative mt-1">
                      <span className="absolute left-3 top-2.5 text-xs text-ink-muted font-bold">
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
                        className="w-full pl-9 pr-3 py-2 text-sm bg-white border border-border rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-600 font-bold text-ink"
                      />
                      <span className="absolute right-3 top-2.5 text-xs text-ink-muted font-bold">
                        / km
                      </span>
                    </div>
                    <p className="text-[11px] text-ink-muted mt-1.5">
                      Incremental surcharge per km beyond the 3 km base.
                    </p>
                  </div>

                  <div className="p-4 bg-surface-subtle rounded-xl border border-border">
                    <label className="block text-xs font-bold text-ink mb-1">
                      Free Delivery Cart Minimum (NPR)
                    </label>
                    <div className="relative mt-1">
                      <span className="absolute left-3 top-2.5 text-xs text-ink-muted font-bold">
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
                        className="w-full pl-9 pr-3 py-2 text-sm bg-white border border-border rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-600 font-bold text-ink"
                      />
                    </div>
                    <p className="text-[11px] text-ink-muted mt-1.5">
                      Baskets equal or exceeding this qualify for 100% free delivery.
                    </p>
                  </div>
                </div>
              </div>
            )}

            {/* TAB CONTENT 2: Platform Fee & VAT */}
            {activeTab === "platform" && (
              <div className="bg-white p-6 rounded-2xl border border-border shadow-sm space-y-5 animate-fade-in">
                <div className="border-b border-border pb-3">
                  <h3 className="text-sm font-bold text-ink flex items-center gap-2">
                    <DollarSign className="w-4 h-4 text-brand-600" />
                    Platform Fee & Tax Governance
                  </h3>
                  <p className="text-xs text-ink-muted mt-0.5">
                    Order handling fee per checkout and Nepal IRD VAT compliance parameters.
                  </p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div className="p-4 bg-surface-subtle rounded-xl border border-border">
                    <label className="block text-xs font-bold text-ink mb-1">
                      Per-Order Platform Fee (NPR)
                    </label>
                    <div className="relative mt-1">
                      <span className="absolute left-3 top-2.5 text-xs text-ink-muted font-bold">
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
                        className="w-full pl-9 pr-3 py-2 text-sm bg-white border border-border rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-600 font-bold text-ink"
                      />
                    </div>
                    <p className="text-[11px] text-ink-muted mt-1.5">
                      Standard fee applied per completed order across Food, Mart, and Parcel checkout.
                    </p>
                  </div>

                  <div className="p-4 bg-surface-subtle rounded-xl border border-border flex flex-col justify-between">
                    <div>
                      <label className="block text-xs font-bold text-ink mb-1">
                        Promotional Fee Waiver
                      </label>
                      <p className="text-[11px] text-ink-muted">
                        Instantly waives the platform fee for all consumer apps during festive promotions.
                      </p>
                    </div>
                    <div className="pt-3 flex items-center gap-3">
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
                      <span className="text-xs font-bold text-ink">
                        {settings.platformFeeWaiver ? "Waiver Active (Rs 0 Fee)" : "Standard Fee Active"}
                      </span>
                    </div>
                  </div>

                  <div className="p-4 bg-surface-subtle rounded-xl border border-border col-span-1 sm:col-span-2">
                    <div className="flex items-center justify-between">
                      <div>
                        <label className="block text-xs font-bold text-ink">
                          Inland Revenue Dept (IRD) Value Added Tax (VAT)
                        </label>
                        <p className="text-[11px] text-ink-muted mt-0.5">
                          Statutory VAT percentage applied to taxable commissions and digital platform fees.
                        </p>
                      </div>
                      <span className="font-mono font-bold text-brand-600 text-sm bg-brand-50 px-2.5 py-1 rounded-lg border border-brand-200">
                        {(settings.vatBps / 100).toFixed(1)}% (Statutory)
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* TAB CONTENT 3: Rider & Driver Earnings Share */}
            {activeTab === "rider" && (
              <div className="bg-white p-6 rounded-2xl border border-border shadow-sm space-y-5 animate-fade-in">
                <div className="border-b border-border pb-3">
                  <h3 className="text-sm font-bold text-ink flex items-center gap-2">
                    <Bike className="w-4 h-4 text-brand-600" />
                    Delivery Rider & Driver Split Model
                  </h3>
                  <p className="text-xs text-ink-muted mt-0.5">
                    Guaranteed delivery payout per drop and extra distance revenue pass-through.
                  </p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div className="p-4 bg-surface-subtle rounded-xl border border-border">
                    <label className="block text-xs font-bold text-ink mb-1">
                      Base Guaranteed Rider Payout (0–3 km)
                    </label>
                    <div className="relative mt-1">
                      <span className="absolute left-3 top-2.5 text-xs text-ink-muted font-bold">
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
                        className="w-full pl-9 pr-3 py-2 text-sm bg-white border border-border rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-600 font-bold text-ink"
                      />
                    </div>
                    <p className="text-[11px] text-ink-muted mt-1.5">
                      Guaranteed minimum earnings per completed order in hand.
                    </p>
                  </div>

                  <div className="p-4 bg-surface-subtle rounded-xl border border-border">
                    <label className="block text-xs font-bold text-ink mb-1">
                      Extra Distance Pass-Through Share (%)
                    </label>
                    <div className="relative mt-1">
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
                        className="w-full px-3 py-2 text-sm bg-white border border-border rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-600 font-bold text-ink"
                      />
                      <span className="absolute right-3 top-2.5 text-xs text-ink-muted font-bold">
                        %
                      </span>
                    </div>
                    <p className="text-[11px] text-ink-muted mt-1.5">
                      Percentage of the extra km fee paid directly to the delivery rider.
                    </p>
                  </div>
                </div>
              </div>
            )}

            {/* TAB CONTENT 4: Vertical Take-Rates */}
            {activeTab === "commissions" && (
              <div className="bg-white p-6 rounded-2xl border border-border shadow-sm space-y-5 animate-fade-in">
                <div className="border-b border-border pb-3">
                  <h3 className="text-sm font-bold text-ink flex items-center gap-2">
                    <Sparkles className="w-4 h-4 text-brand-600" />
                    Vertical Commission Rates (Take-Rates)
                  </h3>
                  <p className="text-xs text-ink-muted mt-0.5">
                    Platform take-rate deducted from merchant and service earnings during midnight settlement.
                  </p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {[
                    {
                      name: "Restaurants & Dining",
                      key: "foodBps" as const,
                      val: settings.commissions.foodBps,
                      max: 3000,
                    },
                    {
                      name: "Grocery & Kirana Marts",
                      key: "groceryBps" as const,
                      val: settings.commissions.groceryBps,
                      max: 2000,
                    },
                    {
                      name: "Bike-Taxi Rides",
                      key: "bikeTaxiBps" as const,
                      val: settings.commissions.bikeTaxiBps,
                      max: 2500,
                    },
                    {
                      name: "Cab & Car Taxi Rides",
                      key: "cabTaxiBps" as const,
                      val: settings.commissions.cabTaxiBps,
                      max: 2500,
                    },
                    {
                      name: "Hotels & Stays",
                      key: "hotelBps" as const,
                      val: settings.commissions.hotelBps,
                      max: 2500,
                    },
                    {
                      name: "Room Rentals (Long-term)",
                      key: "rentalBps" as const,
                      val: settings.commissions.rentalBps,
                      max: 1500,
                    },
                  ].map((item) => (
                    <div
                      key={item.key}
                      className="p-4 bg-surface-subtle rounded-xl border border-border space-y-2"
                    >
                      <div className="flex items-center justify-between text-xs font-bold">
                        <span className="text-ink">{item.name}</span>
                        <span className="text-brand-600 font-mono text-xs bg-brand-50 px-2 py-0.5 rounded border border-brand-200">
                          {(item.val / 100).toFixed(1)}%
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
                      <div className="flex justify-between text-[10px] text-ink-subtle font-mono">
                        <span>0%</span>
                        <span>{item.val} bps</span>
                        <span>{item.max / 100}%</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Right Column: Live Financial Simulator Desk (5 cols - Clean Light Surface) */}
          <div className="lg:col-span-5 space-y-4">
            <div className="bg-white p-6 rounded-2xl border border-border shadow-sm space-y-5 sticky top-24">
              {/* Simulator Header */}
              <div className="flex items-center justify-between border-b border-border pb-3">
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-lg bg-brand-50 text-brand-600 flex items-center justify-center font-bold">
                    <TrendingUp className="w-4 h-4" />
                  </div>
                  <div>
                    <h3 className="text-sm font-bold text-ink">Live Financial Simulator</h3>
                    <p className="text-[11px] text-ink-muted">Pre-flight unit economics validator</p>
                  </div>
                </div>
                <span className="text-[10px] bg-brand-50 text-brand-700 border border-brand-200 px-2 py-0.5 rounded-full font-bold">
                  Interactive
                </span>
              </div>

              {/* Explanatory note */}
              <div className="text-[11px] text-ink-muted bg-surface-subtle p-3 rounded-xl border border-border flex items-start gap-2">
                <HelpCircle className="w-3.5 h-3.5 text-brand-600 shrink-0 mt-0.5" />
                <span>
                  Simulates customer delivery fees, rider payout, and platform net margin based on distance and basket size before saving changes.
                </span>
              </div>

              {/* Interactive Scenario Controls */}
              <div className="space-y-3 text-xs">
                <div>
                  <div className="flex justify-between text-[11px] text-ink-muted mb-1.5 font-medium">
                    <span>Simulated Distance:</span>
                    <span className="font-mono font-bold text-ink">{simDistanceKm} km</span>
                  </div>
                  <div className="grid grid-cols-4 gap-1.5">
                    {[3, 5, 8, 12].map((km) => (
                      <button
                        key={km}
                        type="button"
                        onClick={() => setSimDistanceKm(km)}
                        className={`py-1.5 rounded-xl text-xs font-semibold transition-all border ${
                          simDistanceKm === km
                            ? "bg-brand-600 text-white border-brand-600 shadow-sm"
                            : "bg-surface-subtle text-ink-muted border-border hover:bg-slate-100"
                        }`}
                      >
                        {km} km
                      </button>
                    ))}
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-[11px] text-ink-muted mb-1.5 font-medium">
                    <span>Order Basket (Food / Mart):</span>
                    <span className="font-mono font-bold text-ink">Rs {simOrderAmountNpr}</span>
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
                        className={`py-1.5 px-2 rounded-xl text-xs font-semibold transition-all border text-center ${
                          simOrderAmountNpr === opt.val
                            ? "bg-brand-600 text-white border-brand-600 shadow-sm"
                            : "bg-surface-subtle text-ink-muted border-border hover:bg-slate-100"
                        }`}
                      >
                        {opt.label}
                      </button>
                    ))}
                  </div>
                </div>
              </div>

              {/* Economic Calculation Breakdown */}
              <div className="p-4 bg-surface-subtle rounded-xl border border-border space-y-2.5 text-xs">
                <div className="flex justify-between py-1 border-b border-slate-200 text-ink-muted">
                  <span>Platform Fee (Checkout):</span>
                  <span className="font-mono font-bold text-ink">
                    Rs {(platformFeePaisa / 100).toFixed(2)}
                  </span>
                </div>

                <div className="flex justify-between py-1 border-b border-slate-200 text-ink-muted">
                  <span>
                    Delivery ({simDistanceKm} km):
                  </span>
                  <span className="font-mono font-bold text-ink">
                    Rs {(standardDeliveryPaisa / 100).toFixed(2)}
                  </span>
                </div>

                <div className="flex justify-between py-1.5 px-2 bg-white rounded-lg border border-border items-center">
                  <div>
                    <div className="font-bold text-ink">Customer Charged Delivery</div>
                    {isFreeDelivery && (
                      <span className="text-[10px] text-emerald-700 font-medium">
                        100% Free Delivery Promotion Applied
                      </span>
                    )}
                  </div>
                  <span
                    className={`text-xs font-bold font-mono ${
                      isFreeDelivery ? "text-slate-400 line-through" : "text-brand-700"
                    }`}
                  >
                    Rs {(customerChargedDeliveryPaisa / 100).toFixed(2)}
                  </span>
                </div>

                <div className="flex justify-between py-1 border-b border-slate-200 text-ink-muted">
                  <span>Driver Payout (Base + Extra):</span>
                  <span className="font-mono font-bold text-ink">
                    Rs {(totalDriverPayoutPaisa / 100).toFixed(2)}
                  </span>
                </div>

                <div className="flex justify-between py-1 border-b border-slate-200 text-ink-muted">
                  <span>Merchant Take-Rate ({settings.commissions.foodBps / 100}%):</span>
                  <span className="font-mono font-bold text-ink">
                    +Rs {(merchantCommissionPaisa / 100).toFixed(2)}
                  </span>
                </div>

                <div className="flex justify-between py-1 border-b border-slate-200 text-ink-muted">
                  <span>Delivery Margin (Fee - Payout):</span>
                  <span
                    className={`font-mono font-bold ${
                      platformDeliveryMarginPaisa >= 0 ? "text-emerald-700" : "text-rose-700"
                    }`}
                  >
                    {platformDeliveryMarginPaisa >= 0 ? "+" : ""}Rs {(platformDeliveryMarginPaisa / 100).toFixed(2)}
                  </span>
                </div>

                {/* Net Unit Economics Card */}
                <div className="p-3 rounded-xl border bg-brand-50 border-brand-200 flex items-center justify-between text-brand-950 mt-2">
                  <div>
                    <div className="text-[10px] uppercase font-bold text-brand-800">
                      Platform Net Revenue
                    </div>
                    <div className="text-[10px] text-brand-700">Fee + Commission + Margin</div>
                  </div>
                  <div
                    className={`text-base font-bold font-mono ${
                      totalPlatformNetRevenuePaisa >= 0 ? "text-brand-900" : "text-rose-700"
                    }`}
                  >
                    Rs {(totalPlatformNetRevenuePaisa / 100).toFixed(2)}
                  </div>
                </div>
              </div>

              {/* Approval Note & Commit Action */}
              <div className="space-y-3 pt-1">
                <div>
                  <label className="block text-[11px] font-bold text-ink mb-1">
                    Audit Note / Governance Reference
                  </label>
                  <input
                    type="text"
                    placeholder="e.g. Approved seasonal delivery adjustments"
                    value={adminNote}
                    onChange={(e) => setAdminNote(e.target.value)}
                    className="w-full bg-surface-subtle border border-border rounded-xl px-3 py-2 text-xs text-ink focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-600"
                  />
                </div>

                <button
                  onClick={handleSave}
                  className="w-full py-3 bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs rounded-xl shadow-md shadow-brand-900/20 transition-all flex items-center justify-center gap-2"
                >
                  <Save className="w-4 h-4" />
                  Commit Changes to Platform Database
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
