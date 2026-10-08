"use client";

import { useState, useEffect } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import {
  Ticket,
  Plus,
  Play,
  Pause,
  CheckCircle2,
  Calendar,
  Loader2,
  Percent,
  DollarSign,
  AlertCircle,
  TrendingUp,
} from "lucide-react";
import { fetchVouchers, createVoucherCampaign, toggleVoucherStatus } from "@/lib/admin/api";
import { VoucherCampaign } from "@/lib/admin/types";

export default function VouchersPage() {
  const [vouchers, setVouchers] = useState<VoucherCampaign[]>([]);
  const [loading, setLoading] = useState(true);
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

  useEffect(() => {
    fetchVouchers().then((data) => {
      setVouchers(data);
      setLoading(false);
    });
  }, []);

  const handleCreateVoucher = async (e: React.FormEvent) => {
    e.preventDefault();
    const payload = {
      code: newCode.toUpperCase(),
      title: newTitle,
      description: newDesc,
      discountType,
      discountValue: discountVal,
      minOrderValuePaisa: minOrder * 100,
      maxDiscountCapPaisa: maxDiscount * 100,
      perUserLimit: 1,
      totalRedemptionsCap: totalClaims,
      allocatedBudgetPaisa: budgetNpr * 100,
      status: "active" as const,
      validFrom: new Date().toISOString().split("T")[0],
      validUntil: new Date(Date.now() + 30 * 86400000).toISOString().split("T")[0],
      verticals: ["food", "grocery"],
    };

    const res = await createVoucherCampaign(payload);
    setVouchers([res.voucher, ...vouchers]);
    setShowCreateModal(false);
    setActionAlert(`Voucher campaign ${res.voucher.code} successfully launched platform-wide!`);
    setTimeout(() => setActionAlert(null), 4000);
  };

  const handleToggleStatus = async (id: string) => {
    const res = await toggleVoucherStatus(id);
    setVouchers((prev) =>
      prev.map((v) => (v.id === id ? { ...v, status: res.status } : v))
    );
  };

  return (
    <div className="flex-1 flex flex-col h-screen overflow-y-auto bg-slate-50/75">
      <AdminHeader
        title="Marketing Vouchers & Promotional Engine"
        subtitle="Zero-SQL Campaign Management • Financial Budget Burn-Rate Tracking"
      />

      <div className="p-6 md:p-8 space-y-6 flex-1 max-w-[1500px] w-full mx-auto">
        {/* Alert Banner */}
        {actionAlert && (
          <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-xl text-emerald-800 text-xs flex items-center justify-between shadow-sm animate-fade-in">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
              <span className="font-semibold">{actionAlert}</span>
            </div>
            <span className="text-[11px] font-mono text-emerald-700 bg-white px-2.5 py-1 rounded-md border border-emerald-200">
              Active in Checkout
            </span>
          </div>
        )}

        {/* Action Header Card */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
          <div>
            <h2 className="text-base font-bold text-slate-900 tracking-tight flex items-center gap-2">
              <Ticket className="w-5 h-5 text-brand-600" />
              Promotional Campaigns & Coupon Codes ({vouchers.length})
            </h2>
            <p className="text-xs text-slate-500 mt-0.5">
              Create discount coupons, set financial burn caps, and target specific order verticals.
            </p>
          </div>
          <button
            onClick={() => setShowCreateModal(true)}
            className="px-4 py-2 bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold rounded-xl transition-all flex items-center gap-1.5 shadow-sm"
          >
            <Plus className="w-4 h-4" />
            Create New Campaign
          </button>
        </div>

        {/* Campaign Cards Grid */}
        {loading ? (
          <div className="bg-white p-12 rounded-2xl border border-slate-200/80 shadow-sm flex items-center justify-center gap-2 text-slate-500 text-xs">
            <Loader2 className="w-4 h-4 animate-spin text-brand-600" />
            <span>Loading marketing campaigns...</span>
          </div>
        ) : vouchers.length === 0 ? (
          <div className="bg-white p-12 rounded-2xl border border-slate-200/80 shadow-sm text-center text-slate-500 text-xs italic">
            No active marketing campaigns found. Click &quot;Create New Campaign&quot; to launch one.
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
            {vouchers.map((v) => {
              const burnedNpr = v.burnedBudgetPaisa / 100;
              const allocatedNpr = (v.allocatedBudgetPaisa || 1) / 100;
              const burnPercent = Math.min(100, Math.round((burnedNpr / allocatedNpr) * 100));
              const redemptionPercent = Math.round(
                (v.redeemedCount / (v.totalRedemptionsCap || 1)) * 100
              );

              return (
                <div
                  key={v.id}
                  className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col justify-between space-y-4 hover:border-slate-300 transition-colors"
                >
                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <span className="px-2.5 py-1 bg-brand-50 text-brand-800 font-mono font-bold text-xs rounded-lg border border-brand-200/60 tracking-wider">
                        {v.code}
                      </span>
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full uppercase ${
                          v.status === "active"
                            ? "bg-emerald-50 text-emerald-700 border border-emerald-200"
                            : v.status === "paused"
                            ? "bg-amber-50 text-amber-700 border border-amber-200"
                            : "bg-slate-100 text-slate-600 border border-slate-200"
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
                    <div className="bg-slate-50 p-3 rounded-xl border border-slate-200/70 text-[11px] space-y-1 font-mono text-slate-600">
                      <div className="flex justify-between">
                        <span>Discount:</span>
                        <span className="font-bold text-slate-900">
                          {v.discountType === "flat" ? `Rs ${v.discountValue}` : `${v.discountValue}%`}
                        </span>
                      </div>
                      <div className="flex justify-between">
                        <span>Min Basket:</span>
                        <span className="font-bold text-slate-900">
                          Rs {(v.minOrderValuePaisa / 100).toLocaleString()}
                        </span>
                      </div>
                      <div className="flex justify-between">
                        <span>Max Cap:</span>
                        <span className="font-bold text-slate-900">
                          Rs {(v.maxDiscountCapPaisa / 100).toLocaleString()}
                        </span>
                      </div>
                    </div>

                    {/* Burn-Rate Visual Meter */}
                    <div className="space-y-1.5 pt-1">
                      <div className="flex justify-between text-[11px]">
                        <span className="text-slate-500">Budget Utilized:</span>
                        <span className="font-mono font-bold text-slate-900">
                          NPR {burnedNpr.toLocaleString()} / {allocatedNpr.toLocaleString()} ({burnPercent}%)
                        </span>
                      </div>
                      <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                        <div
                          className={`h-full rounded-full transition-all ${
                            burnPercent >= 90
                              ? "bg-rose-500"
                              : burnPercent >= 70
                              ? "bg-amber-500"
                              : "bg-brand-600"
                          }`}
                          style={{ width: `${burnPercent}%` }}
                        />
                      </div>
                      <div className="flex justify-between text-[10px] text-slate-400 font-mono">
                        <span>{redemptionPercent}% claimed</span>
                        <span>{v.redeemedCount.toLocaleString()} redemptions</span>
                      </div>
                    </div>
                  </div>

                  {/* Campaign Controls */}
                  <div className="pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
                    <span className="text-[10px] text-slate-400 font-mono flex items-center gap-1">
                      <Calendar className="w-3 h-3" />
                      Expires: {v.validUntil}
                    </span>

                    <div className="flex items-center gap-2">
                      {v.status !== "exhausted" && (
                        <button
                          type="button"
                          onClick={() => handleToggleStatus(v.id)}
                          className={`p-1.5 rounded-lg border text-xs font-semibold transition-colors flex items-center gap-1 ${
                            v.status === "active"
                              ? "text-amber-800 border-amber-200 bg-amber-50 hover:bg-amber-100"
                              : "text-brand-800 border-brand-200 bg-brand-50 hover:bg-brand-100"
                          }`}
                          title={v.status === "active" ? "Pause Campaign" : "Resume Campaign"}
                        >
                          {v.status === "active" ? (
                            <>
                              <Pause className="w-3.5 h-3.5" />
                              <span>Pause</span>
                            </>
                          ) : (
                            <>
                              <Play className="w-3.5 h-3.5" />
                              <span>Resume</span>
                            </>
                          )}
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* Modal: Create New Campaign */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-slate-200 max-w-lg w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <Ticket className="w-4 h-4 text-brand-600" />
                <h3 className="text-sm font-bold text-slate-900">
                  Launch New Voucher Campaign
                </h3>
              </div>
              <button
                onClick={() => setShowCreateModal(false)}
                className="text-slate-400 hover:text-slate-700 text-sm"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleCreateVoucher} className="space-y-3.5 text-xs">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-semibold text-slate-800 mb-1">Coupon Code</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. MONSOON2026"
                    value={newCode}
                    onChange={(e) => setNewCode(e.target.value.toUpperCase())}
                    className="w-full p-2 bg-slate-50 border border-slate-200 rounded-xl font-mono uppercase text-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500/20"
                  />
                </div>

                <div>
                  <label className="block font-semibold text-slate-800 mb-1">Discount Type</label>
                  <div className="grid grid-cols-2 gap-1.5">
                    <button
                      type="button"
                      onClick={() => setDiscountType("flat")}
                      className={`py-1.5 text-xs font-semibold rounded-lg border transition-all ${
                        discountType === "flat"
                          ? "bg-brand-600 text-white border-brand-600 shadow-sm"
                          : "bg-slate-50 text-slate-600 border-slate-200"
                      }`}
                    >
                      Flat NPR
                    </button>
                    <button
                      type="button"
                      onClick={() => setDiscountType("percentage")}
                      className={`py-1.5 text-xs font-semibold rounded-lg border transition-all ${
                        discountType === "percentage"
                          ? "bg-brand-600 text-white border-brand-600 shadow-sm"
                          : "bg-slate-50 text-slate-600 border-slate-200"
                      }`}
                    >
                      Percentage %
                    </button>
                  </div>
                </div>
              </div>

              <div>
                <label className="block font-semibold text-slate-800 mb-1">Campaign Title</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Free Delivery & Flat 100 Off on Food"
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  className="w-full p-2 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500/20"
                />
              </div>

              <div>
                <label className="block font-semibold text-slate-800 mb-1">Campaign Description</label>
                <input
                  type="text"
                  placeholder="e.g. Valid on all restaurant orders above Rs 400."
                  value={newDesc}
                  onChange={(e) => setNewDesc(e.target.value)}
                  className="w-full p-2 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500/20"
                />
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block font-semibold text-slate-800 mb-1">
                    {discountType === "flat" ? "Discount (Rs)" : "Discount (%)"}
                  </label>
                  <input
                    type="number"
                    min="1"
                    required
                    value={discountVal}
                    onChange={(e) => setDiscountVal(Number(e.target.value))}
                    className="w-full p-2 bg-slate-50 border border-slate-200 rounded-xl font-mono text-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500/20"
                  />
                </div>

                <div>
                  <label className="block font-semibold text-slate-800 mb-1">Min Spend (Rs)</label>
                  <input
                    type="number"
                    min="50"
                    required
                    value={minOrder}
                    onChange={(e) => setMinOrder(Number(e.target.value))}
                    className="w-full p-2 bg-slate-50 border border-slate-200 rounded-xl font-mono text-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500/20"
                  />
                </div>

                <div>
                  <label className="block font-semibold text-slate-800 mb-1">Max Cap (Rs)</label>
                  <input
                    type="number"
                    min="10"
                    required
                    value={maxDiscount}
                    onChange={(e) => setMaxDiscount(Number(e.target.value))}
                    className="w-full p-2 bg-slate-50 border border-slate-200 rounded-xl font-mono text-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500/20"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-semibold text-slate-800 mb-1">Budget Allocation (NPR)</label>
                  <input
                    type="number"
                    min="1000"
                    required
                    value={budgetNpr}
                    onChange={(e) => setBudgetNpr(Number(e.target.value))}
                    className="w-full p-2 bg-slate-50 border border-slate-200 rounded-xl font-mono text-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500/20"
                  />
                </div>

                <div>
                  <label className="block font-semibold text-slate-800 mb-1">Redemptions Cap</label>
                  <input
                    type="number"
                    min="10"
                    required
                    value={totalClaims}
                    onChange={(e) => setTotalClaims(Number(e.target.value))}
                    className="w-full p-2 bg-slate-50 border border-slate-200 rounded-xl font-mono text-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500/20"
                  />
                </div>
              </div>

              <div className="flex items-center justify-end gap-2.5 pt-3 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="px-4 py-2 font-semibold text-slate-500 hover:text-slate-800"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-brand-600 hover:bg-brand-700 text-white font-bold rounded-xl shadow-sm transition-all"
                >
                  Launch Voucher
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
