"use client";

import { useState, useEffect } from "react";
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
  Zap,
  Activity,
  Layers,
  Database,
} from "lucide-react";
import {
  fetchPricingSettings,
  fetchDashboardMetrics,
  fetchAuditLogs,
  DashboardMetrics,
} from "@/lib/admin/api";
import {
  PlatformPricingSettings,
  AuditLogEntry,
} from "@/lib/admin/types";
import { initialPricingSettings, sampleAuditLogs } from "@/lib/admin/mockData";

export default function AdminDashboardPage() {
  const [pricing, setPricing] = useState<PlatformPricingSettings>(initialPricingSettings);
  const [metrics, setMetrics] = useState<DashboardMetrics>({
    todayGmvNpr: 2266000,
    todayOrdersCount: 148,
    gmvGrowthPercent: 14.2,
    platformFeeNpr: 10,
    pendingKycCount: 4,
    midnightPayoutTotalNpr: 1703850,
    merchantPayoutCount: 48,
    riderPayoutCount: 112,
    liveDbConnected: false,
  });
  const [auditLogs, setAuditLogs] = useState<AuditLogEntry[]>(sampleAuditLogs);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const loadDashboardData = async () => {
    setIsRefreshing(true);
    const [p, m, logs] = await Promise.all([
      fetchPricingSettings(),
      fetchDashboardMetrics(),
      fetchAuditLogs(),
    ]);
    setPricing(p);
    setMetrics(m);
    setAuditLogs(logs);
    setIsRefreshing(false);
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="Superadmin Operations Console"
        subtitle="Zero-SQL Administrative Governance Platform • Live Metrics & Real-Time Controls"
        onRefresh={loadDashboardData}
        isRefreshing={isRefreshing}
      />

      <div className="p-8 space-y-8 flex-1">
        {/* Banner Alert for Midnight Payout & pg_cron Status */}
        <div className="bg-gradient-to-r from-slate-900 via-slate-900 to-brand-950 rounded-2xl p-6 text-white shadow-xl border border-slate-800 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-start gap-4">
            <div className="p-3 bg-brand-500/20 border border-brand-500/30 rounded-xl text-brand-300 shrink-0">
              <Zap className="w-6 h-6 text-brand-400" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-base font-bold tracking-tight">
                  Midnight Settlement Batch Ready for Review
                </h2>
                <span className="px-2 py-0.5 text-[10px] font-semibold bg-brand-500/20 text-brand-300 rounded border border-brand-500/30 uppercase">
                  Automated pg_cron
                </span>
                {metrics.liveDbConnected && (
                  <span className="px-2 py-0.5 text-[10px] font-semibold bg-emerald-500/20 text-emerald-300 rounded border border-emerald-500/30 uppercase flex items-center gap-1">
                    <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
                    Live Supabase
                  </span>
                )}
              </div>
              <p className="text-xs text-slate-300 mt-1">
                Aggregated daily volume of NPR {metrics.midnightPayoutTotalNpr.toLocaleString()} across {metrics.merchantPayoutCount} verified merchants and {metrics.riderPayoutCount} riders.
              </p>
            </div>
          </div>
          <div className="flex items-center gap-3 shrink-0">
            <Link
              href="/finance/payouts"
              className="px-4 py-2 bg-brand-600 hover:bg-brand-500 text-white text-xs font-bold rounded-lg transition-all flex items-center gap-1.5 shadow-md shadow-brand-900/40"
            >
              Review Payouts
              <ArrowUpRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        </div>

        {/* Core KPI Metrics */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <div className="bg-white p-5 rounded-2xl border border-border shadow-card">
            <div className="flex items-center justify-between text-xs text-ink-muted font-medium">
              <span>Today&apos;s Gross Volume (GMV)</span>
              <TrendingUp className="w-4 h-4 text-brand-600" />
            </div>
            <div className="mt-3 text-2xl font-bold text-ink tracking-tight">
              NPR {metrics.todayGmvNpr.toLocaleString()}
            </div>
            <div className="mt-1 flex items-center gap-1 text-[11px] text-emerald-600 font-semibold">
              <span>+{metrics.gmvGrowthPercent}%</span>
              <span className="text-ink-muted font-normal">({metrics.todayOrdersCount} orders today)</span>
            </div>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-border shadow-card">
            <div className="flex items-center justify-between text-xs text-ink-muted font-medium">
              <span>Standard Platform Fee</span>
              <Percent className="w-4 h-4 text-brand-600" />
            </div>
            <div className="mt-3 text-2xl font-bold text-ink tracking-tight">
              Rs {(pricing.platformFeePaisa / 100).toFixed(2)}
            </div>
            <div className="mt-1 flex items-center gap-1.5 text-[11px]">
              <span className="inline-block w-2 h-2 rounded-full bg-emerald-500" />
              <span className="text-ink-muted font-medium">Active on checkout</span>
              <Link href="/pricing" className="text-brand-600 hover:underline ml-auto font-semibold">
                Edit
              </Link>
            </div>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-border shadow-card">
            <div className="flex items-center justify-between text-xs text-ink-muted font-medium">
              <span>Pending Partner KYC</span>
              <FileCheck2 className="w-4 h-4 text-amber-500" />
            </div>
            <div className="mt-3 text-2xl font-bold text-ink tracking-tight flex items-center gap-2">
              <span>{metrics.pendingKycCount}</span>
              <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-amber-100 text-amber-800">
                Awaiting Review
              </span>
            </div>
            <div className="mt-1 flex items-center justify-between text-[11px]">
              <span className="text-amber-700 font-medium">Action required</span>
              <Link
                href="/compliance/kyc"
                className="text-brand-600 hover:underline font-semibold"
              >
                Inspect Queue &rarr;
              </Link>
            </div>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-border shadow-card">
            <div className="flex items-center justify-between text-xs text-ink-muted font-medium">
              <span>Midnight Payout Batch</span>
              <Wallet className="w-4 h-4 text-brand-600" />
            </div>
            <div className="mt-3 text-2xl font-bold text-ink tracking-tight">
              NPR {metrics.midnightPayoutTotalNpr.toLocaleString()}
            </div>
            <div className="mt-1 flex items-center justify-between text-[11px]">
              <span className="text-ink-muted">
                {metrics.merchantPayoutCount} Venues • {metrics.riderPayoutCount} Riders
              </span>
            </div>
          </div>
        </div>

        {/* Live Operational Grids */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Quick Control Hub */}
          <div className="lg:col-span-1 bg-white p-6 rounded-2xl border border-border shadow-card space-y-4">
            <h3 className="text-sm font-bold text-ink tracking-tight flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-brand-600" />
              Zero-SQL Operational Controls
            </h3>
            <p className="text-xs text-ink-muted">
              Configure live business rules, fees, and safety parameters instantly without deploying new code.
            </p>

            <div className="space-y-2.5 pt-2">
              <Link
                href="/pricing"
                className="flex items-center justify-between p-3.5 rounded-xl border border-border hover:border-brand-300 hover:bg-brand-50/50 transition-all group"
              >
                <div>
                  <div className="text-xs font-semibold text-ink group-hover:text-brand-700">
                    Pricing & Delivery Matrices
                  </div>
                  <div className="text-[11px] text-ink-muted">
                    Rs {(pricing.platformFeePaisa / 100).toFixed(0)} fee • Rs {(pricing.freeDeliveryThresholdPaisa / 100).toLocaleString()} free delivery
                  </div>
                </div>
                <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-brand-600" />
              </Link>

              <Link
                href="/compliance/kyc"
                className="flex items-center justify-between p-3.5 rounded-xl border border-border hover:border-amber-300 hover:bg-amber-50/50 transition-all group"
              >
                <div>
                  <div className="text-xs font-semibold text-ink group-hover:text-amber-800">
                    Partner Document Review
                  </div>
                  <div className="text-[11px] text-ink-muted">
                    Smart driving licenses, Blue Books, IRD PAN
                  </div>
                </div>
                <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-amber-700" />
              </Link>

              <Link
                href="/finance/payouts"
                className="flex items-center justify-between p-3.5 rounded-xl border border-border hover:border-brand-300 hover:bg-brand-50/50 transition-all group"
              >
                <div>
                  <div className="text-xs font-semibold text-ink group-hover:text-brand-700">
                    Midnight Settlement Payouts
                  </div>
                  <div className="text-[11px] text-ink-muted">
                    ConnectIPS batch release & Rider COD safety locks
                  </div>
                </div>
                <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-brand-600" />
              </Link>

              <Link
                href="/marketing/vouchers"
                className="flex items-center justify-between p-3.5 rounded-xl border border-border hover:border-brand-300 hover:bg-brand-50/50 transition-all group"
              >
                <div>
                  <div className="text-xs font-semibold text-ink group-hover:text-brand-700">
                    Voucher & Promotion Engine
                  </div>
                  <div className="text-[11px] text-ink-muted">
                    Budget caps, flat vs % discounts, usage limits
                  </div>
                </div>
                <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-brand-600" />
              </Link>

              <Link
                href="/settings/governance"
                className="flex items-center justify-between p-3.5 rounded-xl border border-border hover:border-rose-300 hover:bg-rose-50/50 transition-all group"
              >
                <div>
                  <div className="text-xs font-semibold text-ink group-hover:text-rose-800">
                    Compliance & Ward Kill-Switches
                  </div>
                  <div className="text-[11px] text-ink-muted">
                    13% VAT toggle & Kathmandu emergency switches
                  </div>
                </div>
                <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-rose-700" />
              </Link>
            </div>
          </div>

          {/* Real-Time Audit Log Feed */}
          <div className="lg:col-span-2 bg-white p-6 rounded-2xl border border-border shadow-card space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-sm font-bold text-ink tracking-tight flex items-center gap-2">
                  <Clock className="w-4 h-4 text-brand-600" />
                  Live Operational Audit Trail
                </h3>
                <p className="text-xs text-ink-muted">
                  Immutable administrative actions logged in PostgreSQL with IP & actor identity.
                </p>
              </div>
              <span className="text-[10px] font-mono text-ink-muted bg-surface-subtle px-2 py-1 rounded border border-border">
                Asia/Kathmandu (UTC+5:45)
              </span>
            </div>

            <div className="divide-y divide-border pt-1">
              {auditLogs.slice(0, 5).map((log) => (
                <div key={log.id} className="py-3.5 first:pt-2 last:pb-0 flex items-start gap-3">
                  <div className="w-2 h-2 rounded-full bg-brand-600 mt-1.5 shrink-0" />
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center justify-between gap-2">
                      <span className="text-xs font-bold text-ink truncate">
                        {log.actionType}
                      </span>
                      <span className="text-[11px] text-ink-muted shrink-0 font-mono">
                        {log.timestamp}
                      </span>
                    </div>
                    <p className="text-xs text-ink-muted mt-0.5 line-clamp-2">
                      {log.details}
                    </p>
                    <div className="mt-1 flex items-center gap-2 text-[10px] text-ink-subtle font-mono">
                      <span>{log.adminName}</span>
                      <span>•</span>
                      <span>{log.ipAddress}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
