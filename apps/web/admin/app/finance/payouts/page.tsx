"use client";

import { useState } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import {
  Wallet,
  Download,
  CheckCircle2,
  AlertTriangle,
  Lock,
  Unlock,
  Building,
  Bike,
  CreditCard,
  Send,
  FileSpreadsheet,
} from "lucide-react";
import {
  samplePayoutBatch,
  sampleRiderCodBalances,
} from "@/lib/admin/mockData";
import { MidnightPayoutBatch, RiderCodBalance } from "@/lib/admin/types";

export default function PayoutsConsolePage() {
  const [batch, setBatch] = useState<MidnightPayoutBatch>(samplePayoutBatch);
  const [riders, setRiders] = useState<RiderCodBalance[]>(sampleRiderCodBalances);
  const [executionMessage, setExecutionMessage] = useState<string | null>(null);

  const handleExecutePayout = () => {
    setBatch({ ...batch, status: "completed" });
    setExecutionMessage(
      `Disbursed NPR ${(batch.totalDisbursementPaisa / 100).toLocaleString()} via ConnectIPS & Khalti Payout API! Reference: DISB-2026-TXN-8812`
    );
    setTimeout(() => setExecutionMessage(null), 5000);
  };

  const handleToggleCodLock = (riderId: string) => {
    setRiders((prev) =>
      prev.map((r) => {
        if (r.riderId === riderId) {
          const nextStatus = r.safetyStatus === "hard_locked" ? "normal" : "hard_locked";
          return { ...r, safetyStatus: nextStatus };
        }
        return r;
      })
    );
  };

  const handleDownloadNchl128 = () => {
    // Generate mock CSV data
    const csvContent =
      "data:text/csv;charset=utf-8," +
      "BatchID,BeneficiaryBank,AccountNumber,AccountName,AmountNPR,Narration\n" +
      `BATCH-2026-10-07,01-NabilBank,0010101010101,Himalayan Java Pvt Ltd,1568250,Settlement 2026-10-07\n` +
      `BATCH-2026-10-07,04-GlobalIMERider,0040202020202,Dawa Sherpa,135600,Rider Payout 2026-10-07\n`;
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", `ConnectIPS_NCHL128_${batch.batchId}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="Midnight Settlement & Payout Oversight"
        subtitle="Automated ConnectIPS Interbank Rails • Zero-Hub Rider COD Safety Monitor"
      />

      <div className="p-8 space-y-8 flex-1 max-w-6xl">
        {/* Execution Alert */}
        {executionMessage && (
          <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-xl text-emerald-800 text-xs flex items-center justify-between shadow-sm">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600" />
              <span className="font-semibold">{executionMessage}</span>
            </div>
            <span className="text-[11px] font-mono text-emerald-600">ConnectIPS / NCHL Gateway</span>
          </div>
        )}

        {/* Midnight Batch Aggregation Card */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-4">
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-base font-bold text-slate-900 tracking-tight">
                  Midnight Settlement Batch: {batch.batchId}
                </h2>
                <span
                  className={`text-[10px] font-semibold px-2 py-0.5 rounded uppercase font-mono ${
                    batch.status === "completed"
                      ? "bg-emerald-100 text-emerald-800"
                      : "bg-amber-100 text-amber-800"
                  }`}
                >
                  {batch.status === "completed" ? "Settled & Disbursed" : "Ready for Approval"}
                </span>
              </div>
              <p className="text-xs text-slate-500 mt-1">
                Aggregated daily earnings calculated by Supabase pg_cron at 00:00 NPT.
              </p>
            </div>

            <div className="flex items-center gap-3">
              <button
                onClick={handleDownloadNchl128}
                className="px-4 py-2 border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-semibold rounded-lg transition-colors flex items-center gap-1.5"
              >
                <FileSpreadsheet className="w-4 h-4 text-emerald-700" />
                Export ConnectIPS NCHL-128
              </button>
              {batch.status !== "completed" && (
                <button
                  onClick={handleExecutePayout}
                  className="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold rounded-lg transition-colors flex items-center gap-1.5 shadow-md shadow-emerald-900/20"
                >
                  <Send className="w-3.5 h-3.5" />
                  Approve & Execute Disbursal
                </button>
              )}
            </div>
          </div>

          {/* Ledger Deductions Breakdown Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Merchant Settlement Ledger */}
            <div className="bg-slate-50 p-5 rounded-xl border border-slate-200 space-y-3 text-xs">
              <div className="flex items-center justify-between font-bold text-slate-900 pb-2 border-b border-slate-200">
                <span className="flex items-center gap-1.5">
                  <Building className="w-4 h-4 text-emerald-600" />
                  Merchant Venues & Stores ({batch.merchantCount})
                </span>
                <span className="text-emerald-700 font-mono">
                  Net: NPR {(batch.netPayableMerchantsPaisa / 100).toLocaleString()}
                </span>
              </div>

              <div className="space-y-2 pt-1 text-slate-600">
                <div className="flex justify-between">
                  <span>Gross Order Volume (GMV):</span>
                  <span className="font-mono font-semibold text-slate-800">
                    NPR {(batch.grossMerchantEarningsPaisa / 100).toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between text-red-600">
                  <span>Less Platform Take-Rate (15% avg):</span>
                  <span className="font-mono">
                    -NPR {(batch.platformCommissionsDeductedPaisa / 100).toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between pt-2 border-t border-slate-200 font-bold text-slate-900">
                  <span>Payable via Direct Bank IPS:</span>
                  <span className="font-mono text-emerald-700 text-sm">
                    NPR {(batch.netPayableMerchantsPaisa / 100).toLocaleString()}
                  </span>
                </div>
              </div>
            </div>

            {/* Rider & Driver Settlement Ledger */}
            <div className="bg-slate-50 p-5 rounded-xl border border-slate-200 space-y-3 text-xs">
              <div className="flex items-center justify-between font-bold text-slate-900 pb-2 border-b border-slate-200">
                <span className="flex items-center gap-1.5">
                  <Bike className="w-4 h-4 text-emerald-600" />
                  Delivery Riders & Drivers ({batch.riderCount})
                </span>
                <span className="text-emerald-700 font-mono">
                  Net: NPR {(batch.netPayableRidersPaisa / 100).toLocaleString()}
                </span>
              </div>

              <div className="space-y-2 pt-1 text-slate-600">
                <div className="flex justify-between">
                  <span>Gross Delivery Fees Earned:</span>
                  <span className="font-mono font-semibold text-slate-800">
                    NPR {(batch.grossRiderFeesPaisa / 100).toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between text-amber-700">
                  <span>Less Rider Cash-in-Hand (COD Offset):</span>
                  <span className="font-mono">
                    -NPR {(batch.riderCodDeductedPaisa / 100).toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between pt-2 border-t border-slate-200 font-bold text-slate-900">
                  <span>Net Bank Disbursal to Riders:</span>
                  <span className="font-mono text-emerald-700 text-sm">
                    NPR {(batch.netPayableRidersPaisa / 100).toLocaleString()}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Zero-Hub Rider COD Safety Monitor */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-100 pb-4">
            <div>
              <h3 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2">
                <Wallet className="w-4 h-4 text-amber-500" />
                Zero-Hub Rider COD Safety Monitor
              </h3>
              <p className="text-xs text-slate-500 mt-0.5">
                Real-time Cash-in-Hand tracking. Soft warning at Rs 3,500; automatic dispatch lock at
                Rs 5,000.
              </p>
            </div>
            <div className="flex items-center gap-3 text-xs">
              <span className="flex items-center gap-1.5 text-amber-600 font-semibold">
                <span className="w-2 h-2 rounded-full bg-amber-500" />
                Soft Warning: &ge; Rs 3,500
              </span>
              <span className="flex items-center gap-1.5 text-red-600 font-semibold">
                <span className="w-2 h-2 rounded-full bg-red-500" />
                Hard Lock: &ge; Rs 5,000
              </span>
            </div>
          </div>

          {/* COD Balance Table */}
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="border-b border-slate-200 text-slate-400 font-semibold uppercase text-[10px]">
                  <th className="py-2.5 px-3">Rider Name</th>
                  <th className="py-2.5 px-3">Phone Number</th>
                  <th className="py-2.5 px-3">Cash-In-Hand</th>
                  <th className="py-2.5 px-3">Orders Today</th>
                  <th className="py-2.5 px-3">Safety Status</th>
                  <th className="py-2.5 px-3">Last Deposit</th>
                  <th className="py-2.5 px-3 text-right">Admin Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {riders.map((rider) => {
                  const cashNpr = rider.cashInHandPaisa / 100;
                  return (
                    <tr key={rider.riderId} className="hover:bg-slate-50/80 transition-colors">
                      <td className="py-3 px-3 font-semibold text-slate-900">{rider.riderName}</td>
                      <td className="py-3 px-3 text-slate-500 font-mono">{rider.phoneNumber}</td>
                      <td className="py-3 px-3 font-mono font-bold">
                        <span
                          className={
                            cashNpr >= 5000
                              ? "text-red-600"
                              : cashNpr >= 3500
                              ? "text-amber-600"
                              : "text-slate-800"
                          }
                        >
                          Rs {cashNpr.toLocaleString()}
                        </span>
                      </td>
                      <td className="py-3 px-3 text-slate-600">{rider.ordersDeliveredToday} pkgs</td>
                      <td className="py-3 px-3">
                        {rider.safetyStatus === "hard_locked" ? (
                          <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-red-100 text-red-700 flex items-center gap-1 w-fit">
                            <Lock className="w-3 h-3" />
                            COD Dispatched Locked
                          </span>
                        ) : rider.safetyStatus === "soft_warning" ? (
                          <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-amber-100 text-amber-800 flex items-center gap-1 w-fit">
                            <AlertTriangle className="w-3 h-3" />
                            Warning (Deposit Due)
                          </span>
                        ) : (
                          <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-100 text-emerald-800 flex items-center gap-1 w-fit">
                            Normal
                          </span>
                        )}
                      </td>
                      <td className="py-3 px-3 text-slate-400">{rider.lastDepositTime || "N/A"}</td>
                      <td className="py-3 px-3 text-right">
                        <button
                          onClick={() => handleToggleCodLock(rider.riderId)}
                          className={`px-3 py-1 text-xs rounded-lg font-semibold border transition-all ${
                            rider.safetyStatus === "hard_locked"
                              ? "border-emerald-300 text-emerald-700 hover:bg-emerald-50"
                              : "border-slate-200 text-slate-600 hover:bg-slate-100"
                          }`}
                        >
                          {rider.safetyStatus === "hard_locked" ? "Override & Unlock" : "Manual Lock"}
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
