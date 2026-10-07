"use client";

import { useState } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import {
  ShieldAlert,
  CloudRain,
  Building2,
  CheckCircle2,
  AlertOctagon,
  Flame,
  Power,
  RotateCcw,
  Save,
} from "lucide-react";

export default function GovernancePage() {
  const [vatEnabled, setVatEnabled] = useState(false);
  const [weatherMultiplier, setWeatherMultiplier] = useState(1.0);
  const [maintenanceMode, setMaintenanceMode] = useState(false);
  const [alertMessage, setAlertMessage] = useState<string | null>(null);

  // Ward emergency availability toggles
  const [wards, setWards] = useState([
    { id: "ktm-01", name: "Kathmandu Ward 1 (Naxal / Hattisar)", active: true },
    { id: "ktm-22", name: "Kathmandu Ward 22 (New Road / Asan)", active: true },
    { id: "ktm-29", name: "Kathmandu Ward 29 (Anamnagar / Babarmahal)", active: true },
    { id: "lal-02", name: "Lalitpur Ward 2 (Sanepa / Jhamsikhel)", active: true },
    { id: "lal-03", name: "Lalitpur Ward 3 (Pulchowk / Jawalakhel)", active: true },
    { id: "bkt-01", name: "Bhaktapur Ward 1 (Durbar Square)", active: true },
  ]);

  const toggleWard = (id: string) => {
    setWards((prev) =>
      prev.map((w) => (w.id === id ? { ...w, active: !w.active } : w))
    );
  };

  const handleSaveGovernance = () => {
    setAlertMessage("Governance parameters & kill-switches updated across all mobile and web clients!");
    setTimeout(() => setAlertMessage(null), 4000);
  };

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="Compliance, Taxation & Emergency Governance"
        subtitle="Inland Revenue VAT Toggle • Ward Kill-Switches • Monsoon Surge Multipliers"
      />

      <div className="p-8 space-y-8 flex-1 max-w-6xl">
        {/* Success Alert */}
        {alertMessage && (
          <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-xl text-emerald-800 text-xs flex items-center justify-between">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600" />
              <span className="font-semibold">{alertMessage}</span>
            </div>
            <span className="text-[11px] font-mono text-emerald-600">Postgres platform_settings</span>
          </div>
        )}

        {/* 1. Nepal VAT Toggle Card */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-4">
            <div>
              <h2 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2">
                <Building2 className="w-4 h-4 text-emerald-600" />
                Inland Revenue Department (IRD) 13% VAT Activation
              </h2>
              <p className="text-xs text-slate-500 mt-1">
                Currently in Pre-Registration Phase (0% VAT). Once Elifsi VAT registration is completed,
                enable this to automatically itemize 13% VAT on checkout and invoices.
              </p>
            </div>

            <div className="flex items-center gap-3">
              <button
                type="button"
                onClick={() => setVatEnabled(!vatEnabled)}
                className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                  vatEnabled ? "bg-emerald-600" : "bg-slate-200"
                }`}
              >
                <span
                  className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                    vatEnabled ? "translate-x-5" : "translate-x-0"
                  }`}
                />
              </button>
              <span className="text-xs font-bold text-slate-800">
                {vatEnabled ? "13% VAT Active (1300 bps)" : "0% VAT (Pre-Registration)"}
              </span>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs text-slate-600 pt-1">
            <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-200">
              <span className="font-semibold text-slate-800 block">Current Status:</span>
              <span>
                {vatEnabled
                  ? "Every food order, grocery item, and ride invoice will itemize 13% VAT for IRD fiscal compliance."
                  : "Zero VAT charged to consumers. Clean pricing without taxation lines."}
              </span>
            </div>
            <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-200">
              <span className="font-semibold text-slate-800 block">IRD Fiscal Register:</span>
              <span className="font-mono text-slate-700">
                {vatEnabled ? "IRD API Endpoint: Active (Realtime Invoicing)" : "IRD API Endpoint: Standby"}
              </span>
            </div>
          </div>
        </div>

        {/* 2. Weather & Monsoon Surge Control */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <div className="border-b border-slate-100 pb-4">
            <h2 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2">
              <CloudRain className="w-4 h-4 text-blue-600" />
              Monsoon & Severe Weather Multiplier
            </h2>
            <p className="text-xs text-slate-500 mt-1">
              Multiplies driver earnings and delivery fees during severe rainfall or waterlogging to
              incentivize drivers on the road.
            </p>
          </div>

          <div className="space-y-3 pt-1">
            <div className="flex items-center justify-between text-xs font-semibold">
              <span className="text-slate-800">Weather Multiplier:</span>
              <span className="text-blue-700 font-mono text-base font-bold">
                {weatherMultiplier.toFixed(1)}x {weatherMultiplier > 1 ? "(Active Surge)" : "(Normal Weather)"}
              </span>
            </div>
            <input
              type="range"
              min="1.0"
              max="2.0"
              step="0.1"
              value={weatherMultiplier}
              onChange={(e) => setWeatherMultiplier(Number(e.target.value))}
              className="w-full accent-blue-600 h-2 bg-slate-200 rounded-lg cursor-pointer"
            />
            <div className="flex justify-between text-[11px] text-slate-400">
              <span>1.0x (Standard dry weather)</span>
              <span>1.5x (Moderate monsoon rain)</span>
              <span>2.0x (Severe flash flood / storm)</span>
            </div>
          </div>
        </div>

        {/* 3. Emergency Ward Geospatial Kill-Switches */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <div className="border-b border-slate-100 pb-4">
            <h2 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2">
              <AlertOctagon className="w-4 h-4 text-red-600" />
              Emergency Ward-Level Kill-Switches
            </h2>
            <p className="text-xs text-slate-500 mt-1">
              Instantly halts order acceptance and ride dispatching in specific wards during roadblocks,
              demonstrations (Banda), or construction emergencies.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3 pt-1">
            {wards.map((ward) => (
              <div
                key={ward.id}
                className={`p-3.5 rounded-xl border flex items-center justify-between transition-all ${
                  ward.active
                    ? "bg-slate-50 border-slate-200"
                    : "bg-red-50 border-red-200 text-red-900"
                }`}
              >
                <div>
                  <div className="text-xs font-semibold text-slate-900">{ward.name}</div>
                  <div className="text-[10px] text-slate-400 font-mono">{ward.id}</div>
                </div>

                <button
                  type="button"
                  onClick={() => toggleWard(ward.id)}
                  className={`px-2.5 py-1 text-xs rounded-lg font-bold border transition-all ${
                    ward.active
                      ? "bg-emerald-100 text-emerald-800 border-emerald-300 hover:bg-emerald-200"
                      : "bg-red-600 text-white border-red-700 hover:bg-red-700"
                  }`}
                >
                  {ward.active ? "Open" : "HALTED"}
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Save Governance Button */}
        <div className="flex justify-end">
          <button
            onClick={handleSaveGovernance}
            className="px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-xs rounded-xl shadow-md transition-all flex items-center gap-2"
          >
            <Save className="w-4 h-4" />
            Apply Governance & Emergency Changes
          </button>
        </div>
      </div>
    </div>
  );
}
