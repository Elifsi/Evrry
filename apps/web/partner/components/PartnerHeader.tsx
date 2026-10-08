"use client";

import { useState } from "react";
import {
  Volume2,
  VolumeX,
  Power,
  Bell,
  RefreshCw,
  Store,
  ChevronDown,
} from "lucide-react";
import { INITIAL_STORES } from "@/lib/partner/api";
import { PartnerStore } from "@/lib/partner/types";

interface PartnerHeaderProps {
  title: string;
  subtitle?: string;
  onRefresh?: () => void;
  isRefreshing?: boolean;
}

export function PartnerHeader({
  title,
  subtitle,
  onRefresh,
  isRefreshing = false,
}: PartnerHeaderProps) {
  const [isAccepting, setIsAccepting] = useState(true);
  const [soundEnabled, setSoundEnabled] = useState(true);
  const [selectedStore, setSelectedStore] = useState<PartnerStore>(INITIAL_STORES[0]);
  const [showStorePicker, setShowStorePicker] = useState(false);

  // Play audio chime test or alert
  const triggerAudioChime = () => {
    try {
      const AudioContextClass = window.AudioContext || (window as any).webkitAudioContext;
      if (!AudioContextClass) return;
      const ctx = new AudioContextClass();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = "sine";
      osc.frequency.setValueAtTime(880, ctx.currentTime); // A5 note
      osc.frequency.exponentialRampToValueAtTime(1320, ctx.currentTime + 0.15); // E6
      gain.gain.setValueAtTime(0.3, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.35);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + 0.35);
    } catch {
      // AudioContext policy
    }
  };

  const toggleSound = () => {
    const next = !soundEnabled;
    setSoundEnabled(next);
    if (next) triggerAudioChime();
  };

  return (
    <header className="h-16 shrink-0 bg-white border-b border-slate-200 px-6 flex items-center justify-between sticky top-0 z-20 shadow-2xs">
      {/* Title & Subtitle */}
      <div className="min-w-0 pr-4">
        <h1 className="text-base font-bold text-slate-900 tracking-tight truncate flex items-center gap-2">
          {title}
        </h1>
        {subtitle && (
          <p className="text-xs text-slate-500 truncate hidden sm:block">
            {subtitle}
          </p>
        )}
      </div>

      {/* Control Actions & Venue Indicator */}
      <div className="flex items-center gap-3 shrink-0">
        {/* Venue Switcher */}
        <div className="relative">
          <button
            type="button"
            onClick={() => setShowStorePicker(!showStorePicker)}
            className="flex items-center gap-2 px-3 py-1.5 rounded-xl border border-slate-200 hover:border-slate-300 bg-slate-50 text-xs font-semibold text-slate-800 transition-colors"
          >
            <Store className="w-3.5 h-3.5 text-brand-600" />
            <span className="truncate max-w-[140px] hidden md:inline">
              {selectedStore.name}
            </span>
            <ChevronDown className="w-3 h-3 text-slate-400" />
          </button>

          {showStorePicker && (
            <div className="absolute right-0 mt-2 w-64 bg-white border border-slate-200 rounded-xl shadow-lg p-2 z-50 animate-in fade-in zoom-in-95 duration-100">
              <p className="px-2.5 py-1 text-[10px] font-bold uppercase tracking-wider text-slate-400">
                Switch Venue / Desk
              </p>
              {INITIAL_STORES.map((s) => (
                <button
                  key={s.id}
                  onClick={() => {
                    setSelectedStore(s);
                    setShowStorePicker(false);
                  }}
                  className={`w-full text-left px-2.5 py-2 rounded-lg text-xs transition-colors flex items-center justify-between ${
                    s.id === selectedStore.id
                      ? "bg-brand-50 text-brand-700 font-bold"
                      : "text-slate-700 hover:bg-slate-50"
                  }`}
                >
                  <span className="truncate">{s.name}</span>
                  <span className="text-[10px] font-mono text-slate-400 uppercase">
                    {s.vertical}
                  </span>
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Accepting Orders Toggle Switch */}
        <button
          type="button"
          onClick={() => setIsAccepting(!isAccepting)}
          className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 border shadow-2xs ${
            isAccepting
              ? "bg-emerald-50 text-emerald-700 border-emerald-200 hover:bg-emerald-100"
              : "bg-amber-50 text-amber-700 border-amber-200 hover:bg-amber-100"
          }`}
          title={isAccepting ? "Store Online (Accepting Orders)" : "Store Paused (Busy)"}
        >
          <span
            className={`w-2 h-2 rounded-full ${
              isAccepting ? "bg-emerald-500 animate-pulse" : "bg-amber-500"
            }`}
          />
          <Power className="w-3.5 h-3.5" />
          <span className="hidden sm:inline">
            {isAccepting ? "Accepting Orders" : "Store Paused"}
          </span>
        </button>

        {/* Audio Chime Notification Toggle */}
        <button
          type="button"
          onClick={toggleSound}
          className={`p-2 rounded-xl border transition-colors ${
            soundEnabled
              ? "border-slate-200 text-slate-700 hover:bg-slate-50"
              : "border-slate-200 text-slate-400 bg-slate-50"
          }`}
          title={soundEnabled ? "Audio Chime ON (Click to Mute)" : "Audio Chime OFF"}
        >
          {soundEnabled ? (
            <Volume2 className="w-4 h-4 text-brand-600" />
          ) : (
            <VolumeX className="w-4 h-4 text-slate-400" />
          )}
        </button>

        {/* Refresh Button */}
        {onRefresh && (
          <button
            type="button"
            onClick={onRefresh}
            disabled={isRefreshing}
            className="p-2 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-600 hover:text-slate-900 transition-colors disabled:opacity-50"
            title="Refresh Data"
          >
            <RefreshCw
              className={`w-4 h-4 ${isRefreshing ? "animate-spin text-brand-600" : ""}`}
            />
          </button>
        )}
      </div>
    </header>
  );
}
