"use client";

import { useRef } from "react";
import { X, Image as ImageIcon } from "lucide-react";
import clsx from "clsx";

export interface WallpaperOption {
  id: string;
  label: string;
  bgClass: string;
  style?: React.CSSProperties;
}

export const WALLPAPER_PRESETS: WallpaperOption[] = [
  { id: "default", label: "Default", bgClass: "bg-slate-50" },
  { id: "dusk", label: "Dusk", bgClass: "", style: { background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)" } },
  { id: "sunrise", label: "Sunrise", bgClass: "", style: { background: "linear-gradient(135deg, #ff6b6b 0%, #ffe66d 100%)" } },
  { id: "ocean", label: "Ocean", bgClass: "", style: { background: "linear-gradient(135deg, #00b4db 0%, #0083b0 100%)" } },
  { id: "forest", label: "Forest", bgClass: "", style: { background: "linear-gradient(135deg, #11998e 0%, #38ef7d 100%)" } },
  { id: "rose", label: "Rose", bgClass: "", style: { background: "linear-gradient(135deg, #ff758c 0%, #ff7eb3 100%)" } },
  { id: "midnight", label: "Midnight", bgClass: "", style: { background: "linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%)" } },
  { id: "peach", label: "Peach", bgClass: "", style: { background: "linear-gradient(135deg, #ffb347 0%, #ff8c94 100%)" } },
  { id: "lavender", label: "Lavender", bgClass: "bg-purple-100" },
  { id: "mint", label: "Mint", bgClass: "bg-emerald-50" },
  { id: "sand", label: "Sand", bgClass: "bg-amber-50" },
  { id: "slate", label: "Slate", bgClass: "bg-slate-200" },
];

export default function WallpaperPicker({
  currentId,
  onPick,
  onClose,
}: {
  currentId?: string;
  onPick: (id: string) => void;
  onClose: () => void;
}) {
  const fileInputRef = useRef<HTMLInputElement>(null);

  return (
    <div className="border-t border-black/5 bg-white p-3 shadow-lg">
      <div className="mb-2 flex items-center justify-between">
        <div>
          <span className="text-sm font-semibold text-ink">Chat Wallpaper</span>
          <p className="text-[11px] text-ink/40">Choose a custom photo or preset</p>
        </div>
        <button
          onClick={onClose}
          className="flex h-7 w-7 items-center justify-center rounded-full text-ink/40 hover:bg-black/5"
        >
          <X size={15} />
        </button>
      </div>

      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        className="hidden"
        onChange={(e) => {
          const file = e.target.files?.[0];
          e.target.value = "";
          if (!file || !file.type.startsWith("image/")) return;
          const reader = new FileReader();
          reader.onload = () => {
            if (typeof reader.result === "string") {
              onPick(reader.result);
            }
          };
          reader.readAsDataURL(file);
        }}
      />

      {/* WhatsApp-style "My Photos / Gallery" button */}
      <button
        onClick={() => fileInputRef.current?.click()}
        className="mb-2.5 flex w-full items-center gap-3 rounded-xl border border-black/10 bg-slate-50 p-2 text-left transition hover:bg-brandSoft/40 active:scale-[0.99]"
      >
        <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-brandSoft text-brand">
          <ImageIcon size={17} />
        </div>
        <div className="flex-1">
          <p className="text-xs font-semibold text-ink">My Photos</p>
          <p className="text-[10px] text-ink/40">Choose from device gallery</p>
        </div>
        <span className="text-xs text-ink/30">›</span>
      </button>

      <div className="mb-1">
        <span className="text-[10px] font-bold text-ink/40 uppercase tracking-wider">Presets</span>
      </div>

      <div className="grid max-h-40 grid-cols-4 gap-2 overflow-y-auto py-1">
        {WALLPAPER_PRESETS.map((p) => {
          const isSelected = currentId === p.id;
          return (
            <button
              key={p.id}
              onClick={() => onPick(p.id)}
              className={clsx(
                "relative flex h-14 flex-col justify-end overflow-hidden rounded-xl border p-1 text-left transition hover:scale-105 active:scale-95",
                p.bgClass,
                isSelected ? "border-brand ring-2 ring-brand/30" : "border-black/10"
              )}
              style={p.style}
            >
              <span className="truncate rounded-md bg-black/40 px-1 py-0.5 text-[9px] font-medium text-white backdrop-blur-[2px]">
                {p.label}
              </span>
            </button>
          );
        })}
      </div>
    </div>
  );
}
