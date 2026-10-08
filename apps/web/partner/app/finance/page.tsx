"use client";

import { useState, useEffect } from "react";
import {
  Wallet,
  TrendingUp,
  Download,
  Building2,
  CheckCircle2,
  Receipt,
  FileSpreadsheet,
  ArrowUpRight,
} from "lucide-react";
import { PartnerHeader } from "@/components/PartnerHeader";
import { fetchPartnerFinancials } from "@/lib/partner/api";
import { PartnerFinancialLedger } from "@/lib/partner/types";

export default function PartnerFinancePage() {
  const [financials, setFinancials] = useState<PartnerFinancialLedger | null>(null);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [invoiceMessage, setInvoiceMessage] = useState<string | null>(null);

  const loadData = async () => {
    setIsRefreshing(true);
    const data = await fetchPartnerFinancials("str-01");
    setFinancials(data);
    setIsRefreshing(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleDownloadInvoice = (batchId: string) => {
    setInvoiceMessage(`Generated IRD Tax Invoice & Statement for Batch #${batchId}`);
    setTimeout(() => setInvoiceMessage(null), 5000);
  };

  return (
    <div className="flex-1 flex flex-col h-screen overflow-y-auto bg-slate-50">
      <PartnerHeader
        title="Merchant Settlement Ledger & Tax Statements"
        subtitle="Live Payout Balances • Platform Commission Deductions • Commercial Bank Account Linkage"
        onRefresh={loadData}
        isRefreshing={isRefreshing}
      />

      <div className="p-6 md:p-8 space-y-6 max-w-[1500px] w-full mx-auto">
        {/* Invoice Download Banner */}
        {invoiceMessage && (
          <div className="bg-emerald-600 text-white px-5 py-3 rounded-xl text-xs font-bold flex items-center justify-between shadow-xs animate-in fade-in">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{invoiceMessage}</span>
            </div>
            <span className="font-mono text-[11px] opacity-90">PDF Downloaded</span>
          </div>
        )}

        {/* 3 Metric Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-xs">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">
              Pending Midnight Settlement
            </span>
            <p className="text-3xl font-black text-slate-900 mt-2 font-mono">
              NPR {financials?.pendingSettlementNpr.toLocaleString() || "76,240"}
            </p>
            <div className="flex items-center gap-2 mt-2 text-xs text-brand-700 font-medium bg-brand-50 p-2 rounded-lg">
              <Building2 className="w-4 h-4 shrink-0" />
              <span>Direct Bank Disbursement at 00:00 NPT</span>
            </div>
          </div>

          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-xs">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">
              Today&apos;s Gross Sales
            </span>
            <p className="text-3xl font-black text-slate-900 mt-2 font-mono">
              NPR {financials?.todaySalesNpr.toLocaleString() || "18,450"}
            </p>
            <p className="text-xs text-slate-500 mt-2">
              Platform Take-Rate:{" "}
              <strong className="text-slate-800">
                {financials?.platformTakeRatePercent || 15}%
              </strong>
            </p>
          </div>

          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-xs">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">
              Lifetime Disbursed Earnings
            </span>
            <p className="text-3xl font-black text-slate-900 mt-2 font-mono">
              NPR {financials?.lifetimeEarningsNpr.toLocaleString() || "1,428,900"}
            </p>
            <p className="text-xs text-emerald-700 font-medium mt-2 flex items-center gap-1">
              <CheckCircle2 className="w-3.5 h-3.5" />
              <span>100% reconciled on NCHL rails</span>
            </p>
          </div>
        </div>

        {/* Bank Account Verification Card */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-xs space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 pb-4">
            <div>
              <h2 className="text-sm font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2">
                <Building2 className="w-4 h-4 text-brand-600" />
                <span>Linked Commercial Bank Account</span>
              </h2>
              <p className="text-xs text-slate-500 mt-0.5">
                Daily midnight net revenues are credited directly to this verified corporate account.
              </p>
            </div>
            <span className="px-3 py-1 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200 inline-flex items-center gap-1.5 self-start sm:self-auto">
              <CheckCircle2 className="w-3.5 h-3.5" />
              <span>KYC Verified</span>
            </span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
            <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-200">
              <span className="text-slate-400 block font-medium">Bank Name & Branch:</span>
              <span className="font-bold text-slate-900 mt-0.5 block">
                {financials?.bankAccount.bankName} ({financials?.bankAccount.branch})
              </span>
            </div>
            <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-200">
              <span className="text-slate-400 block font-medium">Account Number:</span>
              <span className="font-mono font-bold text-slate-900 mt-0.5 block">
                {financials?.bankAccount.accountNumber}
              </span>
            </div>
            <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-200">
              <span className="text-slate-400 block font-medium">Account Holder Name:</span>
              <span className="font-bold text-slate-900 mt-0.5 block">
                {financials?.bankAccount.accountHolder}
              </span>
            </div>
          </div>
        </div>

        {/* Historical Settlement Batches Table */}
        <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
          <div className="p-5 border-b border-slate-100 flex items-center justify-between">
            <h3 className="text-sm font-bold text-slate-900">
              Disbursement & Settlement History
            </h3>
            <span className="text-xs text-slate-500 font-mono">
              Filtered for October 2026
            </span>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="bg-slate-50/75 border-b border-slate-200/80 text-[11px] font-bold uppercase tracking-wider text-slate-500">
                  <th className="py-3 px-4">Batch ID</th>
                  <th className="py-3 px-4">Date</th>
                  <th className="py-3 px-4">Gross Revenue</th>
                  <th className="py-3 px-4">Platform Fee (15%)</th>
                  <th className="py-3 px-4">Net Payout Transferred</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Tax Invoice</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 font-mono">
                {[
                  {
                    batch: "BATCH-2026-10-07",
                    date: "2026-10-07",
                    gross: "NPR 31,200",
                    fee: "-NPR 4,680",
                    net: "NPR 26,520",
                    status: "DISBURSED",
                  },
                  {
                    batch: "BATCH-2026-10-06",
                    date: "2026-10-06",
                    gross: "NPR 28,400",
                    fee: "-NPR 4,260",
                    net: "NPR 24,140",
                    status: "DISBURSED",
                  },
                  {
                    batch: "BATCH-2026-10-05",
                    date: "2026-10-05",
                    gross: "NPR 22,900",
                    fee: "-NPR 3,435",
                    net: "NPR 19,465",
                    status: "DISBURSED",
                  },
                ].map((row) => (
                  <tr key={row.batch} className="hover:bg-slate-50/50 transition-colors">
                    <td className="py-3 px-4 font-bold text-slate-900">{row.batch}</td>
                    <td className="py-3 px-4 text-slate-600">{row.date}</td>
                    <td className="py-3 px-4 text-slate-800">{row.gross}</td>
                    <td className="py-3 px-4 text-rose-600">{row.fee}</td>
                    <td className="py-3 px-4 font-black text-slate-900">{row.net}</td>
                    <td className="py-3 px-4">
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold uppercase bg-emerald-100 text-emerald-800">
                        {row.status}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right font-sans">
                      <button
                        type="button"
                        onClick={() => handleDownloadInvoice(row.batch)}
                        className="inline-flex items-center gap-1 text-xs font-semibold text-brand-600 hover:text-brand-800"
                      >
                        <Download className="w-3.5 h-3.5" />
                        <span>IRD VAT Bill</span>
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
