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
  ChevronDown,
  ChevronUp,
  AlertTriangle,
} from "lucide-react";
import { fetchPricingSettings, updatePricingSettings } from "@/lib/admin/api";
import { PlatformPricingSettings } from "@/lib/admin/types";
import { initialPricingSettings } from "@/lib/admin/mockData";

export default function PricingControlPage() {
  const [settings, setSettings] = useState<PlatformPricingSettings>(initialPricingSettings);
  const [savedSuccess, setSavedSuccess] = useState(false);
  const [savedMessage, setSavedMessage] = useState("");
  const [isRefreshing, setIsRefreshing] = useState(false);

  // Deploy Confirmation Modal
  const [showDeployModal, setShowDeployModal] = useState(false);
  const [deployAuditNote, setDeployAuditNote] = useState("");
  const [isDeploying, setIsDeploying] = useState(false);

  // Section Accordion State
  const [expandedSections, setExpandedSections] = useState({
    delivery: true,
    platform: true,
    splits: true,
  });

  const toggleSection = (sec: "delivery" | "platform" | "splits") => {
    setExpandedSections((prev) => ({ ...prev, [sec]: !prev[sec] }));
  };

  const loadSettings = async () => {
    setIsRefreshing(true);
    const data = await fetchPricingSettings();
    setSettings(data);
    setIsRefreshing(false);
  };

  useEffect(() => {
    loadSettings();
  }, []);

  const handleReset = () => {
    setSettings(initialPricingSettings);
  };

  const handleConfirmDeploy = async () => {
    if (!deployAuditNote.trim()) return;
    setIsDeploying(true);
    const res = await updatePricingSettings(settings, "suman@evrry.com.np");
    setIsDeploying(false);
    setShowDeployModal(false);
    setSavedMessage(res.message);
    setSavedSuccess(true);
    setDeployAuditNote("");
    setTimeout(() => setSavedSuccess(false), 5000);
  };

  // Interactive Live Pre-Flight Simulator State
  const [simDistanceKm, setSimDistanceKm] = useState(5);
  const [simOrderAmountNpr, setSimOrderAmountNpr] = useState(800);

  // Unit Economics Calculations matching Prompt Specifications
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
  const vatPaisa = Math.round((platformFeePaisa * settings.vatBps) / 10000);

  // Customer Pays = Basket + Delivery Fee + Platform Fee + VAT
  const totalCustomerPaysPaisa =
    simOrderAmountNpr * 100 + customerChargedDeliveryPaisa + platformFeePaisa + vatPaisa;

  // Net EVRRY Revenue = Platform Fee + Merchant Commission + Delivery Margin
  const platformDeliveryMarginPaisa = customerChargedDeliveryPaisa - totalDriverPayoutPaisa;
  const totalPlatformNetRevenuePaisa =
    platformFeePaisa + merchantCommissionPaisa + platformDeliveryMarginPaisa;

  return (
    <div className="flex-1 flex flex-col h-screen overflow-y-auto bg-slate-50/75">
      <AdminHeader
        title="Dynamic Fee & Pricing Control Matrix"
        subtitle="Zero-SQL Live Governance • Instant Client-Side Reactive Updates"
        onRefresh={loadSettings}
        isRefreshing={isRefreshing}
      />

      <div className="p-6 md:p-8 space-y-6 flex-1 max-w-[1500px] w-full mx-auto">
        {/* Success Alert */}
        {savedSuccess && (
          <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-xl text-emerald-800 text-xs flex items-center justify-between shadow-sm animate-fade-in">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
              <span className="font-semibold">{savedMessage}</span>
            </div>
            <span className="text-[11px] font-mono text-emerald-700 bg-white px-2.5 py-1 rounded-md border border-emerald-200">
              Live in Database
            </span>
          </div>
        )}

        {/* Action Header Card */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
          <div>
            <h2 className="text-base font-bold text-slate-900 tracking-tight flex items-center gap-2">
              <Percent className="w-5 h-5 text-brand-600" />
              Platform Pricing & Take-Rate Governance
            </h2>
            <p className="text-xs text-slate-500 mt-0.5">
              Parameters apply dynamically across Kathmandu Valley and nationwide operations. Updates permanently logged.
            </p>
          </div>
          <div className="flex items-center gap-2.5 shrink-0">
            <button
              onClick={handleReset}
              className="px-4 py-2 border border-slate-200 hover:bg-slate-50 text-slate-600 hover:text-slate-900 text-xs font-semibold rounded-xl transition-colors flex items-center gap-1.5"
            >
              <RotateCcw className="w-3.5 h-3.5" />
              Reset Defaults
            </button>
            <button
              onClick={() => setShowDeployModal(true)}
              className="px-5 py-2 bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold rounded-xl transition-all flex items-center gap-1.5 shadow-sm"
            >
              <Save className="w-3.5 h-3.5" />
              Deploy Pricing Version
            </button>
          </div>
        </div>

        {/* Main Grid: Parameters Workspace (Left) & Simulator Desk (Right) */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          {/* Left Column: Expandable Parameter Sections (7 cols) */}
          <div className="lg:col-span-7 space-y-4">
            {/* 1. Delivery Fee Engine */}
            <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
              <button
                type="button"
                onClick={() => toggleSection("delivery")}
                className="w-full p-4 flex items-center justify-between bg-white hover:bg-slate-50/50 transition-colors border-b border-slate-100"
              >
                <div className="flex items-center gap-2.5">
                  <div className="w-8 h-8 rounded-lg bg-brand-50 text-brand-600 flex items-center justify-center">
                    <Truck className="w-4 h-4" />
                  </div>
                  <div className="text-left">
                    <h3 className="text-sm font-bold text-slate-900">Delivery Fee Engine</h3>
                    <p className="text-[11px] text-slate-500">Base radius, delivery fees, and free delivery thresholds</p>
                  </div>
                </div>
                {expandedSections.delivery ? (
                  <ChevronUp className="w-4 h-4 text-slate-400" />
                ) : (
                  <ChevronDown className="w-4 h-4 text-slate-400" />
                )}
              </button>

              {expandedSections.delivery && (
                <div className="p-5 grid grid-cols-1 sm:grid-cols-2 gap-4 animate-fade-in">
                  <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/70">
                    <label className="block text-xs font-bold text-slate-800 mb-1">
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
                        className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500/20 font-bold text-slate-900"
                      />
                      <span className="absolute right-3 top-2 text-xs text-slate-400 font-bold">km</span>
                    </div>
                    <p className="text-[10px] text-slate-500 mt-1">Distance covered by base fee (Default: 3 km).</p>
                  </div>

                  <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/70">
                    <label className="block text-xs font-bold text-slate-800 mb-1">
                      Base Delivery Fee (NPR within radius)
                    </label>
                    <div className="relative mt-1">
                      <span className="absolute left-3 top-2 text-xs text-slate-400 font-bold">Rs</span>
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
                        className="w-full pl-9 pr-3 py-2 text-xs bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500/20 font-bold text-slate-900"
                      />
                    </div>
                    <p className="text-[10px] text-slate-500 mt-1">Standard rate charged within base radius (Default: Rs 50).</p>
                  </div>

                  <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/70">
                    <label className="block text-xs font-bold text-slate-800 mb-1">
                      Incremental Distance Rate (NPR per km)
                    </label>
                    <div className="relative mt-1">
                      <span className="absolute left-3 top-2 text-xs text-slate-400 font-bold">Rs</span>
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
                        className="w-full pl-9 pr-3 py-2 text-xs bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500/20 font-bold text-slate-900"
                      />
                      <span className="absolute right-3 top-2 text-xs text-slate-400 font-bold">/km</span>
                    </div>
                    <p className="text-[10px] text-slate-500 mt-1">Added per km beyond base radius (Default: Rs 15/km).</p>
                  </div>

                  <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/70">
                    <label className="block text-xs font-bold text-slate-800 mb-1">
                      Free Delivery Threshold (NPR Basket)
                    </label>
                    <div className="relative mt-1">
                      <span className="absolute left-3 top-2 text-xs text-slate-400 font-bold">Rs</span>
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
                        className="w-full pl-9 pr-3 py-2 text-xs bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500/20 font-bold text-slate-900"
                      />
                    </div>
                    <p className="text-[10px] text-slate-500 mt-1">Baskets &ge; this receive 100% free delivery (Default: Rs 1,200).</p>
                  </div>
                </div>
              )}
            </div>

            {/* 2. Platform Fees & Taxes */}
            <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
              <button
                type="button"
                onClick={() => toggleSection("platform")}
                className="w-full p-4 flex items-center justify-between bg-white hover:bg-slate-50/50 transition-colors border-b border-slate-100"
              >
                <div className="flex items-center gap-2.5">
                  <div className="w-8 h-8 rounded-lg bg-brand-50 text-brand-600 flex items-center justify-center">
                    <DollarSign className="w-4 h-4" />
                  </div>
                  <div className="text-left">
                    <h3 className="text-sm font-bold text-slate-900">Platform Fees & Taxes</h3>
                    <p className="text-[11px] text-slate-500">Fixed checkout fee, promotional waivers, and statutory VAT</p>
                  </div>
                </div>
                {expandedSections.platform ? (
                  <ChevronUp className="w-4 h-4 text-slate-400" />
                ) : (
                  <ChevronDown className="w-4 h-4 text-slate-400" />
                )}
              </button>

              {expandedSections.platform && (
                <div className="p-5 grid grid-cols-1 sm:grid-cols-2 gap-4 animate-fade-in">
                  <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/70">
                    <label className="block text-xs font-bold text-slate-800 mb-1">
                      Fixed Order Platform Fee (NPR)
                    </label>
                    <div className="relative mt-1">
                      <span className="absolute left-3 top-2 text-xs text-slate-400 font-bold">Rs</span>
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
                        className="w-full pl-9 pr-3 py-2 text-xs bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500/20 font-bold text-slate-900"
                      />
                    </div>
                    <p className="text-[10px] text-slate-500 mt-1">Charged on every food, mart, and parcel checkout (Default: Rs 10).</p>
                  </div>

                  <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/70 flex flex-col justify-between">
                    <div>
                      <label className="block text-xs font-bold text-slate-800 mb-0.5">
                        Promotional Fee Waiver Switch
                      </label>
                      <p className="text-[10px] text-slate-500">Instantly waives the platform fee across mobile apps.</p>
                    </div>
                    <div className="pt-2 flex items-center gap-3">
                      <button
                        type="button"
                        onClick={() =>
                          setSettings({
                            ...settings,
                            platformFeeWaiver: !settings.platformFeeWaiver,
                          })
                        }
                        className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                          settings.platformFeeWaiver ? "bg-brand-600" : "bg-slate-300"
                        }`}
                      >
                        <span
                          className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                            settings.platformFeeWaiver ? "translate-x-5" : "translate-x-0"
                          }`}
                        />
                      </button>
                      <span className="text-xs font-bold text-slate-800">
                        {settings.platformFeeWaiver ? "Waiver Active (Rs 0)" : "Standard Fee Active"}
                      </span>
                    </div>
                  </div>

                  <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/70 col-span-1 sm:col-span-2 flex items-center justify-between">
                    <div>
                      <label className="block text-xs font-bold text-slate-800">
                        Nepal IRD Value Added Tax (VAT)
                      </label>
                      <p className="text-[10px] text-slate-500 mt-0.5">
                        Statutory VAT itemized on taxable digital platform revenue.
                      </p>
                    </div>
                    <span className="font-mono font-bold text-xs bg-brand-50 text-brand-800 px-2.5 py-1 rounded-lg border border-brand-200/70">
                      {(settings.vatBps / 100).toFixed(1)}% (1300 bps)
                    </span>
                  </div>
                </div>
              )}
            </div>

            {/* 3. Take-Rate Splits & Commissions */}
            <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
              <button
                type="button"
                onClick={() => toggleSection("splits")}
                className="w-full p-4 flex items-center justify-between bg-white hover:bg-slate-50/50 transition-colors border-b border-slate-100"
              >
                <div className="flex items-center gap-2.5">
                  <div className="w-8 h-8 rounded-lg bg-brand-50 text-brand-600 flex items-center justify-center">
                    <Sparkles className="w-4 h-4" />
                  </div>
                  <div className="text-left">
                    <h3 className="text-sm font-bold text-slate-900">Take-Rate Splits & Vertical Commissions</h3>
                    <p className="text-[11px] text-slate-500">Rider distance share and merchant commission percentages</p>
                  </div>
                </div>
                {expandedSections.splits ? (
                  <ChevronUp className="w-4 h-4 text-slate-400" />
                ) : (
                  <ChevronDown className="w-4 h-4 text-slate-400" />
                )}
              </button>

              {expandedSections.splits && (
                <div className="p-5 space-y-4 animate-fade-in">
                  {/* Rider Split */}
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pb-4 border-b border-slate-100">
                    <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/70">
                      <label className="block text-xs font-bold text-slate-800 mb-1">
                        Base Rider Pay (0-3 km)
                      </label>
                      <div className="relative mt-1">
                        <span className="absolute left-3 top-2 text-xs text-slate-400 font-bold">Rs</span>
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
                          className="w-full pl-9 pr-3 py-2 text-xs bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500/20 font-bold text-slate-900"
                        />
                      </div>
                      <p className="text-[10px] text-slate-500 mt-1">Guaranteed rider pay per completed drop (Default: Rs 40).</p>
                    </div>

                    <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/70">
                      <label className="block text-xs font-bold text-slate-800 mb-1">
                        Rider Distance Share (%)
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
                          className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500/20 font-bold text-slate-900"
                        />
                        <span className="absolute right-3 top-2 text-xs text-slate-400 font-bold">%</span>
                      </div>
                      <p className="text-[10px] text-slate-500 mt-1">Extra distance tariff passed directly to rider (Default: 80%).</p>
                    </div>
                  </div>

                  {/* Vertical Commissions Sliders */}
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
                    {[
                      { name: "Restaurant Commission", key: "foodBps" as const, val: settings.commissions.foodBps, max: 3000 },
                      { name: "Grocery Mart Commission", key: "groceryBps" as const, val: settings.commissions.groceryBps, max: 2000 },
                      { name: "Bike-Taxi Rides", key: "bikeTaxiBps" as const, val: settings.commissions.bikeTaxiBps, max: 2500 },
                      { name: "Cab & Taxi Rides", key: "cabTaxiBps" as const, val: settings.commissions.cabTaxiBps, max: 2500 },
                      { name: "Hotels & Stays", key: "hotelBps" as const, val: settings.commissions.hotelBps, max: 2500 },
                      { name: "Room Rentals", key: "rentalBps" as const, val: settings.commissions.rentalBps, max: 1500 },
                    ].map((item) => (
                      <div key={item.key} className="p-3 bg-slate-50 rounded-xl border border-slate-200/70 space-y-1.5">
                        <div className="flex items-center justify-between text-xs font-bold">
                          <span className="text-slate-800">{item.name}</span>
                          <span className="text-brand-700 font-mono text-[11px] bg-brand-50 px-2 py-0.5 rounded border border-brand-200/60">
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
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Right Column: Live Pre-Flight Unit Economics Simulator (5 cols) */}
          <div className="lg:col-span-5 space-y-4">
            <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm space-y-5 sticky top-20">
              <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-lg bg-brand-50 text-brand-600 flex items-center justify-center font-bold">
                    <TrendingUp className="w-4 h-4" />
                  </div>
                  <div>
                    <h3 className="text-sm font-bold text-slate-900">Pre-Flight Unit Economics Simulator</h3>
                    <p className="text-[11px] text-slate-500">Live parametric formula validator</p>
                  </div>
                </div>
                <span className="text-[10px] bg-brand-50 text-brand-700 border border-brand-200 px-2 py-0.5 rounded-full font-bold">
                  Live
                </span>
              </div>

              {/* Dynamic Sliders */}
              <div className="space-y-4 text-xs">
                {/* Distance Slider */}
                <div className="space-y-1.5">
                  <div className="flex justify-between font-semibold">
                    <span className="text-slate-700">Distance Slider:</span>
                    <span className="font-mono font-bold text-brand-700">{simDistanceKm} km</span>
                  </div>
                  <input
                    type="range"
                    min="1"
                    max="15"
                    step="1"
                    value={simDistanceKm}
                    onChange={(e) => setSimDistanceKm(Number(e.target.value))}
                    className="w-full accent-brand-600 h-1.5 bg-slate-200 rounded-lg cursor-pointer"
                  />
                  <div className="flex justify-between text-[10px] text-slate-400 font-mono">
                    <span>1 km</span>
                    <span>5 km</span>
                    <span>10 km</span>
                    <span>15 km</span>
                  </div>
                </div>

                {/* Cart Basket Slider */}
                <div className="space-y-1.5">
                  <div className="flex justify-between font-semibold">
                    <span className="text-slate-700">Cart Basket Size:</span>
                    <span className="font-mono font-bold text-brand-700">Rs {simOrderAmountNpr.toLocaleString()}</span>
                  </div>
                  <input
                    type="range"
                    min="300"
                    max="5000"
                    step="100"
                    value={simOrderAmountNpr}
                    onChange={(e) => setSimOrderAmountNpr(Number(e.target.value))}
                    className="w-full accent-brand-600 h-1.5 bg-slate-200 rounded-lg cursor-pointer"
                  />
                  <div className="flex justify-between text-[10px] text-slate-400 font-mono">
                    <span>Rs 300</span>
                    <span>Rs 1,200 (Threshold)</span>
                    <span>Rs 5,000</span>
                  </div>
                </div>
              </div>

              {/* Specification Formulas Output Table */}
              <div className="p-4 bg-slate-50 rounded-xl border border-slate-200/80 space-y-3 text-xs">
                {/* 1. Customer Pays Formula */}
                <div className="space-y-1 pb-2 border-b border-slate-200/80">
                  <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">
                    1. Customer Pays Breakdown
                  </div>
                  <div className="flex justify-between text-slate-600">
                    <span>Basket + Delivery ({simDistanceKm} km):</span>
                    <span className="font-mono">
                      Rs {simOrderAmountNpr} + Rs {(customerChargedDeliveryPaisa / 100).toFixed(0)}
                    </span>
                  </div>
                  <div className="flex justify-between text-slate-600">
                    <span>Platform Fee + VAT ({settings.vatBps / 100}%):</span>
                    <span className="font-mono">
                      Rs {(platformFeePaisa / 100).toFixed(0)} + Rs {(vatPaisa / 100).toFixed(0)}
                    </span>
                  </div>
                  <div className="flex justify-between font-bold text-slate-900 pt-1">
                    <span>Total Customer Pays:</span>
                    <span className="font-mono text-sm text-brand-700">
                      Rs {(totalCustomerPaysPaisa / 100).toFixed(0)}
                    </span>
                  </div>
                  {isFreeDelivery && (
                    <div className="text-[10px] text-emerald-700 font-semibold bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                      ✓ Free delivery qualified (&ge; Rs {settings.freeDeliveryThresholdPaisa / 100})
                    </div>
                  )}
                </div>

                {/* 2. Driver Take-Home Formula */}
                <div className="space-y-1 pb-2 border-b border-slate-200/80">
                  <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">
                    2. Driver Take-Home Formula
                  </div>
                  <div className="flex justify-between text-slate-600">
                    <span>Base Cut (0-3 km):</span>
                    <span className="font-mono">Rs {(settings.driverBaseSharePaisa / 100).toFixed(0)}</span>
                  </div>
                  <div className="flex justify-between text-slate-600">
                    <span>Distance Share ({settings.driverExtraDistanceShareBps / 100}% of {extraKm} km):</span>
                    <span className="font-mono">+Rs {(driverExtraCutPaisa / 100).toFixed(0)}</span>
                  </div>
                  <div className="flex justify-between font-bold text-slate-900 pt-1">
                    <span>Total Driver Take-Home:</span>
                    <span className="font-mono text-sm text-emerald-700">
                      Rs {(totalDriverPayoutPaisa / 100).toFixed(0)}
                    </span>
                  </div>
                </div>

                {/* 3. Net EVRRY Revenue Formula */}
                <div className="space-y-1">
                  <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">
                    3. Net EVRRY Revenue Formula
                  </div>
                  <div className="flex justify-between text-slate-600">
                    <span>Platform Fee:</span>
                    <span className="font-mono">+Rs {(platformFeePaisa / 100).toFixed(0)}</span>
                  </div>
                  <div className="flex justify-between text-slate-600">
                    <span>Merchant Commission ({settings.commissions.foodBps / 100}%):</span>
                    <span className="font-mono">+Rs {(merchantCommissionPaisa / 100).toFixed(0)}</span>
                  </div>
                  <div className="flex justify-between text-slate-600">
                    <span>Delivery Margin (Fee - Driver):</span>
                    <span className={`font-mono ${platformDeliveryMarginPaisa >= 0 ? "text-emerald-700" : "text-rose-700"}`}>
                      {platformDeliveryMarginPaisa >= 0 ? "+" : ""}Rs {(platformDeliveryMarginPaisa / 100).toFixed(0)}
                    </span>
                  </div>
                  <div className="flex justify-between font-bold text-slate-900 pt-1 bg-white p-2 rounded-lg border border-slate-200">
                    <span>Net EVRRY Revenue:</span>
                    <span className="font-mono text-sm text-brand-800 font-extrabold">
                      Rs {(totalPlatformNetRevenuePaisa / 100).toFixed(0)}
                    </span>
                  </div>
                </div>
              </div>

              {/* Primary Action Button */}
              <button
                onClick={() => setShowDeployModal(true)}
                className="w-full py-3 bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs rounded-xl shadow-sm transition-all flex items-center justify-center gap-2"
              >
                <Save className="w-4 h-4" />
                Deploy Pricing Version
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Deploy Confirmation Modal */}
      {showDeployModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-slate-200 max-w-md w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <AlertTriangle className="w-4 h-4 text-amber-600" />
                <h3 className="text-sm font-bold text-slate-900">
                  Deploy Pricing Version to Production
                </h3>
              </div>
              <button
                onClick={() => setShowDeployModal(false)}
                className="text-slate-400 hover:text-slate-700 text-sm"
              >
                ✕
              </button>
            </div>

            <p className="text-xs text-slate-500">
              This action updates platform settings and invalidates client fee cache immediately across all consumer and rider apps in Nepal.
            </p>

            <div>
              <label className="block text-xs font-semibold text-slate-800 mb-1">
                Audit Reason & Reference (Required):
              </label>
              <textarea
                rows={2}
                required
                value={deployAuditNote}
                onChange={(e) => setDeployAuditNote(e.target.value)}
                placeholder="e.g. Revised monsoon delivery tariffs approved by VP Operations"
                className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500/20"
              />
            </div>

            <div className="flex items-center justify-end gap-2.5 pt-2 border-t border-slate-100">
              <button
                onClick={() => setShowDeployModal(false)}
                className="px-4 py-2 text-xs font-semibold text-slate-500 hover:text-slate-800"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmDeploy}
                disabled={!deployAuditNote.trim() || isDeploying}
                className="px-5 py-2 text-xs font-bold text-white bg-brand-600 hover:bg-brand-700 rounded-xl shadow-sm disabled:opacity-50"
              >
                {isDeploying ? "Deploying..." : "Confirm & Deploy"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
