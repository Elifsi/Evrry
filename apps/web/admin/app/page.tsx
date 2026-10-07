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
  Users,
  Ticket,
  HelpCircle,
  ShieldAlert,
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
    <div className="flex-1 flex flex-col h-screen overflow-y-auto bg-slate-50/75">
      <AdminHeader
        title="Executive Operations Console"
        subtitle="Zero-SQL Administrative Governance Platform • Live Metrics & Real-Time Controls"
        onRefresh={loadDashboardData}
        isRefreshing={isRefreshing}
      />

      <div className="p-6 md:p-8 space-y-6 flex-1 max-w-[1500px] w-full mx-auto">
        {/* Banner Alert for Midnight Payout & pg_cron Status */}
        <div className="bg-white rounded-2xl p-5 text-slate-900 shadow-sm border border-slate-200/80 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-start gap-3.5">
            <div className="p-2.5 bg-teal-50 border border-teal-200/60 rounded-xl text-teal-600 shrink-0">
              <Zap className="w-5 h-5 text-teal-600" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-sm font-bold text-slate-900 tracking-tight">
                  Midnight Settlement Batch Ready for Review
                </h2>
                <span className="px-2 py-0.5 text-[10px] font-semibold bg-teal-50 text-teal-700 rounded-full border border-teal-200 uppercase">
                  Automated pg_cron
                </span>
                {metrics.liveDbConnected && (
                  <span className="px-2 py-0.5 text-[10px] font-semibold bg-emerald-50 text-emerald-800 rounded-full border border-emerald-200 uppercase flex items-center gap-1">
                    <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
                    Supabase Live
                  </span>
                )}
              </div>
              <p className="text-xs text-slate-500 mt-0.5">
                Aggregated daily volume of NPR {metrics.midnightPayoutTotalNpr.toLocaleString()} across {metrics.merchantPayoutCount} verified merchants and {metrics.riderPayoutCount} riders.
              </p>
            </div>
          </div>
          <div className="flex items-center gap-2.5 shrink-0">
            <Link
              href="/finance/payouts"
              className="px-4 py-2 bg-teal-600 hover:bg-teal-700 text-white text-xs font-bold rounded-xl transition-all flex items-center gap-1.5 shadow-sm"
            >
              Review Payouts
              <ArrowUpRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        </div>

        {/* Core KPI Widgets (4 Cards) */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Card 1: Today GMV */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
            <div className="flex items-center justify-between text-xs text-slate-500 font-semibold">
              <span>Today&apos;s Gross Volume (GMV)</span>
              <TrendingUp className="w-4 h-4 text-teal-600" />
            </div>
            <div className="mt-2.5 text-2xl font-bold text-slate-900 tracking-tight">
              NPR {metrics.todayGmvNpr.toLocaleString()}
            </div>
            <div className="mt-1 flex items-center gap-1 text-[11px] text-emerald-700 font-semibold">
              <span>+{metrics.gmvGrowthPercent}% vs yesterday</span>
              <span className="text-slate-400 font-normal">({metrics.todayOrdersCount} orders)</span>
            </div>
          </div>

          {/* Card 2: Platform Fee Revenue */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
            <div className="flex items-center justify-between text-xs text-slate-500 font-semibold">
              <span>Platform Take & Fees</span>
              <Percent className="w-4 h-4 text-teal-600" />
            </div>
            <div className="mt-2.5 text-2xl font-bold text-slate-900 tracking-tight">
              Rs {(metrics.todayOrdersCount * metrics.platformFeeNpr).toLocaleString()}
            </div>
            <div className="mt-1 text-[11px] text-slate-500 font-medium">
              Standard order tariff: Rs {metrics.platformFeeNpr} / checkout
            </div>
          </div>

          {/* Card 3: Pending KYC Verification */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
            <div className="flex items-center justify-between text-xs text-slate-500 font-semibold">
              <span>Pending Partner KYC</span>
              <FileCheck2 className="w-4 h-4 text-amber-600" />
            </div>
            <div className="mt-2.5 text-2xl font-bold text-amber-700 tracking-tight">
              {metrics.pendingKycCount} Applicants
            </div>
            <div className="mt-1 flex items-center gap-1.5 text-[11px] text-amber-800 font-semibold">
              <Link href="/compliance/kyc" className="hover:underline flex items-center gap-0.5">
                Audit Government Docs &rarr;
              </Link>
            </div>
          </div>

          {/* Card 4: Midnight Disbursal Pool */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
            <div className="flex items-center justify-between text-xs text-slate-500 font-semibold">
              <span>Midnight ConnectIPS Pool</span>
              <Wallet className="w-4 h-4 text-teal-600" />
            </div>
            <div className="mt-2.5 text-2xl font-bold text-slate-900 tracking-tight">
              NPR {metrics.midnightPayoutTotalNpr.toLocaleString()}
            </div>
            <div className="mt-1 text-[11px] text-slate-500 font-medium">
              {metrics.merchantPayoutCount} merchants • {metrics.riderPayoutCount} riders
            </div>
          </div>
        </div>

        {/* Main Grid: Quick Operational Navigation Hub & Audit Trail Feed */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 items-start">
          {/* Quick Operations Navigation Hub (1 col) */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-3">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                Operational Desks
              </h3>
              <span className="text-[10px] text-slate-400 font-mono">Zero-SQL</span>
            </div>

            <div className="space-y-1.5">
              {[
                {
                  title: "Partner KYC & Compliance Desk",
                  desc: "Driving licenses, bluebooks, and tax tokens",
                  href: "/compliance/kyc",
                  icon: FileCheck2,
                  badge: "Audit",
                },
                {
                  title: "Dynamic Fee & Pricing Matrix",
                  desc: "Delivery tariffs, platform fees & take-rates",
                  href: "/pricing",
                  icon: Percent,
                },
                {
                  title: "Customer Dispute & Refund Desk",
                  desc: "Photo evidence and 1-click gateway refunds",
                  href: "/disputes",
                  icon: HelpCircle,
                },
                {
                  title: "Midnight Payouts & ConnectIPS",
                  desc: "NCHL-128 export and rider COD safety locks",
                  href: "/finance/payouts",
                  icon: Wallet,
                },
                {
                  title: "User & Partner Moderation",
                  desc: "COD privilege toggles & device blacklisting",
                  href: "/users",
                  icon: Users,
                },
                {
                  title: "Marketing Vouchers & Burn",
                  desc: "Budget burn tracker & promotional codes",
                  href: "/marketing/vouchers",
                  icon: Ticket,
                },
                {
                  title: "Governance & Ward Kill-Switches",
                  desc: "13% VAT activation & emergency stops",
                  href: "/settings/governance",
                  icon: ShieldAlert,
                },
              ].map((desk) => {
                const Icon = desk.icon;
                return (
                  <Link
                    key={desk.href}
                    href={desk.href}
                    className="flex items-center justify-between p-3 rounded-xl border border-slate-200/70 hover:border-teal-300 hover:bg-teal-50/40 transition-all group"
                  >
                    <div className="flex items-center gap-2.5 min-w-0">
                      <div className="w-7 h-7 rounded-lg bg-slate-100 text-slate-600 group-hover:bg-teal-600 group-hover:text-white flex items-center justify-center shrink-0 transition-colors">
                        <Icon className="w-3.5 h-3.5" />
                      </div>
                      <div className="min-w-0">
                        <div className="text-xs font-bold text-slate-900 truncate group-hover:text-teal-900">
                          {desk.title}
                        </div>
                        <div className="text-[11px] text-slate-500 truncate mt-0.5">
                          {desk.desc}
                        </div>
                      </div>
                    </div>
                    <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-teal-600 shrink-0 ml-1" />
                  </Link>
                );
              })}
            </div>
          </div>

          {/* Real-Time Immutable Audit Trail Feed (2 cols) */}
          <div className="lg:col-span-2 bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div>
                <h3 className="text-sm font-bold text-slate-900 tracking-tight flex items-center gap-2">
                  <Clock className="w-4 h-4 text-teal-600" />
                  Immutable Administrative Audit Trail
                </h3>
                <p className="text-xs text-slate-500 mt-0.5">
                  Logged in PostgreSQL public.admin_audit_logs with operator identity and IP.
                </p>
              </div>
              <span className="text-[10px] font-mono text-slate-500 bg-slate-50 px-2.5 py-1 rounded-lg border border-slate-200">
                Asia/Kathmandu (UTC+5:45)
              </span>
            </div>

            <div className="divide-y divide-slate-100">
              {auditLogs.slice(0, 6).map((log) => (
                <div key={log.id} className="py-3 first:pt-1 last:pb-0 flex items-start gap-3">
                  <div className="w-2 h-2 rounded-full bg-teal-600 mt-1.5 shrink-0" />
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center justify-between gap-2">
                      <span className="text-xs font-bold text-slate-900 truncate">
                        {log.actionType}
                      </span>
                      <span className="text-[10px] text-slate-400 font-mono shrink-0">
                        {log.timestamp}
                      </span>
                    </div>
                    <p className="text-xs text-slate-600 mt-0.5 line-clamp-2">
                      {log.details}
                    </p>
                    <div className="mt-1 flex items-center gap-2 text-[10px] text-slate-400 font-mono">
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
