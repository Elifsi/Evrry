"use client";

import { useState } from "react";
import { useAppStore } from "@/lib/store/useAppStore";
import AccordionSection from "@/components/AccordionSection";
import { Settings, FileText, Shield, Info, Trash2, Download, Check, X, HardDrive, Cloud, RefreshCw, Key, CheckCircle2, Copy } from "lucide-react";
import clsx from "clsx";

function Toggle({ on, onChange }: { on: boolean; onChange: (v: boolean) => void }) {
  return (
    <button
      onClick={() => onChange(!on)}
      className={clsx("h-6 w-11 shrink-0 rounded-full p-0.5 transition", on ? "bg-brand" : "bg-ink/15")}
    >
      <span className={clsx("block h-5 w-5 rounded-full bg-white transition", on && "translate-x-5")} />
    </button>
  );
}

export default function SettingsSection({ open, onToggle }: { open: boolean; onToggle: () => void }) {
  const personalizationEnabled = useAppStore((s) => s.personalizationEnabled);
  const setPersonalizationEnabled = useAppStore((s) => s.setPersonalizationEnabled);
  const clearChat = useAppStore((s) => s.clearChat);

  // WeChat-style privacy setting: allow friends to find you by phone number
  const [findByPhone, setFindByPhone] = useState(true);

  // Modals: terms, privacy, about, delete, backup
  const [activeModal, setActiveModal] = useState<"terms" | "privacy" | "about" | "delete" | "backup" | null>(null);
  const [cacheCleared, setCacheCleared] = useState(false);
  const [exportNotice, setExportNotice] = useState<string | null>(null);

  // Google Drive E2EE Chat Backup state
  const [backupFrequency, setBackupFrequency] = useState<"Daily" | "Weekly" | "Monthly" | "Off">("Daily");
  const [includeVideos, setIncludeVideos] = useState(false);
  const [backupOverWifiOnly, setBackupOverWifiOnly] = useState(true);
  const [lastBackupText, setLastBackupText] = useState("Today, 11:42 AM");
  const [backupSizeText, setBackupSizeText] = useState("43.1 MB");
  const [backupProgress, setBackupProgress] = useState<number | null>(null);
  const [backupStage, setBackupStage] = useState<string>("");
  const [restoreProgress, setRestoreProgress] = useState<number | null>(null);
  const [restoreStage, setRestoreStage] = useState<string>("");
  const [keyCopied, setKeyCopied] = useState(false);

  // 30-Day Industry Standard Account Deletion state
  const [deletionScheduled, setDeletionScheduled] = useState(false);
  const [deletionDateText] = useState(() => {
    const d = new Date();
    d.setDate(d.getDate() + 30);
    return d.toLocaleDateString("en-US", { month: "long", day: "numeric", year: "numeric" });
  });

  const handleClearCache = () => {
    setCacheCleared(true);
    setTimeout(() => setCacheCleared(false), 3000);
  };

  const handleExportData = () => {
    setExportNotice("Export archive generated: evrry_account_export.json");
    setTimeout(() => setExportNotice(null), 4000);
  };

  const runBackup = () => {
    setBackupProgress(10);
    setBackupStage("Exporting local SQLite E2EE database...");
    setTimeout(() => {
      setBackupProgress(40);
      setBackupStage("Encrypting with 64-digit zero-knowledge key...");
      setTimeout(() => {
        setBackupProgress(80);
        setBackupStage("Uploading encrypted archive to Google Drive...");
        setTimeout(() => {
          setBackupProgress(100);
          setBackupStage("Backup successful!");
          setTimeout(() => {
            setBackupProgress(null);
            setLastBackupText("Just now");
            setBackupSizeText(includeVideos ? "89.4 MB" : "43.1 MB");
          }, 600);
        }, 700);
      }, 600);
    }, 500);
  };

  const runRestore = () => {
    setRestoreProgress(15);
    setRestoreStage("Connecting to Google Drive...");
    setTimeout(() => {
      setRestoreProgress(50);
      setRestoreStage("Downloading encrypted archive...");
      setTimeout(() => {
        setRestoreProgress(85);
        setRestoreStage("Decrypting keys & restoring chat threads...");
        setTimeout(() => {
          setRestoreProgress(100);
          setRestoreStage("Restore complete! All chats restored.");
          setTimeout(() => {
            setRestoreProgress(null);
          }, 600);
        }, 700);
      }, 600);
    }, 500);
  };

  return (
    <>
      <AccordionSection icon={Settings} title="Settings, Legal & Data Privacy" open={open} onToggle={onToggle}>
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-ink">Personalization</p>
              <p className="text-[11px] text-ink/40">Lets the concierge use your AI memory and preferences.</p>
            </div>
            <Toggle on={personalizationEnabled} onChange={setPersonalizationEnabled} />
          </div>

          <div className="border-t border-black/5 pt-3.5">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-ink">Find me by phone number</p>
                <p className="text-[11px] text-ink/40">
                  WeChat-style: Friends who have your number can discover your @handle.
                </p>
              </div>
              <Toggle on={findByPhone} onChange={setFindByPhone} />
            </div>
          </div>

          <div className="rounded-xl bg-paper border border-black/5 p-3 text-[11px] text-ink/60">
            <p className="font-semibold text-ink">🔒 Phone Privacy: Always Masked</p>
            <p className="mt-0.5 text-ink/45">
              Your phone number is never visible in group chats, peer threads, or to delivery riders. Calls are routed through encrypted In-App VoIP.
            </p>
          </div>

          {/* LEGAL & COMPANY POLICIES */}
          <div className="border-t border-black/5 pt-3.5 space-y-2">
            <p className="text-xs font-bold uppercase tracking-wider text-ink/40">Legal & Company</p>
            
            <button
              onClick={() => setActiveModal("terms")}
              className="w-full flex items-center justify-between p-2.5 rounded-xl border border-black/5 hover:bg-black/5 transition text-left"
            >
              <div className="flex items-center gap-2.5">
                <FileText size={15} className="text-brand" />
                <div>
                  <p className="text-xs font-semibold text-ink">Terms of Service</p>
                  <p className="text-[10px] text-ink/40">User agreement, escrow payments & community rules</p>
                </div>
              </div>
              <span className="text-[11px] font-bold text-brand">View</span>
            </button>

            <button
              onClick={() => setActiveModal("privacy")}
              className="w-full flex items-center justify-between p-2.5 rounded-xl border border-black/5 hover:bg-black/5 transition text-left"
            >
              <div className="flex items-center gap-2.5">
                <Shield size={15} className="text-emerald-600" />
                <div>
                  <p className="text-xs font-semibold text-ink">Privacy Policy</p>
                  <p className="text-[10px] text-ink/40">Signal E2EE zero-knowledge & Nepal Privacy Act 2075</p>
                </div>
              </div>
              <span className="text-[11px] font-bold text-brand">View</span>
            </button>

            <button
              onClick={() => setActiveModal("about")}
              className="w-full flex items-center justify-between p-2.5 rounded-xl border border-black/5 hover:bg-black/5 transition text-left"
            >
              <div className="flex items-center gap-2.5">
                <Info size={15} className="text-purple-600" />
                <div>
                  <p className="text-xs font-semibold text-ink">About Evrry</p>
                  <p className="text-[10px] text-ink/40">Elifsi Technologies Private Limited • v2.4.0</p>
                </div>
              </div>
              <span className="text-[11px] font-bold text-brand">Info</span>
            </button>
          </div>

          {/* DATA PRIVACY & STORAGE (DELETE DATA) */}
          <div className="border-t border-black/5 pt-3.5 space-y-2.5">
            <p className="text-xs font-bold uppercase tracking-wider text-ink/40">Data Management & Erasure</p>

            {/* GOOGLE DRIVE CHAT BACKUP & RESTORE */}
            <div className="flex items-center justify-between p-2.5 rounded-xl border border-emerald-200 bg-emerald-50/70">
              <div className="flex items-center gap-2.5">
                <Cloud size={16} className="text-emerald-600" />
                <div>
                  <div className="flex items-center gap-1.5">
                    <p className="text-xs font-semibold text-ink">Google Drive E2EE Chat Backup</p>
                    <span className="rounded bg-emerald-100 px-1 py-0.2 text-[9px] font-bold text-emerald-800">64-digit Key</span>
                  </div>
                  <p className="text-[10px] text-ink/40">Auto: {backupFrequency} • Last: {lastBackupText} ({backupSizeText})</p>
                </div>
              </div>
              <button
                onClick={() => setActiveModal("backup")}
                className="px-2.5 py-1 rounded-lg bg-emerald-600 text-[11px] font-bold text-white hover:bg-emerald-700 transition"
              >
                Manage
              </button>
            </div>

            <div className="flex items-center justify-between p-2.5 rounded-xl border border-black/5 bg-slate-50">
              <div className="flex items-center gap-2.5">
                <HardDrive size={15} className="text-ink/60" />
                <div>
                  <p className="text-xs font-semibold text-ink">Local Cache (Media & Data)</p>
                  <p className="text-[10px] text-ink/40">{cacheCleared ? "0.0 MB (Clean)" : "14.8 MB temporary files"}</p>
                </div>
              </div>
              <button
                onClick={handleClearCache}
                className="px-2.5 py-1 rounded-lg border border-black/10 bg-white text-[11px] font-bold text-ink hover:bg-slate-100 transition"
              >
                {cacheCleared ? "Cleared ✓" : "Clear Cache"}
              </button>
            </div>

            <div className="flex items-center justify-between p-2.5 rounded-xl border border-black/5 bg-slate-50">
              <div className="flex items-center gap-2.5">
                <Download size={15} className="text-brand" />
                <div>
                  <p className="text-xs font-semibold text-ink">Download Account Archive</p>
                  <p className="text-[10px] text-ink/40">Export profile, orders & ledger in JSON</p>
                </div>
              </div>
              <button
                onClick={handleExportData}
                className="px-2.5 py-1 rounded-lg bg-brand text-[11px] font-bold text-white hover:bg-brand/90 transition"
              >
                Export JSON
              </button>
            </div>

            {exportNotice && (
              <p className="text-[11px] font-semibold text-emerald-600 bg-emerald-50 p-2 rounded-lg border border-emerald-100">
                {exportNotice}
              </p>
            )}

            <button
              onClick={clearChat}
              className="w-full rounded-lg border border-black/10 px-3 py-2 text-left text-xs font-medium text-ink/70 hover:border-red-300 hover:text-red-500 transition"
            >
              Clear AI Concierge Memory
            </button>

            {/* DANGER ZONE: 30-DAY ACCOUNT DELETION */}
            {deletionScheduled ? (
              <div className="rounded-xl border border-rose-300 bg-rose-50 p-3 text-xs">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-1.5 font-bold text-rose-800">
                    <Trash2 size={14} />
                    <span>Account Deletion Scheduled</span>
                  </div>
                  <span className="text-[10px] font-bold text-rose-700 bg-rose-200/60 px-1.5 py-0.5 rounded">30-Day Grace</span>
                </div>
                <p className="mt-1 text-[11px] text-rose-700/80 leading-relaxed">
                  Your account is de-activated. All personal data, keys, and cloud backups will be permanently purged on <strong>{deletionDateText}</strong>.
                </p>
                <button
                  onClick={() => {
                    setDeletionScheduled(false);
                    alert("Account deletion request cancelled. Your account remains fully active.");
                  }}
                  className="mt-2 w-full py-1.5 rounded-lg bg-white border border-rose-200 text-rose-700 text-xs font-bold hover:bg-rose-100 transition shadow-sm"
                >
                  Cancel Deletion Request & Keep Account
                </button>
              </div>
            ) : (
              <button
                onClick={() => setActiveModal("delete")}
                className="w-full rounded-xl bg-rose-50 border border-rose-200 px-3 py-2.5 text-center text-xs font-bold text-rose-700 hover:bg-rose-100 transition flex items-center justify-center gap-1.5"
              >
                <Trash2 size={13} />
                <span>Delete Account (30-Day Industry Standard Policy)</span>
              </button>
            )}
          </div>
        </div>
      </AccordionSection>

      {/* MODALS */}
      {activeModal === "terms" && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm animate-in fade-in">
          <div className="relative w-full max-w-sm rounded-2xl bg-white p-5 shadow-2xl max-h-[85vh] flex flex-col">
            <div className="flex items-center justify-between border-b border-black/5 pb-3">
              <div>
                <h3 className="text-base font-bold text-ink">Terms of Service</h3>
                <p className="text-[11px] text-ink/40">Elifsi Technologies Private Limited</p>
              </div>
              <button onClick={() => setActiveModal(null)} className="p-1 rounded-full text-ink/40 hover:bg-black/5">
                <X size={18} />
              </button>
            </div>
            <div className="overflow-y-auto space-y-3 py-3 text-xs text-ink/70 pr-1">
              <div>
                <p className="font-bold text-ink">1. Acceptance of Terms</p>
                <p className="text-[11px] text-ink/50 mt-0.5">By accessing Evrry Super App, you agree to comply with all platform policies and Nepal legal regulations.</p>
              </div>
              <div>
                <p className="font-bold text-ink">2. Payments & Escrow</p>
                <p className="text-[11px] text-ink/50 mt-0.5">Payments via Fonepay, eSewa, Khalti, or COD are secured and held until order fulfillment.</p>
              </div>
              <div>
                <p className="font-bold text-ink">3. Messaging & VoIP Conduct</p>
                <p className="text-[11px] text-ink/50 mt-0.5">Spamming, harassment, and unauthorized automated scraping are strictly prohibited.</p>
              </div>
              <div>
                <p className="font-bold text-ink">4. Governing Jurisdiction</p>
                <p className="text-[11px] text-ink/50 mt-0.5">Governed by the laws of Nepal. Exclusive jurisdiction: Kathmandu District Court.</p>
              </div>
            </div>
            <button onClick={() => setActiveModal(null)} className="w-full mt-2 py-2 rounded-xl bg-brand text-white text-xs font-bold">
              I Understand
            </button>
          </div>
        </div>
      )}

      {activeModal === "privacy" && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm animate-in fade-in">
          <div className="relative w-full max-w-sm rounded-2xl bg-white p-5 shadow-2xl max-h-[85vh] flex flex-col">
            <div className="flex items-center justify-between border-b border-black/5 pb-3">
              <div>
                <h3 className="text-base font-bold text-ink">Privacy Policy</h3>
                <p className="text-[11px] text-emerald-600 font-semibold">Nepal Privacy Act 2075 & GDPR Aligned</p>
              </div>
              <button onClick={() => setActiveModal(null)} className="p-1 rounded-full text-ink/40 hover:bg-black/5">
                <X size={18} />
              </button>
            </div>
            <div className="overflow-y-auto space-y-3 py-3 text-xs text-ink/70 pr-1">
              <div>
                <p className="font-bold text-ink">1. Zero-Knowledge E2EE</p>
                <p className="text-[11px] text-ink/50 mt-0.5">All 1-to-1 chats, group chats, and audio/video calls are encrypted with Signal Protocol. Evrry cannot read your content.</p>
              </div>
              <div>
                <p className="font-bold text-ink">2. Phone Number Stealth</p>
                <p className="text-[11px] text-ink/50 mt-0.5">Your phone number is masked from riders, kitchens, and group threads.</p>
              </div>
              <div>
                <p className="font-bold text-ink">3. Private Cloud Backups</p>
                <p className="text-[11px] text-ink/50 mt-0.5">Chat backups are encrypted and saved to your personal Google Drive or iCloud, never stored unencrypted on company servers.</p>
              </div>
              <div>
                <p className="font-bold text-ink">4. Right to Erasure</p>
                <p className="text-[11px] text-ink/50 mt-0.5">You hold full rights under Nepal Privacy Act 2075 to export your data or delete your account at any time.</p>
              </div>
            </div>
            <button onClick={() => setActiveModal(null)} className="w-full mt-2 py-2 rounded-xl bg-brand text-white text-xs font-bold">
              Accept Policy
            </button>
          </div>
        </div>
      )}

      {activeModal === "about" && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm animate-in fade-in">
          <div className="relative w-full max-w-sm rounded-2xl bg-white p-5 shadow-2xl text-center">
            <button onClick={() => setActiveModal(null)} className="absolute right-4 top-4 p-1 rounded-full text-ink/40 hover:bg-black/5">
              <X size={18} />
            </button>
            <div className="mx-auto w-12 h-12 rounded-full bg-brandSoft flex items-center justify-center text-brand mb-2">
              <Info size={24} />
            </div>
            <h3 className="text-base font-bold text-ink">Evrry Super App</h3>
            <p className="text-xs font-semibold text-brand">v2.4.0 (Build 2026.10)</p>
            <p className="text-xs text-ink/60 mt-3 leading-relaxed">
              Developed with ❤️ by<br />
              <strong className="text-ink">Elifsi Technologies Private Limited</strong><br />
              Kathmandu, Bagmati Province, Nepal
            </p>
            <div className="mt-4 p-3 bg-slate-50 rounded-xl border border-black/5 text-[11px] text-ink/50 text-left space-y-1">
              <p>Registration: REG-314982/080/081</p>
              <p>Headquarters: Pulchowk, Lalitpur</p>
              <p>Website: https://evrry.com</p>
            </div>
            <button onClick={() => setActiveModal(null)} className="w-full mt-4 py-2 rounded-xl bg-brand text-white text-xs font-bold">
              Close
            </button>
          </div>
        </div>
      )}

      {activeModal === "delete" && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm animate-in fade-in">
          <div className="relative w-full max-w-sm rounded-2xl bg-white p-5 shadow-2xl text-center">
            <div className="mx-auto w-12 h-12 rounded-full bg-rose-100 text-rose-600 flex items-center justify-center mb-2">
              <Trash2 size={24} />
            </div>
            <h3 className="text-base font-bold text-ink">Schedule Account Deletion?</h3>
            <span className="inline-block mt-1 text-[10px] font-bold text-rose-700 bg-rose-50 border border-rose-200 px-2 py-0.5 rounded-full">
              30-Day Industry Standard Policy (GDPR / ISO 27001)
            </span>
            <div className="text-xs text-ink/70 mt-3 text-left space-y-1.5 leading-relaxed bg-slate-50 p-3 rounded-xl border border-black/5">
              <p className="font-semibold text-ink">What happens during the 30-day period:</p>
              <p>• <strong>Immediate deactivation:</strong> Your profile, @handle, and status will be hidden from other users.</p>
              <p>• <strong>30-day cooling-off:</strong> If you change your mind, simply log in within 30 days to cancel this request and restore your account.</p>
              <p>• <strong>Permanent purge on {deletionDateText}:</strong> After 30 days, all chat archives, order history, biometric hashes, and keys will be permanently deleted and cannot be recovered.</p>
            </div>
            <div className="mt-4 flex gap-2">
              <button
                onClick={() => setActiveModal(null)}
                className="flex-1 py-2 rounded-xl border border-black/10 text-xs font-bold text-ink hover:bg-slate-50"
              >
                Keep Account
              </button>
              <button
                onClick={() => {
                  setDeletionScheduled(true);
                  setActiveModal(null);
                  alert("Account scheduled for deletion. You have 30 days to cancel by logging in again.");
                }}
                className="flex-1 py-2 rounded-xl bg-rose-600 text-white text-xs font-bold hover:bg-rose-700 transition"
              >
                Schedule Deletion (30 Days)
              </button>
            </div>
          </div>
        </div>
      )}

      {/* GOOGLE DRIVE E2EE CHAT BACKUP MODAL */}
      {activeModal === "backup" && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm animate-in fade-in">
          <div className="relative w-full max-w-sm rounded-2xl bg-white p-5 shadow-2xl max-h-[90vh] flex flex-col">
            <div className="flex items-center justify-between border-b border-black/5 pb-3">
              <div className="flex items-center gap-2">
                <div className="flex h-8 w-8 items-center justify-center rounded-full bg-emerald-100 text-emerald-600">
                  <Cloud size={16} />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-ink">Google Drive Chat Backup</h3>
                  <p className="text-[10px] text-ink/40">End-to-End Encrypted Zero-Knowledge</p>
                </div>
              </div>
              <button onClick={() => setActiveModal(null)} className="p-1 rounded-full text-ink/40 hover:bg-black/5">
                <X size={18} />
              </button>
            </div>

            <div className="overflow-y-auto space-y-3.5 py-3 text-xs text-ink/70 pr-1">
              <div className="rounded-xl border border-emerald-200 bg-emerald-50/50 p-2.5 text-[11px]">
                <div className="flex items-center gap-1.5 font-bold text-emerald-800">
                  <Shield size={13} />
                  <span>WhatsApp-Grade E2EE Security</span>
                </div>
                <p className="mt-1 text-emerald-700/80 leading-relaxed text-[10px]">
                  Neither Google nor Evrry can read your backed-up messages or listen to your voice notes. Protected with your 64-digit encryption key.
                </p>
              </div>

              {/* ACCOUNT & STATS */}
              <div className="space-y-1.5 rounded-xl border border-black/5 bg-slate-50 p-2.5 text-[11px]">
                <div className="flex justify-between">
                  <span className="text-ink/40">Google Account:</span>
                  <span className="font-semibold text-ink">rahul.dev@gmail.com</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-ink/40">Last Backup:</span>
                  <span className="font-semibold text-ink">{lastBackupText}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-ink/40">Total Size:</span>
                  <span className="font-semibold text-ink">{backupSizeText}</span>
                </div>
              </div>

              {/* 64-DIGIT ENCRYPTION KEY */}
              <div className="rounded-xl border border-black/5 bg-slate-50 p-2.5">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-1.5 text-xs font-bold text-ink">
                    <Key size={13} className="text-amber-600" />
                    <span>64-Digit E2EE Key</span>
                  </div>
                  <button
                    onClick={() => {
                      setKeyCopied(true);
                      setTimeout(() => setKeyCopied(false), 2000);
                    }}
                    className="flex items-center gap-1 text-[10px] font-bold text-brand hover:underline"
                  >
                    {keyCopied ? <Check size={11} /> : <Copy size={11} />}
                    <span>{keyCopied ? "Copied" : "Copy"}</span>
                  </button>
                </div>
                <p className="mt-1 font-mono text-[9px] text-ink/60 break-all bg-white p-1.5 rounded border border-black/5 select-all">
                  9f8a-4c2e-7b1d-88f0-1a3c-5e7b-99d2-44fa-66bc-11de-33ac-55df-77fa-99be-22dc-44ff
                </p>
              </div>

              {/* FREQUENCY PICKER */}
              <div>
                <p className="text-[11px] font-bold text-ink mb-1.5">Auto-Backup Frequency</p>
                <div className="grid grid-cols-4 gap-1.5">
                  {(["Daily", "Weekly", "Monthly", "Off"] as const).map((freq) => (
                    <button
                      key={freq}
                      onClick={() => setBackupFrequency(freq)}
                      className={clsx(
                        "py-1.5 rounded-lg text-[11px] font-bold transition border",
                        backupFrequency === freq
                          ? "bg-brand text-white border-brand shadow-sm"
                          : "bg-white text-ink/70 border-black/10 hover:bg-slate-50"
                      )}
                    >
                      {freq}
                    </button>
                  ))}
                </div>
              </div>

              {/* TOGGLES */}
              <div className="space-y-2 border-t border-black/5 pt-2">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-[11px] font-medium text-ink">Include Videos</p>
                    <p className="text-[9px] text-ink/40">Includes video attachments in Google Drive</p>
                  </div>
                  <Toggle on={includeVideos} onChange={setIncludeVideos} />
                </div>
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-[11px] font-medium text-ink">Back up over Wi-Fi only</p>
                    <p className="text-[9px] text-ink/40">Saves mobile cellular data in Nepal</p>
                  </div>
                  <Toggle on={backupOverWifiOnly} onChange={setBackupOverWifiOnly} />
                </div>
              </div>

              {/* PROGRESS BAR */}
              {backupProgress !== null && (
                <div className="rounded-xl border border-emerald-200 bg-emerald-50 p-2.5">
                  <div className="flex items-center justify-between text-[11px] font-bold text-emerald-800">
                    <span className="flex items-center gap-1.5">
                      <RefreshCw size={12} className="animate-spin text-emerald-600" />
                      <span>Backing up to Drive...</span>
                    </span>
                    <span>{backupProgress}%</span>
                  </div>
                  <div className="mt-1.5 h-1.5 w-full overflow-hidden rounded-full bg-emerald-200">
                    <div
                      className="h-full bg-emerald-600 transition-all duration-300"
                      style={{ width: `${backupProgress}%` }}
                    />
                  </div>
                  <p className="mt-1 text-[10px] text-emerald-700/80">{backupStage}</p>
                </div>
              )}

              {restoreProgress !== null && (
                <div className="rounded-xl border border-blue-200 bg-blue-50 p-2.5">
                  <div className="flex items-center justify-between text-[11px] font-bold text-blue-800">
                    <span className="flex items-center gap-1.5">
                      <RefreshCw size={12} className="animate-spin text-brand" />
                      <span>Restoring chats...</span>
                    </span>
                    <span>{restoreProgress}%</span>
                  </div>
                  <div className="mt-1.5 h-1.5 w-full overflow-hidden rounded-full bg-blue-200">
                    <div
                      className="h-full bg-brand transition-all duration-300"
                      style={{ width: `${restoreProgress}%` }}
                    />
                  </div>
                  <p className="mt-1 text-[10px] text-blue-700/80">{restoreStage}</p>
                </div>
              )}
            </div>

            {/* BUTTONS */}
            <div className="border-t border-black/5 pt-3 flex gap-2">
              <button
                disabled={backupProgress !== null || restoreProgress !== null}
                onClick={runBackup}
                className="flex-1 py-2 rounded-xl bg-emerald-600 text-white text-xs font-bold hover:bg-emerald-700 transition disabled:opacity-50 flex items-center justify-center gap-1.5"
              >
                <Cloud size={13} />
                <span>Back Up Now</span>
              </button>
              <button
                disabled={backupProgress !== null || restoreProgress !== null}
                onClick={runRestore}
                className="flex-1 py-2 rounded-xl border border-black/10 bg-slate-50 text-ink text-xs font-bold hover:bg-slate-100 transition disabled:opacity-50 flex items-center justify-center gap-1.5"
              >
                <RefreshCw size={13} />
                <span>Restore</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
