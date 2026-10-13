"use client";

import { useState } from "react";
import { useSnapStore } from "@/lib/store/useSnapStore";
import { useChatStore } from "@/lib/store/useChatStore";
import { X, Download, Send, BookOpen, Lock, Type, MapPin } from "lucide-react";

interface SnapReviewScreenProps {
  dataUrl: string;
  filterName: string;
  forStory: boolean;
  onClose: () => void;
}

export default function SnapReviewScreen({
  dataUrl,
  filterName,
  forStory,
  onClose,
}: SnapReviewScreenProps) {
  const addSnap = useSnapStore((s) => s.addSnap);
  const moveToLocker = useSnapStore((s) => s.moveToLocker);
  const setMyStory = useChatStore((s) => s.setMyStory);
  const contacts = useChatStore((s) => s.contacts);
  const sendMessage = useChatStore((s) => s.sendMessage);

  const [caption, setCaption] = useState("");
  const [isEditingCaption, setIsEditingCaption] = useState(false);
  const [showSendSheet, setShowSendSheet] = useState(false);
  const [confirmation, setConfirmation] = useState<string | null>(null);

  function flashAndClose(message: string) {
    setConfirmation(message);
    window.setTimeout(() => {
      setConfirmation(null);
      onClose();
    }, 750);
  }

  function handleSaveMemories() {
    addSnap(dataUrl, filterName, "Kathmandu, Nepal", caption);
    flashAndClose("Saved to Memories ✨");
  }

  function handleSaveToLocker() {
    const snap = addSnap(dataUrl, filterName, "Personal Vault", caption);
    moveToLocker(snap.id, "snaps");
    flashAndClose("Saved to My Locker 🔒");
  }

  function handlePostStory() {
    setMyStory(dataUrl);
    flashAndClose("Posted to your Story 📖");
  }

  function handleSendTo(contactId: string) {
    void sendMessage(contactId, caption || "Photo Snap", dataUrl);
    setShowSendSheet(false);
    flashAndClose("Sent to friend! 🚀");
  }

  return (
    <div className="fixed inset-0 z-40 flex flex-col bg-black select-none">
      {/* Photo Preview Container */}
      <div className="relative flex-1 flex items-center justify-center overflow-hidden">
        {/* eslint-disable-next-line @next/next/no-img-element */}
        <img src={dataUrl} alt="Captured preview" className="h-full w-full object-cover" />

        {/* Top Controls Overlay */}
        <div className="absolute top-4 inset-x-4 flex items-center justify-between">
          <button
            onClick={onClose}
            className="flex h-10 w-10 items-center justify-center rounded-full bg-black/50 text-white backdrop-blur-md transition active:scale-90"
            title="Close"
          >
            <X size={20} />
          </button>

          <div className="flex items-center gap-2">
            <button
              onClick={() => setIsEditingCaption((prev) => !prev)}
              className="flex h-10 w-10 items-center justify-center rounded-full bg-black/50 text-white backdrop-blur-md transition active:scale-90"
              title="Add text caption"
            >
              <Type size={18} />
            </button>

            <button
              onClick={handleSaveToLocker}
              className="flex h-10 w-10 items-center justify-center rounded-full bg-amber-500/30 text-amber-300 border border-amber-500/40 backdrop-blur-md transition active:scale-90"
              title="Save directly to My Locker"
            >
              <Lock size={17} />
            </button>
          </div>
        </div>

        {/* Caption Overlay */}
        {isEditingCaption ? (
          <div className="absolute inset-x-4 top-1/2 -translate-y-1/2">
            <div className="rounded-2xl bg-black/75 p-3 backdrop-blur-md border border-white/20">
              <input
                type="text"
                value={caption}
                onChange={(e) => setCaption(e.target.value)}
                placeholder="Add a caption..."
                autoFocus
                onKeyDown={(e) => {
                  if (e.key === "Enter") setIsEditingCaption(false);
                }}
                className="w-full bg-transparent text-center text-sm font-semibold text-white placeholder-white/50 outline-none"
              />
              <button
                onClick={() => setIsEditingCaption(false)}
                className="mt-2 w-full rounded-xl bg-white/20 py-1.5 text-xs font-semibold text-white hover:bg-white/30"
              >
                Done
              </button>
            </div>
          </div>
        ) : caption ? (
          <div
            onClick={() => setIsEditingCaption(true)}
            className="absolute bottom-24 inset-x-4 flex justify-center cursor-pointer"
          >
            <p className="rounded-2xl bg-black/60 px-5 py-2.5 text-center text-sm font-semibold text-white shadow-xl backdrop-blur-md border border-white/10">
              {caption}
            </p>
          </div>
        ) : (
          <div
            onClick={() => setIsEditingCaption(true)}
            className="absolute bottom-24 inset-x-8 flex justify-center cursor-pointer"
          >
            <p className="rounded-full bg-black/35 px-4 py-1.5 text-xs text-white/70 backdrop-blur-sm border border-white/10">
              Tap to add caption...
            </p>
          </div>
        )}

        {/* Flash Confirmation Banner */}
        {confirmation && (
          <div className="absolute inset-0 flex items-center justify-center bg-black/55 backdrop-blur-xs">
            <p className="rounded-full bg-white px-5 py-2.5 text-sm font-bold text-ink shadow-2xl animate-in zoom-in-95 duration-150">
              {confirmation}
            </p>
          </div>
        )}
      </div>

      {/* Bottom Actions Bar */}
      <div className="flex items-center justify-around bg-black/95 px-6 py-4 border-t border-white/10">
        {forStory ? (
          <button
            onClick={handlePostStory}
            className="flex items-center gap-2 rounded-full bg-brand px-6 py-3 text-sm font-bold text-white shadow-lg active:scale-95"
          >
            <BookOpen size={17} />
            <span>Post to Story</span>
          </button>
        ) : (
          <>
            <button
              onClick={handleSaveMemories}
              className="flex flex-col items-center gap-1 text-white/80 hover:text-white transition active:scale-90"
            >
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-white/10 shadow">
                <Download size={20} />
              </div>
              <span className="text-[11px] font-medium">Save</span>
            </button>

            <button
              onClick={handlePostStory}
              className="flex flex-col items-center gap-1 text-white/80 hover:text-white transition active:scale-90"
            >
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-white/10 shadow">
                <BookOpen size={20} />
              </div>
              <span className="text-[11px] font-medium">Story</span>
            </button>

            <button
              onClick={() => setShowSendSheet(true)}
              className="flex items-center gap-2 rounded-full bg-brand px-6 py-3 text-sm font-bold text-white shadow-lg transition active:scale-95"
            >
              <span>Send To</span>
              <Send size={16} />
            </button>
          </>
        )}
      </div>

      {/* Send to Friend Bottom Sheet */}
      {showSendSheet && (
        <div
          className="fixed inset-0 z-50 flex items-end bg-black/75 animate-in fade-in duration-200"
          onClick={() => setShowSendSheet(false)}
        >
          <div
            className="w-full max-h-[75vh] flex flex-col rounded-t-3xl bg-slate-900 p-5 text-white shadow-2xl border-t border-white/20"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between pb-3 border-b border-white/10">
              <div>
                <h3 className="text-base font-bold text-white">Send to Friend</h3>
                <p className="text-xs text-white/60">Share via WhatsApp-grade end-to-end encrypted chat</p>
              </div>
              <button
                onClick={() => setShowSendSheet(false)}
                className="flex h-8 w-8 items-center justify-center rounded-full bg-white/10 text-white"
              >
                <X size={16} />
              </button>
            </div>

            <div className="mt-3 flex-1 overflow-y-auto space-y-2">
              {contacts.map((c) => (
                <div
                  key={c.id}
                  onClick={() => handleSendTo(c.id)}
                  className="flex items-center justify-between p-3 rounded-2xl bg-white/5 hover:bg-white/10 cursor-pointer transition active:scale-98 border border-white/5"
                >
                  <div className="flex items-center gap-3">
                    <div className="relative h-11 w-11 rounded-full bg-brand/30 flex items-center justify-center text-sm font-bold text-white">
                      {c.name.charAt(0)}
                      {c.online && (
                        <div className="absolute bottom-0 right-0 h-3 w-3 rounded-full bg-emerald-500 border-2 border-slate-900" />
                      )}
                    </div>
                    <div>
                      <p className="text-sm font-semibold text-white">{c.name}</p>
                      <p className="text-xs text-white/50">{c.handle || "Evrry Friend"}</p>
                    </div>
                  </div>
                  <div className="flex h-8 w-8 items-center justify-center rounded-full bg-brand text-white shadow">
                    <Send size={14} />
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
