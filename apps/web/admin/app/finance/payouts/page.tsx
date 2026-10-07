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
  ArrowUpRight,
  TrendingUp,
  ShieldCheck,
  CreditCard,
  DollarSign,
  Utensils,
  Car,
  Bed,
} from "lucide-react";
import {
  fetchPayoutBatch,
  approveAndExecutePayout,
  fetchRiderCodBalances,
  toggleRiderCodLock,
  recordRiderCashHandover,
  exportConnectIpsBatchCsv,
} from "@/lib/admin/api";
import { MidnightPayoutBatch, RiderCodBalance } from "@/lib/admin/types";
import { samplePayoutBatch, sampleRiderCodBalances } from "@/lib/admin/mockData";

export default function PayoutsConsolePage() {
  const [batch, setBatch] = useState<MidnightPayoutBatch>(samplePayoutBatch);
  const [riders, setRiders] = useState<RiderCodBalance[]>(sampleRiderCodBalances);
  const [executionMessage, setExecutionMessage] = useState<string | null>(null);
  const [isRefreshing, setIsRefreshing] = useState(false);

  // Vertical filter for Section 2
  const [verticalFilter, setVerticalFilter] = useState<"all" | "food" | "rides" | "stays">("all");

  // Cash Handover Modal
  const [handoverRider, setHandoverRider] = useState<RiderCodBalance | null>(null);
  const [handoverAmount, setHandoverAmount] = useState<number>(3000);
  const [receiptRef, setReceiptRef] = useState<string>("");

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

  const handleConfirmHandover = async () => {
    if (!handoverRider) return;
    const res = await recordRiderCashHandover(
      handoverRider.riderId,
      handoverAmount * 100,
      receiptRef || "BANK-DIRECT-DEPOSIT"
    );
    if (res.success && res.rider) {
      setRiders((prev) =>
        prev.map((r) => (r.riderId === handoverRider.riderId ? res.rider! : r))
      );
      setExecutionMessage(
        `Recorded Rs ${handoverAmount.toLocaleString()} cash handover for ${handoverRider.riderName}. Safety float updated.`
      );
      setHandoverRider(null);
      setReceiptRef("");
      setTimeout(() => setExecutionMessage(null), 5000);
    }
  };

  const handleDownloadNchl128 = async () => {
    const res = await exportConnectIpsBatchCsv(batch, riders);
    setExecutionMessage(
      `Exported ${res.filename} with ${res.count} bank payout instructions totaling NPR ${res.totalNpr.toLocaleString()} for ConnectIPS corporate batch upload.`
    );
    setTimeout(() => setExecutionMessage(null), 6000);
  };

  // Compute metrics for top strip
  const totalPendingSettlementNpr =
    (batch.netPayableMerchantsPaisa + batch.netPayableRidersPaisa) / 100;
  const connectIpsReadyBatchNpr = batch.totalDisbursementPaisa / 100;
  const totalRiderCodFloatNpr = riders.reduce(
    (acc, r) => acc + r.cashInHandPaisa / 100,
    0
  );

  return (
    <div className="flex-1 flex flex-col h-screen overflow-y-auto bg-slate-50/75">
      <AdminHeader
        title="Midnight Settlement & ConnectIPS Payouts"
        subtitle="Automated ConnectIPS Interbank Rails • Zero-Hub Rider COD Safety Monitor"
        onRefresh={loadData}
        isRefreshing={isRefreshing}
      />

      <div className="p-6 md:p-8 space-y-6 flex-1 max-w-[1500px] w-full mx-auto">
        {/* Execution Alert Banner */}
        {executionMessage && (
          <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-2xl text-emerald-800 text-xs flex items-center justify-between shadow-sm animate-fade-in">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
              <span className="font-semibold">{executionMessage}</span>
            </div>
            <span className="text-[11px] font-mono text-emerald-700 bg-white px-2.5 py-1 rounded-md border border-emerald-200">
              ConnectIPS NCHL-128
            </span>
          </div>
        )}

        {/* TOP METRIC STRIP (3 Compact Cards) */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          {/* Card 1: Total Pending Settlement */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-2">
            <div className="flex items-center justify-between text-xs font-semibold text-slate-500">
              <span>Total Pending Settlement</span>
              <Building className="w-4 h-4 text-teal-600" />
            </div>
            <div className="text-2xl font-bold text-slate-900 tracking-tight">
              NPR {totalPendingSettlementNpr.toLocaleString()}
            </div>
            <div className="text-xs text-slate-500 font-medium">
              {batch.merchantCount} verified venues • {batch.riderCount} riders
            </div>
          </div>

          {/* Card 2: ConnectIPS Ready Batch */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-2">
            <div className="flex items-center justify-between text-xs font-semibold text-slate-500">
              <span>ConnectIPS Ready Batch</span>
              <CreditCard className="w-4 h-4 text-teal-600" />
            </div>
            <div className="text-2xl font-bold text-teal-700 tracking-tight">
              NPR {connectIpsReadyBatchNpr.toLocaleString()}
            </div>
            <div className="flex items-center gap-2 text-xs">
              <span
                className={`px-2 py-0.5 rounded-full font-bold text-[10px] uppercase ${
                  batch.status === "completed"
                    ? "bg-emerald-50 text-emerald-700 border border-emerald-200"
                    : "bg-amber-50 text-amber-700 border border-amber-200"
                }`}
              >
                {batch.status === "completed" ? "Settled & Disbursed" : "Ready for Midnight Release"}
              </span>
              <span className="text-slate-400 font-mono text-[11px]">{batch.batchId}</span>
            </div>
          </div>

          {/* Card 3: At-Risk COD Float */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-2">
            <div className="flex items-center justify-between text-xs font-semibold text-slate-500">
              <span>At-Risk Rider COD Float</span>
              <Wallet className="w-4 h-4 text-amber-600" />
            </div>
            <div className="text-2xl font-bold text-slate-900 tracking-tight">
              NPR {totalRiderCodFloatNpr.toLocaleString()}
            </div>
            <div className="text-xs text-slate-500 font-medium">
              Physical cash held by {riders.length} active delivery riders
            </div>
          </div>
        </div>

        {/* SECTION 1: Zero-Hub Rider COD Safety Monitor */}
        <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6 space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 pb-4">
            <div>
              <h3 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2">
                <ShieldCheck className="w-4 h-4 text-teal-600" />
                Zero-Hub Rider COD Safety Monitor
              </h3>
              <p className="text-xs text-slate-500 mt-0.5">
                Real-time monitor tracking physical cash held by riders. Auto-locks at Rs 5,000 threshold.
              </p>
            </div>

            {/* Threshold Legend */}
            <div className="flex items-center gap-2 text-[11px]">
              <span className="px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-800 border border-emerald-200 font-medium">
                Normal (&lt; Rs 3,500)
              </span>
              <span className="px-2 py-0.5 rounded-full bg-amber-50 text-amber-800 border border-amber-200 font-medium">
                Soft Warning (Rs 3,500 - 5,000)
              </span>
              <span className="px-2 py-0.5 rounded-full bg-rose-50 text-rose-800 border border-rose-200 font-medium">
                Hard Lock (&gt; Rs 5,000)
              </span>
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="border-b border-slate-200/80 text-[11px] font-semibold text-slate-500 uppercase bg-slate-50/75">
                  <th className="py-3 px-4">Rider</th>
                  <th className="py-3 px-4">Phone</th>
                  <th className="py-3 px-4">Cash in Hand</th>
                  <th className="py-3 px-4">Threshold Progress</th>
                  <th className="py-3 px-4">Safety Status</th>
                  <th className="py-3 px-4">Last Deposit</th>
                  <th className="py-3 px-4 text-right">Quick Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-slate-700">
                {riders.map((r) => {
                  const cashNpr = r.cashInHandPaisa / 100;
                  const percentOfLock = Math.min(100, Math.round((cashNpr / 5000) * 100));

                  return (
                    <tr key={r.riderId} className="hover:bg-slate-50/60 transition-colors">
                      <td className="py-3 px-4 font-bold text-slate-900">{r.riderName}</td>
                      <td className="py-3 px-4 font-mono text-slate-500">{r.phoneNumber}</td>
                      <td className="py-3 px-4 font-mono font-bold text-slate-900">
                        Rs {cashNpr.toLocaleString()}
                      </td>
                      <td className="py-3 px-4 min-w-[140px]">
                        <div className="space-y-1">
                          <div className="w-full h-1.5 bg-slate-100 rounded-full overflow-hidden">
                            <div
                              className={`h-full rounded-full transition-all ${
                                r.safetyStatus === "hard_locked"
                                  ? "bg-rose-500"
                                  : r.safetyStatus === "soft_warning"
                                  ? "bg-amber-500"
                                  : "bg-emerald-500"
                              }`}
                              style={{ width: `${percentOfLock}%` }}
                            />
                          </div>
                          <span className="text-[10px] text-slate-400 font-mono">
                            {percentOfLock}% of Rs 5k cap
                          </span>
                        </div>
                      </td>
                      <td className="py-3 px-4">
                        <span
                          className={`px-2 py-0.5 rounded-full text-[10px] font-bold uppercase ${
                            r.safetyStatus === "hard_locked"
                              ? "bg-rose-50 text-rose-700 border border-rose-200"
                              : r.safetyStatus === "soft_warning"
                              ? "bg-amber-50 text-amber-700 border border-amber-200"
                              : "bg-emerald-50 text-emerald-700 border border-emerald-200"
                          }`}
                        >
                          {r.safetyStatus.replace("_", " ")}
                        </span>
                      </td>
                      <td className="py-3 px-4 text-slate-500">{r.lastDepositTime || "Pending"}</td>
                      <td className="py-3 px-4 text-right">
                        <div className="flex items-center justify-end gap-1.5">
                          <button
                            type="button"
                            onClick={() => {
                              setHandoverRider(r);
                              setHandoverAmount(cashNpr);
                            }}
                            className="px-2.5 py-1 text-xs font-semibold text-teal-800 bg-teal-50 hover:bg-teal-100 border border-teal-200/80 rounded-lg transition-colors"
                          >
                            Record Cash Handover
                          </button>

                          <button
                            type="button"
                            onClick={() => handleToggleCodLock(r.riderId)}
                            className={`p-1 rounded-lg border transition-colors ${
                              r.safetyStatus === "hard_locked"
                                ? "text-emerald-700 border-emerald-200 bg-emerald-50 hover:bg-emerald-100"
                                : "text-rose-700 border-rose-200 bg-rose-50 hover:bg-rose-100"
                            }`}
                            title={r.safetyStatus === "hard_locked" ? "Unlock COD" : "Emergency Lock"}
                          >
                            {r.safetyStatus === "hard_locked" ? (
                              <Unlock className="w-3.5 h-3.5" />
                            ) : (
                              <Lock className="w-3.5 h-3.5" />
                            )}
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>

        {/* SECTION 2: Merchant & Rider Settlement Queue */}
        <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6 space-y-5">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-4">
            <div>
              <h3 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2">
                <CreditCard className="w-4 h-4 text-teal-600" />
                Merchant & Rider Settlement Queue
              </h3>
              <p className="text-xs text-slate-500 mt-0.5">
                Calculated daily earnings aggregated by Supabase pg_cron at 00:00 NPT.
              </p>
            </div>

            {/* Actions: Export NCHL-128 & Disburse */}
            <div className="flex items-center gap-2.5">
              <button
                onClick={handleDownloadNchl128}
                className="px-4 py-2 border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-semibold rounded-xl transition-colors flex items-center gap-1.5 shadow-2xs"
              >
                <FileSpreadsheet className="w-4 h-4 text-teal-600" />
                Export NCHL-128 Batch CSV
              </button>

              {batch.status !== "completed" && (
                <button
                  onClick={handleExecutePayout}
                  className="px-5 py-2 bg-teal-600 hover:bg-teal-700 text-white text-xs font-bold rounded-xl transition-all flex items-center gap-1.5 shadow-sm"
                >
                  <Send className="w-3.5 h-3.5" />
                  Mark Batch Disbursed
                </button>
              )}
            </div>
          </div>

          {/* Vertical Filters */}
          <div className="flex items-center gap-1.5 overflow-x-auto pb-1">
            {[
              { id: "all" as const, label: "All Beneficiaries", icon: Building },
              { id: "food" as const, label: "Food & Cafes", icon: Utensils },
              { id: "rides" as const, label: "Riders & Drivers", icon: Bike },
              { id: "stays" as const, label: "Hotels & Stays", icon: Bed },
            ].map((f) => {
              const Icon = f.icon;
              const isSelected = verticalFilter === f.id;
              return (
                <button
                  key={f.id}
                  onClick={() => setVerticalFilter(f.id)}
                  className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold transition-colors border shrink-0 ${
                    isSelected
                      ? "bg-slate-900 text-white border-slate-900"
                      : "bg-slate-50 text-slate-600 border-slate-200 hover:bg-slate-100"
                  }`}
                >
                  <Icon className="w-3.5 h-3.5" />
                  <span>{f.label}</span>
                </button>
              );
            })}
          </div>

          {/* Settlement Breakdown Ledger Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {/* Merchant Ledger */}
            <div className="p-4 bg-slate-50 rounded-xl border border-slate-200/80 space-y-3 text-xs">
              <div className="flex items-center justify-between font-bold text-slate-900 pb-2 border-b border-slate-200/80">
                <span className="flex items-center gap-1.5">
                  <Building className="w-4 h-4 text-teal-600" />
                  Merchant Venues & Stores ({batch.merchantCount})
                </span>
                <span className="text-teal-700 font-mono">
                  Net: NPR {(batch.netPayableMerchantsPaisa / 100).toLocaleString()}
                </span>
              </div>

              <div className="space-y-1.5 font-mono text-[11px] text-slate-600">
                <div className="flex justify-between">
                  <span>Gross Order Volume (GMV):</span>
                  <span className="font-bold text-slate-900">
                    NPR {(batch.grossMerchantEarningsPaisa / 100).toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between text-rose-600">
                  <span>Less Platform Take-Rate (15% avg):</span>
                  <span>-NPR {(batch.platformCommissionsDeductedPaisa / 100).toLocaleString()}</span>
                </div>
                <div className="flex justify-between pt-1.5 border-t border-slate-200/80 font-bold text-slate-900">
                  <span>Net Payable via ConnectIPS:</span>
                  <span className="text-teal-700 text-sm">
                    NPR {(batch.netPayableMerchantsPaisa / 100).toLocaleString()}
                  </span>
                </div>
              </div>
            </div>

            {/* Rider Ledger */}
            <div className="p-4 bg-slate-50 rounded-xl border border-slate-200/80 space-y-3 text-xs">
              <div className="flex items-center justify-between font-bold text-slate-900 pb-2 border-b border-slate-200/80">
                <span className="flex items-center gap-1.5">
                  <Bike className="w-4 h-4 text-teal-600" />
                  Delivery Riders & Drivers ({batch.riderCount})
                </span>
                <span className="text-teal-700 font-mono">
                  Net: NPR {(batch.netPayableRidersPaisa / 100).toLocaleString()}
                </span>
              </div>

              <div className="space-y-1.5 font-mono text-[11px] text-slate-600">
                <div className="flex justify-between">
                  <span>Gross Delivery Fees Earned:</span>
                  <span className="font-bold text-slate-900">
                    NPR {(batch.grossRiderFeesPaisa / 100).toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between text-rose-600">
                  <span>Less Physical Cash-on-Delivery Retained:</span>
                  <span>-NPR {(batch.riderCodDeductedPaisa / 100).toLocaleString()}</span>
                </div>
                <div className="flex justify-between pt-1.5 border-t border-slate-200/80 font-bold text-slate-900">
                  <span>Net Payable via Bank/Digital Rail:</span>
                  <span className="text-teal-700 text-sm">
                    NPR {(batch.netPayableRidersPaisa / 100).toLocaleString()}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Modal: Record Cash Handover */}
      {handoverRider && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-slate-200 max-w-md w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <Wallet className="w-4 h-4 text-teal-600" />
                <h3 className="text-sm font-bold text-slate-900">
                  Record Cash Handover for {handoverRider.riderName}
                </h3>
              </div>
              <button
                onClick={() => setHandoverRider(null)}
                className="text-slate-400 hover:text-slate-700 text-sm"
              >
                ✕
              </button>
            </div>

            <p className="text-xs text-slate-500">
              Confirm receipt of physical COD funds deposited at hub or via direct bank transfer.
            </p>

            <div className="space-y-3 text-xs">
              <div>
                <label className="block font-semibold text-slate-800 mb-1">
                  Handover Amount (NPR)
                </label>
                <div className="relative">
                  <span className="absolute left-3 top-2 text-slate-400 font-bold">Rs</span>
                  <input
                    type="number"
                    step="50"
                    value={handoverAmount}
                    onChange={(e) => setHandoverAmount(Number(e.target.value))}
                    className="w-full pl-9 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-xl font-bold text-slate-900 focus:outline-none focus:ring-2 focus:ring-teal-500/20"
                  />
                </div>
                <p className="text-[10px] text-slate-400 mt-1">
                  Current cash float in hand: Rs {(handoverRider.cashInHandPaisa / 100).toLocaleString()}
                </p>
              </div>

              <div>
                <label className="block font-semibold text-slate-800 mb-1">
                  Bank / Deposit Receipt Reference
                </label>
                <input
                  type="text"
                  placeholder="e.g. NABIL-DEP-994821"
                  value={receiptRef}
                  onChange={(e) => setReceiptRef(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 focus:outline-none focus:ring-2 focus:ring-teal-500/20"
                />
              </div>
            </div>

            <div className="flex items-center justify-end gap-2.5 pt-2 border-t border-slate-100">
              <button
                onClick={() => setHandoverRider(null)}
                className="px-4 py-2 text-xs font-semibold text-slate-500 hover:text-slate-800"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmHandover}
                className="px-5 py-2 text-xs font-bold text-white bg-teal-600 hover:bg-teal-700 rounded-xl shadow-sm"
              >
                Confirm Cash Deposit
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
