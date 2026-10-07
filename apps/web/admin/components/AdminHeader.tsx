"use client";

import { Bell, Search, Activity, Database, RefreshCw } from "lucide-react";

interface AdminHeaderProps {
  title: string;
  subtitle?: string;
  onRefresh?: () => void;
}

export function AdminHeader({ title, subtitle, onRefresh }: AdminHeaderProps) {
  return (
    <header className="h-16 bg-white border-b border-slate-200 px-8 flex items-center justify-between sticky top-0 z-20">
      <div>
        <h1 className="text-lg font-bold text-slate-900 tracking-tight">{title}</h1>
        {subtitle && <p className="text-xs text-slate-500 font-medium">{subtitle}</p>}
      </div>

      <div className="flex items-center gap-4">
        {/* System Health Status Pills */}
        <div className="hidden lg:flex items-center gap-2 bg-slate-50 border border-slate-200 px-3 py-1.5 rounded-full text-xs">
          <div className="flex items-center gap-1.5 text-emerald-700 font-medium">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            <Activity className="w-3.5 h-3.5 text-emerald-600" />
            <span>OCI Core Cluster</span>
          </div>
          <span className="text-slate-300">|</span>
          <div className="flex items-center gap-1.5 text-slate-600 font-medium">
            <Database className="w-3.5 h-3.5 text-slate-500" />
            <span>Postgres 16 RLS (20 Migrations)</span>
          </div>
        </div>

        {/* Refresh Action */}
        {onRefresh && (
          <button
            onClick={onRefresh}
            className="p-2 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-lg transition-colors"
            title="Refresh live data"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
        )}

        {/* Notifications */}
        <div className="relative">
          <button className="p-2 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-lg transition-colors relative">
            <Bell className="w-4 h-4" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-amber-500 rounded-full ring-2 ring-white" />
          </button>
        </div>
      </div>
    </header>
  );
}
