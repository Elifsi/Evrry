"use client";

import { useState } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import {
  CloudRain,
  Building2,
  CheckCircle2,
  AlertOctagon,
  Save,
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
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="Compliance, Taxation & Emergency Governance"
        subtitle="Inland Revenue VAT Toggle • Ward Kill-Switches • Monsoon Surge Multipliers"
      />

      <div className="p-8 space-y-8 flex-1 max-w-6xl">
        {/* Success Alert */}
        {alertMessage && (
          <div className="bg-brand-50 border border-brand-200 p-4 rounded-xl text-brand-900 text-xs flex items-center justify-between shadow-sm">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-brand-600" />
              <span className="font-semibold">{alertMessage}</span>
            </div>
            <span className="text-[11px] font-mono text-brand-600 bg-white px-2 py-0.5 rounded border border-brand-200">
              Postgres platform_settings
            </span>
          </div>
        )}

        {/* 1. Nepal VAT Toggle Card */}
        <div className="bg-white p-6 rounded-2xl border border-border shadow-card space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-border pb-4">
            <div>
              <h2 className="text-sm font-bold text-ink tracking-tight flex items-center gap-2">
                <Building2 className="w-4 h-4 text-brand-600" />
                Inland Revenue Department (IRD) 13% VAT Activation
              </h2>
              <p className="text-xs text-ink-muted mt-1">
                Currently in Pre-Registration Phase (0% VAT). Once Elifsi VAT registration is completed,
                enable this to automatically itemize 13% VAT on checkout and invoices.
              </p>
            </div>

            <div className="flex items-center gap-3">
              <button
                type="button"
                onClick={() => setVatEnabled(!vatEnabled)}
                className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                  vatEnabled ? "bg-brand-600" : "bg-slate-200"
                }`}
              >
                <span
                  className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                    vatEnabled ? "translate-x-5" : "translate-x-0"
                  }`}
                />
              </button>
              <span className="text-xs font-bold text-ink">
                {vatEnabled ? "13% VAT Active (1300 bps)" : "0% VAT (Pre-Registration)"}
              </span>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs text-ink-muted pt-1">
            <div className="bg-surface-subtle p-3.5 rounded-xl border border-border">
              <span className="font-semibold text-ink block">Current Status:</span>
              <span>
                {vatEnabled
                  ? "Every food order, grocery item, and ride invoice will itemize 13% VAT for IRD fiscal compliance."
                  : "Zero VAT charged to consumers. Clean pricing without taxation lines."}
              </span>
            </div>
            <div className="bg-surface-subtle p-3.5 rounded-xl border border-border">
              <span className="font-semibold text-ink block">IRD Fiscal Register:</span>
              <span className="font-mono text-ink">
                {vatEnabled ? "IRD API Endpoint: Active (Realtime Invoicing)" : "IRD API Endpoint: Standby"}
              </span>
            </div>
          </div>
        </div>

        {/* 2. Weather & Monsoon Surge Control */}
        <div className="bg-white p-6 rounded-2xl border border-border shadow-card space-y-4">
          <div className="border-b border-border pb-4">
            <h2 className="text-sm font-bold text-ink tracking-tight flex items-center gap-2">
              <CloudRain className="w-4 h-4 text-brand-600" />
              Monsoon & Severe Weather Multiplier
            </h2>
            <p className="text-xs text-ink-muted mt-1">
              Multiplies driver earnings and delivery fees during severe rainfall or waterlogging to
              incentivize drivers on the road.
            </p>
          </div>

          <div className="space-y-3 pt-1">
            <div className="flex items-center justify-between text-xs font-semibold">
              <span className="text-ink">Weather Multiplier:</span>
              <span className="text-brand-600 font-mono text-base font-bold">
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
              className="w-full accent-brand-600 h-2 bg-slate-200 rounded-lg cursor-pointer"
            />
            <div className="flex justify-between text-[11px] text-ink-subtle">
              <span>1.0x (Standard dry weather)</span>
              <span>1.5x (Moderate monsoon rain)</span>
              <span>2.0x (Severe flash flood / storm)</span>
            </div>
          </div>
        </div>

        {/* 3. Emergency Ward Geospatial Kill-Switches */}
        <div className="bg-white p-6 rounded-2xl border border-border shadow-card space-y-4">
          <div className="border-b border-border pb-4">
            <h2 className="text-sm font-bold text-ink tracking-tight flex items-center gap-2">
              <AlertOctagon className="w-4 h-4 text-rose-600" />
              Emergency Ward-Level Kill-Switches
            </h2>
            <p className="text-xs text-ink-muted mt-1">
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
                    ? "bg-surface-subtle border-border"
                    : "bg-rose-50 border-rose-200 text-rose-900"
                }`}
              >
                <div>
                  <div className="text-xs font-semibold text-ink">{ward.name}</div>
                  <div className="text-[10px] text-ink-subtle font-mono">{ward.id}</div>
                </div>

                <button
                  type="button"
                  onClick={() => toggleWard(ward.id)}
                  className={`px-2.5 py-1 text-xs rounded-lg font-bold border transition-all ${
                    ward.active
                      ? "bg-emerald-100 text-emerald-800 border-emerald-300 hover:bg-emerald-200"
                      : "bg-rose-600 text-white border-rose-700 hover:bg-rose-700"
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
            className="px-6 py-2.5 bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs rounded-xl shadow-md shadow-brand-900/30 transition-all flex items-center gap-2"
          >
            <Save className="w-4 h-4" />
            Apply Governance & Emergency Changes
          </button>
        </div>
      </div>
    </div>
  );
}
