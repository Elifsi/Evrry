"use client";

import { useEffect, useRef, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { ChatMessageE2E, useChatStore } from "@/lib/store/useChatStore";
import { ChevronLeft, Send, Lock, Check, CheckCheck, Clock, Phone, Video, Paperclip, X, Smile, MapPin, Plus, Palette } from "lucide-react";
import WallpaperPicker, { WALLPAPER_PRESETS } from "@/components/chat/WallpaperPicker";
import clsx from "clsx";
import EmojiPicker from "@/components/chat/EmojiPicker";
import StickerSheet from "@/components/chat/StickerSheet";
import VoiceRecorder from "@/components/chat/VoiceRecorder";
import VoiceBubble from "@/components/chat/VoiceBubble";
import ContactProfileModal from "@/components/chat/ContactProfileModal";

// Stable reference: `?? []` inline in a selector would allocate a new array
// every render, which makes useSyncExternalStore think the snapshot changed
// on every call and spin into an infinite render loop.
const EMPTY_MESSAGES: ChatMessageE2E[] = [];

export default function ConversationPage() {
  const params = useParams<{ contactId: string }>();
  const router = useRouter();
  const contactId = params.contactId;

  const identity = useChatStore((s) => s.identity);
  const contact = useChatStore((s) => s.contacts.find((c) => c.id === contactId));
  const messages = useChatStore((s) => s.messagesByContact[contactId] ?? EMPTY_MESSAGES);
  const connectionStatus = useChatStore((s) => s.connectionStatus);
  const ensureIdentity = useChatStore((s) => s.ensureIdentity);
  const connect = useChatStore((s) => s.connect);
  const markRead = useChatStore((s) => s.markRead);
  const sendMessage = useChatStore((s) => s.sendMessage);
  const wallpaperByContact = useChatStore((s) => s.wallpaperByContact);
  const setWallpaper = useChatStore((s) => s.setWallpaper);
  const currentWallpaper = wallpaperByContact?.[contactId];
  const activePreset = WALLPAPER_PRESETS.find((p) => p.id === currentWallpaper);
  const call = useChatStore((s) => s.call);
  const startCall = useChatStore((s) => s.startCall);
  const onlineIds = useChatStore((s) => s.onlineIds);
  const isOnline = onlineIds.has(contactId);

  const [input, setInput] = useState("");
  const [pendingImage, setPendingImage] = useState<string | null>(null);
  const [showEmojiPicker, setShowEmojiPicker] = useState(false);
  const [showAttachmentMenu, setShowAttachmentMenu] = useState(false);
  const [showStickerSheet, setShowStickerSheet] = useState(false);
  const [showWallpaperPicker, setShowWallpaperPicker] = useState(false);
  const [showProfileModal, setShowProfileModal] = useState(false);
  const scrollRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  function insertEmoji(emoji: string) {
    const el = inputRef.current;
    if (el && document.activeElement === el) {
      const start = el.selectionStart ?? input.length;
      const end = el.selectionEnd ?? input.length;
      const next = input.slice(0, start) + emoji + input.slice(end);
      setInput(next);
      requestAnimationFrame(() => {
        el.focus();
        el.setSelectionRange(start + emoji.length, start + emoji.length);
      });
    } else {
      setInput((v) => v + emoji);
    }
  }

  function handleStickerPick(url: string) {
    void sendMessage(contactId, "", undefined, undefined, {
      type: "sticker",
      stickerUrl: url,
    });
    setShowStickerSheet(false);
  }

  function handleImageSelected(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    e.target.value = "";
    if (!file || !file.type.startsWith("image/")) return;
    const reader = new FileReader();
    reader.onload = () => {
      if (typeof reader.result === "string") setPendingImage(reader.result);
    };
    reader.readAsDataURL(file);
  }

  useEffect(() => {
    (async () => {
      await ensureIdentity();
      connect();
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    markRead(contactId);
  }, [contactId, markRead, messages.length]);

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: "smooth" });
  }, [messages.length]);

  if (!identity) return null;

  if (!contact) {
    return (
      <div className="px-5 pt-6">
        <button onClick={() => router.push("/chat")} className="inline-flex items-center gap-1 text-sm text-ink/50 hover:text-ink">
          <ChevronLeft size={16} /> Messages
        </button>
        <p className="mt-8 text-center text-sm text-ink/40">Contact not found — it may have been removed.</p>
      </div>
    );
  }

  return (
    <div className="flex h-[calc(100vh-5rem)] flex-col">
      <div className="flex items-center gap-3 border-b border-black/5 px-5 py-4">
        <button onClick={() => router.push("/chat")} className="text-ink/50 hover:text-ink">
          <ChevronLeft size={20} />
        </button>
        <button
          onClick={() => setShowProfileModal(true)}
          className="flex min-w-0 flex-1 items-center gap-3 text-left transition hover:opacity-85"
        >
          {contact.avatarDataUrl ? (
            // eslint-disable-next-line @next/next/no-img-element
            <img src={contact.avatarDataUrl} alt="" className="h-9 w-9 shrink-0 rounded-full object-cover" />
          ) : (
            <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-brandSoft text-sm font-semibold text-brand">
              {contact.username.slice(0, 1).toUpperCase()}
            </div>
          )}
          <div className="min-w-0 flex-1">
            <p className="truncate text-sm font-semibold text-ink">@{contact.username}</p>
            {isOnline ? (
              <p className="text-[11px] font-medium text-brand">online</p>
            ) : (
              <p className="inline-flex items-center gap-1 text-[11px] text-ink/40">
                <Lock size={9} /> End-to-end encrypted · {connectionStatus}
              </p>
            )}
          </div>
        </button>
        <button
          onClick={() => startCall(contactId, "audio")}
          disabled={!!call || connectionStatus !== "online"}
          className="flex h-9 w-9 items-center justify-center rounded-full text-ink/50 hover:bg-brandSoft hover:text-brand disabled:opacity-30"
          title="Voice call"
        >
          <Phone size={17} />
        </button>
        <button
          onClick={() => startCall(contactId, "video")}
          disabled={!!call || connectionStatus !== "online"}
          className="flex h-9 w-9 items-center justify-center rounded-full text-ink/50 hover:bg-brandSoft hover:text-brand disabled:opacity-30"
          title="Video call"
        >
          <Video size={17} />
        </button>
        <button
          onClick={() => {
            setShowWallpaperPicker((v) => !v);
            setShowEmojiPicker(false);
            setShowStickerSheet(false);
            setShowAttachmentMenu(false);
          }}
          className="flex h-9 w-9 items-center justify-center rounded-full text-ink/50 hover:bg-brandSoft hover:text-brand"
          title="Chat wallpaper"
        >
          <Palette size={17} />
        </button>
      </div>

      <div
        ref={scrollRef}
        style={
          currentWallpaper?.startsWith("data:") || currentWallpaper?.startsWith("http")
            ? {
                backgroundImage: `linear-gradient(rgba(0,0,0,0.18), rgba(0,0,0,0.18)), url(${currentWallpaper})`,
                backgroundSize: "cover",
                backgroundPosition: "center",
              }
            : activePreset?.style
        }
        className={clsx(
          "flex-1 space-y-2 overflow-y-auto px-5 py-4 transition-colors",
          !currentWallpaper?.startsWith("data:") && !currentWallpaper?.startsWith("http") && (activePreset?.bgClass || "bg-slate-50")
        )}
      >
        {messages.length === 0 ? (
          <p className="mt-10 text-center text-sm text-ink/35">
            Say hello — only you and @{contact.username} can read this.
          </p>
        ) : (
          messages.map((m) => (
            <div key={m.id} className={clsx("flex", m.direction === "out" ? "justify-end" : "justify-start")}>
              <div
                className={clsx(
                  "max-w-[80%] whitespace-pre-wrap rounded-2xl px-4 py-2 text-sm leading-relaxed",
                  m.type === "sticker" || m.stickerUrl
                    ? "bg-transparent p-1 shadow-none"
                    : m.direction === "out" ? "bg-brand text-white" : "bg-white text-ink shadow-sm"
                )}
              >
                {/* STICKER / ANIMATED EMOJI */}
                {(m.type === "sticker" || m.stickerUrl) && (
                  // eslint-disable-next-line @next/next/no-img-element
                  <img src={m.stickerUrl} alt="Sticker" className="max-h-32 max-w-32 object-contain" />
                )}
                {m.imageDataUrl && (
                  // eslint-disable-next-line @next/next/no-img-element
                  <img src={m.imageDataUrl} alt="" className={clsx("max-h-64 rounded-lg object-cover", (m.text || m.audioDataUrl) && "mb-2")} />
                )}
                {m.audioDataUrl && (
                  <VoiceBubble dataUrl={m.audioDataUrl} durationMs={m.audioDurationMs} tone={m.direction === "out" ? "out" : "in"} />
                )}

                {/* LIVE LOCATION CARD */}
                {m.locationMeta ? (
                  <div className="space-y-1.5 py-1 min-w-[190px]">
                    <div className="flex items-center gap-1.5 text-xs font-bold text-emerald-600">
                      <MapPin size={14} />
                      <span>LIVE LOCATION</span>
                    </div>
                    <div className={clsx("rounded-xl p-2.5 text-xs", m.direction === "out" ? "bg-white/15 text-white" : "bg-slate-50 text-ink")}>
                      <p className="font-semibold">{m.locationMeta.address}</p>
                      <p className="text-[11px] opacity-80 mt-0.5">● {m.locationMeta.expiresAtTimeText}</p>
                    </div>
                  </div>
                ) : (
                  m.text
                )}
                {m.direction === "out" && (
                  <span className="mt-0.5 flex items-center justify-end gap-1 text-[10px] text-white/75">
                    {m.status === "read" && (
                      <>
                        <CheckCheck size={11} className="text-sky-200" /> <span className="text-sky-200">read</span>
                      </>
                    )}
                    {m.status === "delivered" && (
                      <>
                        <CheckCheck size={11} /> delivered
                      </>
                    )}
                    {m.status === "queued_remote" && (
                      <>
                        <Check size={11} /> sent
                      </>
                    )}
                    {(m.status === "sending" || m.status === "queued_local") && (
                      <>
                        <Clock size={11} /> {m.status === "queued_local" ? "waiting for connection" : "sending…"}
                      </>
                    )}
                  </span>
                )}
              </div>
            </div>
          ))
        )}
      </div>

      <input ref={fileInputRef} type="file" accept="image/*" className="hidden" onChange={handleImageSelected} />

      {pendingImage && (
        <div className="mx-4 mb-2 flex items-center gap-2 rounded-xl border border-black/10 bg-white px-3 py-2 shadow-sm">
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img src={pendingImage} alt="Selected" className="h-10 w-10 rounded-lg object-cover" />
          <span className="flex-1 text-xs text-ink/50">Image attached — will send with your next message</span>
          <button
            onClick={() => setPendingImage(null)}
            className="flex h-6 w-6 items-center justify-center rounded-full bg-black/5 text-ink/60 hover:bg-black/10"
          >
            <X size={12} />
          </button>
        </div>
      )}

      {showEmojiPicker && <EmojiPicker onPick={insertEmoji} onClose={() => setShowEmojiPicker(false)} />}
      {showStickerSheet && <StickerSheet onPick={handleStickerPick} onClose={() => setShowStickerSheet(false)} />}
      {showWallpaperPicker && (
        <WallpaperPicker
          currentId={currentWallpaper}
          onPick={(id) => {
            setWallpaper(contactId, id);
            setShowWallpaperPicker(false);
          }}
          onClose={() => setShowWallpaperPicker(false)}
        />
      )}

      {/* ATTACHMENT QUICK MENU */}
      {showAttachmentMenu && (
        <div className="mx-4 mb-2 flex items-center justify-around rounded-2xl border border-black/10 bg-white p-2.5 shadow-lg animate-in fade-in">
          <button
            type="button"
            onClick={() => {
              fileInputRef.current?.click();
              setShowAttachmentMenu(false);
            }}
            className="flex flex-col items-center gap-1 text-[10px] font-bold text-ink/70 hover:text-brand"
          >
            <div className="flex h-9 w-9 items-center justify-center rounded-full bg-slate-100 text-ink/70 hover:bg-brandSoft hover:text-brand">
              <Paperclip size={16} />
            </div>
            <span>Photo</span>
          </button>
          <button
            type="button"
            onClick={() => {
              setShowStickerSheet(true);
              setShowAttachmentMenu(false);
            }}
            className="flex flex-col items-center gap-1 text-[10px] font-bold text-ink/70 hover:text-brand"
          >
            <div className="flex h-9 w-9 items-center justify-center rounded-full bg-amber-100 text-amber-700 hover:bg-amber-200 text-base">
              🗂️
            </div>
            <span>Sticker</span>
          </button>
          <button
            type="button"
            onClick={() => {
              void sendMessage(contactId, "Sharing Live Location (60 mins)", undefined, undefined, {
                locationMeta: {
                  address: "Jhamsikhel Cafe Hub, Lalitpur",
                  expiresAtTimeText: "Live for 60 mins",
                  isSharingActive: true,
                },
              });
              setShowAttachmentMenu(false);
            }}
            className="flex flex-col items-center gap-1 text-[10px] font-bold text-ink/70 hover:text-brand"
          >
            <div className="flex h-9 w-9 items-center justify-center rounded-full bg-emerald-100 text-emerald-600 hover:bg-emerald-200">
              <MapPin size={16} />
            </div>
            <span>Live Location</span>
          </button>
        </div>
      )}

      <div className="mx-4 mb-3 flex items-center gap-2">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            if (!input.trim() && !pendingImage) return;
            void sendMessage(contactId, input, pendingImage ?? undefined);
            setInput("");
            setPendingImage(null);
            setShowEmojiPicker(false);
            setShowStickerSheet(false);
            setShowWallpaperPicker(false);
            setShowAttachmentMenu(false);
          }}
          className="flex flex-1 items-center gap-2 rounded-full border border-black/10 bg-white px-3 py-2 shadow-sm"
        >
          <button
            type="button"
            onClick={() => {
              setShowEmojiPicker((v) => !v);
              setShowStickerSheet(false);
              setShowWallpaperPicker(false);
              setShowAttachmentMenu(false);
            }}
            className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-ink/50 hover:text-ink"
            title="Emoji"
          >
            <Smile size={17} />
          </button>
          <button
            type="button"
            onClick={() => {
              setShowStickerSheet((v) => !v);
              setShowEmojiPicker(false);
              setShowWallpaperPicker(false);
              setShowAttachmentMenu(false);
            }}
            className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-ink/50 hover:text-ink text-sm"
            title="Stickers & Animated Emojis"
          >
            🎭
          </button>
          <button
            type="button"
            onClick={() => {
              setShowAttachmentMenu((v) => !v);
              setShowEmojiPicker(false);
              setShowStickerSheet(false);
              setShowWallpaperPicker(false);
            }}
            className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-ink/50 hover:text-ink"
            title="Attach options"
          >
            <Plus size={18} />
          </button>
          <input
            ref={inputRef}
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onFocus={() => {
              setShowEmojiPicker(false);
              setShowAttachmentMenu(false);
            }}
            placeholder="Message…"
            className="flex-1 bg-transparent text-sm outline-none placeholder:text-ink/35"
          />
        </form>

        {/* Voice Note Mic (Beside send button, WhatsApp standard) */}
        <VoiceRecorder
          onSend={(dataUrl, durationMs) =>
            void sendMessage(contactId, "", undefined, { dataUrl, durationMs })
          }
        />

        {/* Send Button (Right beside voice note when text or image is present) */}
        {(input.trim() || pendingImage) && (
          <button
            type="button"
            onClick={() => {
              if (!input.trim() && !pendingImage) return;
              void sendMessage(contactId, input, pendingImage ?? undefined);
              setInput("");
              setPendingImage(null);
              setShowEmojiPicker(false);
              setShowAttachmentMenu(false);
            }}
            className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-brand text-white shadow-sm transition active:scale-95"
            title="Send"
          >
            <Send size={16} />
          </button>
        )}
      </div>

      <ContactProfileModal
        contact={contact}
        isOpen={showProfileModal}
        onClose={() => setShowProfileModal(false)}
        onVoiceCall={() => startCall(contactId, "audio")}
        onVideoCall={() => startCall(contactId, "video")}
        mediaCount={messages.filter((m) => !!m.imageDataUrl).length}
        sharedImages={messages.filter((m) => !!m.imageDataUrl).map((m) => m.imageDataUrl!)}
      />
    </div>
  );
}
