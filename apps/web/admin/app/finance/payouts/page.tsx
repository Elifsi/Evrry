"use client";

import { useState, useEffect } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import {
  Wallet,
  CheckCircle2,
  AlertTriangle,
  Lock,
  Unlock,
  Building,
  Bike,
  Send,
  FileSpreadsheet,
} from "lucide-react";
import {
  fetchPayoutBatch,
  approveAndExecutePayout,
  fetchRiderCodBalances,
  toggleRiderCodLock,
} from "@/lib/admin/api";
import { MidnightPayoutBatch, RiderCodBalance } from "@/lib/admin/types";
import { samplePayoutBatch, sampleRiderCodBalances } from "@/lib/admin/mockData";

export default function PayoutsConsolePage() {
  const [batch, setBatch] = useState<MidnightPayoutBatch>(samplePayoutBatch);
  const [riders, setRiders] = useState<RiderCodBalance[]>(sampleRiderCodBalances);
  const [executionMessage, setExecutionMessage] = useState<string | null>(null);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const loadData = async () => {
    setIsRefreshing(true);
    const [b, r] = await Promise.all([fetchPayoutBatch(), fetchRiderCodBalances()]);
    setBatch(b);
    setRiders(r);
    setIsRefreshing(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleExecutePayout = async () => {
    const res = await approveAndExecutePayout(batch.batchId);
    if (res.success) {
      setBatch({ ...batch, status: "completed" });
      setExecutionMessage(res.message);
      setTimeout(() => setExecutionMessage(null), 6000);
    }
  };

  const handleToggleCodLock = async (riderId: string) => {
    const updated = await toggleRiderCodLock(riderId);
    if (updated) {
      setRiders((prev) => prev.map((r) => (r.riderId === riderId ? updated : r)));
    }
  };

  const handleDownloadNchl128 = () => {
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
        onRefresh={loadData}
        isRefreshing={isRefreshing}
      />

      <div className="p-8 space-y-8 flex-1 max-w-6xl">
        {/* Execution Alert */}
        {executionMessage && (
          <div className="bg-brand-50 border border-brand-200 p-4 rounded-xl text-brand-900 text-xs flex items-center justify-between shadow-sm">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-brand-600" />
              <span className="font-semibold">{executionMessage}</span>
            </div>
            <span className="text-[11px] font-mono text-brand-600 bg-white px-2 py-0.5 rounded border border-brand-200">
              ConnectIPS / NCHL Gateway
            </span>
          </div>
        )}

        {/* Midnight Batch Aggregation Card */}
        <div className="bg-white p-6 rounded-2xl border border-border shadow-card space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-border pb-4">
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-base font-bold text-ink tracking-tight">
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
              <p className="text-xs text-ink-muted mt-1">
                Aggregated daily earnings calculated by Supabase pg_cron at 00:00 NPT.
              </p>
            </div>

            <div className="flex items-center gap-3">
              <button
                onClick={handleDownloadNchl128}
                className="px-4 py-2 border border-border hover:bg-slate-50 text-ink-muted hover:text-ink text-xs font-semibold rounded-lg transition-colors flex items-center gap-1.5"
              >
                <FileSpreadsheet className="w-4 h-4 text-brand-600" />
                Export ConnectIPS NCHL-128
              </button>
              {batch.status !== "completed" && (
                <button
                  onClick={handleExecutePayout}
                  className="px-5 py-2 bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold rounded-lg transition-all flex items-center gap-1.5 shadow-md shadow-brand-900/30"
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
            <div className="bg-surface-subtle p-5 rounded-xl border border-border space-y-3 text-xs">
              <div className="flex items-center justify-between font-bold text-ink pb-2 border-b border-border">
                <span className="flex items-center gap-1.5">
                  <Building className="w-4 h-4 text-brand-600" />
                  Merchant Venues & Stores ({batch.merchantCount})
                </span>
                <span className="text-brand-600 font-mono font-bold">
                  Net: NPR {(batch.netPayableMerchantsPaisa / 100).toLocaleString()}
                </span>
              </div>

              <div className="space-y-2 pt-1 text-ink-muted">
                <div className="flex justify-between">
                  <span>Gross Order Volume (GMV):</span>
                  <span className="font-mono font-semibold text-ink">
                    NPR {(batch.grossMerchantEarningsPaisa / 100).toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between text-rose-600">
                  <span>Less Platform Take-Rate (15% avg):</span>
                  <span className="font-mono">
                    -NPR {(batch.platformCommissionsDeductedPaisa / 100).toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between pt-2 border-t border-border font-bold text-ink">
                  <span>Payable via Direct Bank IPS:</span>
                  <span className="font-mono text-brand-600 text-sm font-bold">
                    NPR {(batch.netPayableMerchantsPaisa / 100).toLocaleString()}
                  </span>
                </div>
              </div>
            </div>

            {/* Rider & Driver Settlement Ledger */}
            <div className="bg-surface-subtle p-5 rounded-xl border border-border space-y-3 text-xs">
              <div className="flex items-center justify-between font-bold text-ink pb-2 border-b border-border">
                <span className="flex items-center gap-1.5">
                  <Bike className="w-4 h-4 text-brand-600" />
                  Delivery Riders & Drivers ({batch.riderCount})
                </span>
                <span className="text-brand-600 font-mono font-bold">
                  Net: NPR {(batch.netPayableRidersPaisa / 100).toLocaleString()}
                </span>
              </div>

              <div className="space-y-2 pt-1 text-ink-muted">
                <div className="flex justify-between">
                  <span>Gross Delivery Fees Earned:</span>
                  <span className="font-mono font-semibold text-ink">
                    NPR {(batch.grossRiderFeesPaisa / 100).toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between text-rose-600">
                  <span>Less Physical Cash-on-Delivery Retained:</span>
                  <span className="font-mono">
                    -NPR {(batch.riderCodDeductedPaisa / 100).toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between pt-2 border-t border-border font-bold text-ink">
                  <span>Payable via Digital Wallet/Bank:</span>
                  <span className="font-mono text-brand-600 text-sm font-bold">
                    NPR {(batch.netPayableRidersPaisa / 100).toLocaleString()}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Zero-Hub Rider COD Safety Monitoring Table */}
        <div className="bg-white p-6 rounded-2xl border border-border shadow-card space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-border pb-3">
            <div>
              <h3 className="text-sm font-bold text-ink tracking-tight flex items-center gap-2">
                <Wallet className="w-4 h-4 text-brand-600" />
                Zero-Hub Rider COD Float & Safety Locks
              </h3>
              <p className="text-xs text-ink-muted">
                Real-time monitor tracking physical cash held by riders. Auto-locks at Rs 5,000 threshold.
              </p>
            </div>
            <div className="flex items-center gap-2 text-xs">
              <span className="px-2 py-0.5 rounded bg-emerald-100 text-emerald-800 font-medium">
                Normal (&lt;Rs 3,500)
              </span>
              <span className="px-2 py-0.5 rounded bg-amber-100 text-amber-800 font-medium">
                Soft Warning (&gt;Rs 3,500)
              </span>
              <span className="px-2 py-0.5 rounded bg-rose-100 text-rose-800 font-medium">
                Hard Lock (&gt;Rs 5,000)
              </span>
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="border-b border-border text-ink-muted">
                  <th className="py-2.5 font-semibold">Rider Name</th>
                  <th className="py-2.5 font-semibold">Phone</th>
                  <th className="py-2.5 font-semibold">Cash in Hand</th>
                  <th className="py-2.5 font-semibold">Today&apos;s Drops</th>
                  <th className="py-2.5 font-semibold">Safety Status</th>
                  <th className="py-2.5 font-semibold">Last Bank/eSewa Deposit</th>
                  <th className="py-2.5 font-semibold text-right">Emergency Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {riders.map((rider) => (
                  <tr key={rider.riderId} className="hover:bg-slate-50/60 transition-colors">
                    <td className="py-3 font-semibold text-ink">{rider.riderName}</td>
                    <td className="py-3 font-mono text-ink-muted">{rider.phoneNumber}</td>
                    <td className="py-3 font-mono font-bold text-ink">
                      Rs {(rider.cashInHandPaisa / 100).toLocaleString()}
                    </td>
                    <td className="py-3 text-ink-muted">{rider.ordersDeliveredToday} orders</td>
                    <td className="py-3">
                      <span
                        className={`px-2 py-0.5 rounded text-[10px] font-bold uppercase ${
                          rider.safetyStatus === "hard_locked"
                            ? "bg-rose-100 text-rose-800"
                            : rider.safetyStatus === "soft_warning"
                            ? "bg-amber-100 text-amber-800"
                            : "bg-emerald-100 text-emerald-800"
                        }`}
                      >
                        {rider.safetyStatus.replace("_", " ")}
                      </span>
                    </td>
                    <td className="py-3 text-ink-muted">{rider.lastDepositTime || "Pending"}</td>
                    <td className="py-3 text-right">
                      <button
                        onClick={() => handleToggleCodLock(rider.riderId)}
                        className={`px-3 py-1 rounded-lg text-xs font-semibold transition-colors flex items-center gap-1.5 ml-auto ${
                          rider.safetyStatus === "hard_locked"
                            ? "bg-brand-50 hover:bg-brand-100 text-brand-700 border border-brand-200"
                            : "bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200"
                        }`}
                      >
                        {rider.safetyStatus === "hard_locked" ? (
                          <>
                            <Unlock className="w-3.5 h-3.5" />
                            Unlock COD
                          </>
                        ) : (
                          <>
                            <Lock className="w-3.5 h-3.5" />
                            Emergency Lock
                          </>
                        )}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
