"use client";

import { useState, useEffect, useMemo } from "react";
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
  Layers,
  FileText,
} from "lucide-react";
import { fetchKycQueue, reviewPartnerKyc } from "@/lib/admin/api";
import { KycDocumentItem, KycStatus, PartnerType } from "@/lib/admin/types";

type VerticalCategory = "mobility" | "food" | "grocery" | "hotel";

interface VerticalConfig {
  id: VerticalCategory;
  label: string;
  icon: typeof Bike;
  partnerTypes: PartnerType[];
  subtitle: string;
}

const VERTICAL_TABS: VerticalConfig[] = [
  {
    id: "mobility",
    label: "Drivers & Riders",
    icon: Bike,
    partnerTypes: ["rider", "driver"],
    subtitle: "Smart Driving Licenses, Blue Books, Tax Clearances",
  },
  {
    id: "food",
    label: "Restaurants & Cafes",
    icon: Utensils,
    partnerTypes: ["restaurant"],
    subtitle: "IRD PAN/VAT Certificates & Food Safety Clearances",
  },
  {
    id: "grocery",
    label: "Grocery & Marts",
    icon: ShoppingBag,
    partnerTypes: ["grocery"],
    subtitle: "Retail Mart Registrations & Ward Operating Permits",
  },
  {
    id: "hotel",
    label: "Hotels & Stays",
    icon: Bed,
    partnerTypes: ["hotel"],
    subtitle: "Tourism Board Licenses, Property Deeds & Tax Filings",
  },
];

export default function KycVerificationPage() {
  const [allPartners, setAllPartners] = useState<KycDocumentItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);

  // Vertical Selection (Default to Mobility)
  const [selectedVertical, setSelectedVertical] = useState<VerticalCategory>("mobility");

  // Status Filter within the active vertical (Default strictly to "pending")
  const [activeStatus, setActiveStatus] = useState<KycStatus>("pending");
  const [searchQuery, setSearchQuery] = useState("");

  // Selected item and document index
  const [selectedPartnerId, setSelectedPartnerId] = useState<string | null>(null);
  const [selectedDocIndex, setSelectedDocIndex] = useState(0);

  // Document Viewer Controls
  const [zoomLevel, setZoomLevel] = useState(1);
  const [rotation, setRotation] = useState(0);
  const [highContrast, setHighContrast] = useState(false);

  // Action Modals & Feedback
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

  // Compute pending counts per vertical for badges
  const pendingCountsByVertical = useMemo(() => {
    const counts: Record<VerticalCategory, number> = {
      mobility: 0,
      food: 0,
      grocery: 0,
      hotel: 0,
    };
    allPartners.forEach((p) => {
      if (p.status === "pending") {
        if (p.partnerType === "rider" || p.partnerType === "driver") {
          counts.mobility++;
        } else if (p.partnerType === "restaurant") {
          counts.food++;
        } else if (p.partnerType === "grocery") {
          counts.grocery++;
        } else if (p.partnerType === "hotel") {
          counts.hotel++;
        }
      }
    });
    return counts;
  }, [allPartners]);

  // Current vertical config
  const activeVerticalConfig =
    VERTICAL_TABS.find((v) => v.id === selectedVertical) || VERTICAL_TABS[0];

  // Partners matching the current vertical
  const verticalPartners = useMemo(() => {
    return allPartners.filter((p) =>
      activeVerticalConfig.partnerTypes.includes(p.partnerType)
    );
  }, [allPartners, activeVerticalConfig]);

  // Counts for status tabs within current vertical
  const statusCounts = useMemo(() => {
    return {
      pending: verticalPartners.filter((p) => p.status === "pending").length,
      approved: verticalPartners.filter((p) => p.status === "approved").length,
      reupload_requested: verticalPartners.filter(
        (p) => p.status === "reupload_requested"
      ).length,
      rejected: verticalPartners.filter((p) => p.status === "rejected").length,
    };
  }, [verticalPartners]);

  // Final filtered list for the left queue pane
  const filteredQueue = useMemo(() => {
    return verticalPartners.filter((p) => {
      if (p.status !== activeStatus) return false;
      if (searchQuery.trim()) {
        const q = searchQuery.toLowerCase();
        const matchesName = p.partnerName.toLowerCase().includes(q);
        const matchesPhone = p.phoneNumber.includes(q);
        const matchesId = p.id.toLowerCase().includes(q);
        const matchesPlate =
          p.vehicleInfo?.plateNumber?.toLowerCase().includes(q) || false;
        const matchesPan =
          p.panVatInfo?.panNumber?.toLowerCase().includes(q) || false;
        return matchesName || matchesPhone || matchesId || matchesPlate || matchesPan;
      }
      return true;
    });
  }, [verticalPartners, activeStatus, searchQuery]);

  // Currently focused partner
  const currentPartner = useMemo(() => {
    if (selectedPartnerId) {
      const found = filteredQueue.find((p) => p.id === selectedPartnerId);
      if (found) return found;
    }
    return filteredQueue[0] || null;
  }, [filteredQueue, selectedPartnerId]);

  // Keep selectedPartnerId synchronized
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

  const currentDoc =
    currentPartner?.documents[selectedDocIndex] || currentPartner?.documents[0];

  const handleApprove = async () => {
    if (!currentPartner) return;
    const res = await reviewPartnerKyc(currentPartner.id, "approved");
    if (res.success && res.updatedPartner) {
      setAllPartners((prev) =>
        prev.map((p) => (p.id === currentPartner.id ? res.updatedPartner! : p))
      );
      setActionFeedback(
        `Approved ${currentPartner.partnerName}! Partner account active, credentials and welcome alert dispatched.`
      );
      setTimeout(() => setActionFeedback(null), 5000);
    }
  };

  const handleConfirmModalAction = async () => {
    if (!currentPartner || !modalMode) return;

    const targetStatus: KycStatus =
      modalMode === "reject" ? "rejected" : "reupload_requested";
    const reasonText =
      modalReason.trim() ||
      (modalMode === "reject"
        ? "Government documents do not meet regulatory standards."
        : "Please re-upload a clear, full-page photo with all official stamps visible.");

    const res = await reviewPartnerKyc(currentPartner.id, targetStatus, reasonText);
    if (res.success && res.updatedPartner) {
      setAllPartners((prev) =>
        prev.map((p) => (p.id === currentPartner.id ? res.updatedPartner! : p))
      );
      setActionFeedback(
        modalMode === "reject"
          ? `Partner ${currentPartner.partnerName} rejected. Reason logged in compliance audit trail.`
          : `Re-upload requested from ${currentPartner.partnerName}. Push alert and in-app action item sent.`
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
        subtitle="Zero-SQL Government Document Audit • Smart Driving Licenses, PAN/VAT & Clearances"
        onRefresh={loadData}
        isRefreshing={isRefreshing}
      />

      <div className="p-6 md:p-8 space-y-6 flex-1 flex flex-col max-w-[1600px] w-full mx-auto">
        {/* Feedback Alert Toast */}
        {actionFeedback && (
          <div className="bg-brand-50 border border-brand-200 p-4 rounded-2xl text-brand-900 text-xs flex items-center justify-between shadow-sm animate-fade-in">
            <div className="flex items-center gap-2.5">
              <CheckCircle2 className="w-5 h-5 text-brand-600 shrink-0" />
              <span className="font-semibold">{actionFeedback}</span>
            </div>
            <span className="text-[11px] font-mono text-brand-700 bg-white px-2.5 py-1 rounded-md border border-brand-200">
              Dispatched: FCM / APNs & In-App Alert
            </span>
          </div>
        )}

        {/* 1. TOP-LEVEL VERTICAL CATEGORY TABS */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
          {VERTICAL_TABS.map((tab) => {
            const Icon = tab.icon;
            const isSelected = selectedVertical === tab.id;
            const pendingCount = pendingCountsByVertical[tab.id];

            return (
              <button
                key={tab.id}
                onClick={() => {
                  setSelectedVertical(tab.id);
                  setSelectedDocIndex(0);
                  setZoomLevel(1);
                  setRotation(0);
                  setHighContrast(false);
                }}
                className={`p-4 rounded-2xl border text-left transition-all relative flex flex-col justify-between ${
                  isSelected
                    ? "bg-white border-brand-600 ring-2 ring-brand-500/20 shadow-sm"
                    : "bg-white/80 border-border hover:border-slate-300 hover:bg-white text-ink"
                }`}
              >
                <div className="flex items-center justify-between mb-2">
                  <div
                    className={`w-9 h-9 rounded-xl flex items-center justify-center transition-colors ${
                      isSelected
                        ? "bg-brand-600 text-white"
                        : "bg-surface-subtle text-ink-muted"
                    }`}
                  >
                    <Icon className="w-4 h-4" />
                  </div>
                  {pendingCount > 0 ? (
                    <span className="text-[11px] font-bold px-2 py-0.5 rounded-full bg-amber-100 text-amber-800 border border-amber-200">
                      {pendingCount} Pending
                    </span>
                  ) : (
                    <span className="text-[11px] font-medium px-2 py-0.5 rounded-full bg-slate-100 text-ink-subtle">
                      0 Pending
                    </span>
                  )}
                </div>
                <div>
                  <h3 className="text-sm font-bold text-ink">{tab.label}</h3>
                  <p className="text-[11px] text-ink-muted line-clamp-1 mt-0.5">
                    {tab.subtitle}
                  </p>
                </div>
              </button>
            );
          })}
        </div>

        {/* 2. STATUS SUB-TABS & SEARCH BAR */}
        <div className="bg-white p-3 rounded-2xl border border-border shadow-sm flex flex-col md:flex-row items-stretch md:items-center justify-between gap-3">
          {/* Status Sub-Filters */}
          <div className="flex items-center gap-1.5 overflow-x-auto pb-1 md:pb-0">
            {[
              {
                id: "pending" as KycStatus,
                label: "Pending Review",
                count: statusCounts.pending,
                badgeClass: "bg-amber-100 text-amber-800",
              },
              {
                id: "approved" as KycStatus,
                label: "Approved Directory",
                count: statusCounts.approved,
                badgeClass: "bg-emerald-100 text-emerald-800",
              },
              {
                id: "reupload_requested" as KycStatus,
                label: "Re-Upload Queue",
                count: statusCounts.reupload_requested,
                badgeClass: "bg-amber-100 text-amber-800",
              },
              {
                id: "rejected" as KycStatus,
                label: "Rejected Archive",
                count: statusCounts.rejected,
                badgeClass: "bg-rose-100 text-rose-800",
              },
            ].map((st) => {
              const isActive = activeStatus === st.id;
              return (
                <button
                  key={st.id}
                  onClick={() => {
                    setActiveStatus(st.id);
                    setSelectedDocIndex(0);
                    setZoomLevel(1);
                    setRotation(0);
                    setHighContrast(false);
                  }}
                  className={`flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold transition-all shrink-0 ${
                    isActive
                      ? "bg-brand-600 text-white shadow-sm"
                      : "bg-surface-subtle text-ink-muted hover:text-ink hover:bg-slate-100"
                  }`}
                >
                  <span>{st.label}</span>
                  <span
                    className={`text-[11px] px-1.5 py-0.2 rounded-full font-bold ${
                      isActive ? "bg-white/20 text-white" : st.badgeClass
                    }`}
                  >
                    {st.count}
                  </span>
                </button>
              );
            })}
          </div>

          {/* Search Box */}
          <div className="relative shrink-0 w-full md:w-64">
            <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-2.5" />
            <input
              type="text"
              placeholder="Search name, phone, plate, PAN..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-9 pr-3 py-1.5 text-xs bg-surface-subtle border border-border rounded-xl text-ink focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-600"
            />
          </div>
        </div>

        {/* 3. VERTICAL MASTER-DETAIL WORKSPACE */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 flex-1 items-start">
          {/* Left Column: Vertical Partner Queue List (4.5 cols) */}
          <div className="lg:col-span-4 bg-white rounded-2xl border border-border shadow-sm p-4 flex flex-col h-[750px]">
            <div className="flex items-center justify-between pb-3 border-b border-border mb-3">
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold text-ink">
                  {activeStatus === "pending"
                    ? "Pending Applicants"
                    : activeStatus === "approved"
                    ? "Verified Partners"
                    : activeStatus === "reupload_requested"
                    ? "Awaiting Documents"
                    : "Rejected Records"}
                </span>
                <span className="text-[11px] font-mono text-ink-muted bg-surface-subtle px-2 py-0.5 rounded-full border border-border">
                  {filteredQueue.length}
                </span>
              </div>
              <span className="text-[11px] font-semibold text-brand-600">
                {activeVerticalConfig.label}
              </span>
            </div>

            {filteredQueue.length === 0 ? (
              <div className="flex-1 flex flex-col items-center justify-center p-6 text-center text-ink-muted">
                <FileCheck2 className="w-10 h-10 text-slate-300 mb-2" />
                <p className="text-xs font-semibold text-ink">No applicants in this queue</p>
                <p className="text-[11px] text-ink-muted mt-0.5 max-w-[200px]">
                  All caught up for {activeVerticalConfig.label} under {activeStatus.replace("_", " ")}.
                </p>
              </div>
            ) : (
              <div className="flex-1 overflow-y-auto space-y-2.5 pr-1">
                {filteredQueue.map((item) => {
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
                      className={`w-full text-left p-3.5 rounded-xl border transition-all flex flex-col gap-2 ${
                        isSelected
                          ? "bg-brand-50/50 border-brand-500 ring-1 ring-brand-500/20 shadow-sm"
                          : "bg-white border-border hover:bg-slate-50/70 hover:border-slate-300"
                      }`}
                    >
                      <div className="flex items-start justify-between gap-2">
                        <div className="flex items-center gap-2.5 min-w-0">
                          <div
                            className={`w-8 h-8 rounded-lg flex items-center justify-center shrink-0 text-xs font-bold ${
                              isSelected
                                ? "bg-brand-600 text-white"
                                : "bg-surface-subtle text-ink-muted"
                            }`}
                          >
                            {item.partnerType === "rider" ? (
                              <Bike className="w-4 h-4" />
                            ) : item.partnerType === "driver" ? (
                              <Car className="w-4 h-4" />
                            ) : item.partnerType === "restaurant" ? (
                              <Utensils className="w-4 h-4" />
                            ) : item.partnerType === "grocery" ? (
                              <ShoppingBag className="w-4 h-4" />
                            ) : (
                              <Bed className="w-4 h-4" />
                            )}
                          </div>
                          <div className="min-w-0">
                            <h4 className="text-xs font-bold text-ink truncate">
                              {item.partnerName}
                            </h4>
                            <p className="text-[11px] text-ink-muted font-mono flex items-center gap-1 mt-0.5">
                              <Phone className="w-3 h-3 text-slate-400" />
                              {item.phoneNumber}
                            </p>
                          </div>
                        </div>

                        <span
                          className={`text-[10px] px-2 py-0.5 rounded font-mono font-bold uppercase shrink-0 ${
                            item.partnerType === "rider"
                              ? "bg-brand-100 text-brand-800"
                              : item.partnerType === "driver"
                              ? "bg-sky-100 text-sky-800"
                              : item.partnerType === "restaurant"
                              ? "bg-amber-100 text-amber-800"
                              : item.partnerType === "grocery"
                              ? "bg-emerald-100 text-emerald-800"
                              : "bg-purple-100 text-purple-800"
                          }`}
                        >
                          {item.partnerType}
                        </span>
                      </div>

                      <div className="flex items-center justify-between text-[11px] text-ink-muted pt-1 border-t border-slate-100">
                        <span className="flex items-center gap-1 font-mono">
                          <FileText className="w-3 h-3 text-slate-400" />
                          {item.documents.length} docs
                        </span>
                        <span className="text-[10px] text-ink-subtle">{item.submittedAt}</span>
                      </div>
                    </button>
                  );
                })}
              </div>
            )}
          </div>

          {/* Right Column: Document Review Canvas & Audit Console (8 cols) */}
          {currentPartner ? (
            <div className="lg:col-span-8 space-y-6">
              {/* Document Review Canvas (Clean Light Surface) */}
              <div className="bg-white rounded-2xl border border-border shadow-sm p-5 space-y-4">
                {/* Canvas Toolbar */}
                <div className="flex flex-wrap items-center justify-between gap-3 bg-surface-subtle p-3 rounded-xl border border-border">
                  <div className="flex items-center gap-2">
                    <span className="text-[11px] font-mono font-bold text-brand-700 bg-brand-50 px-2 py-0.5 rounded border border-brand-200">
                      Doc {selectedDocIndex + 1} of {currentPartner.documents.length}
                    </span>
                    <span className="text-xs font-bold text-ink">
                      {currentDoc?.title}
                    </span>
                  </div>

                  <div className="flex items-center gap-1.5">
                    <button
                      onClick={() => setZoomLevel((z) => Math.min(z + 0.25, 2.5))}
                      className="p-1.5 bg-white hover:bg-slate-100 text-ink border border-border rounded-lg transition-colors"
                      title="Zoom In"
                    >
                      <ZoomIn className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => setZoomLevel((z) => Math.max(z - 0.25, 0.75))}
                      className="p-1.5 bg-white hover:bg-slate-100 text-ink border border-border rounded-lg transition-colors"
                      title="Zoom Out"
                    >
                      <ZoomOut className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => setRotation((r) => (r + 90) % 360)}
                      className="p-1.5 bg-white hover:bg-slate-100 text-ink border border-border rounded-lg transition-colors"
                      title="Rotate 90°"
                    >
                      <RotateCw className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => setHighContrast(!highContrast)}
                      className={`p-1.5 rounded-lg border transition-colors ${
                        highContrast
                          ? "bg-brand-600 text-white border-brand-600"
                          : "bg-white hover:bg-slate-100 text-ink border-border"
                      }`}
                      title="High Contrast (For faint stamps & seals)"
                    >
                      <SunMedium className="w-4 h-4" />
                    </button>
                    {currentDoc?.imageUrl && (
                      <a
                        href={currentDoc.imageUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="p-1.5 bg-white hover:bg-slate-100 text-ink border border-border rounded-lg transition-colors ml-1"
                        title="Open Full Resolution"
                      >
                        <ExternalLink className="w-4 h-4" />
                      </a>
                    )}
                  </div>
                </div>

                {/* Viewport with Document Image */}
                <div className="bg-slate-50/70 border border-slate-200 rounded-xl min-h-[380px] max-h-[460px] flex items-center justify-center overflow-auto p-4 relative select-none">
                  {currentDoc ? (
                    // eslint-disable-next-line @next/next/no-img-element
                    <img
                      src={currentDoc.imageUrl}
                      alt={currentDoc.title}
                      style={{
                        transform: `scale(${zoomLevel}) rotate(${rotation}deg)`,
                        filter: highContrast
                          ? "contrast(180%) brightness(95%) grayscale(20%)"
                          : "none",
                        transition: "transform 0.15s ease-out, filter 0.2s ease",
                      }}
                      className="max-h-[400px] object-contain rounded-lg shadow-md origin-center"
                    />
                  ) : (
                    <div className="text-slate-400 text-xs">No document preview available</div>
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
                            ? "bg-brand-600 text-white border-brand-600 font-semibold shadow-sm"
                            : "bg-surface-subtle text-ink-muted border-border hover:bg-slate-100"
                        }`}
                      >
                        {doc.title.split("(")[0]}
                      </button>
                    ))}
                  </div>

                  <span className="text-[11px] font-mono text-ink-muted shrink-0 pl-2">
                    Zoom: {Math.round(zoomLevel * 100)}%
                  </span>
                </div>
              </div>

              {/* Structured Metadata & Zero-SQL Decision Actions */}
              <div className="bg-white rounded-2xl border border-border shadow-sm p-6 space-y-6">
                {/* Applicant Header */}
                <div className="flex items-start justify-between pb-4 border-b border-border">
                  <div>
                    <div className="flex items-center gap-2">
                      <h3 className="text-base font-bold text-ink">{currentPartner.partnerName}</h3>
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
                    <p className="text-xs text-ink-muted mt-1 font-mono">
                      Phone: {currentPartner.phoneNumber} • ID: {currentPartner.id}
                    </p>
                  </div>

                  <div className="text-right">
                    <span className="text-[10px] text-ink-subtle uppercase block font-semibold">
                      Submitted On
                    </span>
                    <span className="text-xs font-semibold text-ink">
                      {currentPartner.submittedAt}
                    </span>
                  </div>
                </div>

                {/* Status Notice if not pending */}
                {currentPartner.status !== "pending" && (
                  <div
                    className={`p-4 rounded-xl border text-xs flex items-start gap-2.5 ${
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
                          ? "Partner Verified & Activated"
                          : currentPartner.status === "rejected"
                          ? "Application Rejected"
                          : "Awaiting Re-Upload from Partner"}
                      </div>
                      {currentPartner.rejectionReason && (
                        <div className="mt-1 text-[11px] text-slate-700">
                          Note: {currentPartner.rejectionReason}
                        </div>
                      )}
                    </div>
                  </div>
                )}

                {/* Vertical-Specific Verification Details */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {/* Drivers / Riders (Mobility) */}
                  {currentPartner.vehicleInfo && (
                    <div className="p-4 bg-surface-subtle rounded-xl border border-border space-y-2">
                      <div className="text-[11px] font-bold text-ink uppercase flex items-center gap-1.5">
                        <Bike className="w-3.5 h-3.5 text-brand-600" />
                        <span>Vehicle Registration & Tax</span>
                      </div>
                      <div className="space-y-1.5 text-xs">
                        <div className="flex justify-between">
                          <span className="text-ink-muted">Plate Number:</span>
                          <span className="font-mono font-bold text-ink">
                            {currentPartner.vehicleInfo.plateNumber}
                          </span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-ink-muted">Vehicle Model:</span>
                          <span className="font-semibold text-ink">
                            {currentPartner.vehicleInfo.model}
                          </span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-ink-muted">Tax Clearance:</span>
                          <span className="font-bold text-emerald-700">
                            BS {currentPartner.vehicleInfo.taxClearanceYear} (Valid)
                          </span>
                        </div>
                      </div>
                    </div>
                  )}

                  {currentPartner.licenseInfo && (
                    <div className="p-4 bg-surface-subtle rounded-xl border border-border space-y-2">
                      <div className="text-[11px] font-bold text-ink uppercase flex items-center gap-1.5">
                        <ShieldCheck className="w-3.5 h-3.5 text-brand-600" />
                        <span>Smart Driving License</span>
                      </div>
                      <div className="space-y-1.5 text-xs">
                        <div className="flex justify-between">
                          <span className="text-ink-muted">License No:</span>
                          <span className="font-mono font-bold text-ink">
                            {currentPartner.licenseInfo.number}
                          </span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-ink-muted">Expiry Date:</span>
                          <span className="font-semibold text-ink">
                            {currentPartner.licenseInfo.expiryDate}
                          </span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-ink-muted">Issue Office:</span>
                          <span className="font-semibold text-ink">
                            {currentPartner.licenseInfo.issueDistrict}
                          </span>
                        </div>
                      </div>
                    </div>
                  )}

                  {/* Merchants & Hotels (PAN/VAT & Business entity) */}
                  {currentPartner.panVatInfo && (
                    <div className="p-4 bg-surface-subtle rounded-xl border border-border space-y-2 col-span-1 md:col-span-2">
                      <div className="text-[11px] font-bold text-ink uppercase flex items-center gap-1.5">
                        <Building className="w-3.5 h-3.5 text-brand-600" />
                        <span>Inland Revenue Dept (IRD) PAN/VAT & Business Entity</span>
                      </div>
                      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs pt-1">
                        <div>
                          <span className="text-ink-muted block text-[11px]">PAN / VAT Number</span>
                          <span className="font-mono font-bold text-ink text-sm">
                            {currentPartner.panVatInfo.panNumber}
                          </span>
                        </div>
                        <div>
                          <span className="text-ink-muted block text-[11px]">Registered Legal Name</span>
                          <span className="font-semibold text-ink">
                            {currentPartner.panVatInfo.businessName}
                          </span>
                        </div>
                        <div>
                          <span className="text-ink-muted block text-[11px]">Entity Classification</span>
                          <span className="font-semibold text-ink">
                            {currentPartner.panVatInfo.entityType}
                          </span>
                        </div>
                      </div>
                    </div>
                  )}
                </div>

                {/* Decision Actions Bar */}
                <div className="pt-4 border-t border-border">
                  {currentPartner.status === "pending" ? (
                    <div className="flex flex-col sm:flex-row items-center gap-3">
                      <button
                        onClick={handleApprove}
                        className="w-full sm:flex-1 py-3 bg-brand-600 hover:bg-brand-700 text-white rounded-xl text-xs font-bold transition-all shadow-md shadow-brand-900/20 flex items-center justify-center gap-2"
                      >
                        <CheckCircle2 className="w-4 h-4" />
                        Approve & Activate Partner Instantly
                      </button>

                      <button
                        onClick={() => {
                          setModalMode("reupload");
                          setModalReason("");
                        }}
                        className="w-full sm:w-auto px-5 py-3 bg-amber-50 hover:bg-amber-100 text-amber-800 border border-amber-300 rounded-xl text-xs font-bold transition-colors flex items-center justify-center gap-1.5"
                      >
                        <AlertCircle className="w-3.5 h-3.5 text-amber-600" />
                        Request Re-Upload
                      </button>

                      <button
                        onClick={() => {
                          setModalMode("reject");
                          setModalReason("");
                        }}
                        className="w-full sm:w-auto px-5 py-3 bg-rose-50 hover:bg-rose-100 text-rose-800 border border-rose-300 rounded-xl text-xs font-bold transition-colors flex items-center justify-center gap-1.5"
                      >
                        <XCircle className="w-3.5 h-3.5 text-rose-600" />
                        Reject Application
                      </button>
                    </div>
                  ) : (
                    <div className="flex items-center justify-between text-xs text-ink-muted bg-surface-subtle p-3 rounded-xl border border-border">
                      <span>Audit decision recorded in database logs.</span>
                      <button
                        onClick={() => {
                          setModalMode("reupload");
                          setModalReason("");
                        }}
                        className="text-brand-600 hover:underline font-bold"
                      >
                        Re-open / Request New Documents &rarr;
                      </button>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ) : (
            <div className="lg:col-span-8 bg-white rounded-2xl border border-border p-16 text-center flex flex-col items-center justify-center shadow-sm min-h-[400px]">
              <FileCheck2 className="w-12 h-12 text-slate-300 mb-3" />
              <h3 className="text-base font-bold text-ink">No Partner Selected</h3>
              <p className="text-xs text-ink-muted mt-1 max-w-sm">
                Select an applicant from the left queue to inspect government documents and render a compliance decision.
              </p>
            </div>
          )}
        </div>
      </div>

      {/* Modal for Reject or Request Re-Upload */}
      {modalMode && (
        <div className="fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
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
              className="w-full p-3 text-xs border border-border rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-600 bg-surface-subtle text-ink"
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
                className={`px-4 py-2 text-xs font-bold text-white rounded-xl shadow-sm transition-colors ${
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
