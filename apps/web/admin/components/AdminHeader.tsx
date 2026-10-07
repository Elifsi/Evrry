"use client";

import { useState, useEffect, useRef } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  Bell,
  Activity,
  Database,
  RefreshCw,
  AlertTriangle,
  Wallet,
  ShieldCheck,
  FileText,
  X,
  Send,
  Radio,
  Search,
  Command,
  ArrowRight,
  Sparkles,
} from "lucide-react";
import {
  fetchAdminNotifications,
  markAdminNotificationRead,
  markAllAdminNotificationsRead,
  dispatchAdminPushBroadcast,
} from "@/lib/admin/api";
import { AdminNotification } from "@/lib/admin/types";

interface AdminHeaderProps {
  title: string;
  subtitle?: string;
  onRefresh?: () => void;
  isRefreshing?: boolean;
}

const COMMAND_ROUTES = [
  { name: "Overview Dashboard", href: "/", category: "Core Operations" },
  { name: "Partner KYC Verification Desk", href: "/compliance/kyc", category: "Core Operations" },
  { name: "Customer Dispute & Refund Review", href: "/disputes", category: "Core Operations" },
  { name: "Midnight Settlement & ConnectIPS Payouts", href: "/finance/payouts", category: "Financials" },
  { name: "Dynamic Fee & Pricing Control Matrix", href: "/pricing", category: "Financials" },
  { name: "User & Driver Moderation Directory", href: "/users", category: "Platform Growth" },
  { name: "Marketing Vouchers & Promotions", href: "/marketing/vouchers", category: "Platform Growth" },
  { name: "Compliance, Taxation & VAT Settings", href: "/settings/governance", category: "Governance" },
];

export function AdminHeader({ title, subtitle, onRefresh, isRefreshing }: AdminHeaderProps) {
  const router = useRouter();
  const [showNotifications, setShowNotifications] = useState(false);
  const [notifications, setNotifications] = useState<AdminNotification[]>([]);
  const [showBroadcastModal, setShowBroadcastModal] = useState(false);
  const [broadcastTitle, setBroadcastTitle] = useState("");
  const [broadcastBody, setBroadcastBody] = useState("");
  const [broadcastTarget, setBroadcastTarget] = useState<"consumer" | "partner" | "admin">("consumer");
  const [isBroadcasting, setIsBroadcasting] = useState(false);
  const [broadcastSuccess, setBroadcastSuccess] = useState<string | null>(null);

  // Command Palette State
  const [showCommandPalette, setShowCommandPalette] = useState(false);
  const [commandQuery, setCommandQuery] = useState("");

  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    fetchAdminNotifications().then(setNotifications);
  }, []);

  // Keyboard shortcut listener for Cmd+K / Ctrl+K
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key === "k") {
        e.preventDefault();
        setShowCommandPalette((prev) => !prev);
      }
      if (e.key === "Escape") {
        setShowCommandPalette(false);
        setShowNotifications(false);
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, []);

  // Close notifications on outside click
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setShowNotifications(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const unreadCount = notifications.filter((n) => !n.read).length;

  const handleMarkRead = async (id: string) => {
    await markAdminNotificationRead(id);
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, read: true } : n))
    );
  };

  const handleMarkAllRead = async () => {
    await markAllAdminNotificationsRead();
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
  };

  const handleSendBroadcast = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!broadcastTitle.trim() || !broadcastBody.trim()) return;

    setIsBroadcasting(true);
    const res = await dispatchAdminPushBroadcast(broadcastTitle, broadcastBody, broadcastTarget);
    setIsBroadcasting(false);
    setBroadcastSuccess(res.message);
    setBroadcastTitle("");
    setBroadcastBody("");
    setTimeout(() => {
      setBroadcastSuccess(null);
      setShowBroadcastModal(false);
    }, 2500);
  };

  const filteredCommands = COMMAND_ROUTES.filter((r) =>
    r.name.toLowerCase().includes(commandQuery.toLowerCase()) ||
    r.category.toLowerCase().includes(commandQuery.toLowerCase())
  );

  return (
    <header className="h-16 bg-white border-b border-slate-200/80 px-6 flex items-center justify-between sticky top-0 z-20 shrink-0">
      <div className="flex items-center gap-4">
        <div>
          <h1 className="text-base font-bold text-slate-900 tracking-tight">{title}</h1>
          {subtitle && <p className="text-xs text-slate-500 font-medium truncate max-w-xl">{subtitle}</p>}
        </div>
      </div>

      <div className="flex items-center gap-3">
        {/* Global Search Command Trigger Button */}
        <button
          type="button"
          onClick={() => setShowCommandPalette(true)}
          className="hidden md:flex items-center gap-2.5 px-3 py-1.5 text-xs text-slate-400 bg-slate-50 hover:bg-slate-100 hover:text-slate-600 border border-slate-200/80 rounded-xl transition-colors"
        >
          <Search className="w-3.5 h-3.5 text-slate-400" />
          <span>Quick actions & desks...</span>
          <kbd className="text-[10px] bg-white border border-slate-200 text-slate-500 font-mono px-1.5 py-0.5 rounded shadow-2xs">
            ⌘K
          </kbd>
        </button>

        {/* System Cluster Status */}
        <div className="hidden lg:flex items-center gap-2 bg-slate-50 border border-slate-200/80 px-3 py-1.5 rounded-full text-xs">
          <div className="flex items-center gap-1.5 text-teal-700 font-semibold">
            <span className="w-2 h-2 rounded-full bg-teal-500 animate-pulse" />
            <Activity className="w-3.5 h-3.5 text-teal-600" />
            <span>Cluster Live</span>
          </div>
          <span className="text-slate-300">|</span>
          <div className="flex items-center gap-1.5 text-slate-500 font-medium">
            <Database className="w-3.5 h-3.5 text-slate-400" />
            <span>Postgres 16 RLS</span>
          </div>
        </div>

        {/* Refresh Action */}
        {onRefresh && (
          <button
            onClick={onRefresh}
            className="p-2 text-slate-500 hover:text-slate-900 hover:bg-slate-100 rounded-xl transition-colors border border-slate-200/60"
            title="Refresh Data"
            disabled={isRefreshing}
          >
            <RefreshCw className={`w-4 h-4 ${isRefreshing ? "animate-spin text-teal-600" : ""}`} />
          </button>
        )}

        {/* Push Broadcast Console Button */}
        <button
          onClick={() => setShowBroadcastModal(true)}
          className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-teal-800 bg-teal-50 hover:bg-teal-100 border border-teal-200/80 rounded-xl transition-colors"
          title="Dispatch Push Broadcast via Edge Function"
        >
          <Radio className="w-3.5 h-3.5 text-teal-600" />
          <span className="hidden sm:inline">Push Broadcast</span>
        </button>

        {/* Operational Notifications Popover */}
        <div className="relative" ref={dropdownRef}>
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            className={`p-2 rounded-xl transition-colors relative border ${
              showNotifications
                ? "bg-slate-100 text-teal-700 border-slate-300"
                : "text-slate-500 hover:text-slate-900 hover:bg-slate-50 border-slate-200/60"
            }`}
            title="Operational Alerts"
          >
            <Bell className="w-4 h-4" />
            {unreadCount > 0 && (
              <span className="absolute -top-1 -right-1 min-w-[18px] h-[18px] px-1 bg-amber-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center ring-2 ring-white shadow-xs">
                {unreadCount}
              </span>
            )}
          </button>

          {/* Flyout Popover */}
          {showNotifications && (
            <div className="absolute right-0 mt-3 w-96 bg-white rounded-2xl shadow-xl border border-slate-200 z-50 overflow-hidden animate-in fade-in slide-in-from-top-2 duration-150">
              <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/75">
                <div className="flex items-center gap-2">
                  <span className="text-xs font-bold text-slate-900">Operational Alerts</span>
                  {unreadCount > 0 && (
                    <span className="bg-amber-50 text-amber-800 text-[10px] font-semibold px-2 py-0.5 rounded-full border border-amber-200">
                      {unreadCount} active
                    </span>
                  )}
                </div>
                {unreadCount > 0 && (
                  <button
                    onClick={handleMarkAllRead}
                    className="text-[11px] text-teal-700 hover:text-teal-800 font-semibold"
                  >
                    Mark all read
                  </button>
                )}
              </div>

              <div className="max-h-80 overflow-y-auto divide-y divide-slate-100">
                {notifications.length === 0 ? (
                  <div className="p-6 text-center text-xs text-slate-400">
                    No active operational alerts.
                  </div>
                ) : (
                  notifications.map((n) => (
                    <div
                      key={n.id}
                      className={`p-4 transition-colors ${
                        n.read ? "bg-white opacity-60" : "bg-teal-50/30"
                      }`}
                    >
                      <div className="flex items-start gap-3">
                        <div className="mt-0.5 shrink-0">
                          {n.type === "urgent" ? (
                            <Wallet className="w-4 h-4 text-rose-600" />
                          ) : n.type === "kyc" ? (
                            <ShieldCheck className="w-4 h-4 text-amber-600" />
                          ) : n.type === "payout" ? (
                            <FileText className="w-4 h-4 text-teal-600" />
                          ) : (
                            <AlertTriangle className="w-4 h-4 text-amber-600" />
                          )}
                        </div>

                        <div className="flex-1 min-w-0">
                          <div className="flex items-center justify-between gap-1">
                            <span className="text-xs font-bold text-slate-900 truncate">
                              {n.title}
                            </span>
                            <span className="text-[10px] text-slate-400 font-mono shrink-0">
                              {n.time}
                            </span>
                          </div>
                          <p className="text-xs text-slate-600 mt-0.5">{n.message}</p>

                          <div className="flex items-center justify-between mt-2 pt-1">
                            {n.actionUrl && (
                              <Link
                                href={n.actionUrl}
                                onClick={() => {
                                  handleMarkRead(n.id);
                                  setShowNotifications(false);
                                }}
                                className="text-[11px] font-semibold text-teal-700 hover:text-teal-800 flex items-center gap-1"
                              >
                                {n.actionLabel || "Review"} &rarr;
                              </Link>
                            )}
                            {!n.read && (
                              <button
                                onClick={() => handleMarkRead(n.id)}
                                className="text-[10px] text-slate-400 hover:text-slate-600 font-medium ml-auto"
                              >
                                Dismiss
                              </button>
                            )}
                          </div>
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Global Command Palette Modal (Cmd+K) */}
      {showCommandPalette && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-sm flex items-start justify-center pt-20 p-4">
          <div className="w-full max-w-xl bg-white rounded-2xl border border-slate-200 shadow-2xl overflow-hidden animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center px-4 border-b border-slate-100 bg-slate-50/50">
              <Search className="w-4 h-4 text-slate-400 shrink-0" />
              <input
                type="text"
                autoFocus
                placeholder="Type a command or search operational desk..."
                value={commandQuery}
                onChange={(e) => setCommandQuery(e.target.value)}
                className="w-full px-3 py-3.5 text-xs bg-transparent border-0 focus:outline-none text-slate-900 placeholder:text-slate-400"
              />
              <kbd className="text-[10px] bg-white border border-slate-200 text-slate-500 font-mono px-1.5 py-0.5 rounded shadow-2xs">
                ESC
              </kbd>
            </div>

            <div className="max-h-80 overflow-y-auto p-2 space-y-1">
              {filteredCommands.length === 0 ? (
                <div className="p-6 text-center text-xs text-slate-400">
                  No operational desks found matching &quot;{commandQuery}&quot;.
                </div>
              ) : (
                filteredCommands.map((cmd) => (
                  <button
                    key={cmd.href}
                    onClick={() => {
                      router.push(cmd.href);
                      setShowCommandPalette(false);
                      setCommandQuery("");
                    }}
                    className="w-full flex items-center justify-between p-2.5 rounded-xl text-left hover:bg-slate-50 text-xs text-slate-700 transition-colors group"
                  >
                    <div className="flex items-center gap-2.5">
                      <ArrowRight className="w-3.5 h-3.5 text-slate-400 group-hover:text-teal-600 transition-colors" />
                      <span className="font-semibold text-slate-900">{cmd.name}</span>
                    </div>
                    <span className="text-[10px] font-mono text-slate-400 uppercase">
                      {cmd.category}
                    </span>
                  </button>
                ))
              )}
            </div>
          </div>
        </div>
      )}

      {/* Push Broadcast Modal */}
      {showBroadcastModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-slate-200 max-w-lg w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <Radio className="w-4 h-4 text-teal-600" />
                <h3 className="text-sm font-bold text-slate-900">
                  Dispatch Operational Push Notification
                </h3>
              </div>
              <button
                onClick={() => setShowBroadcastModal(false)}
                className="text-slate-400 hover:text-slate-700 text-sm"
              >
                ✕
              </button>
            </div>

            <p className="text-xs text-slate-500">
              Dispatches live push notifications via FCM / APNs Edge Function. Zero carrier SMS cost.
            </p>

            {broadcastSuccess && (
              <div className="p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs rounded-xl font-semibold">
                ✓ {broadcastSuccess}
              </div>
            )}

            <form onSubmit={handleSendBroadcast} className="space-y-3.5 text-xs">
              <div>
                <label className="block font-semibold text-slate-800 mb-1">Target Audience</label>
                <div className="grid grid-cols-3 gap-2">
                  {(["consumer", "partner", "admin"] as const).map((t) => (
                    <button
                      key={t}
                      type="button"
                      onClick={() => setBroadcastTarget(t)}
                      className={`py-1.5 px-2 rounded-xl font-semibold uppercase text-[10px] border transition-all ${
                        broadcastTarget === t
                          ? "bg-teal-600 text-white border-teal-600 shadow-sm"
                          : "bg-slate-50 text-slate-600 border-slate-200 hover:bg-slate-100"
                      }`}
                    >
                      {t}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block font-semibold text-slate-800 mb-1">Notification Title</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Kathmandu Valley Monsoon Advisory"
                  value={broadcastTitle}
                  onChange={(e) => setBroadcastTitle(e.target.value)}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-500 text-slate-900"
                />
              </div>

              <div>
                <label className="block font-semibold text-slate-800 mb-1">Message Body</label>
                <textarea
                  rows={3}
                  required
                  placeholder="e.g. Heavy rainfall reported in Ring Road corridor. Expect minor delivery delays."
                  value={broadcastBody}
                  onChange={(e) => setBroadcastBody(e.target.value)}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-500 text-slate-900"
                />
              </div>

              <div className="flex items-center justify-end gap-2.5 pt-3 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setShowBroadcastModal(false)}
                  className="px-4 py-2 font-semibold text-slate-600 hover:text-slate-900 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isBroadcasting}
                  className="px-5 py-2 bg-teal-600 hover:bg-teal-700 text-white font-bold rounded-xl shadow-sm transition-all flex items-center gap-1.5 disabled:opacity-50"
                >
                  <Send className="w-3.5 h-3.5" />
                  {isBroadcasting ? "Sending..." : "Dispatch Broadcast"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </header>
  );
}
