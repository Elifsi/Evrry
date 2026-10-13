"use client";

import { useState } from "react";
import { X } from "lucide-react";
import clsx from "clsx";

// Open-source Twemoji SVG Stickers (CC BY 4.0)
const STICKERS = [
  "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f600.svg",
  "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f602.svg",
  "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f970.svg",
  "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f525.svg",
  "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f389.svg",
  "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f499.svg",
  "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f44d.svg",
  "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f64f.svg",
  "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f4af.svg",
  "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/2764.svg",
  "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f308.svg",
  "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f381.svg",
];

// Open-source Noto Animated Emojis (Apache 2.0)
const ANIMATED_EMOJIS = [
  "https://fonts.gstatic.com/s/e/notoemoji/latest/1f600/512.gif",
  "https://fonts.gstatic.com/s/e/notoemoji/latest/1f602/512.gif",
  "https://fonts.gstatic.com/s/e/notoemoji/latest/1f525/512.gif",
  "https://fonts.gstatic.com/s/e/notoemoji/latest/1f389/512.gif",
  "https://fonts.gstatic.com/s/e/notoemoji/latest/1f499/512.gif",
  "https://fonts.gstatic.com/s/e/notoemoji/latest/1f44d/512.gif",
  "https://fonts.gstatic.com/s/e/notoemoji/latest/1f64f/512.gif",
  "https://fonts.gstatic.com/s/e/notoemoji/latest/2764_fe0f/512.gif",
];

export default function StickerSheet({
  onPick,
  onClose,
}: {
  onPick: (url: string) => void;
  onClose: () => void;
}) {
  const [tab, setTab] = useState<"stickers" | "animated">("stickers");
  const list = tab === "stickers" ? STICKERS : ANIMATED_EMOJIS;

  return (
    <div className="border-t border-black/5 bg-white p-2">
      <div className="flex items-center justify-between px-2 pt-1 pb-2">
        <div className="flex gap-2">
          <button
            onClick={() => setTab("stickers")}
            className={clsx(
              "rounded-full px-3 py-1 text-xs font-semibold transition",
              tab === "stickers" ? "bg-brandSoft text-brand" : "text-ink/50 hover:text-ink"
            )}
          >
            🗂️ Stickers
          </button>
          <button
            onClick={() => setTab("animated")}
            className={clsx(
              "rounded-full px-3 py-1 text-xs font-semibold transition",
              tab === "animated" ? "bg-brandSoft text-brand" : "text-ink/50 hover:text-ink"
            )}
          >
            🎭 Animated
          </button>
        </div>
        <button
          onClick={onClose}
          className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full text-ink/40 hover:bg-black/5"
        >
          <X size={15} />
        </button>
      </div>

      <div className="grid max-h-48 grid-cols-4 gap-2 overflow-y-auto px-2 py-1">
        {list.map((url, i) => (
          <button
            key={url + i}
            onClick={() => onPick(url)}
            className="flex h-16 w-16 items-center justify-center rounded-xl bg-slate-50 p-2 transition hover:scale-105 hover:bg-slate-100 active:scale-95"
          >
            {/* eslint-disable-next-line @next/next/no-img-element */}
            <img src={url} alt="Sticker" className="max-h-full max-w-full object-contain" />
          </button>
        ))}
      </div>
    </div>
  );
}
