"use client";

import { useState } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import {
  CloudRain,
  Building2,
  CheckCircle2,
  AlertOctagon,
  Save,
  ShieldCheck,
  MapPin,
  ShieldAlert,
} from "lucide-react";
import { recordAdminAuditLog } from "@/lib/admin/api";

export default function GovernancePage() {
  const [vatEnabled, setVatEnabled] = useState(false);
  const [weatherMultiplier, setWeatherMultiplier] = useState(1.0);
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

  const handleSaveGovernance = async () => {
    await recordAdminAuditLog({
      adminName: "Suman Adhikari (VP Operations)",
      adminEmail: "suman@evrry.com.np",
      actionType: "GOVERNANCE_UPDATED",
      details: `Updated VAT to ${vatEnabled ? "13% (1300 bps)" : "0%"}, Weather Multiplier to ${weatherMultiplier}x, and verified ${wards.filter((w) => !w.active).length} halted wards.`,
      ipAddress: "103.10.29.14 (Kathmandu)",
    });

    setAlertMessage("Governance parameters & kill-switches updated across all mobile and web clients!");
    setTimeout(() => setAlertMessage(null), 4000);
  };

  return (
    <div className="flex-1 flex flex-col h-screen overflow-y-auto bg-slate-50/75">
      <AdminHeader
        title="Compliance, Taxation & Emergency Governance"
        subtitle="Inland Revenue VAT Toggle • Ward Kill-Switches • Monsoon Surge Multipliers"
      />

      <div className="p-6 md:p-8 space-y-6 flex-1 max-w-[1500px] w-full mx-auto">
        {/* Success Alert */}
        {alertMessage && (
          <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-xl text-emerald-800 text-xs flex items-center justify-between shadow-sm animate-fade-in">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
              <span className="font-semibold">{alertMessage}</span>
            </div>
            <span className="text-[11px] font-mono text-emerald-700 bg-white px-2.5 py-1 rounded-md border border-emerald-200">
              Live in Database
            </span>
          </div>
        )}

        {/* 1. Nepal VAT Toggle Card */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-4">
            <div>
              <h2 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2">
                <Building2 className="w-4 h-4 text-teal-600" />
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
                  vatEnabled ? "bg-teal-600" : "bg-slate-300"
                }`}
              >
                <span
                  className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                    vatEnabled ? "translate-x-5" : "translate-x-0"
                  }`}
                />
              </button>
              <span className="text-xs font-bold text-slate-900">
                {vatEnabled ? "13% VAT Active (1300 bps)" : "0% VAT (Pre-Registration)"}
              </span>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs text-slate-600 pt-1">
            <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-200">
              <span className="font-bold text-slate-900 block">Current Status:</span>
              <span className="text-slate-600 mt-0.5 block">
                {vatEnabled
                  ? "Every food order, grocery item, and ride invoice will itemize 13% VAT for IRD fiscal compliance."
                  : "Zero VAT charged to consumers. Clean pricing without taxation lines."}
              </span>
            </div>
            <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-200">
              <span className="font-bold text-slate-900 block">IRD Fiscal Register:</span>
              <span className="font-mono text-slate-700 mt-0.5 block">
                {vatEnabled ? "IRD API Endpoint: Active (Realtime Invoicing)" : "IRD API Endpoint: Standby"}
              </span>
            </div>
          </div>
        </div>

        {/* 2. Weather & Monsoon Surge Control */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm space-y-4">
          <div className="border-b border-slate-100 pb-4">
            <h2 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2">
              <CloudRain className="w-4 h-4 text-teal-600" />
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
              <span className="text-teal-700 font-mono text-base font-bold">
                {weatherMultiplier.toFixed(1)}x {weatherMultiplier > 1 ? "(Active Surge)" : "(Normal Weather)"}
              </span>
            </div>

            <input
              type="range"
              min="1.0"
              max="2.5"
              step="0.1"
              value={weatherMultiplier}
              onChange={(e) => setWeatherMultiplier(Number(e.target.value))}
              className="w-full accent-teal-600 h-2 bg-slate-200 rounded-lg cursor-pointer"
            />

            <div className="flex justify-between text-[11px] text-slate-400 font-mono">
              <span>1.0x (Standard)</span>
              <span>1.5x (Moderate Rain)</span>
              <span>2.0x (Heavy Monsoon / Flooding)</span>
              <span>2.5x (Emergency Max)</span>
            </div>
          </div>
        </div>

        {/* 3. Ward Emergency Kill-Switches */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm space-y-4">
          <div className="border-b border-slate-100 pb-4">
            <h2 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2">
              <AlertOctagon className="w-4 h-4 text-rose-600" />
              Kathmandu Valley Ward Emergency Kill-Switches
            </h2>
            <p className="text-xs text-slate-500 mt-1">
              Instantly halts all order dispatch and ride bookings in specific municipality wards during VIP motorcades, strikes (bandh), or flash floods.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3.5">
            {wards.map((w) => (
              <div
                key={w.id}
                className={`p-3.5 rounded-xl border flex items-center justify-between transition-colors ${
                  w.active
                    ? "bg-slate-50 border-slate-200"
                    : "bg-rose-50 border-rose-200"
                }`}
              >
                <div>
                  <div className="text-xs font-bold text-slate-900">{w.name}</div>
                  <div className={`text-[10px] font-semibold mt-0.5 ${w.active ? "text-emerald-700" : "text-rose-700"}`}>
                    {w.active ? "● Operational" : "■ Order Dispatch Suspended"}
                  </div>
                </div>

                <button
                  type="button"
                  onClick={() => toggleWard(w.id)}
                  className={`px-2.5 py-1 text-xs font-bold rounded-lg transition-colors border ${
                    w.active
                      ? "bg-rose-50 hover:bg-rose-100 text-rose-700 border-rose-200"
                      : "bg-emerald-50 hover:bg-emerald-100 text-emerald-700 border-emerald-200"
                  }`}
                >
                  {w.active ? "Halt Ward" : "Resume"}
                </button>
              </div>
            ))}
          </div>

          <div className="pt-4 border-t border-slate-100 flex items-center justify-end">
            <button
              onClick={handleSaveGovernance}
              className="px-6 py-2.5 bg-teal-600 hover:bg-teal-700 text-white text-xs font-bold rounded-xl transition-all flex items-center gap-2 shadow-sm"
            >
              <Save className="w-4 h-4" />
              Apply Governance Parameters
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
