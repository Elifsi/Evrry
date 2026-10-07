"use client";

import { useState } from "react";
import Link from "next/link";
import { AdminHeader } from "@/components/AdminHeader";
import {
  TrendingUp,
  Percent,
  FileCheck2,
  Wallet,
  ArrowUpRight,
  ShieldCheck,
  CheckCircle2,
  Clock,
  AlertTriangle,
  Zap,
} from "lucide-react";
import {
  samplePayoutBatch,
  sampleKycQueue,
  sampleAuditLogs,
  initialPricingSettings,
} from "@/lib/admin/mockData";

export default function AdminDashboardPage() {
  const [pricing] = useState(initialPricingSettings);

  const pendingKycCount = sampleKycQueue.filter((k) => k.status === "pending").length;

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="Superadmin Operations Console"
        subtitle="Zero-SQL Administrative Governance Platform • Live Metrics"
      />

      <div className="p-8 space-y-8 flex-1">
        {/* Banner Alert for Midnight Payout & KYC Queue */}
        <div className="bg-gradient-to-r from-slate-900 to-slate-800 rounded-2xl p-6 text-white shadow-lg border border-slate-700/60 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-start gap-4">
            <div className="p-3 bg-emerald-500/20 border border-emerald-500/30 rounded-xl text-emerald-400 shrink-0">
              <Zap className="w-6 h-6" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-base font-bold tracking-tight">
                  Midnight Settlement Batch Ready for Review
                </h2>
                <span className="px-2 py-0.5 text-[10px] font-semibold bg-emerald-500/20 text-emerald-300 rounded border border-emerald-500/30 uppercase">
                  Automated pg_cron
                </span>
              </div>
              <p className="text-xs text-slate-300 mt-1">
                Batch <span className="font-mono text-emerald-400">BATCH-2026-10-07</span> has
                aggregated NPR 1,703,850 across 48 verified merchants and 112 riders.
              </p>
            </div>
          </div>
          <div className="flex items-center gap-3 shrink-0">
            <Link
              href="/finance/payouts"
              className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold rounded-lg transition-colors flex items-center gap-1.5 shadow-md shadow-emerald-900/30"
            >
              Review Payouts
              <ArrowUpRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        </div>

        {/* Core KPI Metrics */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between text-xs text-slate-500 font-medium">
              <span>Today&apos;s Gross Volume (GMV)</span>
              <TrendingUp className="w-4 h-4 text-emerald-600" />
            </div>
            <div className="mt-3 text-2xl font-bold text-slate-900 tracking-tight">
              NPR 2,266,000
            </div>
            <div className="mt-1 flex items-center gap-1 text-[11px] text-emerald-600 font-medium">
              <span>+14.2%</span>
              <span className="text-slate-400">vs yesterday (Festival rush)</span>
            </div>
          </div>

          <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between text-xs text-slate-500 font-medium">
              <span>Standard Platform Fee</span>
              <Percent className="w-4 h-4 text-brand-600" />
            </div>
            <div className="mt-3 text-2xl font-bold text-slate-900 tracking-tight">
              Rs {(pricing.platformFeePaisa / 100).toFixed(2)}
            </div>
            <div className="mt-1 flex items-center gap-1.5 text-[11px]">
              <span className="inline-block w-2 h-2 rounded-full bg-emerald-500" />
              <span className="text-slate-600 font-medium">Active on checkout</span>
              <Link href="/pricing" className="text-emerald-700 hover:underline ml-auto">
                Edit
              </Link>
            </div>
          </div>

          <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between text-xs text-slate-500 font-medium">
              <span>Pending Partner KYC</span>
              <FileCheck2 className="w-4 h-4 text-amber-500" />
            </div>
            <div className="mt-3 text-2xl font-bold text-slate-900 tracking-tight">
              {pendingKycCount} Queue
            </div>
            <div className="mt-1 flex items-center justify-between text-[11px]">
              <span className="text-amber-700 font-medium">Action required</span>
              <Link
                href="/compliance/kyc"
                className="text-amber-700 hover:underline font-semibold"
              >
                Inspect Queue &rarr;
              </Link>
            </div>
          </div>

          <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between text-xs text-slate-500 font-medium">
              <span>Midnight Payout Batch</span>
              <Wallet className="w-4 h-4 text-emerald-600" />
            </div>
            <div className="mt-3 text-2xl font-bold text-slate-900 tracking-tight">
              NPR {(samplePayoutBatch.totalDisbursementPaisa / 100).toLocaleString()}
            </div>
            <div className="mt-1 flex items-center justify-between text-[11px]">
              <span className="text-slate-500">
                {samplePayoutBatch.merchantCount} Venues • {samplePayoutBatch.riderCount} Riders
              </span>
            </div>
          </div>
        </div>

        {/* Two-Column Grid: Quick Actions & Audit Trail */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Quick Control Hub */}
          <div className="lg:col-span-1 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
            <h3 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-emerald-600" />
              Zero-SQL Control Center
            </h3>
            <p className="text-xs text-slate-500">
              Modify business rules and financial parameters instantly without deployment latency.
            </p>

            <div className="space-y-2.5 pt-2">
              <Link
                href="/pricing"
                className="flex items-center justify-between p-3.5 rounded-xl border border-slate-200 hover:border-emerald-300 hover:bg-emerald-50/40 transition-all group"
              >
                <div>
                  <div className="text-xs font-semibold text-slate-900 group-hover:text-emerald-800">
                    Pricing & Delivery Bands
                  </div>
                  <div className="text-[11px] text-slate-500">
                    Rs 10 fee • Rs 1,000 free delivery • Rs 15/km
                  </div>
                </div>
                <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-emerald-700" />
              </Link>

              <Link
                href="/compliance/kyc"
                className="flex items-center justify-between p-3.5 rounded-xl border border-slate-200 hover:border-amber-300 hover:bg-amber-50/40 transition-all group"
              >
                <div>
                  <div className="text-xs font-semibold text-slate-900 group-hover:text-amber-800">
                    Partner Document Review
                  </div>
                  <div className="text-[11px] text-slate-500">
                    Smart driving licenses & Blue Books
                  </div>
                </div>
                <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-amber-700" />
              </Link>

              <Link
                href="/finance/payouts"
                className="flex items-center justify-between p-3.5 rounded-xl border border-slate-200 hover:border-emerald-300 hover:bg-emerald-50/40 transition-all group"
              >
                <div>
                  <div className="text-xs font-semibold text-slate-900 group-hover:text-emerald-800">
                    Midnight Settlement Payouts
                  </div>
                  <div className="text-[11px] text-slate-500">
                    ConnectIPS batch & Rider COD safety locks
                  </div>
                </div>
                <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-emerald-700" />
              </Link>

              <Link
                href="/marketing/vouchers"
                className="flex items-center justify-between p-3.5 rounded-xl border border-slate-200 hover:border-indigo-300 hover:bg-indigo-50/40 transition-all group"
              >
                <div>
                  <div className="text-xs font-semibold text-slate-900 group-hover:text-indigo-800">
                    Vouchers & Campaigns
                  </div>
                  <div className="text-[11px] text-slate-500">
                    EVRRYNEW100 promo caps & budget tracking
                  </div>
                </div>
                <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-indigo-700" />
              </Link>
            </div>
          </div>

          {/* Immutable Audit Trail */}
          <div className="lg:col-span-2 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col justify-between">
            <div>
              <div className="flex items-center justify-between mb-4">
                <div>
                  <h3 className="text-sm font-bold text-slate-900 tracking-tight">
                    Immutable Audit Trail (`public.admin_audit_logs`)
                  </h3>
                  <p className="text-xs text-slate-500">
                    Cryptographically tracked admin modifications across all platform subsystems.
                  </p>
                </div>
                <span className="text-[11px] font-mono bg-slate-100 text-slate-600 px-2.5 py-1 rounded-md">
                  Live Stream
                </span>
              </div>

              <div className="divide-y divide-slate-100">
                {sampleAuditLogs.map((log) => (
                  <div key={log.id} className="py-3.5 flex items-start gap-3">
                    <div className="mt-0.5">
                      {log.actionType === "PRICING_UPDATED" ? (
                        <Percent className="w-4 h-4 text-emerald-600" />
                      ) : log.actionType === "KYC_APPROVED" ? (
                        <CheckCircle2 className="w-4 h-4 text-blue-600" />
                      ) : (
                        <Clock className="w-4 h-4 text-purple-600" />
                      )}
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center justify-between text-xs">
                        <span className="font-semibold text-slate-900">{log.actionType}</span>
                        <span className="text-[11px] text-slate-400 font-mono">
                          {log.timestamp}
                        </span>
                      </div>
                      <p className="text-xs text-slate-600 mt-0.5">{log.details}</p>
                      <div className="flex items-center gap-3 mt-1 text-[11px] text-slate-400">
                        <span>By: {log.adminName}</span>
                        <span>•</span>
                        <span className="font-mono">{log.ipAddress}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="pt-4 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
              <span>All mutations logged permanently with IP & admin ID</span>
              <span className="font-mono text-emerald-600 font-semibold">PostgreSQL RLS Enforced</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
