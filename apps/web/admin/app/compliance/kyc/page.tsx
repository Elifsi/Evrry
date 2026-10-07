"use client";

import { useState } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import {
  FileCheck2,
  ZoomIn,
  ZoomOut,
  RotateCw,
  SunMedium,
  CheckCircle2,
  XCircle,
  AlertCircle,
  ChevronLeft,
  ChevronRight,
  Send,
  Building,
  User,
  Bike,
  ShieldCheck,
} from "lucide-react";
import { sampleKycQueue } from "@/lib/admin/mockData";
import { KycDocumentItem } from "@/lib/admin/types";

export default function KycVerificationPage() {
  const [queue, setQueue] = useState<KycDocumentItem[]>(sampleKycQueue);
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [selectedDocIndex, setSelectedDocIndex] = useState(0);

  // Document Viewer Controls
  const [zoomLevel, setZoomLevel] = useState(1);
  const [rotation, setRotation] = useState(0);
  const [highContrast, setHighContrast] = useState(false);

  // Rejection Modal
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [rejectionReason, setRejectionReason] = useState("");
  const [actionFeedback, setActionFeedback] = useState<string | null>(null);

  const currentPartner = queue[selectedIndex] || queue[0];
  const currentDoc = currentPartner?.documents[selectedDocIndex] || currentPartner?.documents[0];

  const handleApprove = () => {
    const updated = [...queue];
    updated[selectedIndex] = {
      ...currentPartner,
      status: "approved",
    };
    setQueue(updated);
    setActionFeedback(`Approved ${currentPartner.partnerName}! Automated activation SMS dispatched.`);
    setTimeout(() => setActionFeedback(null), 4000);
  };

  const handleReject = () => {
    const updated = [...queue];
    updated[selectedIndex] = {
      ...currentPartner,
      status: "rejected",
      rejectionReason: rejectionReason || "Document does not meet verification requirements.",
    };
    setQueue(updated);
    setShowRejectModal(false);
    setActionFeedback(`Rejected with reason. Partner notified to review submitted files.`);
    setTimeout(() => setActionFeedback(null), 4000);
  };

  const handleRequestReupload = () => {
    const updated = [...queue];
    updated[selectedIndex] = {
      ...currentPartner,
      status: "reupload_requested",
      rejectionReason: "Please re-upload a clearer, full-page photo of your document.",
    };
    setQueue(updated);
    setActionFeedback(`Requested re-upload from ${currentPartner.partnerName} via push alert.`);
    setTimeout(() => setActionFeedback(null), 4000);
  };

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="Partner KYC & Compliance Queue"
        subtitle="Split-Screen Verification • High-Resolution Document Inspection"
      />

      <div className="p-8 space-y-6 flex-1 flex flex-col">
        {/* Feedback Alert */}
        {actionFeedback && (
          <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-xl text-emerald-800 text-xs flex items-center justify-between">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600" />
              <span className="font-semibold">{actionFeedback}</span>
            </div>
            <span className="text-[11px] font-mono text-emerald-600">SMS Gateway: Meta / Aakash</span>
          </div>
        )}

        {/* Top Queue Ribbon */}
        <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between gap-4 overflow-x-auto">
          <div className="flex items-center gap-2 shrink-0">
            <FileCheck2 className="w-4 h-4 text-emerald-600" />
            <span className="text-xs font-bold text-slate-900">Pending Submissions:</span>
          </div>

          <div className="flex items-center gap-3">
            {queue.map((item, idx) => (
              <button
                key={item.id}
                onClick={() => {
                  setSelectedIndex(idx);
                  setSelectedDocIndex(0);
                  setZoomLevel(1);
                  setRotation(0);
                  setHighContrast(false);
                }}
                className={`flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all border shrink-0 ${
                  selectedIndex === idx
                    ? "bg-slate-900 text-white border-slate-900 shadow-sm"
                    : "bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100"
                }`}
              >
                <span>{item.partnerName}</span>
                <span
                  className={`text-[10px] px-1.5 py-0.2 rounded font-mono uppercase ${
                    item.status === "approved"
                      ? "bg-emerald-500/20 text-emerald-400"
                      : item.status === "rejected"
                      ? "bg-red-500/20 text-red-400"
                      : "bg-amber-500/20 text-amber-500"
                  }`}
                >
                  {item.status}
                </span>
              </button>
            ))}
          </div>
        </div>

        {/* Split Screen Workspace */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 flex-1 min-h-[620px]">
          {/* Left Pane: Document Zoom & Contrast Viewer (7 Cols) */}
          <div className="lg:col-span-7 bg-slate-900 rounded-2xl border border-slate-800 p-4 flex flex-col justify-between overflow-hidden relative shadow-inner">
            {/* Viewer Controls Toolbar */}
            <div className="flex items-center justify-between bg-slate-800/90 backdrop-blur px-4 py-2.5 rounded-xl border border-slate-700/80 z-10">
              <div className="text-xs text-white font-semibold flex items-center gap-2">
                <span className="text-emerald-400 font-mono">[{selectedDocIndex + 1}/{currentPartner.documents.length}]</span>
                <span className="truncate max-w-[280px]">{currentDoc?.title}</span>
              </div>

              <div className="flex items-center gap-1.5">
                <button
                  onClick={() => setZoomLevel((z) => Math.min(z + 0.25, 3))}
                  className="p-1.5 bg-slate-700 hover:bg-slate-600 text-slate-200 rounded-lg transition-colors"
                  title="Zoom In"
                >
                  <ZoomIn className="w-4 h-4" />
                </button>
                <button
                  onClick={() => setZoomLevel((z) => Math.max(z - 0.25, 0.75))}
                  className="p-1.5 bg-slate-700 hover:bg-slate-600 text-slate-200 rounded-lg transition-colors"
                  title="Zoom Out"
                >
                  <ZoomOut className="w-4 h-4" />
                </button>
                <button
                  onClick={() => setRotation((r) => (r + 90) % 360)}
                  className="p-1.5 bg-slate-700 hover:bg-slate-600 text-slate-200 rounded-lg transition-colors"
                  title="Rotate"
                >
                  <RotateCw className="w-4 h-4" />
                </button>
                <button
                  onClick={() => setHighContrast(!highContrast)}
                  className={`p-1.5 rounded-lg transition-colors ${
                    highContrast
                      ? "bg-emerald-600 text-white"
                      : "bg-slate-700 hover:bg-slate-600 text-slate-200"
                  }`}
                  title="Toggle High Contrast for faint stamps"
                >
                  <SunMedium className="w-4 h-4" />
                </button>
              </div>
            </div>

            {/* Document Image Viewport */}
            <div className="flex-1 flex items-center justify-center p-6 overflow-hidden min-h-[440px]">
              {currentDoc?.imageUrl ? (
                // eslint-disable-next-line @next/next/no-img-element
                <img
                  src={currentDoc.imageUrl}
                  alt={currentDoc.title}
                  style={{
                    transform: `scale(${zoomLevel}) rotate(${rotation}deg)`,
                    filter: highContrast ? "contrast(180%) brightness(110%)" : "none",
                  }}
                  className="max-h-[420px] max-w-full object-contain rounded-lg shadow-2xl transition-transform duration-200 select-none cursor-grab active:cursor-grabbing"
                />
              ) : (
                <div className="text-slate-500 text-xs">No preview available</div>
              )}
            </div>

            {/* Document Thumbnails Selector */}
            <div className="flex items-center justify-center gap-3 pt-2 z-10">
              {currentPartner.documents.map((doc, idx) => (
                <button
                  key={doc.type}
                  onClick={() => {
                    setSelectedDocIndex(idx);
                    setZoomLevel(1);
                    setRotation(0);
                  }}
                  className={`px-3 py-1.5 text-xs font-semibold rounded-lg border transition-all ${
                    selectedDocIndex === idx
                      ? "bg-emerald-600 text-white border-emerald-500 shadow-md"
                      : "bg-slate-800 text-slate-300 border-slate-700 hover:bg-slate-700"
                  }`}
                >
                  {doc.title.split("(")[0]}
                </button>
              ))}
            </div>
          </div>

          {/* Right Pane: Verification Details & Action Desk (5 Cols) */}
          <div className="lg:col-span-5 bg-white rounded-2xl border border-slate-200 p-6 flex flex-col justify-between shadow-sm space-y-6">
            <div className="space-y-5">
              {/* Partner Overview */}
              <div className="border-b border-slate-100 pb-4">
                <div className="flex items-center justify-between">
                  <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400">
                    Applicant Information
                  </span>
                  <span className="text-xs font-mono text-slate-400">{currentPartner.id}</span>
                </div>
                <h3 className="text-lg font-bold text-slate-900 mt-1">
                  {currentPartner.partnerName}
                </h3>
                <div className="flex items-center gap-2 mt-1 text-xs text-slate-500">
                  <span className="font-semibold text-slate-700">{currentPartner.phoneNumber}</span>
                  <span>•</span>
                  <span>Submitted: {currentPartner.submittedAt}</span>
                </div>
              </div>

              {/* Specific Metadata depending on Partner Type */}
              {currentPartner.licenseInfo && (
                <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 space-y-2 text-xs">
                  <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider flex items-center gap-1.5">
                    <User className="w-3.5 h-3.5 text-emerald-600" />
                    Driving License Verification
                  </div>
                  <div className="grid grid-cols-2 gap-2 pt-1">
                    <div>
                      <span className="text-slate-400 block text-[11px]">License Number:</span>
                      <span className="font-mono font-semibold text-slate-900">
                        {currentPartner.licenseInfo.number}
                      </span>
                    </div>
                    <div>
                      <span className="text-slate-400 block text-[11px]">Expiry Date:</span>
                      <span className="font-semibold text-emerald-700">
                        {currentPartner.licenseInfo.expiryDate} (Valid)
                      </span>
                    </div>
                    <div>
                      <span className="text-slate-400 block text-[11px]">Issue District:</span>
                      <span className="font-semibold text-slate-800">
                        {currentPartner.licenseInfo.issueDistrict}
                      </span>
                    </div>
                  </div>
                </div>
              )}

              {currentPartner.vehicleInfo && (
                <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 space-y-2 text-xs">
                  <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider flex items-center gap-1.5">
                    <Bike className="w-3.5 h-3.5 text-emerald-600" />
                    Vehicle & Blue Book Tax Stamp
                  </div>
                  <div className="grid grid-cols-2 gap-2 pt-1">
                    <div>
                      <span className="text-slate-400 block text-[11px]">Plate Number:</span>
                      <span className="font-bold text-slate-900">
                        {currentPartner.vehicleInfo.plateNumber}
                      </span>
                    </div>
                    <div>
                      <span className="text-slate-400 block text-[11px]">Tax Paid Until:</span>
                      <span className="font-semibold text-emerald-700">
                        BS {currentPartner.vehicleInfo.taxClearanceYear} (Cleared)
                      </span>
                    </div>
                    <div className="col-span-2">
                      <span className="text-slate-400 block text-[11px]">Make / Model:</span>
                      <span className="font-semibold text-slate-800">
                        {currentPartner.vehicleInfo.model}
                      </span>
                    </div>
                  </div>
                </div>
              )}

              {currentPartner.panVatInfo && (
                <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 space-y-2 text-xs">
                  <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider flex items-center gap-1.5">
                    <Building className="w-3.5 h-3.5 text-emerald-600" />
                    Inland Revenue PAN / VAT Details
                  </div>
                  <div className="grid grid-cols-2 gap-2 pt-1">
                    <div>
                      <span className="text-slate-400 block text-[11px]">PAN/VAT Number:</span>
                      <span className="font-mono font-bold text-slate-900">
                        {currentPartner.panVatInfo.panNumber}
                      </span>
                    </div>
                    <div>
                      <span className="text-slate-400 block text-[11px]">Entity Type:</span>
                      <span className="font-semibold text-slate-800">
                        {currentPartner.panVatInfo.entityType}
                      </span>
                    </div>
                    <div className="col-span-2">
                      <span className="text-slate-400 block text-[11px]">Registered Legal Name:</span>
                      <span className="font-semibold text-slate-900">
                        {currentPartner.panVatInfo.businessName}
                      </span>
                    </div>
                  </div>
                </div>
              )}
            </div>

            {/* Verification Actions */}
            <div className="pt-4 border-t border-slate-100 space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <button
                  onClick={handleApprove}
                  className="py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-xs rounded-xl shadow-md transition-all flex items-center justify-center gap-1.5"
                >
                  <CheckCircle2 className="w-4 h-4" />
                  Approve & Activate
                </button>
                <button
                  onClick={() => setShowRejectModal(true)}
                  className="py-2.5 border border-red-200 text-red-600 hover:bg-red-50 font-semibold text-xs rounded-xl transition-all flex items-center justify-center gap-1.5"
                >
                  <XCircle className="w-4 h-4" />
                  Reject with Reason
                </button>
              </div>

              <button
                onClick={handleRequestReupload}
                className="w-full py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-xs rounded-lg transition-all flex items-center justify-center gap-1.5"
              >
                <AlertCircle className="w-3.5 h-3.5 text-amber-600" />
                Request Specific Document Re-Upload
              </button>
            </div>
          </div>
        </div>

        {/* Rejection Modal */}
        {showRejectModal && (
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl max-w-md w-full p-6 space-y-4 shadow-2xl border border-slate-200">
              <h4 className="text-base font-bold text-slate-900">
                Reject KYC: {currentPartner.partnerName}
              </h4>
              <p className="text-xs text-slate-500">
                Select or write the reason. This message is dispatched via SMS directly to the partner.
              </p>

              <div className="space-y-2">
                {[
                  "Blue Book tax clearance stamp is expired or unreadable.",
                  "Driving license photo is blurry or corners are cropped.",
                  "Vehicle plate number photo does not match registration book.",
                  "PAN/VAT name does not match bank account holder name.",
                ].map((reason) => (
                  <button
                    key={reason}
                    onClick={() => setRejectionReason(reason)}
                    className={`w-full text-left p-2.5 text-xs rounded-lg border transition-all ${
                      rejectionReason === reason
                        ? "bg-red-50 border-red-300 text-red-900 font-semibold"
                        : "bg-slate-50 border-slate-200 text-slate-700 hover:bg-slate-100"
                    }`}
                  >
                    {reason}
                  </button>
                ))}
              </div>

              <textarea
                value={rejectionReason}
                onChange={(e) => setRejectionReason(e.target.value)}
                placeholder="Or type custom rejection reason..."
                className="w-full h-20 p-3 text-xs border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
              />

              <div className="flex items-center justify-end gap-3 pt-2">
                <button
                  onClick={() => setShowRejectModal(false)}
                  className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-lg"
                >
                  Cancel
                </button>
                <button
                  onClick={handleReject}
                  className="px-5 py-2 bg-red-600 hover:bg-red-700 text-white text-xs font-semibold rounded-lg shadow-md flex items-center gap-1.5"
                >
                  <Send className="w-3.5 h-3.5" />
                  Confirm Rejection & Send SMS
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
