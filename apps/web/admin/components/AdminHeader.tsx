"use client";

import { useState, useEffect, useRef } from "react";
import Link from "next/link";
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
  Check,
  Radio,
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

export function AdminHeader({ title, subtitle, onRefresh, isRefreshing }: AdminHeaderProps) {
  const [showNotifications, setShowNotifications] = useState(false);
  const [notifications, setNotifications] = useState<AdminNotification[]>([]);
  const [showBroadcastModal, setShowBroadcastModal] = useState(false);
  const [broadcastTitle, setBroadcastTitle] = useState("");
  const [broadcastBody, setBroadcastBody] = useState("");
  const [broadcastTarget, setBroadcastTarget] = useState<"consumer" | "partner" | "admin">("consumer");
  const [broadcastSuccess, setBroadcastSuccess] = useState<string | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    fetchAdminNotifications().then(setNotifications);
  }, []);

  // Close dropdown on click outside
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setShowNotifications(false);
      }
    }
    if (showNotifications) {
      document.addEventListener("mousedown", handleClickOutside);
    }
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showNotifications]);

  const unreadCount = notifications.filter((n) => !n.read).length;

  const handleMarkAsRead = async (id: string) => {
    await markAdminNotificationRead(id);
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, read: true } : n))
    );
  };

  const handleMarkAllAsRead = async () => {
    await markAllAdminNotificationsRead();
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
  };

  const handleSendBroadcast = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!broadcastTitle || !broadcastBody) return;
    const res = await dispatchAdminPushBroadcast(broadcastTitle, broadcastBody, broadcastTarget);
    setBroadcastSuccess(res.message);
    setBroadcastTitle("");
    setBroadcastBody("");
    setTimeout(() => {
      setBroadcastSuccess(null);
      setShowBroadcastModal(false);
    }, 2500);
  };

  return (
    <header className="h-16 bg-white border-b border-border px-8 flex items-center justify-between sticky top-0 z-20">
      <div>
        <h1 className="text-lg font-bold text-ink tracking-tight">{title}</h1>
        {subtitle && <p className="text-xs text-ink-muted font-medium">{subtitle}</p>}
      </div>

      <div className="flex items-center gap-4">
        {/* System Health Status Pills */}
        <div className="hidden lg:flex items-center gap-2 bg-surface-subtle border border-border px-3 py-1.5 rounded-full text-xs">
          <div className="flex items-center gap-1.5 text-brand-700 font-medium">
            <span className="w-2 h-2 rounded-full bg-brand-500 animate-pulse" />
            <Activity className="w-3.5 h-3.5 text-brand-600" />
            <span>OCI Core Cluster</span>
          </div>
          <span className="text-slate-300">|</span>
          <div className="flex items-center gap-1.5 text-ink-muted font-medium">
            <Database className="w-3.5 h-3.5 text-slate-500" />
            <span>Postgres 16 RLS (20 Migrations)</span>
          </div>
        </div>

        {/* Refresh Action */}
        {onRefresh && (
          <button
            onClick={onRefresh}
            disabled={isRefreshing}
            className="p-2 text-ink-muted hover:text-ink hover:bg-slate-100 rounded-lg transition-colors disabled:opacity-50"
            title="Refresh live data"
          >
            <RefreshCw className={`w-4 h-4 ${isRefreshing ? "animate-spin text-brand-600" : ""}`} />
          </button>
        )}

        {/* Notifications Popover */}
        <div className="relative" ref={dropdownRef}>
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            className={`p-2 rounded-lg transition-colors relative ${
              showNotifications ? "bg-slate-100 text-brand-600" : "text-ink-muted hover:text-ink hover:bg-slate-100"
            }`}
            title="Operational Notifications"
          >
            <Bell className="w-4 h-4" />
            {unreadCount > 0 && (
              <span className="absolute -top-0.5 -right-0.5 min-w-[18px] h-[18px] px-1 bg-brand-600 text-white text-[10px] font-bold rounded-full flex items-center justify-center ring-2 ring-white">
                {unreadCount}
              </span>
            )}
          </button>

          {/* Flyout Popover */}
          {showNotifications && (
            <div className="absolute right-0 mt-3 w-96 bg-white rounded-2xl shadow-2xl border border-border z-50 overflow-hidden animate-in fade-in slide-in-from-top-2 duration-150">
              <div className="p-4 border-b border-border flex items-center justify-between bg-surface-subtle">
                <div className="flex items-center gap-2">
                  <span className="text-xs font-bold text-ink">Operational Alerts</span>
                  {unreadCount > 0 && (
                    <span className="bg-brand-50 text-brand-700 text-[10px] font-semibold px-2 py-0.5 rounded-full border border-brand-200">
                      {unreadCount} new
                    </span>
                  )}
                </div>
                <div className="flex items-center gap-2">
                  {unreadCount > 0 && (
                    <button
                      onClick={handleMarkAllAsRead}
                      className="text-[11px] text-brand-600 hover:text-brand-800 font-semibold transition-colors"
                    >
                      Mark all read
                    </button>
                  )}
                  <button
                    onClick={() => setShowNotifications(false)}
                    className="text-slate-400 hover:text-ink p-1 rounded-md"
                  >
                    <X className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>

              {/* Notification Items List */}
              <div className="max-h-96 overflow-y-auto divide-y divide-border">
                {notifications.length === 0 ? (
                  <div className="p-8 text-center text-ink-muted text-xs">
                    No active notifications. Systems operational.
                  </div>
                ) : (
                  notifications.map((notif) => (
                    <div
                      key={notif.id}
                      className={`p-3.5 transition-colors flex items-start gap-3 ${
                        notif.read ? "bg-white hover:bg-slate-50/60" : "bg-brand-50/30 hover:bg-brand-50/60"
                      }`}
                    >
                      <div className="mt-0.5 shrink-0">
                        {notif.type === "urgent" ? (
                          <div className="p-1.5 rounded-lg bg-rose-100 text-rose-700">
                            <AlertTriangle className="w-4 h-4" />
                          </div>
                        ) : notif.type === "payout" ? (
                          <div className="p-1.5 rounded-lg bg-amber-100 text-amber-700">
                            <Wallet className="w-4 h-4" />
                          </div>
                        ) : notif.type === "kyc" ? (
                          <div className="p-1.5 rounded-lg bg-blue-100 text-brand-700">
                            <ShieldCheck className="w-4 h-4" />
                          </div>
                        ) : (
                          <div className="p-1.5 rounded-lg bg-purple-100 text-purple-700">
                            <FileText className="w-4 h-4" />
                          </div>
                        )}
                      </div>

                      <div className="flex-1 min-w-0">
                        <div className="flex items-center justify-between">
                          <h4 className="text-xs font-semibold text-ink truncate">{notif.title}</h4>
                          <span className="text-[10px] text-ink-subtle ml-2 whitespace-nowrap">{notif.time}</span>
                        </div>
                        <p className="text-[11px] text-ink-muted mt-0.5 line-clamp-2">{notif.message}</p>

                        <div className="flex items-center gap-2 mt-2">
                          <Link
                            href={notif.actionUrl}
                            onClick={() => {
                              handleMarkAsRead(notif.id);
                              setShowNotifications(false);
                            }}
                            className="text-[10px] font-bold text-brand-600 hover:text-brand-800 bg-brand-50/80 px-2.5 py-1 rounded-md border border-brand-200 transition-colors inline-block"
                          >
                            {notif.actionLabel || "View Details"}
                          </Link>
                          {!notif.read && (
                            <button
                              onClick={() => handleMarkAsRead(notif.id)}
                              className="text-[10px] text-slate-400 hover:text-ink flex items-center gap-0.5"
                            >
                              <Check className="w-3 h-3" />
                              Dismiss
                            </button>
                          )}
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>

              {/* Bottom Quick Broadcast Action */}
              <div className="p-3 bg-surface-subtle border-t border-border flex items-center justify-between">
                <button
                  onClick={() => {
                    setShowNotifications(false);
                    setShowBroadcastModal(true);
                  }}
                  className="w-full py-1.5 px-3 bg-white hover:bg-slate-50 border border-border text-ink text-xs font-semibold rounded-lg transition-colors flex items-center justify-center gap-1.5"
                >
                  <Radio className="w-3.5 h-3.5 text-brand-600" />
                  Dispatch Push Broadcast
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Broadcast Push Notification Modal */}
      {showBroadcastModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 space-y-4 shadow-2xl border border-border text-xs">
            <div className="flex items-center justify-between border-b border-border pb-3">
              <div className="flex items-center gap-2">
                <Radio className="w-4 h-4 text-brand-600" />
                <h3 className="text-sm font-bold text-ink">Broadcast Push Notification</h3>
              </div>
              <button
                onClick={() => setShowBroadcastModal(false)}
                className="text-slate-400 hover:text-ink"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            {broadcastSuccess && (
              <div className="bg-emerald-50 border border-emerald-200 text-emerald-800 p-3 rounded-xl flex items-center gap-2 font-medium">
                <Check className="w-4 h-4 text-emerald-600" />
                <span>{broadcastSuccess}</span>
              </div>
            )}

            <form onSubmit={handleSendBroadcast} className="space-y-3">
              <div>
                <label className="block font-semibold text-ink mb-1">Target Audience</label>
                <div className="grid grid-cols-3 gap-2">
                  {(["consumer", "partner", "admin"] as const).map((t) => (
                    <button
                      key={t}
                      type="button"
                      onClick={() => setBroadcastTarget(t)}
                      className={`py-1.5 px-2 rounded-lg font-semibold uppercase text-[10px] border transition-all ${
                        broadcastTarget === t
                          ? "bg-slate-900 text-white border-slate-900"
                          : "bg-surface-subtle text-ink-muted border-border hover:bg-slate-100"
                      }`}
                    >
                      {t}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block font-semibold text-ink mb-1">Notification Title</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Flash Deal Alert or Heavy Rain Notice"
                  value={broadcastTitle}
                  onChange={(e) => setBroadcastTitle(e.target.value)}
                  className="w-full px-3 py-2 border border-border rounded-lg focus:ring-2 focus:ring-brand-600 focus:outline-none"
                />
              </div>

              <div>
                <label className="block font-semibold text-ink mb-1">Notification Body</label>
                <textarea
                  required
                  rows={3}
                  placeholder="e.g. Enjoy flat 20% off on all restaurants in Kathmandu valley until 8 PM."
                  value={broadcastBody}
                  onChange={(e) => setBroadcastBody(e.target.value)}
                  className="w-full px-3 py-2 border border-border rounded-lg focus:ring-2 focus:ring-brand-600 focus:outline-none resize-none"
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-border">
                <button
                  type="button"
                  onClick={() => setShowBroadcastModal(false)}
                  className="px-3 py-1.5 text-ink-muted hover:bg-slate-100 rounded-lg font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-1.5 bg-brand-600 hover:bg-brand-700 text-white rounded-lg font-bold flex items-center gap-1.5 shadow-md shadow-brand-900/30"
                >
                  <Send className="w-3.5 h-3.5" />
                  Dispatch (FCM & APNs)
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </header>
  );
}

