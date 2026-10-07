"use client";

import { useState } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import {
  HelpCircle,
  CheckCircle2,
  XCircle,
  MessageSquare,
  DollarSign,
  AlertCircle,
  Receipt,
  User,
  Store,
  RotateCcw,
} from "lucide-react";

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
  const [selectedDispute, setSelectedDispute] = useState<DisputeItem>(sampleDisputes[0]);
  const [actionAlert, setActionAlert] = useState<string | null>(null);

  const handleApproveRefund = () => {
    setDisputes((prev) =>
      prev.map((d) => (d.id === selectedDispute.id ? { ...d, status: "refunded" } : d))
    );
    setSelectedDispute({ ...selectedDispute, status: "refunded" });
    setActionAlert(
      `Refund of Rs ${(selectedDispute.orderTotalPaisa / 100).toFixed(2)} dispatched to ${selectedDispute.customerName}'s ${selectedDispute.paymentMethod} account!`
    );
    setTimeout(() => setActionAlert(null), 5000);
  };

  const handleRejectClaim = () => {
    setDisputes((prev) =>
      prev.map((d) => (d.id === selectedDispute.id ? { ...d, status: "rejected" } : d))
    );
    setSelectedDispute({ ...selectedDispute, status: "rejected" });
    setActionAlert(`Dispute ${selectedDispute.id} rejected. Customer informed via SMS.`);
    setTimeout(() => setActionAlert(null), 4000);
  };

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="Dispute Resolutions & Refund Console"
        subtitle="Customer Complaints • Photo Inspection • 1-Click Fintech Gateway Refunds"
      />

      <div className="p-8 space-y-6 flex-1 max-w-6xl">
        {/* Alert Feedback */}
        {actionAlert && (
          <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-xl text-emerald-800 text-xs flex items-center justify-between shadow-sm">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600" />
              <span className="font-semibold">{actionAlert}</span>
            </div>
            <span className="text-[11px] font-mono text-emerald-600">Fintech Refund Bridge</span>
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 min-h-[550px]">
          {/* Left Column: Disputes List (5 cols) */}
          <div className="lg:col-span-5 bg-white rounded-2xl border border-slate-200 shadow-sm p-4 space-y-3">
            <div className="text-xs font-bold text-slate-900 flex items-center justify-between border-b border-slate-100 pb-3">
              <span>Customer Dispute Queue</span>
              <span className="text-[10px] bg-slate-100 text-slate-600 px-2 py-0.5 rounded-full font-mono">
                {disputes.filter((d) => d.status === "open").length} Open
              </span>
            </div>

            <div className="space-y-2.5">
              {disputes.map((d) => (
                <button
                  key={d.id}
                  onClick={() => setSelectedDispute(d)}
                  className={`w-full text-left p-3.5 rounded-xl border transition-all ${
                    selectedDispute.id === d.id
                      ? "bg-slate-900 text-white border-slate-900 shadow-md"
                      : "bg-slate-50 text-slate-800 border-slate-200 hover:bg-slate-100"
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-xs">{d.customerName}</span>
                    <span
                      className={`text-[10px] font-semibold px-2 py-0.5 rounded uppercase font-mono ${
                        d.status === "open"
                          ? "bg-amber-500/20 text-amber-500"
                          : d.status === "refunded"
                          ? "bg-emerald-500/20 text-emerald-400"
                          : "bg-red-500/20 text-red-400"
                      }`}
                    >
                      {d.status}
                    </span>
                  </div>

                  <div className={`text-[11px] mt-1 line-clamp-1 ${selectedDispute.id === d.id ? "text-slate-300" : "text-slate-500"}`}>
                    {d.disputeReason}
                  </div>

                  <div className="flex items-center justify-between mt-2 pt-2 border-t border-slate-200/40 text-[10px]">
                    <span className="font-mono">{d.orderId}</span>
                    <span className="font-bold font-mono">
                      Rs {(d.orderTotalPaisa / 100).toFixed(2)} ({d.paymentMethod})
                    </span>
                  </div>
                </button>
              ))}
            </div>
          </div>

          {/* Right Column: Dispute Inspection & Refund Action Desk (7 cols) */}
          <div className="lg:col-span-7 bg-white rounded-2xl border border-slate-200 shadow-sm p-6 flex flex-col justify-between space-y-6">
            <div className="space-y-5">
              {/* Header */}
              <div className="border-b border-slate-100 pb-4">
                <div className="flex items-center justify-between">
                  <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">
                    Complaint Dossier
                  </span>
                  <span className="text-xs font-mono text-slate-400">{selectedDispute.id}</span>
                </div>
                <h3 className="text-base font-bold text-slate-900 mt-1">
                  {selectedDispute.disputeReason}
                </h3>
                <div className="flex items-center gap-2 mt-1 text-xs text-slate-500">
                  <span>Customer: {selectedDispute.customerName} ({selectedDispute.customerPhone})</span>
                  <span>•</span>
                  <span>Store: {selectedDispute.vendorName}</span>
                </div>
              </div>

              {/* Customer Comment */}
              <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 text-xs">
                <div className="font-semibold text-slate-700 flex items-center gap-1.5 mb-1">
                  <MessageSquare className="w-3.5 h-3.5 text-slate-500" />
                  Customer Stated:
                </div>
                <p className="text-slate-600 italic">&ldquo;{selectedDispute.customerComment}&rdquo;</p>
              </div>

              {/* Photo Evidence */}
              {selectedDispute.photoEvidenceUrl && (
                <div>
                  <div className="text-[11px] font-semibold text-slate-500 mb-2">
                    Photo Evidence Submitted by Customer:
                  </div>
                  {/* eslint-disable-next-line @next/next/no-img-element */}
                  <img
                    src={selectedDispute.photoEvidenceUrl}
                    alt="Customer Evidence"
                    className="h-44 w-full object-cover rounded-xl border border-slate-200"
                  />
                </div>
              )}

              {/* Payment Summary */}
              <div className="flex items-center justify-between p-3.5 bg-slate-50 rounded-xl border border-slate-200 text-xs">
                <div className="flex items-center gap-2">
                  <Receipt className="w-4 h-4 text-emerald-600" />
                  <span className="font-semibold text-slate-800">
                    Original Payment via {selectedDispute.paymentMethod}
                  </span>
                </div>
                <span className="font-mono font-bold text-slate-900 text-sm">
                  Rs {(selectedDispute.orderTotalPaisa / 100).toFixed(2)}
                </span>
              </div>
            </div>

            {/* Action Bar */}
            <div className="pt-4 border-t border-slate-100 flex items-center gap-3">
              {selectedDispute.status === "open" ? (
                <>
                  <button
                    onClick={handleApproveRefund}
                    className="flex-1 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-xs rounded-xl shadow-md transition-all flex items-center justify-center gap-1.5"
                  >
                    <DollarSign className="w-4 h-4" />
                    Approve 100% Refund (to {selectedDispute.paymentMethod})
                  </button>
                  <button
                    onClick={handleRejectClaim}
                    className="px-5 py-2.5 border border-red-200 text-red-600 hover:bg-red-50 font-semibold text-xs rounded-xl transition-all"
                  >
                    Reject Claim
                  </button>
                </>
              ) : (
                <div className="w-full text-center py-2 text-xs font-semibold text-slate-500 bg-slate-100 rounded-lg">
                  Case status: {selectedDispute.status.toUpperCase()}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
