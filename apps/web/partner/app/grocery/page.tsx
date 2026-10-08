"use client";

import { useState, useEffect } from "react";
import {
  ShoppingBag,
  CheckSquare,
  Square,
  PackageCheck,
  CheckCircle2,
  ScanBarcode,
  Bike,
  Clock,
  ArrowRight,
} from "lucide-react";
import { PartnerHeader } from "@/components/PartnerHeader";
import {
  fetchActiveOrders,
  toggleGroceryItemPacked,
  updateOrderStatus,
} from "@/lib/partner/api";
import { KdsOrderTicket } from "@/lib/partner/types";

export default function GroceryPickAndPackPage() {
  const [orders, setOrders] = useState<KdsOrderTicket[]>([]);
  const [selectedOrderId, setSelectedOrderId] = useState<string>("");
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [actionAlert, setActionAlert] = useState<string | null>(null);

  const loadOrders = async () => {
    setIsRefreshing(true);
    const data = await fetchActiveOrders();
    const groceryOnly = data.filter((o) => o.vertical === "grocery");
    setOrders(groceryOnly);
    if (groceryOnly.length > 0 && !selectedOrderId) {
      setSelectedOrderId(groceryOnly[0].id);
    }
    setIsRefreshing(false);
  };

  useEffect(() => {
    loadOrders();
  }, []);

  const currentOrder =
    orders.find((o) => o.id === selectedOrderId) || orders[0] || null;

  const handleToggleItem = async (orderId: string, itemId: string) => {
    await toggleGroceryItemPacked(orderId, itemId);
    setOrders((prev) =>
      prev.map((o) => {
        if (o.id === orderId) {
          return {
            ...o,
            items: o.items.map((i) =>
              i.id === itemId ? { ...i, isPacked: !i.isPacked } : i
            ),
          };
        }
        return o;
      })
    );
  };

  const handleMarkBagged = async (orderId: string) => {
    await updateOrderStatus(orderId, "READY_FOR_PICKUP");
    setOrders((prev) =>
      prev.map((o) => (o.id === orderId ? { ...o, status: "READY_FOR_PICKUP" } : o))
    );
    setActionAlert(`Order #${orderId} bagged and sealed! Rider dispatched.`);
    setTimeout(() => setActionAlert(null), 4000);
  };

  const packedCount = currentOrder?.items.filter((i) => i.isPacked).length || 0;
  const totalCount = currentOrder?.items.length || 0;
  const progressPercent = totalCount > 0 ? Math.round((packedCount / totalCount) * 100) : 0;

  return (
    <div className="flex-1 flex flex-col h-screen overflow-hidden bg-slate-50">
      <PartnerHeader
        title="Grocery Picking & Counter Packing Desk"
        subtitle="Quick-Commerce Item Checklists • Barcode Verification • Bag Sealing & Rider Handoff"
        onRefresh={loadOrders}
        isRefreshing={isRefreshing}
      />

      {/* Action Notification Banner */}
      {actionAlert && (
        <div className="bg-emerald-600 text-white px-6 py-2.5 text-xs font-bold flex items-center justify-between shrink-0 shadow-xs animate-in fade-in">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 shrink-0" />
            <span>{actionAlert}</span>
          </div>
          <span className="font-mono text-[11px] opacity-90">Bag Sealed</span>
        </div>
      )}

      {/* Split 2-Pane Picking Desk */}
      <div className="flex-1 flex overflow-hidden">
        {/* Left Pane: Orders Awaiting Packing */}
        <div className="w-80 md:w-96 border-r border-slate-200 bg-white flex flex-col shrink-0">
          <div className="p-4 border-b border-slate-100 flex items-center justify-between">
            <h2 className="text-xs font-black uppercase tracking-wider text-slate-800 flex items-center gap-2">
              <ShoppingBag className="w-4 h-4 text-brand-600" />
              <span>Pending Picklists ({orders.length})</span>
            </h2>
            <span className="text-[10px] font-mono font-bold text-slate-400">
              Live Queue
            </span>
          </div>

          <div className="flex-1 overflow-y-auto p-3 space-y-2.5">
            {orders.map((o) => {
              const isSelected = o.id === selectedOrderId;
              const pCount = o.items.filter((i) => i.isPacked).length;
              return (
                <button
                  key={o.id}
                  type="button"
                  onClick={() => setSelectedOrderId(o.id)}
                  className={`w-full text-left p-3.5 rounded-xl border transition-all space-y-2 ${
                    isSelected
                      ? "border-brand-500 bg-brand-50/40 ring-1 ring-brand-500 shadow-xs"
                      : "border-slate-200 hover:border-slate-300 bg-white"
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <span className="font-mono text-xs font-bold text-slate-900">
                      {o.displayNumber}
                    </span>
                    <span
                      className={`text-[10px] font-bold uppercase px-2 py-0.5 rounded-full ${
                        o.status === "READY_FOR_PICKUP"
                          ? "bg-emerald-100 text-emerald-800"
                          : "bg-blue-100 text-blue-800"
                      }`}
                    >
                      {o.status.replace(/_/g, " ")}
                    </span>
                  </div>

                  <p className="text-xs text-slate-600 font-medium">
                    {o.customerName}
                  </p>

                  <div className="flex items-center justify-between text-[11px] text-slate-500 pt-1 border-t border-slate-100">
                    <span>
                      {pCount} / {o.items.length} items packed
                    </span>
                    <span className="font-mono font-semibold">NPR {o.totalNpr}</span>
                  </div>
                </button>
              );
            })}
          </div>
        </div>

        {/* Right Pane: Active Picklist Inspection & Packing */}
        <div className="flex-1 flex flex-col h-full bg-slate-50 overflow-y-auto p-6 md:p-8 space-y-6">
          {currentOrder ? (
            <div className="max-w-3xl w-full mx-auto space-y-6">
              {/* Top Order Card */}
              <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-xs space-y-4">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 pb-4">
                  <div>
                    <div className="flex items-center gap-2.5">
                      <h2 className="text-lg font-black text-slate-900 font-mono">
                        {currentOrder.displayNumber}
                      </h2>
                      <span className="text-xs px-2.5 py-0.5 bg-brand-50 text-brand-700 font-bold rounded-md border border-brand-200">
                        Bhatbhateni Quick-Commerce
                      </span>
                    </div>
                    <p className="text-xs text-slate-500 mt-1">
                      Customer: {currentOrder.customerName} ({currentOrder.customerPhone})
                    </p>
                    <p className="text-xs text-slate-500">
                      Destination: {currentOrder.deliveryAddress}
                    </p>
                  </div>

                  <div className="text-right">
                    <span className="text-xs text-slate-400 block">Total Amount</span>
                    <span className="text-xl font-black text-slate-900 font-mono">
                      NPR {currentOrder.totalNpr}
                    </span>
                    <span className="text-[11px] font-bold text-emerald-600 block mt-0.5">
                      {currentOrder.paymentMethod.replace(/_/g, " ")}
                    </span>
                  </div>
                </div>

                {/* Pick Progress Bar */}
                <div className="space-y-1.5">
                  <div className="flex items-center justify-between text-xs font-semibold">
                    <span className="text-slate-700">
                      Packing Progress: {packedCount} of {totalCount} Items Packed
                    </span>
                    <span className="font-mono text-brand-600">{progressPercent}%</span>
                  </div>
                  <div className="w-full h-2.5 rounded-full bg-slate-100 overflow-hidden">
                    <div
                      className="h-full bg-brand-600 transition-all duration-300 rounded-full"
                      style={{ width: `${progressPercent}%` }}
                    />
                  </div>
                </div>
              </div>

              {/* Items Picklist */}
              <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
                <div className="p-4 bg-slate-50/60 border-b border-slate-200/80 flex items-center justify-between">
                  <h3 className="text-xs font-black uppercase tracking-wider text-slate-800">
                    Aisle Items Checklist (Tap item to check off)
                  </h3>
                  <button
                    type="button"
                    className="flex items-center gap-1 text-xs font-bold text-brand-600 hover:text-brand-700"
                  >
                    <ScanBarcode className="w-3.5 h-3.5" />
                    <span>Scan Barcode</span>
                  </button>
                </div>

                <div className="divide-y divide-slate-100">
                  {currentOrder.items.map((item) => (
                    <div
                      key={item.id}
                      onClick={() => handleToggleItem(currentOrder.id, item.id)}
                      className={`p-4 flex items-center justify-between cursor-pointer transition-colors ${
                        item.isPacked ? "bg-emerald-50/30" : "hover:bg-slate-50"
                      }`}
                    >
                      <div className="flex items-center gap-3.5">
                        <button
                          type="button"
                          className="text-slate-400 hover:text-brand-600 transition-colors"
                        >
                          {item.isPacked ? (
                            <CheckSquare className="w-5 h-5 text-emerald-600" />
                          ) : (
                            <Square className="w-5 h-5 text-slate-300" />
                          )}
                        </button>
                        <div>
                          <p
                            className={`text-xs font-bold ${
                              item.isPacked
                                ? "text-slate-500 line-through"
                                : "text-slate-900"
                            }`}
                          >
                            {item.quantity}x {item.name}
                          </p>
                          <span className="text-[11px] text-slate-400 font-mono">
                            Unit Price: NPR {item.priceNpr}
                          </span>
                        </div>
                      </div>

                      <span className="font-mono text-xs font-bold text-slate-700">
                        NPR {item.priceNpr * item.quantity}
                      </span>
                    </div>
                  ))}
                </div>

                {/* Bottom Bag Seal Button */}
                <div className="p-4 bg-slate-50 border-t border-slate-100 flex items-center justify-between">
                  <span className="text-xs text-slate-500">
                    {packedCount === totalCount
                      ? "All items verified. Ready to seal bag."
                      : "Check off all items before sealing bag."}
                  </span>

                  <button
                    type="button"
                    disabled={packedCount !== totalCount || currentOrder.status === "READY_FOR_PICKUP"}
                    onClick={() => handleMarkBagged(currentOrder.id)}
                    className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-700 disabled:bg-slate-300 disabled:cursor-not-allowed text-white text-xs font-bold rounded-xl transition-all shadow-xs flex items-center gap-1.5"
                  >
                    <PackageCheck className="w-4 h-4" />
                    <span>Seal Bag & Alert Rider</span>
                  </button>
                </div>
              </div>
            </div>
          ) : (
            <div className="text-center py-24 text-slate-400 text-xs italic">
              No grocery orders currently in packing queue.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
