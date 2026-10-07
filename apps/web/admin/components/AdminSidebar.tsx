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
  Layers,
  ChevronRight,
  ShieldCheck,
  Users,
  HelpCircle,
} from "lucide-react";
import clsx from "clsx";

interface NavItem {
  name: string;
  href: string;
  icon: React.ComponentType<{ className?: string }>;
  badge?: string;
  badgeColor?: string;
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
        badge: "2 Pending",
        badgeColor: "bg-amber-500 text-white",
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
        badge: "Rs 1.7M",
        badgeColor: "bg-emerald-600 text-white",
      },
      { name: "Disputes & Refunds", href: "/disputes", icon: HelpCircle, badge: "2 Open", badgeColor: "bg-amber-500 text-white" },
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

  return (
    <aside className="w-64 bg-slate-900 text-slate-300 flex flex-col shrink-0 border-r border-slate-800 min-h-screen">
      {/* Brand Header */}
      <div className="p-5 border-b border-slate-800 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-emerald-600 to-teal-400 flex items-center justify-center text-white font-bold text-lg shadow-md shadow-emerald-900/30">
            E
          </div>
          <div>
            <div className="text-white font-bold text-base tracking-tight flex items-center gap-1.5">
              EVRRY
              <span className="text-[10px] uppercase font-semibold tracking-wider px-1.5 py-0.5 rounded bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                Admin
              </span>
            </div>
            <div className="text-[11px] text-slate-400 font-medium">Operations Console</div>
          </div>
        </div>
      </div>

      {/* Navigation Sections */}
      <nav className="flex-1 px-3 py-4 space-y-6 overflow-y-auto">
        {navSections.map((section) => (
          <div key={section.title}>
            <div className="px-3 mb-2 text-[10px] font-semibold tracking-wider text-slate-400 uppercase">
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
                      "flex items-center justify-between px-3 py-2.5 rounded-lg text-xs font-medium transition-all group",
                      isActive
                        ? "bg-emerald-600 text-white shadow-sm shadow-emerald-900/50"
                        : "text-slate-300 hover:text-white hover:bg-slate-800/80"
                    )}
                  >
                    <div className="flex items-center gap-2.5">
                      <Icon
                        className={clsx(
                          "w-4 h-4 transition-colors",
                          isActive ? "text-white" : "text-slate-400 group-hover:text-slate-200"
                        )}
                      />
                      <span>{item.name}</span>
                    </div>
                    {item.badge ? (
                      <span
                        className={clsx(
                          "text-[10px] font-semibold px-2 py-0.5 rounded-full",
                          item.badgeColor || "bg-slate-700 text-slate-200"
                        )}
                      >
                        {item.badge}
                      </span>
                    ) : (
                      <ChevronRight
                        className={clsx(
                          "w-3.5 h-3.5 opacity-0 -translate-x-1 transition-all group-hover:opacity-100 group-hover:translate-x-0",
                          isActive && "opacity-100 translate-x-0"
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

      {/* Security & Admin Profile Footer */}
      <div className="p-4 border-t border-slate-800 bg-slate-950/60">
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center gap-1.5 text-[11px] text-emerald-400 font-medium">
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
            <span>MFA Protected</span>
          </div>
          <span className="text-[10px] font-mono text-slate-400">KTM-DC-01</span>
        </div>
        <div className="flex items-center gap-2.5 pt-2 border-t border-slate-800/80">
          <div className="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-xs font-semibold text-slate-200">
            SA
          </div>
          <div className="flex-1 min-w-0">
            <div className="text-xs font-semibold text-white truncate">Suman Adhikari</div>
            <div className="text-[10px] text-slate-400 truncate">VP Operations (Superadmin)</div>
          </div>
        </div>
      </div>
    </aside>
  );
}
