"use client";

import { QrScanResult } from "@/lib/types/snap";
import { QrCode, CreditCard, User, Utensils, ExternalLink, X, ArrowRight } from "lucide-react";
import { useRouter } from "next/navigation";

interface QrScanCardProps {
  result: QrScanResult;
  onDismiss: () => void;
}

export default function QrScanCard({ result, onDismiss }: QrScanCardProps) {
  const router = useRouter();

  function handleAction() {
    if (result.type === "url" && result.target) {
      window.open(result.target, "_blank", "noopener,noreferrer");
    } else if (result.target) {
      router.push(result.target);
    }
    onDismiss();
  }

  function getIcon() {
    switch (result.type) {
      case "fonepay":
        return <CreditCard className="text-emerald-400" size={20} />;
      case "contact":
        return <User className="text-brand-light" size={20} />;
      case "restaurant":
        return <Utensils className="text-amber-400" size={20} />;
      case "url":
        return <ExternalLink className="text-cyan-400" size={20} />;
      default:
        return <QrCode className="text-white" size={20} />;
    }
  }

  return (
    <div className="animate-in fade-in slide-in-from-bottom-3 duration-250 flex items-center justify-between gap-3 rounded-2xl border border-white/25 bg-black/80 px-4 py-3 text-white shadow-2xl backdrop-blur-md">
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-white/10">
          {getIcon()}
        </div>
        <div className="min-w-0 pr-1">
          <p className="truncate text-xs font-semibold tracking-tight text-white">{result.title}</p>
          <p className="truncate text-[11px] text-white/70">{result.subtitle}</p>
        </div>
      </div>

      <div className="flex items-center gap-1.5 shrink-0">
        <button
          onClick={handleAction}
          className="flex items-center gap-1.5 rounded-full bg-brand px-3.5 py-1.5 text-xs font-semibold text-white shadow-sm transition active:scale-95"
        >
          <span>{result.actionLabel}</span>
          <ArrowRight size={13} />
        </button>
        <button
          onClick={onDismiss}
          className="flex h-7 w-7 items-center justify-center rounded-full text-white/50 hover:bg-white/10 hover:text-white"
          title="Dismiss"
        >
          <X size={15} />
        </button>
      </div>
    </div>
  );
}
