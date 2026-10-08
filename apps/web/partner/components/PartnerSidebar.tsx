"use client";

import { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  LayoutDashboard,
  UtensilsCrossed,
  ShoppingBag,
  Hotel,
  Home,
  BookOpen,
  Wallet,
  LogOut,
  ChevronLeft,
  ChevronRight,
  Store,
} from "lucide-react";
import clsx from "clsx";

interface NavItem {
  name: string;
  href: string;
  icon: React.ComponentType<{ className?: string }>;
  badge?: string;
  badgeVariant?: "brand" | "warning" | "success";
}

interface NavSection {
  title: string;
  items: NavItem[];
}

const navSections: NavSection[] = [
  {
    title: "Operations & Fulfillment",
    items: [
      { name: "Overview Hub", href: "/", icon: LayoutDashboard },
      {
        name: "Kitchen KDS (Dining)",
        href: "/kds",
        icon: UtensilsCrossed,
        badge: "Live",
        badgeVariant: "brand",
      },
      {
        name: "Grocery Picking",
        href: "/grocery",
        icon: ShoppingBag,
      },
      {
        name: "Hotel Front Desk",
        href: "/hotel",
        icon: Hotel,
      },
      {
        name: "Landlord Stays",
        href: "/rentals",
        icon: Home,
      },
    ],
  },
  {
    title: "Catalog & Pricing",
    items: [
      {
        name: "Menu & Item 86's",
        href: "/menu",
        icon: BookOpen,
      },
    ],
  },
  {
    title: "Accounts & Payouts",
    items: [
      {
        name: "Settlement Ledger",
        href: "/finance",
        icon: Wallet,
      },
    ],
  },
];

export function PartnerSidebar() {
  const pathname = usePathname();
  const [isCollapsed, setIsCollapsed] = useState(false);

  if (pathname === "/login") {
    return null;
  }

  return (
    <aside
      className={clsx(
        "h-screen flex flex-col shrink-0 bg-white border-r border-slate-200 select-none shadow-sm transition-all duration-200 ease-in-out sticky top-0 z-30",
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
          <div className="w-9 h-9 rounded-xl bg-brand-600 flex items-center justify-center text-white font-bold text-base shadow-sm shrink-0 group-hover:bg-brand-700 transition-colors">
            E
          </div>
          {!isCollapsed && (
            <div className="min-w-0">
              <div className="text-slate-900 font-bold text-sm tracking-tight flex items-center gap-1.5 truncate">
                <span>EVRRY</span>
                <span className="text-[10px] font-semibold text-brand-700 bg-brand-50 px-1.5 py-0.5 rounded border border-brand-200">
                  PARTNER
                </span>
              </div>
              <p className="text-[11px] text-slate-500 truncate">
                Merchant Operations
              </p>
            </div>
          )}
        </Link>
      </div>

      {/* Navigation Sections */}
      <div className="flex-1 overflow-y-auto p-3 space-y-6">
        {navSections.map((section, idx) => (
          <div key={idx} className="space-y-1">
            {!isCollapsed && (
              <h3 className="px-3 text-[10px] font-bold uppercase tracking-wider text-slate-400 mb-1.5">
                {section.title}
              </h3>
            )}
            <ul className="space-y-1">
              {section.items.map((item) => {
                const isActive =
                  item.href === "/"
                    ? pathname === "/"
                    : pathname.startsWith(item.href);
                const Icon = item.icon;

                return (
                  <li key={item.href}>
                    <Link
                      href={item.href}
                      className={clsx(
                        "flex items-center gap-3 px-3 py-2 rounded-xl text-xs font-semibold transition-all group",
                        isActive
                          ? "bg-brand-50 text-brand-700 font-bold shadow-xs"
                          : "text-slate-600 hover:text-slate-900 hover:bg-slate-50",
                        isCollapsed && "justify-center px-2"
                      )}
                      title={isCollapsed ? item.name : undefined}
                    >
                      <Icon
                        className={clsx(
                          "w-4 h-4 shrink-0 transition-colors",
                          isActive
                            ? "text-brand-600"
                            : "text-slate-400 group-hover:text-slate-600"
                        )}
                      />
                      {!isCollapsed && (
                        <div className="flex-1 flex items-center justify-between min-w-0">
                          <span className="truncate">{item.name}</span>
                          {item.badge && (
                            <span
                              className={clsx(
                                "text-[10px] px-1.5 py-0.5 rounded-md font-bold uppercase tracking-wide",
                                item.badgeVariant === "brand"
                                  ? "bg-brand-100 text-brand-800"
                                  : item.badgeVariant === "warning"
                                  ? "bg-amber-100 text-amber-800"
                                  : "bg-emerald-100 text-emerald-800"
                              )}
                            >
                              {item.badge}
                            </span>
                          )}
                        </div>
                      )}
                    </Link>
                  </li>
                );
              })}
            </ul>
          </div>
        ))}
      </div>

      {/* Footer Profile & Collapse Toggle */}
      <div className="shrink-0 p-3 border-t border-slate-100 bg-slate-50/50 space-y-2">
        <div
          className={clsx(
            "flex items-center gap-2.5 p-2 rounded-xl bg-white border border-slate-200/80 shadow-2xs",
            isCollapsed && "justify-center"
          )}
        >
          <div className="w-8 h-8 rounded-lg bg-brand-100 text-brand-700 font-bold text-xs flex items-center justify-center shrink-0">
            <Store className="w-4 h-4" />
          </div>
          {!isCollapsed && (
            <div className="min-w-0 flex-1">
              <p className="text-xs font-bold text-slate-900 truncate">
                Roadhouse Cafe
              </p>
              <p className="text-[10px] text-slate-500 font-mono truncate">
                Pulchowk • Online
              </p>
            </div>
          )}
        </div>

        <div className="flex items-center justify-between pt-1">
          <button
            type="button"
            onClick={() => setIsCollapsed(!isCollapsed)}
            className="p-1.5 rounded-lg border border-slate-200 hover:bg-white text-slate-500 hover:text-slate-800 transition-colors"
            title={isCollapsed ? "Expand Sidebar" : "Collapse Sidebar"}
          >
            {isCollapsed ? (
              <ChevronRight className="w-4 h-4" />
            ) : (
              <ChevronLeft className="w-4 h-4" />
            )}
          </button>

          {!isCollapsed && (
            <Link
              href="/login"
              className="flex items-center gap-1 px-2.5 py-1 text-slate-500 hover:text-rose-600 text-xs font-medium rounded-lg hover:bg-rose-50 transition-colors"
            >
              <LogOut className="w-3.5 h-3.5" />
              <span>Switch / Sign Out</span>
            </Link>
          )}
        </div>
      </div>
    </aside>
  );
}
