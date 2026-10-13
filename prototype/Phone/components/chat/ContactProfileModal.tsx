"use client";

import { useState } from "react";
import { ChatContact } from "@/lib/store/useChatStore/types";
import {
  X,
  Phone,
  Video,
  Share2,
  Lock,
  QrCode,
  Bell,
  BellOff,
  Ban,
  AlertTriangle,
  Users,
  Image as ImageIcon,
  Check,
  ShieldCheck,
  Clock,
  Star,
} from "lucide-react";

interface ContactProfileModalProps {
  contact: ChatContact;
  isOpen: boolean;
  onClose: () => void;
  onVoiceCall: () => void;
  onVideoCall: () => void;
  mediaCount?: number;
  sharedImages?: string[];
}

export default function ContactProfileModal({
  contact,
  isOpen,
  onClose,
  onVoiceCall,
  onVideoCall,
  mediaCount = 0,
  sharedImages = [],
}: ContactProfileModalProps) {
  const [isMuted, setIsMuted] = useState(false);
  const [isBlocked, setIsBlocked] = useState(false);
  const [showSecurityQr, setShowSecurityQr] = useState(false);
  const [showReportDialog, setShowReportDialog] = useState(false);
  const [selectedReportReason, setSelectedReportReason] = useState("Spam / Unsolicited messages");
  const [reportSubmitted, setReportSubmitted] = useState(false);
  const [disappearingTimer, setDisappearingTimer] = useState("Off");

  if (!isOpen) return null;

  return (
    <>
      <div className="fixed inset-0 z-50 flex items-end justify-center bg-black/60 backdrop-blur-sm sm:items-center">
        <div className="relative max-h-[90vh] w-full max-w-md overflow-y-auto rounded-t-3xl bg-white p-6 shadow-2xl transition-all sm:rounded-3xl">
          {/* Close button */}
          <button
            onClick={onClose}
            className="absolute right-5 top-5 flex h-8 w-8 items-center justify-center rounded-full bg-slate-100 text-slate-500 hover:bg-slate-200"
          >
            <X size={18} />
          </button>

          {/* Profile Header */}
          <div className="flex flex-col items-center pt-2 text-center">
            {contact.avatarDataUrl ? (
              // eslint-disable-next-line @next/next/no-img-element
              <img
                src={contact.avatarDataUrl}
                alt=""
                className="h-20 w-20 rounded-full border-2 border-slate-100 object-cover shadow-sm"
              />
            ) : (
              <div className="flex h-20 w-20 items-center justify-center rounded-full bg-blue-50 text-2xl font-bold text-blue-600 border-2 border-blue-100">
                {contact.username.slice(0, 2).toUpperCase()}
              </div>
            )}
            <h3 className="mt-3 text-lg font-bold text-slate-900">@{contact.username}</h3>
            <p className="text-xs text-slate-500">
              {contact.phone || "+977 9841******"} • Registered on Evrry
            </p>

            {/* Quick Actions */}
            <div className="mt-4 flex w-full items-center justify-center gap-3">
              <button
                onClick={() => {
                  onClose();
                  onVoiceCall();
                }}
                className="flex flex-1 flex-col items-center gap-1 rounded-2xl bg-slate-50 py-2.5 text-xs font-semibold text-slate-700 hover:bg-blue-50 hover:text-blue-600 transition"
              >
                <Phone size={18} className="text-blue-600" />
                Audio
              </button>

              <button
                onClick={() => {
                  onClose();
                  onVideoCall();
                }}
                className="flex flex-1 flex-col items-center gap-1 rounded-2xl bg-slate-50 py-2.5 text-xs font-semibold text-slate-700 hover:bg-blue-50 hover:text-blue-600 transition"
              >
                <Video size={18} className="text-blue-600" />
                Video
              </button>

              <button
                onClick={() => {
                  if (navigator.share) {
                    navigator.share({
                      title: contact.username,
                      text: `Connect with @${contact.username} on Evrry Super App`,
                    });
                  }
                }}
                className="flex flex-1 flex-col items-center gap-1 rounded-2xl bg-slate-50 py-2.5 text-xs font-semibold text-slate-700 hover:bg-blue-50 hover:text-blue-600 transition"
              >
                <Share2 size={18} className="text-blue-600" />
                Share
              </button>
            </div>
          </div>

          <div className="mt-5 space-y-4">
            {/* Bio & Phone Card */}
            <div className="rounded-2xl bg-slate-50 p-4 text-left">
              <span className="text-[10px] font-bold tracking-wider text-slate-400">ABOUT / STATUS</span>
              <p className="mt-1 text-sm font-medium text-slate-800">
                Momo lover • Urgent calls only 🏔️ Available on Evrry
              </p>
              <div className="my-3 border-t border-slate-200/60" />
              <span className="text-[10px] font-bold tracking-wider text-slate-400">PHONE NUMBER</span>
              <p className="mt-1 text-sm font-semibold text-blue-600">
                {contact.phone || "+977 9841112233"}
              </p>
            </div>

            {/* Media, Links & Docs Card */}
            <div className="rounded-2xl bg-slate-50 p-4 text-left">
              <div className="flex items-center justify-between">
                <span className="text-[10px] font-bold tracking-wider text-slate-400">
                  MEDIA, LINKS & DOCS
                </span>
                <span className="text-xs font-bold text-blue-600">{mediaCount} items &gt;</span>
              </div>
              <div className="mt-2.5 flex items-center gap-2">
                {sharedImages.length > 0 ? (
                  sharedImages.slice(0, 3).map((img, i) => (
                    // eslint-disable-next-line @next/next/no-img-element
                    <img key={i} src={img} alt="" className="h-14 w-14 rounded-xl object-cover" />
                  ))
                ) : (
                  <>
                    <div className="flex h-14 flex-1 items-center justify-center rounded-xl bg-slate-200 text-slate-400">
                      <ImageIcon size={20} />
                    </div>
                    <div className="flex h-14 flex-1 items-center justify-center rounded-xl bg-slate-200 text-slate-400">
                      <ImageIcon size={20} />
                    </div>
                    <div className="flex h-14 flex-1 items-center justify-center rounded-xl bg-slate-200 text-slate-400">
                      <ImageIcon size={20} />
                    </div>
                  </>
                )}
              </div>
            </div>

            {/* Groups in Common */}
            <div className="rounded-2xl bg-slate-50 p-4 text-left">
              <span className="text-[10px] font-bold tracking-wider text-slate-400">
                2 GROUPS IN COMMON
              </span>
              <div className="mt-2 space-y-2">
                <div className="flex items-center gap-3">
                  <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                    <Users size={14} />
                  </div>
                  <div>
                    <p className="text-xs font-semibold text-slate-800">Kathmandu Foodies &amp; Tech</p>
                    <p className="text-[10px] text-slate-500">4 participants</p>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                    <Users size={14} />
                  </div>
                  <div>
                    <p className="text-xs font-semibold text-slate-800">Family &amp; Home Hub</p>
                    <p className="text-[10px] text-slate-500">3 participants</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Security & Verification */}
            <div className="rounded-2xl bg-slate-50 p-4 text-left space-y-3">
              <button
                onClick={() => setShowSecurityQr(true)}
                className="flex w-full items-center justify-between text-left"
              >
                <div className="flex items-center gap-3">
                  <ShieldCheck size={18} className="text-emerald-600" />
                  <div>
                    <p className="text-xs font-bold text-slate-800">Encryption</p>
                    <p className="text-[11px] text-slate-500">
                      End-to-end encrypted. Tap to verify security QR code.
                    </p>
                  </div>
                </div>
                <QrCode size={18} className="text-blue-600 shrink-0" />
              </button>

              <div className="border-t border-slate-200/60" />

              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  {isMuted ? (
                    <BellOff size={18} className="text-slate-400" />
                  ) : (
                    <Bell size={18} className="text-slate-600" />
                  )}
                  <span className="text-xs font-bold text-slate-800">Mute Notifications</span>
                </div>
                <button
                  onClick={() => setIsMuted(!isMuted)}
                  className={`h-6 w-11 rounded-full p-0.5 transition-colors ${
                    isMuted ? "bg-blue-600" : "bg-slate-300"
                  }`}
                >
                  <div
                    className={`h-5 w-5 rounded-full bg-white transition-transform ${
                      isMuted ? "translate-x-5" : "translate-x-0"
                    }`}
                  />
                </button>
              </div>

              <div className="border-t border-slate-200/60" />

              {/* Disappearing Messages */}
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Clock size={18} className="text-slate-600" />
                  <div>
                    <span className="text-xs font-bold text-slate-800">Disappearing Messages</span>
                    <p className="text-[11px] text-slate-500">For privacy, new messages expire</p>
                  </div>
                </div>
                <button
                  onClick={() => {
                    const timers = ["Off", "24 hours", "7 days", "90 days"];
                    const cur = timers.indexOf(disappearingTimer);
                    setDisappearingTimer(timers[(cur + 1) % timers.length]);
                  }}
                  className="rounded-lg bg-blue-50 px-2.5 py-1 text-xs font-bold text-blue-600 hover:bg-blue-100"
                >
                  {disappearingTimer}
                </button>
              </div>

              <div className="border-t border-slate-200/60" />

              {/* Starred Messages */}
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Star size={18} className="text-amber-500" />
                  <span className="text-xs font-bold text-slate-800">Starred Messages</span>
                </div>
                <span className="text-xs text-slate-500 font-medium">0 starred</span>
              </div>
            </div>

            {/* Danger Zone: Block & Report */}
            <div className="rounded-2xl border border-slate-200 bg-white p-3 space-y-2">
              <button
                onClick={() => setIsBlocked(!isBlocked)}
                className="flex w-full items-center gap-3 rounded-xl p-2 text-left text-xs font-bold text-red-600 hover:bg-red-50"
              >
                <Ban size={16} />
                {isBlocked ? `Unblock @${contact.username}` : `Block @${contact.username}`}
              </button>

              <div className="border-t border-slate-100" />

              <button
                onClick={() => setShowReportDialog(true)}
                className="flex w-full items-center gap-3 rounded-xl p-2 text-left text-xs font-bold text-red-600 hover:bg-red-50"
              >
                <AlertTriangle size={16} />
                Report @{contact.username}
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Security QR Code Modal */}
      {showSecurityQr && (
        <div className="fixed inset-0 z-60 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
          <div className="relative w-full max-w-sm rounded-3xl bg-white p-6 text-center shadow-2xl">
            <button
              onClick={() => setShowSecurityQr(false)}
              className="absolute right-4 top-4 flex h-8 w-8 items-center justify-center rounded-full bg-slate-100 text-slate-500 hover:bg-slate-200"
            >
              <X size={16} />
            </button>
            <h4 className="text-base font-bold text-slate-900">Verify Security Code</h4>
            <p className="mt-1 text-xs text-slate-500">
              Scan this QR code on another device or compare numbers with @{contact.username} to verify
              end-to-end encryption.
            </p>

            <div className="my-5 flex flex-col items-center">
              <div className="relative flex h-48 w-48 items-center justify-center rounded-2xl border-2 border-slate-200 bg-white shadow-inner">
                <QrCode size={140} className="text-slate-800" />
                <div className="absolute flex h-10 w-10 items-center justify-center rounded-full bg-white shadow-md">
                  <Lock size={18} className="text-emerald-600" />
                </div>
              </div>
            </div>

            <div className="rounded-xl bg-slate-50 p-3 text-center">
              <span className="text-[10px] font-bold text-slate-400">SIGNAL PROTOCOL FINGERPRINT</span>
              <p className="mt-1 font-mono text-xs font-medium tracking-wider text-slate-800">
                4819 2019 4820 9182 3019
                <br />
                0281 9482 1029 4829 1049
              </p>
            </div>

            <button
              onClick={() => setShowSecurityQr(false)}
              className="mt-4 w-full rounded-xl bg-blue-600 py-2.5 text-xs font-bold text-white shadow-sm hover:bg-blue-700"
            >
              Done
            </button>
          </div>
        </div>
      )}

      {/* Report Contact Modal */}
      {showReportDialog && (
        <div className="fixed inset-0 z-60 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
          <div className="relative w-full max-w-sm rounded-3xl bg-white p-6 shadow-2xl">
            <button
              onClick={() => setShowReportDialog(false)}
              className="absolute right-4 top-4 flex h-8 w-8 items-center justify-center rounded-full bg-slate-100 text-slate-500 hover:bg-slate-200"
            >
              <X size={16} />
            </button>
            <h4 className="text-base font-bold text-slate-900">Report @{contact.username}</h4>
            <p className="mt-1 text-xs text-slate-500">
              The last 5 messages will be forwarded to the Evrry Trust &amp; Safety team.
            </p>

            {reportSubmitted ? (
              <div className="my-6 flex flex-col items-center text-center">
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-emerald-100 text-emerald-600">
                  <Check size={24} />
                </div>
                <p className="mt-3 text-sm font-bold text-slate-900">Report Submitted</p>
                <p className="mt-1 text-xs text-slate-500">
                  Thank you for keeping Evrry safe. Our team is reviewing this report.
                </p>
              </div>
            ) : (
              <>
                <div className="my-4 space-y-2">
                  {[
                    "Spam / Unsolicited messages",
                    "Harassment or Hate speech",
                    "Fraud, Scam, or Impersonation",
                    "Other Trust & Safety concerns",
                  ].map((reason) => (
                    <button
                      key={reason}
                      onClick={() => setSelectedReportReason(reason)}
                      className={`flex w-full items-center justify-between rounded-xl p-3 text-left text-xs font-medium transition ${
                        selectedReportReason === reason
                          ? "bg-blue-50 text-blue-700 border border-blue-200"
                          : "bg-slate-50 text-slate-700 hover:bg-slate-100"
                      }`}
                    >
                      {reason}
                      {selectedReportReason === reason && (
                        <Check size={14} className="text-blue-600 shrink-0" />
                      )}
                    </button>
                  ))}
                </div>

                <button
                  onClick={() => {
                    setIsBlocked(true);
                    setReportSubmitted(true);
                    setTimeout(() => {
                      setShowReportDialog(false);
                      setReportSubmitted(false);
                    }, 1200);
                  }}
                  className="w-full rounded-xl bg-red-600 py-3 text-xs font-bold text-white shadow-sm hover:bg-red-700"
                >
                  Submit Report
                </button>
              </>
            )}
          </div>
        </div>
      )}
    </>
  );
}
