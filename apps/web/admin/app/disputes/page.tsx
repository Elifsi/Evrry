"use client";

import { useState } from "react";
import { AdminHeader } from "@/components/AdminHeader";
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
} from "lucide-react";
import { recordAdminAuditLog } from "@/lib/admin/api";

interface DisputeItem {
  id: string;
  orderId: string;
  customerName: string;
  customerPhone: string;
  vendorName: string;
  orderTotalPaisa: number;
  disputeReason: string;
  customerComment: string;
  status: "open" | "refunded" | "rejected";
  photoEvidenceUrl?: string;
  paymentMethod: "eSewa" | "Khalti" | "Fonepay" | "Cash on Delivery";
  submittedAt: string;
}

const sampleDisputes: DisputeItem[] = [
  {
    id: "disp-101",
    orderId: "ORD-99120",
    customerName: "Pooja Gurung",
    customerPhone: "+977 9841334455",
    vendorName: "Bhatbhateni Kitchen & Deli",
    orderTotalPaisa: 85000, // Rs 850
    disputeReason: "Wrong item delivered & container spilled in transit",
    customerComment: "I ordered Chicken Biryani and Momos, but received Veg Chowmein and the box was completely crushed.",
    status: "open",
    photoEvidenceUrl: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=600&q=80",
    paymentMethod: "Khalti",
    submittedAt: "Today 12:15 PM",
  },
  {
    id: "disp-102",
    orderId: "ORD-98841",
    customerName: "Kishor Silwal",
    customerPhone: "+977 9801889900",
    vendorName: "Kathmandu Bakery & Pastry",
    orderTotalPaisa: 42000, // Rs 420
    disputeReason: "Expired dairy product in grocery bag",
    customerComment: "The milk delivered had an expiration date of yesterday.",
    status: "open",
    photoEvidenceUrl: "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=600&q=80",
    paymentMethod: "eSewa",
    submittedAt: "Today 10:40 AM",
  },
];

export default function DisputesPage() {
  const [disputes, setDisputes] = useState<DisputeItem[]>(sampleDisputes);
  const [statusFilter, setStatusFilter] = useState<"all" | "open" | "refunded" | "rejected">("all");
  const [selectedDispute, setSelectedDispute] = useState<DisputeItem>(sampleDisputes[0]);
  const [actionAlert, setActionAlert] = useState<string | null>(null);

  const filteredDisputes = disputes.filter((d) => {
    if (statusFilter === "all") return true;
    return d.status === statusFilter;
  });

  const handleApproveRefund = async () => {
    setDisputes((prev) =>
      prev.map((d) => (d.id === selectedDispute.id ? { ...d, status: "refunded" } : d))
    );
    setSelectedDispute({ ...selectedDispute, status: "refunded" });

    await recordAdminAuditLog({
      adminName: "Suman Adhikari (VP Operations)",
      adminEmail: "suman@evrry.com.np",
      actionType: "REFUND_APPROVED",
      details: `Approved full refund of Rs ${(selectedDispute.orderTotalPaisa / 100).toFixed(2)} for ${selectedDispute.customerName} via ${selectedDispute.paymentMethod}. Order: ${selectedDispute.orderId}`,
      ipAddress: "103.10.29.14 (Kathmandu)",
    });

    setActionAlert(
      `Refund of Rs ${(selectedDispute.orderTotalPaisa / 100).toFixed(2)} dispatched to ${selectedDispute.customerName}'s ${selectedDispute.paymentMethod} account!`
    );
    setTimeout(() => setActionAlert(null), 5000);
  };

  const handleRejectClaim = async () => {
    setDisputes((prev) =>
      prev.map((d) => (d.id === selectedDispute.id ? { ...d, status: "rejected" } : d))
    );
    setSelectedDispute({ ...selectedDispute, status: "rejected" });

    await recordAdminAuditLog({
      adminName: "Suman Adhikari (VP Operations)",
      adminEmail: "suman@evrry.com.np",
      actionType: "REFUND_REJECTED",
      details: `Rejected dispute claim ${selectedDispute.id} for order ${selectedDispute.orderId}.`,
      ipAddress: "103.10.29.14 (Kathmandu)",
    });

    setActionAlert(`Dispute ${selectedDispute.id} rejected. Customer notified via push & in-app alert.`);
    setTimeout(() => setActionAlert(null), 4000);
  };

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="Dispute Resolutions & Refund Console"
        subtitle="Customer Complaints • Photo Evidence Inspection • 1-Click Fintech Gateway Refunds"
      />

      <div className="p-6 md:p-8 space-y-6 flex-1 max-w-[1500px] w-full mx-auto">
        {/* Alert Feedback */}
        {actionAlert && (
          <div className="bg-brand-50 border border-brand-200 p-4 rounded-2xl text-brand-900 text-xs flex items-center justify-between shadow-sm animate-fade-in">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-brand-600 shrink-0" />
              <span className="font-semibold">{actionAlert}</span>
            </div>
            <span className="text-[11px] font-mono text-brand-700 bg-white px-2.5 py-1 rounded-md border border-brand-200">
              Fintech Gateway Bridge
            </span>
          </div>
        )}

        {/* Filter Pills Ribbon */}
        <div className="bg-white p-3 rounded-2xl border border-border shadow-sm flex items-center justify-between gap-3">
          <div className="flex items-center gap-1.5 overflow-x-auto">
            {[
              { id: "all", label: "All Complaints", count: disputes.length },
              {
                id: "open",
                label: "Open Tickets",
                count: disputes.filter((d) => d.status === "open").length,
              },
              {
                id: "refunded",
                label: "Refunded",
                count: disputes.filter((d) => d.status === "refunded").length,
              },
              {
                id: "rejected",
                label: "Rejected",
                count: disputes.filter((d) => d.status === "rejected").length,
              },
            ].map((f) => (
              <button
                key={f.id}
                onClick={() => setStatusFilter(f.id as any)}
                className={`flex items-center gap-2 px-3.5 py-1.5 rounded-xl text-xs font-semibold transition-all shrink-0 ${
                  statusFilter === f.id
                    ? "bg-brand-600 text-white shadow-sm"
                    : "bg-surface-subtle text-ink-muted hover:text-ink hover:bg-slate-100"
                }`}
              >
                <span>{f.label}</span>
                <span
                  className={`text-[10px] px-1.5 py-0.2 rounded-full font-bold ${
                    statusFilter === f.id ? "bg-white/20 text-white" : "bg-slate-200 text-ink"
                  }`}
                >
                  {f.count}
                </span>
              </button>
            ))}
          </div>

          <div className="text-xs text-ink-muted hidden sm:block font-mono">
            Gateway: Fonepay / eSewa / Khalti Interbank
          </div>
        </div>

        {/* Main Workspace Master-Detail Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          {/* Left Column: Disputes List (4.5 cols) */}
          <div className="lg:col-span-5 bg-white rounded-2xl border border-border shadow-sm p-4 space-y-3">
            <div className="text-xs font-bold text-ink flex items-center justify-between pb-3 border-b border-border">
              <span>Customer Dispute Directory</span>
              <span className="text-[11px] font-mono text-ink-muted">
                Showing {filteredDisputes.length} cases
              </span>
            </div>

            <div className="space-y-2.5 max-h-[640px] overflow-y-auto pr-1">
              {filteredDisputes.length === 0 ? (
                <div className="p-8 text-center text-xs text-ink-muted italic">
                  No dispute records matching current filter.
                </div>
              ) : (
                filteredDisputes.map((d) => {
                  const isSelected = selectedDispute.id === d.id;
                  return (
                    <button
                      key={d.id}
                      onClick={() => setSelectedDispute(d)}
                      className={`w-full text-left p-4 rounded-xl border transition-all flex flex-col gap-2 ${
                        isSelected
                          ? "bg-brand-50/50 border-brand-500 ring-1 ring-brand-500/20 shadow-sm"
                          : "bg-white border-border hover:bg-slate-50/70 hover:border-slate-300"
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-xs text-ink">{d.customerName}</span>
                        <span
                          className={`text-[10px] font-bold px-2 py-0.5 rounded font-mono uppercase ${
                            d.status === "open"
                              ? "bg-amber-100 text-amber-800"
                              : d.status === "refunded"
                              ? "bg-emerald-100 text-emerald-800"
                              : "bg-rose-100 text-rose-800"
                          }`}
                        >
                          {d.status}
                        </span>
                      </div>

                      <div className="text-xs text-ink font-medium line-clamp-1">
                        {d.disputeReason}
                      </div>

                      <div className="flex items-center justify-between text-[11px] text-ink-muted pt-2 border-t border-slate-100 font-mono">
                        <span>{d.orderId}</span>
                        <span className="font-bold text-ink">
                          Rs {(d.orderTotalPaisa / 100).toFixed(2)} ({d.paymentMethod})
                        </span>
                      </div>
                    </button>
                  );
                })
              )}
            </div>
          </div>

          {/* Right Column: Dispute Inspection & Action Console (7.5 cols) */}
          <div className="lg:col-span-7 bg-white rounded-2xl border border-border shadow-sm p-6 space-y-6">
            <div className="space-y-5">
              {/* Header Dossier */}
              <div className="border-b border-border pb-4">
                <div className="flex items-center justify-between">
                  <span className="text-[11px] font-bold text-brand-700 bg-brand-50 px-2.5 py-0.5 rounded border border-brand-200 uppercase tracking-wider font-mono">
                    Complaint Case #{selectedDispute.id}
                  </span>
                  <span className="text-xs text-ink-muted font-medium flex items-center gap-1">
                    <Clock className="w-3.5 h-3.5 text-slate-400" />
                    {selectedDispute.submittedAt}
                  </span>
                </div>

                <h3 className="text-base font-bold text-ink mt-2">
                  {selectedDispute.disputeReason}
                </h3>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-3 pt-3 border-t border-slate-100 text-xs">
                  <div className="flex items-center gap-2 text-ink">
                    <Phone className="w-4 h-4 text-brand-600 shrink-0" />
                    <div>
                      <span className="font-bold">{selectedDispute.customerName}</span>
                      <span className="text-ink-muted block text-[11px] font-mono">
                        {selectedDispute.customerPhone}
                      </span>
                    </div>
                  </div>

                  <div className="flex items-center gap-2 text-ink">
                    <Store className="w-4 h-4 text-brand-600 shrink-0" />
                    <div>
                      <span className="font-bold">{selectedDispute.vendorName}</span>
                      <span className="text-ink-muted block text-[11px] font-mono">
                        Order Ref: {selectedDispute.orderId}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Customer Comment Note */}
              <div className="bg-surface-subtle p-4 rounded-xl border border-border text-xs space-y-1">
                <div className="font-bold text-ink flex items-center gap-1.5">
                  <MessageSquare className="w-3.5 h-3.5 text-brand-600" />
                  <span>Customer Statement & Claim:</span>
                </div>
                <p className="text-ink-muted italic pl-5">
                  &ldquo;{selectedDispute.customerComment}&rdquo;
                </p>
              </div>

              {/* Photo Evidence */}
              {selectedDispute.photoEvidenceUrl && (
                <div className="space-y-2">
                  <div className="text-[11px] font-bold text-ink flex items-center gap-1.5">
                    <FileText className="w-3.5 h-3.5 text-brand-600" />
                    <span>Customer Photo Evidence Inspection:</span>
                  </div>
                  {/* eslint-disable-next-line @next/next/no-img-element */}
                  <img
                    src={selectedDispute.photoEvidenceUrl}
                    alt="Customer Evidence"
                    className="h-48 w-full object-cover rounded-xl border border-border shadow-sm"
                  />
                </div>
              )}

              {/* Payment Summary */}
              <div className="flex items-center justify-between p-4 bg-surface-subtle rounded-xl border border-border text-xs">
                <div className="flex items-center gap-2">
                  <Receipt className="w-4 h-4 text-brand-600" />
                  <div>
                    <span className="font-bold text-ink block">
                      Paid via {selectedDispute.paymentMethod}
                    </span>
                    <span className="text-[11px] text-ink-muted">
                      100% refundable directly to original payment rail
                    </span>
                  </div>
                </div>
                <span className="font-mono font-bold text-ink text-base">
                  Rs {(selectedDispute.orderTotalPaisa / 100).toFixed(2)}
                </span>
              </div>
            </div>

            {/* Action Bar */}
            <div className="pt-4 border-t border-border flex items-center gap-3">
              {selectedDispute.status === "open" ? (
                <>
                  <button
                    onClick={handleApproveRefund}
                    className="flex-1 py-3 bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs rounded-xl shadow-md shadow-brand-900/20 transition-all flex items-center justify-center gap-2"
                  >
                    <DollarSign className="w-4 h-4" />
                    Approve 100% Refund (Disburse to {selectedDispute.paymentMethod})
                  </button>
                  <button
                    onClick={handleRejectClaim}
                    className="px-5 py-3 border border-rose-200 text-rose-700 hover:bg-rose-50 font-bold text-xs rounded-xl transition-all flex items-center gap-1.5"
                  >
                    <XCircle className="w-3.5 h-3.5" />
                    Reject Claim
                  </button>
                </>
              ) : (
                <div className="w-full text-center py-3 text-xs font-bold text-ink bg-surface-subtle rounded-xl border border-border flex items-center justify-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-brand-600" />
                  <span>Case Status: {selectedDispute.status.toUpperCase()}</span>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
