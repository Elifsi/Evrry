"use client";

import { useState, useEffect } from "react";
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
  Building,
  User,
  Bike,
  ShieldCheck,
  Send,
  Search,
  Filter,
} from "lucide-react";
import { fetchKycQueue, reviewPartnerKyc } from "@/lib/admin/api";
import { KycDocumentItem, KycStatus, PartnerType } from "@/lib/admin/types";

export default function KycVerificationPage() {
  const [allPartners, setAllPartners] = useState<KycDocumentItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);

  // Active status tab: default strictly to "pending"
  const [activeTab, setActiveTab] = useState<KycStatus>("pending");
  const [verticalFilter, setVerticalFilter] = useState<PartnerType | "all">("all");
  const [searchQuery, setSearchQuery] = useState("");

  const [selectedPartnerId, setSelectedPartnerId] = useState<string | null>(null);
  const [selectedDocIndex, setSelectedDocIndex] = useState(0);

  // Document Viewer Controls
  const [zoomLevel, setZoomLevel] = useState(1);
  const [rotation, setRotation] = useState(0);
  const [highContrast, setHighContrast] = useState(false);

  // Rejection & Re-upload Modal
  const [modalMode, setModalMode] = useState<"reject" | "reupload" | null>(null);
  const [modalReason, setModalReason] = useState("");
  const [actionFeedback, setActionFeedback] = useState<string | null>(null);

  const loadData = async () => {
    setIsRefreshing(true);
    const data = await fetchKycQueue();
    setAllPartners(data);
    setIsLoading(false);
    setIsRefreshing(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  // Filter partners strictly by the active tab
  const tabFilteredPartners = allPartners.filter((p) => {
    if (p.status !== activeTab) return false;
    if (verticalFilter !== "all" && p.partnerType !== verticalFilter) return false;
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      return (
        p.partnerName.toLowerCase().includes(q) ||
        p.phoneNumber.includes(q) ||
        p.id.toLowerCase().includes(q)
      );
    }
    return true;
  });

  // Current active partner in view
  const currentPartner =
    tabFilteredPartners.find((p) => p.id === selectedPartnerId) ||
    tabFilteredPartners[0] ||
    null;

  // Sync selectedPartnerId when current list changes
  useEffect(() => {
    if (tabFilteredPartners.length > 0) {
      if (!tabFilteredPartners.some((p) => p.id === selectedPartnerId)) {
        setSelectedPartnerId(tabFilteredPartners[0].id);
        setSelectedDocIndex(0);
        setZoomLevel(1);
        setRotation(0);
      }
    } else {
      setSelectedPartnerId(null);
    }
  }, [activeTab, verticalFilter, searchQuery, tabFilteredPartners]);

  const currentDoc = currentPartner?.documents[selectedDocIndex] || currentPartner?.documents[0];

  // Counts for tabs
  const pendingCount = allPartners.filter((p) => p.status === "pending").length;
  const approvedCount = allPartners.filter((p) => p.status === "approved").length;
  const reuploadCount = allPartners.filter((p) => p.status === "reupload_requested").length;
  const rejectedCount = allPartners.filter((p) => p.status === "rejected").length;

  const handleApprove = async () => {
    if (!currentPartner) return;
    const res = await reviewPartnerKyc(currentPartner.id, "approved");
    if (res.success && res.updatedPartner) {
      setAllPartners((prev) =>
        prev.map((p) => (p.id === currentPartner.id ? res.updatedPartner! : p))
      );
      setActionFeedback(
        `Approved ${currentPartner.partnerName}! Activation push notification & credentials dispatched.`
      );
      setTimeout(() => setActionFeedback(null), 5000);
    }
  };

  const handleConfirmModalAction = async () => {
    if (!currentPartner || !modalMode) return;

    const targetStatus: KycStatus = modalMode === "reject" ? "rejected" : "reupload_requested";
    const reasonText =
      modalReason.trim() ||
      (modalMode === "reject"
        ? "Document does not meet government regulatory standards."
        : "Please re-upload a clear, full-page photo with all seals visible.");

    const res = await reviewPartnerKyc(currentPartner.id, targetStatus, reasonText);
    if (res.success && res.updatedPartner) {
      setAllPartners((prev) =>
        prev.map((p) => (p.id === currentPartner.id ? res.updatedPartner! : p))
      );
      setActionFeedback(
        modalMode === "reject"
          ? `Partner ${currentPartner.partnerName} rejected. Reason logged in audit trail.`
          : `Re-upload requested from ${currentPartner.partnerName}. Partner notified via push & in-app alert.`
      );
      setModalMode(null);
      setModalReason("");
      setTimeout(() => setActionFeedback(null), 5000);
    }
  };

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="Partner KYC & Compliance Verification"
        subtitle="Zero-SQL Government Document Audit • Smart Driving Licenses & Blue Books"
        onRefresh={loadData}
        isRefreshing={isRefreshing}
      />

      <div className="p-8 space-y-6 flex-1 flex flex-col">
        {/* Feedback Alert Toast */}
        {actionFeedback && (
          <div className="bg-brand-50 border border-brand-200 p-4 rounded-xl text-brand-900 text-xs flex items-center justify-between shadow-sm">
            <div className="flex items-center gap-2.5">
              <CheckCircle2 className="w-5 h-5 text-brand-600 shrink-0" />
              <span className="font-semibold">{actionFeedback}</span>
            </div>
            <span className="text-[11px] font-mono text-brand-600 bg-white px-2 py-0.5 rounded border border-brand-200">
              Channel: In-App & Push (FCM/APNs)
            </span>
          </div>
        )}

        {/* Tab Navigation Ribbon */}
        <div className="bg-white p-3 rounded-2xl border border-border shadow-sm flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-4">
          {/* Status Tabs */}
          <div className="flex items-center gap-1.5 overflow-x-auto pb-1 sm:pb-0">
            <button
              onClick={() => setActiveTab("pending")}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold transition-all shrink-0 ${
                activeTab === "pending"
                  ? "bg-brand-600 text-white shadow-md shadow-brand-900/30"
                  : "bg-surface-subtle text-ink-muted hover:text-ink hover:bg-slate-100"
              }`}
            >
              <FileCheck2 className="w-4 h-4" />
              <span>Pending Review</span>
              <span
                className={`text-[11px] px-1.5 py-0.2 rounded-full font-bold ${
                  activeTab === "pending"
                    ? "bg-white/20 text-white"
                    : "bg-amber-100 text-amber-800"
                }`}
              >
                {pendingCount}
              </span>
            </button>

            <button
              onClick={() => setActiveTab("approved")}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold transition-all shrink-0 ${
                activeTab === "approved"
                  ? "bg-brand-600 text-white shadow-md shadow-brand-900/30"
                  : "bg-surface-subtle text-ink-muted hover:text-ink hover:bg-slate-100"
              }`}
            >
              <CheckCircle2 className="w-4 h-4" />
              <span>Approved</span>
              <span
                className={`text-[11px] px-1.5 py-0.2 rounded-full font-bold ${
                  activeTab === "approved"
                    ? "bg-white/20 text-white"
                    : "bg-emerald-100 text-emerald-800"
                }`}
              >
                {approvedCount}
              </span>
            </button>

            <button
              onClick={() => setActiveTab("reupload_requested")}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold transition-all shrink-0 ${
                activeTab === "reupload_requested"
                  ? "bg-brand-600 text-white shadow-md shadow-brand-900/30"
                  : "bg-surface-subtle text-ink-muted hover:text-ink hover:bg-slate-100"
              }`}
            >
              <AlertCircle className="w-4 h-4" />
              <span>Re-Upload Requested</span>
              <span
                className={`text-[11px] px-1.5 py-0.2 rounded-full font-bold ${
                  activeTab === "reupload_requested"
                    ? "bg-white/20 text-white"
                    : "bg-amber-100 text-amber-800"
                }`}
              >
                {reuploadCount}
              </span>
            </button>

            <button
              onClick={() => setActiveTab("rejected")}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold transition-all shrink-0 ${
                activeTab === "rejected"
                  ? "bg-brand-600 text-white shadow-md shadow-brand-900/30"
                  : "bg-surface-subtle text-ink-muted hover:text-ink hover:bg-slate-100"
              }`}
            >
              <XCircle className="w-4 h-4" />
              <span>Rejected</span>
              <span
                className={`text-[11px] px-1.5 py-0.2 rounded-full font-bold ${
                  activeTab === "rejected"
                    ? "bg-white/20 text-white"
                    : "bg-rose-100 text-rose-800"
                }`}
              >
                {rejectedCount}
              </span>
            </button>
          </div>

          {/* Vertical Filter & Search */}
          <div className="flex items-center gap-2 shrink-0">
            <div className="relative">
              <Search className="w-3.5 h-3.5 text-slate-400 absolute left-2.5 top-2.5" />
              <input
                type="text"
                placeholder="Search name, phone, ID..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-8 pr-3 py-1.5 text-xs bg-surface-subtle border border-border rounded-lg text-ink focus:outline-none focus:ring-1 focus:ring-brand-600 w-44"
              />
            </div>

            <select
              value={verticalFilter}
              onChange={(e) => setVerticalFilter(e.target.value as any)}
              className="px-2.5 py-1.5 text-xs bg-surface-subtle border border-border rounded-lg text-ink font-medium focus:outline-none focus:ring-1 focus:ring-brand-600"
            >
              <option value="all">All Verticals</option>
              <option value="rider">Riders (Two-Wheeler)</option>
              <option value="driver">Drivers (Cab/Taxi)</option>
              <option value="restaurant">Restaurants</option>
              <option value="grocery">Groceries</option>
              <option value="hotel">Hotels & Stays</option>
            </select>
          </div>
        </div>

        {/* Current Tab Partner Selector Bar */}
        <div className="bg-white p-3 rounded-2xl border border-border shadow-sm flex items-center justify-between gap-4 overflow-x-auto">
          <div className="flex items-center gap-2 shrink-0">
            <span className="text-xs font-bold text-ink">
              {activeTab === "pending"
                ? "Pending Queue:"
                : activeTab === "approved"
                ? "Approved Directory:"
                : activeTab === "reupload_requested"
                ? "Awaiting Re-Upload:"
                : "Rejected Records:"}
            </span>
          </div>

          {tabFilteredPartners.length === 0 ? (
            <div className="text-xs text-ink-muted italic py-1">
              No partner records matching current filters.
            </div>
          ) : (
            <div className="flex items-center gap-2.5">
              {tabFilteredPartners.map((item) => (
                <button
                  key={item.id}
                  onClick={() => {
                    setSelectedPartnerId(item.id);
                    setSelectedDocIndex(0);
                    setZoomLevel(1);
                    setRotation(0);
                    setHighContrast(false);
                  }}
                  className={`flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all border shrink-0 ${
                    currentPartner?.id === item.id
                      ? "bg-slate-900 text-white border-slate-900 shadow-sm"
                      : "bg-surface-subtle text-ink-muted border-border hover:bg-slate-100"
                  }`}
                >
                  <span>{item.partnerName}</span>
                  <span
                    className={`text-[10px] px-1.5 py-0.2 rounded font-mono uppercase ${
                      item.partnerType === "rider"
                        ? "bg-brand-100 text-brand-800"
                        : item.partnerType === "driver"
                        ? "bg-sky-100 text-sky-800"
                        : item.partnerType === "restaurant"
                        ? "bg-amber-100 text-amber-800"
                        : "bg-emerald-100 text-emerald-800"
                    }`}
                  >
                    {item.partnerType}
                  </span>
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Split Screen Workspace */}
        {currentPartner ? (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 flex-1 min-h-[620px]">
            {/* Left Pane: Document Zoom & Contrast Viewer (7 Cols) */}
            <div className="lg:col-span-7 bg-slate-900 rounded-2xl border border-slate-800 p-4 flex flex-col justify-between overflow-hidden relative shadow-inner">
              {/* Viewer Controls Toolbar */}
              <div className="flex items-center justify-between bg-slate-800/90 backdrop-blur px-4 py-2.5 rounded-xl border border-slate-700/80 z-10">
                <div className="text-xs text-white font-semibold flex items-center gap-2">
                  <span className="text-brand-300 font-mono">
                    [{selectedDocIndex + 1}/{currentPartner.documents.length}]
                  </span>
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
                    title="Rotate 90°"
                  >
                    <RotateCw className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => setHighContrast(!highContrast)}
                    className={`p-1.5 rounded-lg transition-colors ${
                      highContrast
                        ? "bg-brand-600 text-white"
                        : "bg-slate-700 hover:bg-slate-600 text-slate-200"
                    }`}
                    title="Toggle High Contrast for faint stamps"
                  >
                    <SunMedium className="w-4 h-4" />
                  </button>
                </div>
              </div>

              {/* Viewport with Document Image */}
              <div className="flex-1 flex items-center justify-center overflow-auto p-4 my-2 relative select-none">
                {currentDoc ? (
                  // eslint-disable-next-line @next/next/no-img-element
                  <img
                    src={currentDoc.imageUrl}
                    alt={currentDoc.title}
                    style={{
                      transform: `scale(${zoomLevel}) rotate(${rotation}deg)`,
                      filter: highContrast ? "contrast(180%) brightness(95%) grayscale(20%)" : "none",
                      transition: "transform 0.15s ease-out, filter 0.2s ease",
                    }}
                    className="max-h-[500px] object-contain rounded-lg shadow-2xl origin-center"
                  />
                ) : (
                  <div className="text-slate-500 text-xs">No document preview available</div>
                )}
              </div>

              {/* Document Thumbnails Selector */}
              <div className="bg-slate-800/90 backdrop-blur px-4 py-2.5 rounded-xl border border-slate-700/80 flex items-center justify-between z-10">
                <div className="flex items-center gap-2 overflow-x-auto">
                  {currentPartner.documents.map((doc, dIdx) => (
                    <button
                      key={doc.type}
                      onClick={() => {
                        setSelectedDocIndex(dIdx);
                        setZoomLevel(1);
                        setRotation(0);
                      }}
                      className={`px-2.5 py-1 rounded-lg text-xs font-medium transition-colors whitespace-nowrap ${
                        selectedDocIndex === dIdx
                          ? "bg-brand-600 text-white font-semibold"
                          : "bg-slate-700 text-slate-300 hover:bg-slate-600"
                      }`}
                    >
                      {doc.title.split("(")[0]}
                    </button>
                  ))}
                </div>

                <div className="text-[11px] text-slate-400 font-mono pl-3 shrink-0">
                  {Math.round(zoomLevel * 100)}%
                </div>
              </div>
            </div>

            {/* Right Pane: Structured Details & Actions (5 Cols) */}
            <div className="lg:col-span-5 bg-white rounded-2xl border border-border p-6 flex flex-col justify-between shadow-sm space-y-6">
              <div className="space-y-6">
                {/* Applicant Summary Header */}
                <div className="flex items-start justify-between pb-4 border-b border-border">
                  <div>
                    <div className="flex items-center gap-2">
                      <h2 className="text-base font-bold text-ink tracking-tight">
                        {currentPartner.partnerName}
                      </h2>
                      <span
                        className={`text-[10px] uppercase font-bold px-2 py-0.5 rounded ${
                          currentPartner.status === "approved"
                            ? "bg-emerald-100 text-emerald-800"
                            : currentPartner.status === "rejected"
                            ? "bg-rose-100 text-rose-800"
                            : currentPartner.status === "reupload_requested"
                            ? "bg-amber-100 text-amber-800"
                            : "bg-brand-100 text-brand-800"
                        }`}
                      >
                        {currentPartner.status.replace("_", " ")}
                      </span>
                    </div>
                    <p className="text-xs text-ink-muted mt-0.5 font-mono">{currentPartner.phoneNumber}</p>
                  </div>

                  <div className="text-right">
                    <span className="text-[10px] text-ink-subtle uppercase block font-semibold">
                      Submitted
                    </span>
                    <span className="text-xs font-medium text-ink">
                      {currentPartner.submittedAt}
                    </span>
                  </div>
                </div>

                {/* Status Alert if not pending */}
                {currentPartner.status !== "pending" && (
                  <div
                    className={`p-3.5 rounded-xl border text-xs flex items-start gap-2.5 ${
                      currentPartner.status === "approved"
                        ? "bg-emerald-50 border-emerald-200 text-emerald-800"
                        : currentPartner.status === "rejected"
                        ? "bg-rose-50 border-rose-200 text-rose-800"
                        : "bg-amber-50 border-amber-200 text-amber-800"
                    }`}
                  >
                    <AlertCircle className="w-4 h-4 shrink-0 mt-0.5" />
                    <div>
                      <div className="font-semibold">
                        {currentPartner.status === "approved"
                          ? "Partner Verified & Operational"
                          : currentPartner.status === "rejected"
                          ? "Verification Rejected"
                          : "Awaiting Re-Upload from Partner"}
                      </div>
                      {currentPartner.rejectionReason && (
                        <div className="mt-1 text-[11px] text-slate-600 font-normal">
                          Note: {currentPartner.rejectionReason}
                        </div>
                      )}
                    </div>
                  </div>
                )}

                {/* Structured Verification Metadata Cards */}
                <div className="space-y-3.5">
                  {currentPartner.vehicleInfo && (
                    <div className="p-3.5 bg-surface-subtle rounded-xl border border-border">
                      <div className="text-[11px] font-bold text-ink uppercase flex items-center gap-1.5 mb-2">
                        <Bike className="w-3.5 h-3.5 text-brand-600" />
                        <span>Vehicle & Tax Clearance Registration</span>
                      </div>
                      <div className="grid grid-cols-2 gap-2 text-xs">
                        <div>
                          <span className="text-ink-muted block text-[11px]">Plate Number</span>
                          <span className="font-mono font-bold text-ink">
                            {currentPartner.vehicleInfo.plateNumber}
                          </span>
                        </div>
                        <div>
                          <span className="text-ink-muted block text-[11px]">Tax Clearance Year</span>
                          <span className="font-semibold text-emerald-700">
                            BS {currentPartner.vehicleInfo.taxClearanceYear} (Valid)
                          </span>
                        </div>
                        <div className="col-span-2">
                          <span className="text-ink-muted block text-[11px]">Model & Class</span>
                          <span className="font-medium text-ink">
                            {currentPartner.vehicleInfo.model} • {currentPartner.vehicleInfo.type}
                          </span>
                        </div>
                      </div>
                    </div>
                  )}

                  {currentPartner.licenseInfo && (
                    <div className="p-3.5 bg-surface-subtle rounded-xl border border-border">
                      <div className="text-[11px] font-bold text-ink uppercase flex items-center gap-1.5 mb-2">
                        <ShieldCheck className="w-3.5 h-3.5 text-brand-600" />
                        <span>Smart Driving License Verification</span>
                      </div>
                      <div className="grid grid-cols-2 gap-2 text-xs">
                        <div>
                          <span className="text-ink-muted block text-[11px]">License No.</span>
                          <span className="font-mono font-bold text-ink">
                            {currentPartner.licenseInfo.number}
                          </span>
                        </div>
                        <div>
                          <span className="text-ink-muted block text-[11px]">Expiry Date</span>
                          <span className="font-semibold text-ink">
                            {currentPartner.licenseInfo.expiryDate}
                          </span>
                        </div>
                        <div className="col-span-2">
                          <span className="text-ink-muted block text-[11px]">Issue District</span>
                          <span className="font-medium text-ink">
                            {currentPartner.licenseInfo.issueDistrict}
                          </span>
                        </div>
                      </div>
                    </div>
                  )}

                  {currentPartner.panVatInfo && (
                    <div className="p-3.5 bg-surface-subtle rounded-xl border border-border">
                      <div className="text-[11px] font-bold text-ink uppercase flex items-center gap-1.5 mb-2">
                        <Building className="w-3.5 h-3.5 text-brand-600" />
                        <span>Inland Revenue Dept (IRD) PAN/VAT Info</span>
                      </div>
                      <div className="grid grid-cols-2 gap-2 text-xs">
                        <div>
                          <span className="text-ink-muted block text-[11px]">PAN Number</span>
                          <span className="font-mono font-bold text-ink">
                            {currentPartner.panVatInfo.panNumber}
                          </span>
                        </div>
                        <div>
                          <span className="text-ink-muted block text-[11px]">Entity Type</span>
                          <span className="font-semibold text-ink">
                            {currentPartner.panVatInfo.entityType}
                          </span>
                        </div>
                        <div className="col-span-2">
                          <span className="text-ink-muted block text-[11px]">Registered Name</span>
                          <span className="font-medium text-ink">
                            {currentPartner.panVatInfo.businessName}
                          </span>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              </div>

              {/* Zero-SQL Decision Actions */}
              <div className="pt-4 border-t border-border space-y-3">
                {currentPartner.status === "pending" ? (
                  <>
                    <button
                      onClick={handleApprove}
                      className="w-full py-3 bg-brand-600 hover:bg-brand-700 text-white rounded-xl text-xs font-bold transition-all shadow-md shadow-brand-900/30 flex items-center justify-center gap-2"
                    >
                      <CheckCircle2 className="w-4 h-4" />
                      Approve & Activate Partner Instantly
                    </button>

                    <div className="grid grid-cols-2 gap-2.5">
                      <button
                        onClick={() => {
                          setModalMode("reupload");
                          setModalReason("");
                        }}
                        className="py-2.5 bg-amber-50 hover:bg-amber-100 text-amber-800 border border-amber-300 rounded-xl text-xs font-semibold transition-colors flex items-center justify-center gap-1.5"
                      >
                        <AlertCircle className="w-3.5 h-3.5 text-amber-600" />
                        Request Re-Upload
                      </button>

                      <button
                        onClick={() => {
                          setModalMode("reject");
                          setModalReason("");
                        }}
                        className="py-2.5 bg-rose-50 hover:bg-rose-100 text-rose-800 border border-rose-300 rounded-xl text-xs font-semibold transition-colors flex items-center justify-center gap-1.5"
                      >
                        <XCircle className="w-3.5 h-3.5 text-rose-600" />
                        Reject Application
                      </button>
                    </div>
                  </>
                ) : (
                  <div className="flex items-center justify-between text-xs text-ink-muted">
                    <span>Partner audit state finalized.</span>
                    <button
                      onClick={() => {
                        setModalMode("reupload");
                        setModalReason("");
                      }}
                      className="text-brand-600 hover:underline font-semibold"
                    >
                      Re-open / Request New Documents &rarr;
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        ) : (
          <div className="bg-white p-16 rounded-2xl border border-border text-center flex-1 flex flex-col items-center justify-center">
            <CheckCircle2 className="w-12 h-12 text-brand-600 mb-3" />
            <h3 className="text-base font-bold text-ink">All Clear! No Partners Pending Review</h3>
            <p className="text-xs text-ink-muted mt-1 max-w-sm">
              The pending KYC queue is empty for this filter. Check other tabs to inspect approved,
              re-upload, or rejected partners.
            </p>
          </div>
        )}
      </div>

      {/* Modal for Reject or Request Re-Upload */}
      {modalMode && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-border max-w-md w-full p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-bold text-ink">
                {modalMode === "reject" ? "Reject KYC Application" : "Request Document Re-Upload"}
              </h3>
              <button
                onClick={() => setModalMode(null)}
                className="text-slate-400 hover:text-ink text-sm"
              >
                ✕
              </button>
            </div>

            <p className="text-xs text-ink-muted">
              {modalMode === "reject"
                ? `Specify why ${currentPartner?.partnerName}'s submission is rejected. Partner will receive this reason via push notification and in-app alert.`
                : `Explain what was unclear or missing so ${currentPartner?.partnerName} can re-upload correct documents.`}
            </p>

            <textarea
              rows={3}
              value={modalReason}
              onChange={(e) => setModalReason(e.target.value)}
              placeholder={
                modalMode === "reject"
                  ? "E.g., Driving license expired in 2024; Category does not permit commercial taxi."
                  : "E.g., PAN certificate image is blurry, please take a well-lit photo of the full certificate."
              }
              className="w-full p-3 text-xs border border-border rounded-xl focus:outline-none focus:ring-1 focus:ring-brand-600 bg-surface-subtle"
            />

            <div className="flex items-center justify-end gap-2.5 pt-2">
              <button
                onClick={() => setModalMode(null)}
                className="px-4 py-2 text-xs font-semibold text-ink-muted hover:text-ink transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmModalAction}
                className={`px-4 py-2 text-xs font-bold text-white rounded-lg shadow-sm transition-colors ${
                  modalMode === "reject"
                    ? "bg-rose-600 hover:bg-rose-700"
                    : "bg-amber-600 hover:bg-amber-700"
                }`}
              >
                {modalMode === "reject" ? "Confirm Rejection" : "Send Re-Upload Request"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
