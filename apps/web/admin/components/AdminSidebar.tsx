"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  LayoutDashboard,
  Percent,
  FileCheck2,
  Wallet,
  Ticket,
  ShieldAlert,
  ChevronRight,
  ShieldCheck,
  Users,
  HelpCircle,
  LogOut,
  UserCheck,
} from "lucide-react";
import clsx from "clsx";
import { useAdminAuth } from "@/lib/admin/auth";

interface NavItem {
  name: string;
  href: string;
  icon: React.ComponentType<{ className?: string }>;
  badge?: string;
  badgeVariant?: "warning" | "brand" | "neutral";
}

const navSections: Array<{ title: string; items: NavItem[] }> = [
  {
    title: "OPERATIONS CORE",
    items: [
      { name: "Overview Dashboard", href: "/", icon: LayoutDashboard },
      { name: "Dynamic Pricing & Fees", href: "/pricing", icon: Percent },
      {
        name: "Partner KYC Queue",
        href: "/compliance/kyc",
        icon: FileCheck2,
        badge: "Pending",
        badgeVariant: "warning",
      },
      { name: "User & Driver Moderation", href: "/users", icon: Users },
    ],
  },
  {
    title: "FINANCE & GROWTH",
    items: [
      {
        name: "Midnight Payouts & COD",
        href: "/finance/payouts",
        icon: Wallet,
        badge: "pg_cron",
        badgeVariant: "brand",
      },
      {
        name: "Disputes & Refunds",
        href: "/disputes",
        icon: HelpCircle,
        badge: "Open",
        badgeVariant: "warning",
      },
      { name: "Vouchers & Promotions", href: "/marketing/vouchers", icon: Ticket },
    ],
  },
  {
    title: "SYSTEM GOVERNANCE",
    items: [
      { name: "Compliance & VAT", href: "/settings/governance", icon: ShieldAlert },
    ],
  },
];

export function AdminSidebar() {
  const pathname = usePathname();
  const { admin, logout } = useAdminAuth();

  // If on login route, sidebar is hidden
  if (pathname === "/login") {
    return null;
  }

  return (
    <aside className="w-64 h-screen flex flex-col shrink-0 bg-white border-r border-border sticky top-0 z-30 select-none shadow-sm">
      {/* Brand Header */}
      <div className="shrink-0 p-5 border-b border-border flex items-center justify-between bg-white">
        <Link href="/" className="flex items-center gap-3 group">
          <div className="w-9 h-9 rounded-xl bg-brand-600 flex items-center justify-center text-white font-bold text-lg shadow-md shadow-brand-900/30 group-hover:bg-brand-700 transition-colors">
            E
          </div>
          <div>
            <div className="text-ink font-bold text-base tracking-tight flex items-center gap-1.5">
              EVRRY
              <span className="text-[10px] uppercase font-bold tracking-wider px-1.5 py-0.5 rounded bg-brand-50 text-brand-700 border border-brand-200">
                Admin
              </span>
            </div>
            <div className="text-[11px] text-ink-muted font-medium">Operations Console</div>
          </div>
        </Link>
      </div>

      {/* Navigation Sections (Independent Scrollable Area) */}
      <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-6">
        {navSections.map((section) => (
          <div key={section.title}>
            <div className="px-3 mb-2 text-[10px] font-bold tracking-wider text-ink-subtle uppercase">
              {section.title}
            </div>
            <div className="space-y-1">
              {section.items.map((item) => {
                const isActive = pathname === item.href;
                const Icon = item.icon;
                return (
                  <Link
                    key={item.href}
                    href={item.href}
                    className={clsx(
                      "flex items-center justify-between px-3 py-2.5 rounded-xl text-xs font-semibold transition-all group",
                      isActive
                        ? "bg-brand-600 text-white shadow-md shadow-brand-900/30"
                        : "text-slate-600 hover:text-ink hover:bg-slate-50"
                    )}
                  >
                    <div className="flex items-center gap-2.5">
                      <Icon
                        className={clsx(
                          "w-4 h-4 transition-colors",
                          isActive ? "text-white" : "text-slate-400 group-hover:text-slate-600"
                        )}
                      />
                      <span>{item.name}</span>
                    </div>
                    {item.badge ? (
                      <span
                        className={clsx(
                          "text-[10px] font-bold px-2 py-0.5 rounded-full border",
                          isActive
                            ? "bg-white/20 text-white border-white/30"
                            : item.badgeVariant === "warning"
                            ? "bg-amber-50 text-amber-800 border-amber-200"
                            : item.badgeVariant === "brand"
                            ? "bg-brand-50 text-brand-800 border-brand-200"
                            : "bg-slate-100 text-slate-700 border-slate-200"
                        )}
                      >
                        {item.badge}
                      </span>
                    ) : (
                      <ChevronRight
                        className={clsx(
                          "w-3.5 h-3.5 opacity-0 -translate-x-1 transition-all group-hover:opacity-100 group-hover:translate-x-0",
                          isActive && "opacity-100 translate-x-0 text-white"
                        )}
                      />
                    )}
                  </Link>
                );
              })}
            </div>
          </div>
        ))}
      </nav>

      {/* Pinned Bottom Profile & Security Footer (Always Visible Without Scrolling) */}
      <div className="shrink-0 p-4 border-t border-border bg-slate-50/80">
        <div className="flex items-center justify-between mb-2.5">
          <div className="flex items-center gap-1.5 text-[11px] text-emerald-700 font-semibold">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
            <span>MFA Protected</span>
          </div>
          <span className="text-[10px] font-mono text-ink-muted bg-white px-1.5 py-0.5 rounded border border-border">
            KTM-DC-01
          </span>
        </div>

        <div className="flex items-center justify-between pt-2.5 border-t border-border/80">
          <div className="flex items-center gap-2.5 min-w-0">
            <div className="w-8 h-8 rounded-xl bg-brand-100 border border-brand-200 flex items-center justify-center text-xs font-bold text-brand-700 shrink-0">
              {admin?.displayName ? admin.displayName.substring(0, 2).toUpperCase() : "SA"}
            </div>
            <div className="min-w-0">
              <div className="text-xs font-bold text-ink truncate">
                {admin?.displayName || "Suman Adhikari"}
              </div>
              <div className="text-[10px] text-ink-muted truncate capitalize">
                {admin?.role ? `${admin.role} (Operator)` : "VP Operations"}
              </div>
            </div>
          </div>

          <button
            onClick={() => logout()}
            className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors ml-1 shrink-0"
            title="Sign out of administrator session"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </aside>
  );
}
