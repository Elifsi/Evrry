"use client";

import { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  LayoutDashboard,
  Percent,
  FileCheck2,
  Wallet,
  Ticket,
  ShieldCheck,
  Users,
  HelpCircle,
  LogOut,
  ChevronLeft,
  ChevronRight,
  ShieldAlert,
} from "lucide-react";
import clsx from "clsx";
import { useAdminAuth } from "@/lib/admin/auth";

interface NavItem {
  name: string;
  href: string;
  icon: React.ComponentType<{ className?: string }>;
  badge?: string;
  badgeVariant?: "warning" | "brand" | "neutral" | "success";
}

interface NavSection {
  title: string;
  items: NavItem[];
}

const navSections: NavSection[] = [
  {
    title: "Core Operations",
    items: [
      { name: "Overview Dashboard", href: "/", icon: LayoutDashboard },
      {
        name: "KYC & Compliance",
        href: "/compliance/kyc",
        icon: FileCheck2,
        badge: "Review",
        badgeVariant: "warning",
      },
      {
        name: "Customer Disputes",
        href: "/disputes",
        icon: HelpCircle,
        badge: "Open",
        badgeVariant: "warning",
      },
    ],
  },
  {
    title: "Financials",
    items: [
      {
        name: "Payouts & Settlement",
        href: "/finance/payouts",
        icon: Wallet,
        badge: "ConnectIPS",
        badgeVariant: "brand",
      },
      { name: "Dynamic Pricing", href: "/pricing", icon: Percent },
    ],
  },
  {
    title: "Platform Growth",
    items: [
      { name: "Users & Partners", href: "/users", icon: Users },
      { name: "Marketing Vouchers", href: "/marketing/vouchers", icon: Ticket },
    ],
  },
  {
    title: "Governance",
    items: [
      {
        name: "Audit Trail & Settings",
        href: "/settings/governance",
        icon: ShieldAlert,
      },
    ],
  },
];

export function AdminSidebar() {
  const pathname = usePathname();
  const { admin, logout } = useAdminAuth();
  const [isCollapsed, setIsCollapsed] = useState(false);

  // If on login route, sidebar is hidden
  if (pathname === "/login") {
    return null;
  }

  return (
    <aside
      className={clsx(
        "h-screen flex flex-col shrink-0 bg-white border-r border-slate-200/80 sticky top-0 z-30 select-none shadow-sm transition-all duration-200 ease-in-out",
        isCollapsed ? "w-20" : "w-64"
      )}
    >
      {/* Brand Header */}
      <div className="shrink-0 p-4 border-b border-slate-100 flex items-center justify-between bg-white h-16">
        <Link
          href="/"
          className={clsx(
            "flex items-center gap-3 group overflow-hidden",
            isCollapsed && "justify-center w-full"
          )}
        >
          <div className="w-9 h-9 rounded-xl bg-teal-600 flex items-center justify-center text-white font-bold text-base shadow-sm shrink-0 group-hover:bg-teal-700 transition-colors">
            E
          </div>
          {!isCollapsed && (
            <div className="min-w-0">
              <div className="text-slate-900 font-bold text-sm tracking-tight flex items-center gap-1.5 truncate">
                EVRRY
                <span className="text-[10px] uppercase font-bold tracking-wider px-1.5 py-0.2 rounded bg-teal-50 text-teal-700 border border-teal-200/60">
                  Admin
                </span>
              </div>
              <div className="text-[11px] text-slate-500 font-medium truncate">
                Operator Workspace
              </div>
            </div>
          )}
        </Link>
      </div>

      {/* Navigation Sections */}
      <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-6">
        {navSections.map((section) => (
          <div key={section.title}>
            {!isCollapsed && (
              <div className="px-3 mb-2 text-[10px] font-bold tracking-wider text-slate-400 uppercase">
                {section.title}
              </div>
            )}
            <div className="space-y-1">
              {section.items.map((item) => {
                const isActive = pathname === item.href;
                const Icon = item.icon;
                return (
                  <Link
                    key={item.href}
                    href={item.href}
                    title={isCollapsed ? item.name : undefined}
                    className={clsx(
                      "flex items-center rounded-xl text-xs font-semibold transition-all group",
                      isCollapsed
                        ? "justify-center p-2.5"
                        : "justify-between px-3 py-2.5",
                      isActive
                        ? "bg-teal-50 text-teal-800 shadow-sm border border-teal-200/60"
                        : "text-slate-600 hover:text-slate-900 hover:bg-slate-50"
                    )}
                  >
                    <div className="flex items-center gap-2.5 min-w-0">
                      <Icon
                        className={clsx(
                          "w-4 h-4 shrink-0 transition-colors",
                          isActive
                            ? "text-teal-600 font-bold"
                            : "text-slate-400 group-hover:text-slate-600"
                        )}
                      />
                      {!isCollapsed && <span className="truncate">{item.name}</span>}
                    </div>
                    {!isCollapsed && item.badge && (
                      <span
                        className={clsx(
                          "text-[10px] font-bold px-2 py-0.5 rounded-full border shrink-0",
                          isActive
                            ? "bg-teal-600 text-white border-teal-600"
                            : item.badgeVariant === "warning"
                            ? "bg-amber-50 text-amber-700 border-amber-200"
                            : item.badgeVariant === "brand"
                            ? "bg-teal-50 text-teal-700 border-teal-200"
                            : "bg-slate-100 text-slate-600 border-slate-200"
                        )}
                      >
                        {item.badge}
                      </span>
                    )}
                  </Link>
                );
              })}
            </div>
          </div>
        ))}
      </nav>

      {/* Collapse Toggle & Operator Profile Footer */}
      <div className="shrink-0 p-3 border-t border-slate-100 bg-white space-y-2">
        <button
          type="button"
          onClick={() => setIsCollapsed(!isCollapsed)}
          className={clsx(
            "w-full flex items-center text-xs font-medium text-slate-500 hover:text-slate-800 hover:bg-slate-50 p-2 rounded-lg transition-colors",
            isCollapsed ? "justify-center" : "justify-between"
          )}
          title={isCollapsed ? "Expand Sidebar" : "Collapse Sidebar"}
        >
          {!isCollapsed && <span>Collapse Sidebar</span>}
          {isCollapsed ? (
            <ChevronRight className="w-4 h-4" />
          ) : (
            <ChevronLeft className="w-4 h-4" />
          )}
        </button>

        {/* User Card */}
        <div
          className={clsx(
            "flex items-center rounded-xl bg-slate-50/80 border border-slate-200/80 p-2",
            isCollapsed ? "justify-center" : "justify-between"
          )}
        >
          <div className="flex items-center gap-2.5 min-w-0">
            <div className="w-7 h-7 rounded-lg bg-teal-600 text-white flex items-center justify-center font-bold text-xs shrink-0 shadow-sm">
              {admin?.displayName ? admin.displayName.charAt(0) : "S"}
            </div>
            {!isCollapsed && (
              <div className="min-w-0">
                <div className="text-xs font-bold text-slate-900 truncate">
                  {admin?.displayName || "Suman Adhikari"}
                </div>
                <div className="text-[10px] text-teal-700 font-semibold truncate uppercase">
                  {admin?.role || "superadmin"}
                </div>
              </div>
            )}
          </div>

          {!isCollapsed && (
            <button
              onClick={logout}
              title="Sign Out"
              className="p-1 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors shrink-0"
            >
              <LogOut className="w-4 h-4" />
            </button>
          )}
        </div>
      </div>
    </aside>
  );
}
