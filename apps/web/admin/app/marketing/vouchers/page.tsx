"use client";

import { useState } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import {
  Ticket,
  Plus,
  Play,
  Pause,
  Trash2,
  TrendingUp,
  Percent,
  CheckCircle2,
  Calendar,
  Layers,
} from "lucide-react";
import { sampleVouchers } from "@/lib/admin/mockData";
import { VoucherCampaign } from "@/lib/admin/types";

export default function VouchersPage() {
  const [vouchers, setVouchers] = useState<VoucherCampaign[]>(sampleVouchers);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [actionAlert, setActionAlert] = useState<string | null>(null);

  // New Voucher Form State
  const [newCode, setNewCode] = useState("");
  const [newTitle, setNewTitle] = useState("");
  const [newDesc, setNewDesc] = useState("");
  const [discountType, setDiscountType] = useState<"flat" | "percentage">("flat");
  const [discountVal, setDiscountVal] = useState(100);
  const [minOrder, setMinOrder] = useState(300);
  const [maxDiscount, setMaxDiscount] = useState(100);
  const [totalClaims, setTotalClaims] = useState(1000);
  const [budgetNpr, setBudgetNpr] = useState(100000);

  const handleCreateVoucher = (e: React.FormEvent) => {
    e.preventDefault();
    const created: VoucherCampaign = {
      id: `v-${Date.now()}`,
      code: newCode.toUpperCase(),
      title: newTitle,
      description: newDesc,
      discountType,
      discountValue: discountVal,
      minOrderValuePaisa: minOrder * 100,
      maxDiscountCapPaisa: maxDiscount * 100,
      perUserLimit: 1,
      totalRedemptionsCap: totalClaims,
      redeemedCount: 0,
      allocatedBudgetPaisa: budgetNpr * 100,
      burnedBudgetPaisa: 0,
      status: "active",
      validFrom: "2026-10-07",
      validUntil: "2026-11-30",
      verticals: ["food", "grocery"],
    };
    setVouchers([created, ...vouchers]);
    setShowCreateModal(false);
    setActionAlert(`Voucher ${created.code} successfully launched platform-wide!`);
    setTimeout(() => setActionAlert(null), 4000);
  };

  const handleToggleStatus = (id: string) => {
    setVouchers((prev) =>
      prev.map((v) => {
        if (v.id === id) {
          const nextStatus = v.status === "active" ? "paused" : "active";
          return { ...v, status: nextStatus };
        }
        return v;
      })
    );
  };

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="Vouchers & Marketing Promotional Engine"
        subtitle="Zero-SQL Campaign Management • Budget Burn Tracking"
      />

      <div className="p-8 space-y-8 flex-1 max-w-6xl">
        {/* Alert Banner */}
        {actionAlert && (
          <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-xl text-emerald-800 text-xs flex items-center justify-between">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600" />
              <span className="font-semibold">{actionAlert}</span>
            </div>
            <span className="text-[11px] font-mono text-emerald-600">Active in Checkout</span>
          </div>
        )}

        {/* Action Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
          <div>
            <h2 className="text-base font-bold text-slate-900 tracking-tight flex items-center gap-2">
              <Ticket className="w-5 h-5 text-indigo-600" />
              Active Marketing Campaigns ({vouchers.length})
            </h2>
            <p className="text-xs text-slate-500 mt-1">
              Create discount coupons, set financial burn limits, and target specific verticals.
            </p>
          </div>
          <button
            onClick={() => setShowCreateModal(true)}
            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold rounded-lg transition-colors flex items-center gap-1.5 shadow-md shadow-indigo-900/20"
          >
            <Plus className="w-4 h-4" />
            Create New Voucher
          </button>
        </div>

        {/* Campaign Cards Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {vouchers.map((v) => {
            const burnPercent = Math.round(
              (v.burnedBudgetPaisa / (v.allocatedBudgetPaisa || 1)) * 100
            );
            const redemptionPercent = Math.round(
              (v.redeemedCount / (v.totalRedemptionsCap || 1)) * 100
            );

            return (
              <div
                key={v.id}
                className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex flex-col justify-between space-y-4"
              >
                <div className="space-y-3">
                  <div className="flex items-center justify-between">
                    <span className="px-2.5 py-1 bg-indigo-50 text-indigo-700 font-mono font-bold text-xs rounded-lg border border-indigo-200 tracking-wider">
                      {v.code}
                    </span>
                    <span
                      className={`text-[10px] font-semibold px-2 py-0.5 rounded-full uppercase ${
                        v.status === "active"
                          ? "bg-emerald-100 text-emerald-700"
                          : v.status === "paused"
                          ? "bg-amber-100 text-amber-800"
                          : "bg-slate-100 text-slate-600"
                      }`}
                    >
                      {v.status}
                    </span>
                  </div>

                  <div>
                    <h3 className="text-xs font-bold text-slate-900">{v.title}</h3>
                    <p className="text-[11px] text-slate-500 mt-0.5 line-clamp-2">{v.description}</p>
                  </div>

                  {/* Discount Rule Summary */}
                  <div className="bg-slate-50 p-2.5 rounded-xl border border-slate-100 text-[11px] space-y-1 text-slate-600">
                    <div className="flex justify-between">
                      <span>Discount:</span>
                      <span className="font-semibold text-slate-900">
                        {v.discountType === "flat" ? `Rs ${v.discountValue}` : `${v.discountValue}%`}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span>Min Basket:</span>
                      <span className="font-semibold text-slate-900">
                        Rs {v.minOrderValuePaisa / 100}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span>Max Cap:</span>
                      <span className="font-semibold text-slate-900">
                        Rs {v.maxDiscountCapPaisa / 100}
                      </span>
                    </div>
                  </div>

                  {/* Budget Burn Tracker */}
                  <div className="space-y-1.5 pt-1">
                    <div className="flex justify-between text-[11px]">
                      <span className="text-slate-500">Budget Burned:</span>
                      <span className="font-mono font-semibold text-slate-900">
                        Rs {(v.burnedBudgetPaisa / 100).toLocaleString()} /{" "}
                        {(v.allocatedBudgetPaisa / 100).toLocaleString()}
                      </span>
                    </div>
                    <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                      <div
                        className={`h-full rounded-full transition-all ${
                          burnPercent >= 90
                            ? "bg-red-500"
                            : burnPercent >= 70
                            ? "bg-amber-500"
                            : "bg-indigo-600"
                        }`}
                        style={{ width: `${Math.min(burnPercent, 100)}%` }}
                      />
                    </div>
                    <div className="flex justify-between text-[10px] text-slate-400">
                      <span>{redemptionPercent}% claimed</span>
                      <span>{v.redeemedCount.toLocaleString()} uses</span>
                    </div>
                  </div>
                </div>

                {/* Campaign Controls */}
                <div className="pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
                  <span className="text-[10px] text-slate-400 flex items-center gap-1">
                    <Calendar className="w-3 h-3" />
                    Until {v.validUntil}
                  </span>
                  <div className="flex items-center gap-2">
                    {v.status !== "exhausted" && (
                      <button
                        onClick={() => handleToggleStatus(v.id)}
                        className={`p-1.5 rounded-lg border text-xs transition-colors ${
                          v.status === "active"
                            ? "text-amber-700 border-amber-200 hover:bg-amber-50"
                            : "text-emerald-700 border-emerald-200 hover:bg-emerald-50"
                        }`}
                        title={v.status === "active" ? "Pause Voucher" : "Resume Voucher"}
                      >
                        {v.status === "active" ? (
                          <Pause className="w-3.5 h-3.5" />
                        ) : (
                          <Play className="w-3.5 h-3.5" />
                        )}
                      </button>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* Create Voucher Modal */}
        {showCreateModal && (
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl max-w-lg w-full p-6 space-y-4 shadow-2xl border border-slate-200">
              <h3 className="text-base font-bold text-slate-900 flex items-center gap-2">
                <Ticket className="w-4 h-4 text-indigo-600" />
                Create New Marketing Voucher
              </h3>

              <form onSubmit={handleCreateVoucher} className="space-y-3 text-xs">
                <div>
                  <label className="block font-semibold text-slate-700 mb-1">
                    Voucher Code (Auto-uppercase)
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. MONSOON200"
                    value={newCode}
                    onChange={(e) => setNewCode(e.target.value)}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg font-mono font-bold uppercase focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label className="block font-semibold text-slate-700 mb-1">Public Title</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Monsoon Special: Flat Rs 200 Off"
                    value={newTitle}
                    onChange={(e) => setNewTitle(e.target.value)}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label className="block font-semibold text-slate-700 mb-1">Description</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Valid on food and mart orders above Rs 600."
                    value={newDesc}
                    onChange={(e) => setNewDesc(e.target.value)}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  />
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block font-semibold text-slate-700 mb-1">Discount Type</label>
                    <select
                      value={discountType}
                      onChange={(e) => setDiscountType(e.target.value as "flat" | "percentage")}
                      className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:outline-none font-semibold"
                    >
                      <option value="flat">Flat Amount (NPR)</option>
                      <option value="percentage">Percentage (%)</option>
                    </select>
                  </div>

                  <div>
                    <label className="block font-semibold text-slate-700 mb-1">Discount Value</label>
                    <input
                      type="number"
                      required
                      value={discountVal}
                      onChange={(e) => setDiscountVal(Number(e.target.value))}
                      className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:outline-none font-semibold"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block font-semibold text-slate-700 mb-1">
                      Min Basket Value (Rs)
                    </label>
                    <input
                      type="number"
                      required
                      value={minOrder}
                      onChange={(e) => setMinOrder(Number(e.target.value))}
                      className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                    />
                  </div>

                  <div>
                    <label className="block font-semibold text-slate-700 mb-1">
                      Max Discount Cap (Rs)
                    </label>
                    <input
                      type="number"
                      required
                      value={maxDiscount}
                      onChange={(e) => setMaxDiscount(Number(e.target.value))}
                      className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block font-semibold text-slate-700 mb-1">
                      Total Redemption Cap
                    </label>
                    <input
                      type="number"
                      required
                      value={totalClaims}
                      onChange={(e) => setTotalClaims(Number(e.target.value))}
                      className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                    />
                  </div>

                  <div>
                    <label className="block font-semibold text-slate-700 mb-1">
                      Marketing Budget (Rs)
                    </label>
                    <input
                      type="number"
                      required
                      value={budgetNpr}
                      onChange={(e) => setBudgetNpr(Number(e.target.value))}
                      className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                    />
                  </div>
                </div>

                <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
                  <button
                    type="button"
                    onClick={() => setShowCreateModal(false)}
                    className="px-4 py-2 text-slate-600 hover:bg-slate-100 rounded-lg font-semibold"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg font-semibold shadow-md"
                  >
                    Publish Voucher
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
