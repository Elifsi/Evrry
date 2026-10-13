"use client";

import { useRef, useState } from "react";
import { useSnapStore } from "@/lib/store/useSnapStore";
import { useChatStore } from "@/lib/store/useChatStore";
import { Snap, MemoryTab } from "@/lib/types/snap";
import {
  ChevronLeft,
  Trash2,
  X,
  Image as ImageIcon,
  Lock,
  Unlock,
  Send,
  BookOpen,
  ShieldCheck,
  Plus,
  MapPin,
  Calendar,
  Share2,
  EyeOff,
  Check,
} from "lucide-react";
import clsx from "clsx";

interface MemoriesScreenProps {
  onBack: () => void;
  onPick: (dataUrl: string) => void;
}

export default function MemoriesScreen({ onBack, onPick }: MemoriesScreenProps) {
  const snaps = useSnapStore((s) => s.snaps);
  const cameraRoll = useSnapStore((s) => s.cameraRoll);
  const lockerSnaps = useSnapStore((s) => s.lockerSnaps);
  const lockerPin = useSnapStore((s) => s.lockerPin);
  const isLockerUnlocked = useSnapStore((s) => s.isLockerUnlocked);
  const deleteSnap = useSnapStore((s) => s.deleteSnap);
  const deleteCameraRoll = useSnapStore((s) => s.deleteCameraRoll);
  const deleteLockerSnap = useSnapStore((s) => s.deleteLockerSnap);
  const unlockLocker = useSnapStore((s) => s.unlockLocker);
  const lockLocker = useSnapStore((s) => s.lockLocker);
  const moveToLocker = useSnapStore((s) => s.moveToLocker);
  const restoreFromLocker = useSnapStore((s) => s.restoreFromLocker);
  const addToCameraRoll = useSnapStore((s) => s.addToCameraRoll);

  const contacts = useChatStore((s) => s.contacts);
  const sendMessage = useChatStore((s) => s.sendMessage);
  const setMyStory = useChatStore((s) => s.setMyStory);

  const [activeTab, setActiveTab] = useState<MemoryTab>("snaps");
  const [selectedSnap, setSelectedSnap] = useState<Snap | null>(null);
  const [showSendSheet, setShowSendSheet] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Keypad PIN state for My Locker
  const [pinInput, setPinInput] = useState("");
  const [pinError, setPinError] = useState(false);

  const fileInputRef = useRef<HTMLInputElement>(null);

  function triggerToast(msg: string) {
    setToastMessage(msg);
    window.setTimeout(() => setToastMessage(null), 1800);
  }

  function handleKeypadPress(digit: string) {
    if (pinInput.length >= 4) return;
    const nextPin = pinInput + digit;
    setPinInput(nextPin);
    setPinError(false);

    if (nextPin.length === 4) {
      const ok = unlockLocker(nextPin);
      if (ok) {
        setPinInput("");
        triggerToast("My Locker Unlocked");
      } else {
        setPinError(true);
        window.setTimeout(() => {
          setPinInput("");
        }, 500);
      }
    }
  }

  function handleKeypadDelete() {
    setPinInput((prev) => prev.slice(0, -1));
    setPinError(false);
  }

  function handleFileImport(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    e.target.value = "";
    if (!file || !file.type.startsWith("image/")) return;
    const reader = new FileReader();
    reader.onload = () => {
      if (typeof reader.result === "string") {
        addToCameraRoll(reader.result, file.name);
        triggerToast("Imported to Camera Roll");
      }
    };
    reader.readAsDataURL(file);
  }

  function handleSendToFriend(contactId: string) {
    if (!selectedSnap) return;
    void sendMessage(contactId, selectedSnap.caption || "Photo Memory", selectedSnap.dataUrl);
    setShowSendSheet(false);
    triggerToast("Sent to friend!");
  }

  function handlePostStory() {
    if (!selectedSnap) return;
    setMyStory(selectedSnap.dataUrl);
    setSelectedSnap(null);
    triggerToast("Shared to your Story!");
  }

  function handleMoveToLocker() {
    if (!selectedSnap) return;
    moveToLocker(selectedSnap.id, activeTab === "snaps" ? "snaps" : "roll");
    setSelectedSnap(null);
    triggerToast("Moved to My Locker 🔒");
  }

  function handleRestoreFromLocker() {
    if (!selectedSnap) return;
    restoreFromLocker(selectedSnap.id);
    setSelectedSnap(null);
    triggerToast("Restored to Snaps");
  }

  function handleDeleteItem() {
    if (!selectedSnap) return;
    if (activeTab === "snaps") {
      deleteSnap(selectedSnap.id);
    } else if (activeTab === "roll") {
      deleteCameraRoll(selectedSnap.id);
    } else {
      deleteLockerSnap(selectedSnap.id);
    }
    setSelectedSnap(null);
    triggerToast("Deleted");
  }

  return (
    <div className="flex h-full min-h-screen flex-col bg-slate-950 px-4 pt-5 pb-8 text-white select-none">
      {/* Toast notification */}
      {toastMessage && (
        <div className="fixed top-5 left-1/2 z-50 -translate-x-1/2 animate-in fade-in slide-in-from-top-3 duration-200">
          <div className="rounded-full bg-brand px-4 py-2 text-xs font-semibold text-white shadow-xl">
            {toastMessage}
          </div>
        </div>
      )}

      {/* Header */}
      <div className="flex items-center justify-between pb-3">
        <div className="flex items-center gap-3">
          <button
            onClick={onBack}
            className="flex h-9 w-9 items-center justify-center rounded-full bg-white/10 text-white transition active:scale-95"
          >
            <ChevronLeft size={22} />
          </button>
          <h1 className="text-xl font-bold tracking-tight text-white">Memories</h1>
        </div>

        {activeTab === "roll" && (
          <button
            onClick={() => fileInputRef.current?.click()}
            className="flex items-center gap-1.5 rounded-full bg-white/10 px-3 py-1.5 text-xs font-medium text-white transition hover:bg-white/20 active:scale-95"
          >
            <Plus size={14} />
            <span>Import</span>
          </button>
        )}

        {activeTab === "locker" && isLockerUnlocked && (
          <button
            onClick={() => {
              lockLocker();
              triggerToast("My Locker Locked");
            }}
            className="flex items-center gap-1.5 rounded-full bg-rose-500/20 px-3 py-1.5 text-xs font-medium text-rose-300 transition hover:bg-rose-500/30 active:scale-95"
          >
            <Lock size={13} />
            <span>Lock Vault</span>
          </button>
        )}
      </div>

      <input ref={fileInputRef} type="file" accept="image/*" className="hidden" onChange={handleFileImport} />

      {/* 3 Tabs: Snaps, Camera Roll, My Locker */}
      <div className="mt-2 flex gap-1.5 rounded-xl bg-white/5 p-1 border border-white/10">
        <button
          onClick={() => setActiveTab("snaps")}
          className={clsx(
            "flex-1 rounded-lg py-2 text-xs font-semibold transition active:scale-98",
            activeTab === "snaps" ? "bg-white text-ink shadow" : "text-white/60 hover:text-white"
          )}
        >
          Snaps ({snaps.length})
        </button>
        <button
          onClick={() => setActiveTab("roll")}
          className={clsx(
            "flex-1 rounded-lg py-2 text-xs font-semibold transition active:scale-98",
            activeTab === "roll" ? "bg-white text-ink shadow" : "text-white/60 hover:text-white"
          )}
        >
          Camera Roll ({cameraRoll.length})
        </button>
        <button
          onClick={() => setActiveTab("locker")}
          className={clsx(
            "flex-1 flex items-center justify-center gap-1.5 rounded-lg py-2 text-xs font-semibold transition active:scale-98",
            activeTab === "locker" ? "bg-white text-ink shadow" : "text-white/60 hover:text-white"
          )}
        >
          {isLockerUnlocked ? <Unlock size={13} className="text-amber-500" /> : <Lock size={13} />}
          <span>My Locker</span>
        </button>
      </div>

      {/* Tab 1: Snaps */}
      {activeTab === "snaps" && (
        <div className="mt-4 flex-1 overflow-y-auto">
          {snaps.length === 0 ? (
            <div className="flex h-64 flex-col items-center justify-center gap-3 text-center text-white/40">
              <ImageIcon size={38} className="stroke-1 text-white/30" />
              <p className="text-sm">No snaps yet — photos and videos you save will appear here.</p>
            </div>
          ) : (
            <div className="grid grid-cols-3 gap-2 pb-12">
              {snaps.map((snap) => (
                <div
                  key={snap.id}
                  onClick={() => setSelectedSnap(snap)}
                  className="group relative aspect-[9/14] cursor-pointer overflow-hidden rounded-xl border border-white/10 bg-slate-900 transition active:scale-95"
                >
                  {/* eslint-disable-next-line @next/next/no-img-element */}
                  <img src={snap.dataUrl} alt={snap.caption || snap.filterName} className="h-full w-full object-cover" />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-80" />
                  <div className="absolute bottom-1.5 left-1.5 right-1.5">
                    <p className="truncate text-[10px] font-medium text-white/90">{snap.location || snap.filterName}</p>
                    <p className="text-[9px] text-white/50">{new Date(snap.createdAt).toLocaleDateString()}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Tab 2: Camera Roll */}
      {activeTab === "roll" && (
        <div className="mt-4 flex-1 overflow-y-auto">
          {cameraRoll.length === 0 ? (
            <div className="flex h-64 flex-col items-center justify-center gap-3 text-center text-white/40">
              <ImageIcon size={38} className="stroke-1 text-white/30" />
              <p className="text-sm">Camera roll is empty.</p>
              <button
                onClick={() => fileInputRef.current?.click()}
                className="mt-2 rounded-full bg-brand px-4 py-2 text-xs font-semibold text-white"
              >
                Choose from device
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-3 gap-2 pb-12">
              {cameraRoll.map((snap) => (
                <div
                  key={snap.id}
                  onClick={() => setSelectedSnap(snap)}
                  className="group relative aspect-[9/14] cursor-pointer overflow-hidden rounded-xl border border-white/10 bg-slate-900 transition active:scale-95"
                >
                  {/* eslint-disable-next-line @next/next/no-img-element */}
                  <img src={snap.dataUrl} alt={snap.caption || "Camera roll"} className="h-full w-full object-cover" />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-transparent to-transparent" />
                  <div className="absolute bottom-1.5 left-1.5 right-1.5">
                    <p className="truncate text-[10px] font-medium text-white/90">{snap.location || "Camera Roll"}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Tab 3: My Locker (Private Vault) */}
      {activeTab === "locker" && (
        <div className="mt-4 flex-1">
          {!isLockerUnlocked ? (
            <div className="flex h-full flex-col items-center justify-center px-4 py-8 text-center">
              <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-amber-500/10 text-amber-400 border border-amber-500/20">
                <ShieldCheck size={32} />
              </div>
              <h2 className="mt-4 text-lg font-bold text-white">My Locker</h2>
              <p className="mt-1 max-w-xs text-xs text-white/60">
                Enter your 4-digit passcode to access your locked photos. (Default: 1234)
              </p>

              {/* 4 Digit Indicators */}
              <div
                className={clsx(
                  "my-8 flex gap-4 transition-transform",
                  pinError && "animate-shake text-rose-500"
                )}
              >
                {[0, 1, 2, 3].map((idx) => (
                  <div
                    key={idx}
                    className={clsx(
                      "h-4 w-4 rounded-full border border-white/30 transition-all",
                      idx < pinInput.length ? (pinError ? "bg-rose-500" : "bg-white scale-110") : "bg-transparent"
                    )}
                  />
                ))}
              </div>

              {/* Keypad */}
              <div className="grid w-64 grid-cols-3 gap-3">
                {["1", "2", "3", "4", "5", "6", "7", "8", "9"].map((digit) => (
                  <button
                    key={digit}
                    onClick={() => handleKeypadPress(digit)}
                    className="flex h-14 w-full items-center justify-center rounded-2xl bg-white/10 text-xl font-medium text-white transition active:scale-90 hover:bg-white/20"
                  >
                    {digit}
                  </button>
                ))}
                <div />
                <button
                  onClick={() => handleKeypadPress("0")}
                  className="flex h-14 w-full items-center justify-center rounded-2xl bg-white/10 text-xl font-medium text-white transition active:scale-90 hover:bg-white/20"
                >
                  0
                </button>
                <button
                  onClick={handleKeypadDelete}
                  className="flex h-14 w-full items-center justify-center rounded-2xl bg-white/10 text-sm font-semibold text-white/70 transition active:scale-90 hover:bg-white/20"
                >
                  Delete
                </button>
              </div>

              {pinError && <p className="mt-4 text-xs font-medium text-rose-400">Incorrect Passcode</p>}
            </div>
          ) : (
            <div className="flex-1 overflow-y-auto">
              <div className="mb-3 flex items-center justify-between rounded-xl bg-amber-500/10 px-3.5 py-2 border border-amber-500/20">
                <div className="flex items-center gap-2 text-xs font-semibold text-amber-300">
                  <ShieldCheck size={16} />
                  <span>My Locker Active · Protected Storage</span>
                </div>
              </div>

              {lockerSnaps.length === 0 ? (
                <div className="flex h-56 flex-col items-center justify-center gap-2 text-center text-white/40">
                  <Lock size={32} className="stroke-1 text-white/30" />
                  <p className="text-xs">No locked items. Move photos here from Snaps or Camera Roll to keep them private.</p>
                </div>
              ) : (
                <div className="grid grid-cols-3 gap-2 pb-12">
                  {lockerSnaps.map((snap) => (
                    <div
                      key={snap.id}
                      onClick={() => setSelectedSnap(snap)}
                      className="group relative aspect-[9/14] cursor-pointer overflow-hidden rounded-xl border border-amber-500/30 bg-slate-900 transition active:scale-95"
                    >
                      {/* eslint-disable-next-line @next/next/no-img-element */}
                      <img src={snap.dataUrl} alt="Locked photo" className="h-full w-full object-cover" />
                      <div className="absolute top-1.5 right-1.5 rounded-full bg-black/60 p-1 text-amber-400">
                        <Lock size={11} />
                      </div>
                      <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent" />
                      <div className="absolute bottom-1.5 left-1.5 right-1.5">
                        <p className="truncate text-[10px] font-medium text-white/90">{snap.caption || "Locked Item"}</p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      )}

      {/* Fullscreen Photo Detail Modal */}
      {selectedSnap && (
        <div
          className="fixed inset-0 z-50 flex flex-col bg-black/95 p-4 animate-in fade-in duration-200"
          onClick={() => setSelectedSnap(null)}
        >
          <div className="flex items-center justify-between pb-3" onClick={(e) => e.stopPropagation()}>
            <button
              onClick={() => setSelectedSnap(null)}
              className="flex h-9 w-9 items-center justify-center rounded-full bg-white/10 text-white"
            >
              <X size={18} />
            </button>
            <div className="text-center">
              <p className="text-xs font-semibold text-white">{selectedSnap.filterName}</p>
              <p className="text-[10px] text-white/50">{new Date(selectedSnap.createdAt).toLocaleString()}</p>
            </div>
            <div className="w-9" />
          </div>

          <div
            className="relative flex-1 flex items-center justify-center overflow-hidden rounded-2xl"
            onClick={(e) => e.stopPropagation()}
          >
            {/* eslint-disable-next-line @next/next/no-img-element */}
            <img
              src={selectedSnap.dataUrl}
              alt="Memory detail"
              className="max-h-full max-w-full rounded-2xl object-contain shadow-2xl"
            />
            {selectedSnap.caption && (
              <div className="absolute bottom-4 inset-x-4 flex justify-center">
                <p className="rounded-xl bg-black/70 px-4 py-2 text-center text-xs font-medium text-white backdrop-blur-md">
                  {selectedSnap.caption}
                </p>
              </div>
            )}
          </div>

          {/* Action Row */}
          <div
            className="mt-4 flex items-center justify-around rounded-2xl bg-white/10 p-3 backdrop-blur-md border border-white/15"
            onClick={(e) => e.stopPropagation()}
          >
            <button
              onClick={() => setShowSendSheet(true)}
              className="flex flex-col items-center gap-1 text-white hover:text-brand-light transition active:scale-95"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-brand text-white shadow">
                <Send size={18} />
              </div>
              <span className="text-[10px] font-medium">Send Friend</span>
            </button>

            <button
              onClick={handlePostStory}
              className="flex flex-col items-center gap-1 text-white hover:text-brand-light transition active:scale-95"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-white/10 text-white">
                <BookOpen size={18} />
              </div>
              <span className="text-[10px] font-medium">Add Story</span>
            </button>

            {activeTab === "locker" ? (
              <button
                onClick={handleRestoreFromLocker}
                className="flex flex-col items-center gap-1 text-amber-300 hover:text-amber-200 transition active:scale-95"
              >
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-amber-500/20 text-amber-300">
                  <Unlock size={18} />
                </div>
                <span className="text-[10px] font-medium">Unhide</span>
              </button>
            ) : (
              <button
                onClick={handleMoveToLocker}
                className="flex flex-col items-center gap-1 text-white hover:text-amber-300 transition active:scale-95"
              >
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-white/10 text-white">
                  <Lock size={18} />
                </div>
                <span className="text-[10px] font-medium">To Locker</span>
              </button>
            )}

            <button
              onClick={handleDeleteItem}
              className="flex flex-col items-center gap-1 text-rose-400 hover:text-rose-300 transition active:scale-95"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-rose-500/20 text-rose-400">
                <Trash2 size={18} />
              </div>
              <span className="text-[10px] font-medium">Delete</span>
            </button>
          </div>
        </div>
      )}

      {/* Send to Friend Modal Sheet */}
      {showSendSheet && (
        <div
          className="fixed inset-0 z-50 flex items-end bg-black/70 animate-in fade-in duration-200"
          onClick={() => setShowSendSheet(false)}
        >
          <div
            className="w-full max-h-[75vh] flex flex-col rounded-t-3xl bg-slate-900 p-5 text-white shadow-2xl border-t border-white/15"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between pb-3 border-b border-white/10">
              <div>
                <h3 className="text-base font-bold text-white">Send to Friend</h3>
                <p className="text-xs text-white/60">Share directly via WhatsApp-grade encrypted chat</p>
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
                  onClick={() => handleSendToFriend(c.id)}
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
                  <button className="flex h-8 w-8 items-center justify-center rounded-full bg-brand text-white shadow">
                    <Send size={14} />
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
