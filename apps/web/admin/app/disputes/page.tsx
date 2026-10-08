"use client";

import { useState } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import { MasterDetailLayout } from "@/components/shared/MasterDetailLayout";
import {
  CheckCircle2,
  MessageSquare,
  DollarSign,
  Receipt,
  AlertCircle,
  XCircle,
  Clock,
  Phone,
  Store,
  FileText,
  Bike,
  CreditCard,
  Wallet,
  ChevronLeft,
  ChevronRight,
  ShieldAlert,
  Search,
} from "lucide-react";
import { recordAdminAuditLog } from "@/lib/admin/api";

interface OrderSnapshotItem {
  name: string;
  qty: number;
  priceNpr: number;
}

interface DisputeItem {
  id: string;
  orderId: string;
  customerName: string;
  customerPhone: string;
  vendorName: string;
  assignedRider: string;
  orderTotalPaisa: number;
  disputeReason: string;
  customerComment: string;
  status: "open" | "refunded" | "rejected";
  urgency: "high" | "medium" | "low";
  verticalTag: "Food" | "Mart" | "Rides" | "Stays";
  photos: string[];
  paymentMethod: "eSewa" | "Khalti" | "Fonepay" | "Cash on Delivery";
  submittedAt: string;
  timeline: {
    orderedAt: string;
    deliveredAt: string;
    disputeLoggedAt: string;
  };
  items: OrderSnapshotItem[];
}

const sampleDisputes: DisputeItem[] = [
  {
    id: "disp-101",
    orderId: "ORD-99120",
    customerName: "Pooja Gurung",
    customerPhone: "+977 9841334455",
    vendorName: "Bhatbhateni Kitchen & Deli",
    assignedRider: "Ram Bahadur Shrestha (Ba 85 Pa 4921)",
    orderTotalPaisa: 85000, // Rs 850
    disputeReason: "Wrong item delivered & container spilled in transit",
    customerComment: "I ordered Chicken Biryani and Momos, but received Veg Chowmein and the box was completely crushed.",
    status: "open",
    urgency: "high",
    verticalTag: "Food",
    photos: [
      "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=600&q=80",
      "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=600&q=80",
    ],
    paymentMethod: "Khalti",
    submittedAt: "Today 12:15 PM",
    timeline: {
      orderedAt: "11:30 AM",
      deliveredAt: "12:05 PM",
      disputeLoggedAt: "12:15 PM",
    },
    items: [
      { name: "Chicken Biryani (Full)", qty: 1, priceNpr: 450 },
      { name: "Steam Chicken Momo", qty: 1, priceNpr: 250 },
      { name: "Packaging & Delivery", qty: 1, priceNpr: 150 },
    ],
  },
  {
    id: "disp-102",
    orderId: "ORD-98841",
    customerName: "Kishor Silwal",
    customerPhone: "+977 9801889900",
    vendorName: "Kathmandu Bakery & Pastry",
    assignedRider: "Bikash Tamang (Ba 1 Ja 7812)",
    orderTotalPaisa: 42000, // Rs 420
    disputeReason: "Expired dairy product in grocery delivery",
    customerComment: "The organic milk bottle delivered had an expiration date stamped yesterday.",
    status: "open",
    urgency: "medium",
    verticalTag: "Mart",
    photos: [
      "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=600&q=80",
    ],
    paymentMethod: "eSewa",
    submittedAt: "Today 10:40 AM",
    timeline: {
      orderedAt: "10:00 AM",
      deliveredAt: "10:30 AM",
      disputeLoggedAt: "10:40 AM",
    },
    items: [
      { name: "DDC Whole Milk 1L", qty: 2, priceNpr: 220 },
      { name: "Fresh White Bread", qty: 1, priceNpr: 120 },
      { name: "Delivery Fee", qty: 1, priceNpr: 80 },
    ],
  },
  {
    id: "disp-103",
    orderId: "RIDE-55291",
    customerName: "Aarati Pradhan",
    customerPhone: "+977 9813224466",
    vendorName: "Evrry City Ride (Thamel -> Patan)",
    assignedRider: "Krishna Pokharel (Ba 2 Ja 4109)",
    orderTotalPaisa: 35000, // Rs 350
    disputeReason: "Fare overcharge & incomplete trip destination drop",
    customerComment: "Driver refused to drop inside alley citing rain and asked for additional cash despite digital Fonepay prepayment.",
    status: "open",
    urgency: "high",
    verticalTag: "Rides",
    photos: [
      "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80",
    ],
    paymentMethod: "Fonepay",
    submittedAt: "Today 09:20 AM",
    timeline: {
      orderedAt: "08:45 AM",
      deliveredAt: "09:15 AM",
      disputeLoggedAt: "09:20 AM",
    },
    items: [
      { name: "City Cab Ride (8.2 km)", qty: 1, priceNpr: 350 },
    ],
  },
];

export default function DisputesPage() {
  const [disputes, setDisputes] = useState<DisputeItem[]>(sampleDisputes);
  const [statusFilter, setStatusFilter] = useState<"all" | "open" | "refunded" | "rejected">("open");
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedDisputeId, setSelectedDisputeId] = useState<string>(sampleDisputes[0].id);

  // Inspection Carousel & Action State
  const [activePhotoIdx, setActivePhotoIdx] = useState(0);
  const [refundAmountNpr, setRefundAmountNpr] = useState<number>(sampleDisputes[0].orderTotalPaisa / 100);
  const [refundDestination, setRefundDestination] = useState<"gateway" | "wallet">("gateway");
  const [actionAlert, setActionAlert] = useState<string | null>(null);

  const filteredDisputes = disputes.filter((d) => {
    if (statusFilter !== "all" && d.status !== statusFilter) return false;
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      return (
        d.customerName.toLowerCase().includes(q) ||
        d.orderId.toLowerCase().includes(q) ||
        d.id.toLowerCase().includes(q) ||
        d.disputeReason.toLowerCase().includes(q)
      );
    }
    return true;
  });

  const selectedDispute =
    filteredDisputes.find((d) => d.id === selectedDisputeId) || filteredDisputes[0] || null;

  const handleSelectDispute = (d: DisputeItem) => {
    setSelectedDisputeId(d.id);
    setActivePhotoIdx(0);
    setRefundAmountNpr(d.orderTotalPaisa / 100);
  };

  const handleDispatchRefund = async () => {
    if (!selectedDispute) return;

    setDisputes((prev) =>
      prev.map((d) => (d.id === selectedDispute.id ? { ...d, status: "refunded" } : d))
    );

    const destLabel =
      refundDestination === "gateway"
        ? `Original Gateway (${selectedDispute.paymentMethod})`
        : "Evrry Wallet Balance (Instant)";

    await recordAdminAuditLog({
      adminName: "Suman Adhikari (VP Operations)",
      adminEmail: "suman@evrry.com.np",
      actionType: "REFUND_DISPATCHED",
      details: `Dispatched refund of NPR ${refundAmountNpr.toFixed(2)} to ${destLabel} for ticket ${selectedDispute.id} (Order ${selectedDispute.orderId}). Customer: ${selectedDispute.customerName}.`,
      ipAddress: "103.10.29.14 (Kathmandu)",
    });

    setActionAlert(
      `Dispatched NPR ${refundAmountNpr.toFixed(2)} refund to ${destLabel}. Ticket ${selectedDispute.id} closed.`
    );
    setTimeout(() => setActionAlert(null), 5000);
  };

  const handleRejectClaim = async () => {
    if (!selectedDispute) return;

    setDisputes((prev) =>
      prev.map((d) => (d.id === selectedDispute.id ? { ...d, status: "rejected" } : d))
    );

    await recordAdminAuditLog({
      adminName: "Suman Adhikari (VP Operations)",
      adminEmail: "suman@evrry.com.np",
      actionType: "DISPUTE_REJECTED",
      details: `Rejected dispute claim ${selectedDispute.id} for order ${selectedDispute.orderId}.`,
      ipAddress: "103.10.29.14 (Kathmandu)",
    });

    setActionAlert(`Dispute claim ${selectedDispute.id} rejected. Customer notified via push notification.`);
    setTimeout(() => setActionAlert(null), 4000);
  };

  return (
    <div className="flex-1 flex flex-col h-screen overflow-hidden">
      <AdminHeader
        title="Customer Dispute Resolution & Fintech Refunds Desk"
        subtitle="Customer Complaints • Photo Evidence Inspection • 1-Click Fintech Gateway Refunds"
      />

      {/* Action Banner */}
      {actionAlert && (
        <div className="bg-emerald-50 border-b border-emerald-200 px-6 py-2.5 text-emerald-800 text-xs font-semibold flex items-center justify-between shrink-0">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
            <span>{actionAlert}</span>
          </div>
          <span className="font-mono text-[11px] text-emerald-700 bg-white px-2 py-0.5 rounded border border-emerald-200">
            Payment Rail Synced
          </span>
        </div>
      )}

      <MasterDetailLayout
        queueTitle="Customer Support Tickets"
        queueCount={filteredDisputes.length}
        queueFilters={
          <div className="space-y-2">
            <div className="relative">
              <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-2.5" />
              <input
                type="text"
                placeholder="Search ticket, order, customer..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-9 pr-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-lg text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500"
              />
            </div>

            <div className="grid grid-cols-3 gap-1">
              {[
                { id: "open" as const, label: "Open" },
                { id: "refunded" as const, label: "Refunded" },
                { id: "all" as const, label: "All" },
              ].map((st) => (
                <button
                  key={st.id}
                  onClick={() => setStatusFilter(st.id)}
                  className={`py-1 text-[10px] font-semibold rounded-md border text-center transition-colors ${
                    statusFilter === st.id
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
        queueContent={
          filteredDisputes.length === 0 ? (
            <div className="p-8 text-center text-xs text-slate-400 italic">
              No tickets found matching current filters.
            </div>
          ) : (
            filteredDisputes.map((d) => {
              const isSelected = selectedDispute?.id === d.id;
              return (
                <button
                  key={d.id}
                  onClick={() => handleSelectDispute(d)}
                  className={`w-full text-left p-3.5 transition-all flex flex-col gap-1.5 ${
                    isSelected
                      ? "bg-brand-50/50 border-l-4 border-l-brand-600 bg-white"
                      : "hover:bg-slate-50/70"
                  }`}
                >
                  <div className="flex items-start justify-between gap-1">
                    <div className="min-w-0">
                      <div className="text-xs font-bold text-slate-900 truncate">
                        {d.customerName}
                      </div>
                      <div className="text-[11px] text-slate-600 truncate mt-0.5 font-medium">
                        {d.disputeReason}
                      </div>
                    </div>

                    <span
                      className={`text-[9px] px-2 py-0.5 rounded font-mono font-bold uppercase shrink-0 ${
                        d.status === "open"
                          ? "bg-amber-50 text-amber-700 border border-amber-200"
                          : d.status === "refunded"
                          ? "bg-emerald-50 text-emerald-700 border border-emerald-200"
                          : "bg-rose-50 text-rose-700 border border-rose-200"
                      }`}
                    >
                      {d.status}
                    </span>
                  </div>

                  <div className="flex items-center justify-between text-[10px] text-slate-400 font-mono pt-1">
                    <span>
                      {d.orderId} • {d.verticalTag}
                    </span>
                    <span className="font-bold text-slate-700">
                      Rs {(d.orderTotalPaisa / 100).toFixed(0)} ({d.paymentMethod})
                    </span>
                  </div>
                </button>
              );
            })
          )
        }
        detailContent={
          selectedDispute ? (
            <div className="space-y-6">
              {/* Order Context Summary Header */}
              <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-3">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-100 pb-3">
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="text-[10px] font-bold text-brand-700 bg-brand-50 px-2.5 py-0.5 rounded border border-brand-200/60 uppercase font-mono">
                        {selectedDispute.id}
                      </span>
                      <h3 className="text-sm font-bold text-slate-900">
                        {selectedDispute.disputeReason}
                      </h3>
                    </div>
                    <div className="text-xs text-slate-500 mt-1 flex flex-wrap items-center gap-3 font-mono">
                      <span>Customer: {selectedDispute.customerName} ({selectedDispute.customerPhone})</span>
                      <span>•</span>
                      <span>Order: {selectedDispute.orderId}</span>
                      <span>•</span>
                      <span>Payment: {selectedDispute.paymentMethod}</span>
                    </div>
                  </div>

                  <span className="text-xs font-mono font-bold text-slate-900 text-right">
                    Total: NPR {(selectedDispute.orderTotalPaisa / 100).toFixed(2)}
                  </span>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
                  <div className="p-3 bg-slate-50 rounded-xl border border-slate-200/70 space-y-1">
                    <span className="text-[10px] font-bold text-slate-400 uppercase">Store / Merchant</span>
                    <div className="font-bold text-slate-800 flex items-center gap-1.5">
                      <Store className="w-3.5 h-3.5 text-brand-600" />
                      <span>{selectedDispute.vendorName}</span>
                    </div>
                  </div>

                  <div className="p-3 bg-slate-50 rounded-xl border border-slate-200/70 space-y-1">
                    <span className="text-[10px] font-bold text-slate-400 uppercase">Assigned Rider</span>
                    <div className="font-bold text-slate-800 flex items-center gap-1.5">
                      <Bike className="w-3.5 h-3.5 text-brand-600" />
                      <span>{selectedDispute.assignedRider}</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Customer Statement */}
              <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-2">
                <div className="text-xs font-bold text-slate-800 flex items-center gap-1.5">
                  <MessageSquare className="w-4 h-4 text-brand-600" />
                  <span>Customer Statement & Complaint</span>
                </div>
                <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 text-xs text-slate-700 italic">
                  &ldquo;{selectedDispute.customerComment}&rdquo;
                </div>
              </div>

              {/* Photo Evidence Carousel */}
              {selectedDispute.photos.length > 0 && (
                <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-3">
                  <div className="flex items-center justify-between">
                    <div className="text-xs font-bold text-slate-800 flex items-center gap-1.5">
                      <FileText className="w-4 h-4 text-brand-600" />
                      <span>Photo Evidence Carousel ({selectedDispute.photos.length} photos)</span>
                    </div>
                    {selectedDispute.photos.length > 1 && (
                      <div className="flex items-center gap-1.5">
                        <button
                          type="button"
                          onClick={() =>
                            setActivePhotoIdx((prev) =>
                              prev > 0 ? prev - 1 : selectedDispute.photos.length - 1
                            )
                          }
                          className="p-1 rounded-lg border border-slate-200 hover:bg-slate-50 text-slate-600"
                        >
                          <ChevronLeft className="w-4 h-4" />
                        </button>
                        <span className="text-xs text-slate-500 font-mono">
                          {activePhotoIdx + 1}/{selectedDispute.photos.length}
                        </span>
                        <button
                          type="button"
                          onClick={() =>
                            setActivePhotoIdx((prev) =>
                              (prev + 1) % selectedDispute.photos.length
                            )
                          }
                          className="p-1 rounded-lg border border-slate-200 hover:bg-slate-50 text-slate-600"
                        >
                          <ChevronRight className="w-4 h-4" />
                        </button>
                      </div>
                    )}
                  </div>

                  <div className="bg-slate-50 rounded-xl border border-slate-200 p-2 flex items-center justify-center min-h-[260px] max-h-[340px] overflow-hidden">
                    {/* eslint-disable-next-line @next/next/no-img-element */}
                    <img
                      src={selectedDispute.photos[activePhotoIdx]}
                      alt="Dispute Evidence"
                      className="max-h-[300px] object-contain rounded-lg shadow-sm"
                    />
                  </div>
                </div>
              )}

              {/* Incident Timeline & Order Snapshot */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Timeline */}
                <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-3">
                  <div className="text-xs font-bold text-slate-800 flex items-center gap-1.5">
                    <Clock className="w-4 h-4 text-brand-600" />
                    <span>Incident Timeline</span>
                  </div>
                  <div className="space-y-3 text-xs pl-2 border-l-2 border-slate-200 text-slate-600 font-mono">
                    <div>
                      <div className="font-bold text-slate-900">1. Order Placed</div>
                      <div className="text-[11px] text-slate-400">{selectedDispute.timeline.orderedAt}</div>
                    </div>
                    <div>
                      <div className="font-bold text-slate-900">2. Order Delivered</div>
                      <div className="text-[11px] text-slate-400">{selectedDispute.timeline.deliveredAt}</div>
                    </div>
                    <div>
                      <div className="font-bold text-amber-700">3. Dispute Logged</div>
                      <div className="text-[11px] text-amber-600">{selectedDispute.timeline.disputeLoggedAt}</div>
                    </div>
                  </div>
                </div>

                {/* Line Items Snapshot */}
                <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-3">
                  <div className="text-xs font-bold text-slate-800 flex items-center gap-1.5">
                    <Receipt className="w-4 h-4 text-brand-600" />
                    <span>Line Items Snapshot</span>
                  </div>
                  <div className="divide-y divide-slate-100 text-xs">
                    {selectedDispute.items.map((it, idx) => (
                      <div key={idx} className="py-2 flex justify-between font-mono">
                        <span className="text-slate-700">
                          {it.qty}x {it.name}
                        </span>
                        <span className="font-bold text-slate-900">
                          Rs {it.priceNpr.toFixed(0)}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {/* 1-Click Refund Action Bar */}
              <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-4">
                <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                  <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2">
                    <DollarSign className="w-4 h-4 text-brand-600" />
                    1-Click Fintech Refund Action Bar
                  </h4>
                  <span className="text-[11px] text-slate-500 font-mono">Instant Gateway Webhook</span>
                </div>

                {selectedDispute.status === "open" ? (
                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 items-end">
                    {/* Refund Amount Input */}
                    <div>
                      <label className="block text-xs font-semibold text-slate-700 mb-1">
                        Refund Amount (NPR)
                      </label>
                      <div className="relative">
                        <span className="absolute left-3 top-2 text-xs text-slate-400 font-bold">Rs</span>
                        <input
                          type="number"
                          step="10"
                          value={refundAmountNpr}
                          onChange={(e) => setRefundAmountNpr(Number(e.target.value))}
                          className="w-full pl-9 pr-3 py-2 text-xs font-bold bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 text-slate-900"
                        />
                      </div>
                    </div>

                    {/* Destination Selector */}
                    <div>
                      <label className="block text-xs font-semibold text-slate-700 mb-1">
                        Refund Destination
                      </label>
                      <select
                        value={refundDestination}
                        onChange={(e) => setRefundDestination(e.target.value as any)}
                        className="w-full px-3 py-2 text-xs font-medium bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 text-slate-900"
                      >
                        <option value="gateway">Original Gateway ({selectedDispute.paymentMethod})</option>
                        <option value="wallet">Evrry Wallet Balance (Instant)</option>
                      </select>
                    </div>

                    {/* Action Buttons */}
                    <div className="flex items-center gap-2">
                      <button
                        onClick={handleDispatchRefund}
                        className="flex-1 py-2.5 bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs rounded-xl shadow-sm transition-all flex items-center justify-center gap-1.5"
                      >
                        <DollarSign className="w-4 h-4" />
                        Dispatch Refund & Close
                      </button>
                      <button
                        onClick={handleRejectClaim}
                        className="px-4 py-2.5 bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 font-bold text-xs rounded-xl transition-all"
                      >
                        Reject
                      </button>
                    </div>
                  </div>
                ) : (
                  <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 text-xs font-bold text-slate-700 flex items-center gap-2">
                    <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                    <span>Dispute Ticket Resolved: {selectedDispute.status.toUpperCase()}</span>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="bg-white rounded-2xl border border-slate-200/80 p-16 text-center flex flex-col items-center justify-center">
              <AlertCircle className="w-12 h-12 text-slate-300 mb-3" />
              <h3 className="text-base font-bold text-slate-900">No Ticket Selected</h3>
              <p className="text-xs text-slate-500 mt-1 max-w-sm">
                Select an open customer ticket from the left queue to inspect evidence and execute a 1-click refund.
              </p>
            </div>
          )
        }
      />
    </div>
  );
}
