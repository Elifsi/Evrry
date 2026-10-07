"use client";

import { useState, useEffect, useMemo } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import { MasterDetailLayout } from "@/components/shared/MasterDetailLayout";
import {
  FileCheck2,
  ZoomIn,
  ZoomOut,
  RotateCw,
  SunMedium,
  CheckCircle2,
  XCircle,
  AlertCircle,
  Building,
  User,
  Bike,
  Car,
  Utensils,
  ShoppingBag,
  Bed,
  ShieldCheck,
  Search,
  ExternalLink,
  Phone,
  Calendar,
  FileText,
  BadgeCheck,
  Send,
  Sparkles,
} from "lucide-react";
import { fetchKycQueue, reviewPartnerKyc } from "@/lib/admin/api";
import { KycDocumentItem, KycStatus, PartnerType } from "@/lib/admin/types";

type VerticalCategory = "all" | "mobility" | "food" | "grocery" | "hotel";

interface VerticalTabConfig {
  id: VerticalCategory;
  label: string;
  icon: typeof Bike;
  partnerTypes?: PartnerType[];
}

const VERTICAL_TABS: VerticalTabConfig[] = [
  { id: "all", label: "All", icon: FileCheck2 },
  { id: "mobility", label: "Drivers & Riders", icon: Bike, partnerTypes: ["rider", "driver"] },
  { id: "food", label: "Restaurants", icon: Utensils, partnerTypes: ["restaurant"] },
  { id: "grocery", label: "Grocery Marts", icon: ShoppingBag, partnerTypes: ["grocery"] },
  { id: "hotel", label: "Hotels", icon: Bed, partnerTypes: ["hotel"] },
];

const REUPLOAD_REASON_OPTIONS = [
  "Bluebook page 2 (Tax Clearance stamp) blurred or faint",
  "Vehicle tax token expired (Must be valid for BS 2082/2083)",
  "Smart driving license photo cut off or unreadable",
  "Inland Revenue PAN/VAT certificate seal missing",
  "Food hygiene (DFTQC) permit expired or illegible",
  "Ward municipality business registration seal cut off",
];

export default function KycVerificationPage() {
  const [allPartners, setAllPartners] = useState<KycDocumentItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);

  // Category and Status Filters
  const [selectedVertical, setSelectedVertical] = useState<VerticalCategory>("all");
  const [activeStatus, setActiveStatus] = useState<KycStatus | "all">("pending");
  const [searchQuery, setSearchQuery] = useState("");

  // Selected Partner & Document Index
  const [selectedPartnerId, setSelectedPartnerId] = useState<string | null>(null);
  const [selectedDocIndex, setSelectedDocIndex] = useState(0);

  // Document Viewer Controls
  const [zoomLevel, setZoomLevel] = useState(1);
  const [rotation, setRotation] = useState(0);
  const [highContrast, setHighContrast] = useState(false);

  // Modals & Action Feedback
  const [showReuploadModal, setShowReuploadModal] = useState(false);
  const [selectedReuploadReasons, setSelectedReuploadReasons] = useState<string[]>([]);
  const [reuploadCustomNote, setReuploadCustomNote] = useState("");

  const [showRejectModal, setShowRejectModal] = useState(false);
  const [rejectReason, setRejectReason] = useState("");

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

  // Compute counts per category tab
  const categoryCounts = useMemo(() => {
    const counts = { all: allPartners.length, mobility: 0, food: 0, grocery: 0, hotel: 0 };
    allPartners.forEach((p) => {
      if (p.partnerType === "rider" || p.partnerType === "driver") counts.mobility++;
      else if (p.partnerType === "restaurant") counts.food++;
      else if (p.partnerType === "grocery") counts.grocery++;
      else if (p.partnerType === "hotel") counts.hotel++;
    });
    return counts;
  }, [allPartners]);

  // Partners matching category and status
  const filteredQueue = useMemo(() => {
    return allPartners.filter((p) => {
      // Vertical filter
      if (selectedVertical === "mobility" && p.partnerType !== "rider" && p.partnerType !== "driver") return false;
      if (selectedVertical === "food" && p.partnerType !== "restaurant") return false;
      if (selectedVertical === "grocery" && p.partnerType !== "grocery") return false;
      if (selectedVertical === "hotel" && p.partnerType !== "hotel") return false;

      // Status filter
      if (activeStatus !== "all" && p.status !== activeStatus) return false;

      // Search Query
      if (searchQuery.trim()) {
        const q = searchQuery.toLowerCase();
        const matchesName = p.partnerName.toLowerCase().includes(q);
        const matchesPhone = p.phoneNumber.includes(q);
        const matchesId = p.id.toLowerCase().includes(q);
        const matchesPlate = p.vehicleInfo?.plateNumber?.toLowerCase().includes(q) || false;
        const matchesPan = p.panVatInfo?.panNumber?.toLowerCase().includes(q) || false;
        return matchesName || matchesPhone || matchesId || matchesPlate || matchesPan;
      }
      return true;
    });
  }, [allPartners, selectedVertical, activeStatus, searchQuery]);

  // Active Partner
  const currentPartner = useMemo(() => {
    if (selectedPartnerId) {
      const found = filteredQueue.find((p) => p.id === selectedPartnerId);
      if (found) return found;
    }
    return filteredQueue[0] || null;
  }, [filteredQueue, selectedPartnerId]);

  // Synchronize selection
  useEffect(() => {
    if (filteredQueue.length > 0) {
      if (!selectedPartnerId || !filteredQueue.some((p) => p.id === selectedPartnerId)) {
        setSelectedPartnerId(filteredQueue[0].id);
        setSelectedDocIndex(0);
        setZoomLevel(1);
        setRotation(0);
        setHighContrast(false);
      }
    } else {
      setSelectedPartnerId(null);
    }
  }, [filteredQueue, selectedPartnerId]);

  const currentDoc = currentPartner?.documents[selectedDocIndex] || currentPartner?.documents[0];

  // Actions
  const handleApprove = async () => {
    if (!currentPartner) return;
    const res = await reviewPartnerKyc(currentPartner.id, "approved");
    if (res.success && res.updatedPartner) {
      setAllPartners((prev) =>
        prev.map((p) => (p.id === currentPartner.id ? res.updatedPartner! : p))
      );
      setActionFeedback(
        `Approved ${currentPartner.partnerName}! Partner activated instantly. Credentials and FCM alert dispatched.`
      );
      setTimeout(() => setActionFeedback(null), 5000);
    }
  };

  const handleConfirmReupload = async () => {
    if (!currentPartner) return;
    const combinedReason = [
      ...selectedReuploadReasons,
      reuploadCustomNote.trim(),
    ].filter(Boolean).join(". ");

    const finalReason = combinedReason || "Please re-upload a clear, full-page photo with all official stamps visible.";
    const res = await reviewPartnerKyc(currentPartner.id, "reupload_requested", finalReason);
    if (res.success && res.updatedPartner) {
      setAllPartners((prev) =>
        prev.map((p) => (p.id === currentPartner.id ? res.updatedPartner! : p))
      );
      setActionFeedback(`Re-upload requested from ${currentPartner.partnerName}. Push alert sent to partner app.`);
      setShowReuploadModal(false);
      setSelectedReuploadReasons([]);
      setReuploadCustomNote("");
      setTimeout(() => setActionFeedback(null), 5000);
    }
  };

  const handleConfirmReject = async () => {
    if (!currentPartner) return;
    const finalReason = rejectReason.trim() || "Documents do not meet government regulatory standards.";
    const res = await reviewPartnerKyc(currentPartner.id, "rejected", finalReason);
    if (res.success && res.updatedPartner) {
      setAllPartners((prev) =>
        prev.map((p) => (p.id === currentPartner.id ? res.updatedPartner! : p))
      );
      setActionFeedback(`Partner ${currentPartner.partnerName} rejected. Reason logged in compliance audit trail.`);
      setShowRejectModal(false);
      setRejectReason("");
      setTimeout(() => setActionFeedback(null), 5000);
    }
  };

  const toggleReuploadReason = (reason: string) => {
    setSelectedReuploadReasons((prev) =>
      prev.includes(reason) ? prev.filter((r) => r !== reason) : [...prev, reason]
    );
  };

  return (
    <div className="flex-1 flex flex-col h-screen overflow-hidden">
      <AdminHeader
        title="Partner KYC & Compliance Verification Desk"
        subtitle="Zero-SQL Government Document Audit • Smart Driving Licenses, PAN/VAT & Clearances"
        onRefresh={loadData}
        isRefreshing={isRefreshing}
      />

      {/* Top Feedback Banner */}
      {actionFeedback && (
        <div className="bg-emerald-50 border-b border-emerald-200 px-6 py-2.5 text-emerald-800 text-xs font-semibold flex items-center justify-between shrink-0">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
            <span>{actionFeedback}</span>
          </div>
          <span className="font-mono text-[11px] text-emerald-700 bg-white px-2 py-0.5 rounded border border-emerald-200">
            Audit Logged
          </span>
        </div>
      )}

      <MasterDetailLayout
        // Category Navigation Tabs at top
        topHeader={
          <div className="flex items-center justify-between gap-4">
            <div className="flex items-center gap-1.5 overflow-x-auto">
              {VERTICAL_TABS.map((tab) => {
                const Icon = tab.icon;
                const isSelected = selectedVertical === tab.id;
                const count = categoryCounts[tab.id];
                return (
                  <button
                    key={tab.id}
                    onClick={() => {
                      setSelectedVertical(tab.id);
                      setSelectedDocIndex(0);
                    }}
                    className={`flex items-center gap-2 px-3.5 py-1.5 rounded-xl text-xs font-semibold transition-all shrink-0 border ${
                      isSelected
                        ? "bg-teal-600 text-white border-teal-600 shadow-sm"
                        : "bg-slate-50 text-slate-600 border-slate-200/80 hover:bg-slate-100"
                    }`}
                  >
                    <Icon className="w-3.5 h-3.5" />
                    <span>{tab.label}</span>
                    <span
                      className={`text-[10px] px-1.5 py-0.2 rounded-full font-bold ${
                        isSelected ? "bg-white/20 text-white" : "bg-white text-slate-700 border border-slate-200/60"
                      }`}
                    >
                      {count}
                    </span>
                  </button>
                );
              })}
            </div>

            <div className="text-xs text-slate-500 font-mono hidden md:block">
              Total Applicants: {allPartners.length}
            </div>
          </div>
        }
        queueTitle="Verification Queue"
        queueCount={filteredQueue.length}
        // Filters inside Left Queue Pane
        queueFilters={
          <div className="space-y-2">
            <div className="relative">
              <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-2.5" />
              <input
                type="text"
                placeholder="Search name, phone, plate, PAN..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-9 pr-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-lg text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-500"
              />
            </div>

            <div className="grid grid-cols-4 gap-1">
              {[
                { id: "pending" as const, label: "Pending" },
                { id: "reupload_requested" as const, label: "Re-Upload" },
                { id: "approved" as const, label: "Approved" },
                { id: "all" as const, label: "All" },
              ].map((st) => (
                <button
                  key={st.id}
                  onClick={() => setActiveStatus(st.id)}
                  className={`py-1 text-[10px] font-semibold rounded-md border text-center transition-colors ${
                    activeStatus === st.id
                      ? "bg-slate-900 text-white border-slate-900"
                      : "bg-slate-50 text-slate-600 border-slate-200 hover:bg-slate-100"
                  }`}
                >
                  {st.label}
                </button>
              ))}
            </div>
          </div>
        }
        // Queue Cards (Left Pane Content)
        queueContent={
          filteredQueue.length === 0 ? (
            <div className="p-8 text-center text-xs text-slate-400">
              No applicants found matching current filter.
            </div>
          ) : (
            filteredQueue.map((item) => {
              const isSelected = currentPartner?.id === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => {
                    setSelectedPartnerId(item.id);
                    setSelectedDocIndex(0);
                    setZoomLevel(1);
                    setRotation(0);
                    setHighContrast(false);
                  }}
                  className={`w-full text-left p-3.5 transition-all flex flex-col gap-1.5 ${
                    isSelected
                      ? "bg-teal-50/50 border-l-4 border-l-teal-600 bg-white"
                      : "hover:bg-slate-50/70"
                  }`}
                >
                  <div className="flex items-start justify-between gap-1">
                    <div className="min-w-0">
                      <div className="text-xs font-bold text-slate-900 truncate">
                        {item.partnerName}
                      </div>
                      <div className="text-[11px] font-mono text-slate-500 mt-0.5">
                        {item.vehicleInfo?.plateNumber || item.panVatInfo?.panNumber || item.phoneNumber}
                      </div>
                    </div>

                    <span
                      className={`text-[9px] px-2 py-0.5 rounded font-mono font-bold uppercase shrink-0 ${
                        item.status === "approved"
                          ? "bg-emerald-50 text-emerald-700 border border-emerald-200"
                          : item.status === "rejected"
                          ? "bg-rose-50 text-rose-700 border border-rose-200"
                          : item.status === "reupload_requested"
                          ? "bg-amber-50 text-amber-700 border border-amber-200"
                          : "bg-teal-50 text-teal-700 border border-teal-200"
                      }`}
                    >
                      {item.status.replace("_", " ")}
                    </span>
                  </div>

                  <div className="flex items-center justify-between text-[10px] text-slate-400 pt-1">
                    <span className="font-mono uppercase text-slate-500 font-semibold">
                      {item.partnerType} • {item.documents.length} docs
                    </span>
                    <span>{item.submittedAt}</span>
                  </div>
                </button>
              );
            })
          )
        }
        // Inspection Desk (Right Pane Content)
        detailContent={
          currentPartner ? (
            <div className="space-y-6">
              {/* Partner Overview Card */}
              <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div>
                  <div className="flex items-center gap-2.5">
                    <h3 className="text-base font-bold text-slate-900">{currentPartner.partnerName}</h3>
                    <span
                      className={`text-[10px] font-bold px-2 py-0.5 rounded uppercase font-mono ${
                        currentPartner.status === "approved"
                          ? "bg-emerald-50 text-emerald-700 border border-emerald-200"
                          : currentPartner.status === "rejected"
                          ? "bg-rose-50 text-rose-700 border border-rose-200"
                          : currentPartner.status === "reupload_requested"
                          ? "bg-amber-50 text-amber-700 border border-amber-200"
                          : "bg-teal-50 text-teal-700 border border-teal-200"
                      }`}
                    >
                      {currentPartner.status.replace("_", " ")}
                    </span>
                  </div>
                  <div className="text-xs text-slate-500 mt-1 flex flex-wrap items-center gap-3 font-mono">
                    <span>Phone: {currentPartner.phoneNumber}</span>
                    <span>•</span>
                    <span>ID: {currentPartner.id}</span>
                    <span>•</span>
                    <span>Nepali BS: 2082 Kartik 12 ({currentPartner.submittedAt})</span>
                  </div>
                </div>

                <div className="shrink-0 text-right">
                  <span className="text-[10px] font-semibold text-slate-400 uppercase block">Category</span>
                  <span className="text-xs font-bold text-teal-700 uppercase">
                    {currentPartner.partnerType}
                  </span>
                </div>
              </div>

              {/* Status Alert if re-uploaded or rejected */}
              {currentPartner.rejectionReason && (
                <div className="p-4 rounded-xl border bg-amber-50 border-amber-200 text-amber-900 text-xs flex items-start gap-2.5">
                  <AlertCircle className="w-4 h-4 text-amber-600 shrink-0 mt-0.5" />
                  <div>
                    <div className="font-bold">Prior Review Note / Flagged Issue:</div>
                    <p className="mt-0.5">{currentPartner.rejectionReason}</p>
                  </div>
                </div>
              )}

              {/* High-Resolution Document Inspection Canvas */}
              <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-4 space-y-3">
                {/* Canvas Toolbar */}
                <div className="flex flex-wrap items-center justify-between gap-2 p-2.5 bg-slate-50 rounded-xl border border-slate-200/80">
                  <div className="flex items-center gap-2">
                    <span className="text-[11px] font-mono font-bold text-teal-700 bg-teal-50 px-2 py-0.5 rounded border border-teal-200/60">
                      Doc {selectedDocIndex + 1}/{currentPartner.documents.length}
                    </span>
                    <span className="text-xs font-bold text-slate-900 truncate max-w-xs">
                      {currentDoc?.title}
                    </span>
                  </div>

                  {/* Toolbar Controls */}
                  <div className="flex items-center gap-1.5">
                    <button
                      onClick={() => setZoomLevel((z) => Math.min(z + 0.25, 2.5))}
                      className="p-1.5 bg-white hover:bg-slate-100 text-slate-700 border border-slate-200 rounded-lg transition-colors"
                      title="Zoom In (+)"
                    >
                      <ZoomIn className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => setZoomLevel((z) => Math.max(z - 0.25, 0.75))}
                      className="p-1.5 bg-white hover:bg-slate-100 text-slate-700 border border-slate-200 rounded-lg transition-colors"
                      title="Zoom Out (-)"
                    >
                      <ZoomOut className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => setRotation((r) => (r + 90) % 360)}
                      className="p-1.5 bg-white hover:bg-slate-100 text-slate-700 border border-slate-200 rounded-lg transition-colors"
                      title="Rotate 90°"
                    >
                      <RotateCw className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => setHighContrast(!highContrast)}
                      className={`flex items-center gap-1 px-2.5 py-1.5 rounded-lg border text-xs font-semibold transition-colors ${
                        highContrast
                          ? "bg-teal-600 text-white border-teal-600 shadow-sm"
                          : "bg-white hover:bg-slate-100 text-slate-700 border-slate-200"
                      }`}
                      title="Toggle High-Contrast Stamp Filter (Inspect faint government seals & watermarks)"
                    >
                      <SunMedium className="w-3.5 h-3.5" />
                      <span>High-Contrast Stamp Filter</span>
                    </button>
                    {currentDoc?.imageUrl && (
                      <a
                        href={currentDoc.imageUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="p-1.5 bg-white hover:bg-slate-100 text-slate-700 border border-slate-200 rounded-lg transition-colors ml-1"
                        title="Open Full Resolution"
                      >
                        <ExternalLink className="w-4 h-4" />
                      </a>
                    )}
                  </div>
                </div>

                {/* Viewport Canvas */}
                <div className="bg-slate-50/75 border border-slate-200/80 rounded-xl min-h-[380px] max-h-[460px] flex items-center justify-center overflow-auto p-4 relative select-none">
                  {currentDoc ? (
                    // eslint-disable-next-line @next/next/no-img-element
                    <img
                      src={currentDoc.imageUrl}
                      alt={currentDoc.title}
                      style={{
                        transform: `scale(${zoomLevel}) rotate(${rotation}deg)`,
                        filter: highContrast
                          ? "contrast(200%) brightness(90%) grayscale(25%)"
                          : "none",
                        transition: "transform 0.15s ease-out, filter 0.2s ease",
                      }}
                      className="max-h-[400px] object-contain rounded-lg shadow-sm origin-center"
                    />
                  ) : (
                    <div className="text-slate-400 text-xs italic">No document image available</div>
                  )}
                </div>

                {/* Document Selector Pills */}
                <div className="flex items-center justify-between gap-2 pt-1 border-t border-slate-100">
                  <div className="flex items-center gap-2 overflow-x-auto">
                    {currentPartner.documents.map((doc, dIdx) => (
                      <button
                        key={doc.type}
                        onClick={() => {
                          setSelectedDocIndex(dIdx);
                          setZoomLevel(1);
                          setRotation(0);
                        }}
                        className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-colors whitespace-nowrap border ${
                          selectedDocIndex === dIdx
                            ? "bg-teal-600 text-white border-teal-600 font-semibold shadow-sm"
                            : "bg-slate-50 text-slate-600 border-slate-200 hover:bg-slate-100"
                        }`}
                      >
                        {doc.title.split("(")[0]}
                      </button>
                    ))}
                  </div>

                  <span className="text-[11px] font-mono text-slate-400 shrink-0">
                    Zoom: {Math.round(zoomLevel * 100)}%
                  </span>
                </div>
              </div>

              {/* Required Documents Checklist Card */}
              <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5 space-y-4">
                <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                  <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2">
                    <ShieldCheck className="w-4 h-4 text-teal-600" />
                    Government Verification Checklist
                  </h4>
                  <span className="text-[11px] text-slate-400 font-mono">Nepal IRD & DoTM Standard</span>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
                  {/* Drivers / Riders */}
                  {currentPartner.vehicleInfo && (
                    <div className="p-3.5 bg-slate-50/80 rounded-xl border border-slate-200/80 space-y-2">
                      <div className="font-bold text-slate-800 flex items-center gap-1.5">
                        <Bike className="w-3.5 h-3.5 text-teal-600" />
                        <span>Vehicle & Tax Clearance (DoTM)</span>
                      </div>
                      <div className="space-y-1 font-mono text-[11px]">
                        <div className="flex justify-between">
                          <span className="text-slate-500">Plate:</span>
                          <span className="font-bold text-slate-900">{currentPartner.vehicleInfo.plateNumber}</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-slate-500">Model:</span>
                          <span className="text-slate-800">{currentPartner.vehicleInfo.model}</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-slate-500">Tax Token:</span>
                          <span className="font-bold text-emerald-700">BS {currentPartner.vehicleInfo.taxClearanceYear} (Valid)</span>
                        </div>
                      </div>
                    </div>
                  )}

                  {currentPartner.licenseInfo && (
                    <div className="p-3.5 bg-slate-50/80 rounded-xl border border-slate-200/80 space-y-2">
                      <div className="font-bold text-slate-800 flex items-center gap-1.5">
                        <BadgeCheck className="w-3.5 h-3.5 text-teal-600" />
                        <span>Smart Driving License (Cat A/B)</span>
                      </div>
                      <div className="space-y-1 font-mono text-[11px]">
                        <div className="flex justify-between">
                          <span className="text-slate-500">License No:</span>
                          <span className="font-bold text-slate-900">{currentPartner.licenseInfo.number}</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-slate-500">Expiry Date:</span>
                          <span className="text-slate-800">{currentPartner.licenseInfo.expiryDate}</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-slate-500">Issue Office:</span>
                          <span className="text-slate-800">{currentPartner.licenseInfo.issueDistrict}</span>
                        </div>
                      </div>
                    </div>
                  )}

                  {/* Merchants / Hotels */}
                  {currentPartner.panVatInfo && (
                    <div className="p-3.5 bg-slate-50/80 rounded-xl border border-slate-200/80 space-y-2 col-span-1 md:col-span-2">
                      <div className="font-bold text-slate-800 flex items-center gap-1.5">
                        <Building className="w-3.5 h-3.5 text-teal-600" />
                        <span>Inland Revenue Dept (IRD) PAN/VAT & Business Deed</span>
                      </div>
                      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 font-mono text-[11px]">
                        <div>
                          <span className="text-slate-500 block">PAN/VAT:</span>
                          <span className="font-bold text-slate-900 text-xs">{currentPartner.panVatInfo.panNumber}</span>
                        </div>
                        <div>
                          <span className="text-slate-500 block">Entity Name:</span>
                          <span className="font-bold text-slate-900">{currentPartner.panVatInfo.businessName}</span>
                        </div>
                        <div>
                          <span className="text-slate-500 block">Classification:</span>
                          <span className="text-slate-800">{currentPartner.panVatInfo.entityType}</span>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              </div>

              {/* Sticky Bottom Action Tray */}
              <div className="sticky bottom-0 bg-white/95 backdrop-blur border border-slate-200/80 p-4 rounded-2xl shadow-lg flex flex-col sm:flex-row items-center justify-between gap-3">
                <div className="text-xs text-slate-500 hidden sm:block">
                  Verified by Operator with immutable audit log.
                </div>

                <div className="flex items-center gap-2.5 w-full sm:w-auto">
                  <button
                    onClick={() => setShowReuploadModal(true)}
                    className="flex-1 sm:flex-initial px-4 py-2.5 bg-amber-50 hover:bg-amber-100 text-amber-800 border border-amber-200 rounded-xl text-xs font-bold transition-colors flex items-center justify-center gap-1.5"
                  >
                    <AlertCircle className="w-4 h-4 text-amber-600" />
                    Request Re-Upload
                  </button>

                  <button
                    onClick={() => setShowRejectModal(true)}
                    className="flex-1 sm:flex-initial px-4 py-2.5 bg-rose-50 hover:bg-rose-100 text-rose-800 border border-rose-200 rounded-xl text-xs font-bold transition-colors flex items-center justify-center gap-1.5"
                  >
                    <XCircle className="w-4 h-4 text-rose-600" />
                    Reject Application
                  </button>

                  <button
                    onClick={handleApprove}
                    className="flex-1 sm:flex-initial px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition-all shadow-sm flex items-center justify-center gap-1.5"
                  >
                    <CheckCircle2 className="w-4 h-4" />
                    1-Click Approve & Activate
                  </button>
                </div>
              </div>
            </div>
          ) : (
            <div className="bg-white rounded-2xl border border-slate-200/80 p-16 text-center flex flex-col items-center justify-center">
              <FileCheck2 className="w-12 h-12 text-slate-300 mb-3" />
              <h3 className="text-base font-bold text-slate-900">No Partner Selected</h3>
              <p className="text-xs text-slate-500 mt-1 max-w-sm">
                Select an applicant from the left queue to inspect government documents and render a compliance decision.
              </p>
            </div>
          )
        }
      />

      {/* Modal: Request Re-Upload */}
      {showReuploadModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-slate-200 max-w-md w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-sm font-bold text-slate-900">
                Request Document Re-Upload
              </h3>
              <button
                onClick={() => setShowReuploadModal(false)}
                className="text-slate-400 hover:text-slate-700 text-sm"
              >
                ✕
              </button>
            </div>

            <p className="text-xs text-slate-500">
              Select specific deficiencies for {currentPartner?.partnerName}. Partner will receive a push notification with instructions.
            </p>

            <div className="space-y-2 text-xs">
              {REUPLOAD_REASON_OPTIONS.map((reason) => (
                <label
                  key={reason}
                  className="flex items-start gap-2.5 p-2 rounded-lg hover:bg-slate-50 cursor-pointer border border-transparent hover:border-slate-200"
                >
                  <input
                    type="checkbox"
                    checked={selectedReuploadReasons.includes(reason)}
                    onChange={() => toggleReuploadReason(reason)}
                    className="mt-0.5 rounded border-slate-300 text-teal-600 focus:ring-teal-500"
                  />
                  <span className="text-slate-700">{reason}</span>
                </label>
              ))}
            </div>

            <div>
              <label className="block text-[11px] font-semibold text-slate-700 mb-1">
                Additional Instructions (Optional):
              </label>
              <textarea
                rows={2}
                value={reuploadCustomNote}
                onChange={(e) => setReuploadCustomNote(e.target.value)}
                placeholder="e.g. Please take photo directly under white light."
                className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-teal-500/20"
              />
            </div>

            <div className="flex items-center justify-end gap-2.5 pt-2 border-t border-slate-100">
              <button
                onClick={() => setShowReuploadModal(false)}
                className="px-4 py-2 text-xs font-semibold text-slate-500 hover:text-slate-800"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmReupload}
                className="px-5 py-2 text-xs font-bold text-white bg-amber-600 hover:bg-amber-700 rounded-xl shadow-sm"
              >
                Dispatch Re-Upload Alert
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal: Reject Application */}
      {showRejectModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-slate-200 max-w-md w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-sm font-bold text-slate-900">
                Reject KYC Application
              </h3>
              <button
                onClick={() => setShowRejectModal(false)}
                className="text-slate-400 hover:text-slate-700 text-sm"
              >
                ✕
              </button>
            </div>

            <p className="text-xs text-slate-500">
              Specify the compliance reason for rejecting {currentPartner?.partnerName}. This will be permanently logged.
            </p>

            <textarea
              rows={3}
              value={rejectReason}
              onChange={(e) => setRejectReason(e.target.value)}
              placeholder="e.g. Driving license Category A expired in 2024 and does not qualify for commercial operations."
              className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-rose-500/20"
            />

            <div className="flex items-center justify-end gap-2.5 pt-2 border-t border-slate-100">
              <button
                onClick={() => setShowRejectModal(false)}
                className="px-4 py-2 text-xs font-semibold text-slate-500 hover:text-slate-800"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmReject}
                className="px-5 py-2 text-xs font-bold text-white bg-rose-600 hover:bg-rose-700 rounded-xl shadow-sm"
              >
                Confirm Rejection
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
