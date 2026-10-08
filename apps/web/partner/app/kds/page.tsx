"use client";

import { useState, useEffect } from "react";
import {
  UtensilsCrossed,
  Printer,
  Clock,
  Bike,
  CheckCircle2,
  XCircle,
  Phone,
  AlertCircle,
  Sparkles,
} from "lucide-react";
import { PartnerHeader } from "@/components/PartnerHeader";
import {
  fetchActiveOrders,
  updateOrderStatus,
} from "@/lib/partner/api";
import { KdsOrderTicket, OrderTicketStatus } from "@/lib/partner/types";

export default function KitchenDisplayPage() {
  const [orders, setOrders] = useState<KdsOrderTicket[]>([]);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [selectedKdsOrder, setSelectedKdsOrder] = useState<KdsOrderTicket | null>(null);
  const [showPrintModal, setShowPrintModal] = useState(false);
  const [actionAlert, setActionAlert] = useState<string | null>(null);

  const loadOrders = async () => {
    setIsRefreshing(true);
    const data = await fetchActiveOrders();
    setOrders(data);
    setIsRefreshing(false);
  };

  useEffect(() => {
    loadOrders();
  }, []);

  const handleUpdateStatus = async (
    orderId: string,
    status: OrderTicketStatus,
    prepMinutes?: number
  ) => {
    const res = await updateOrderStatus(orderId, status, prepMinutes);
    if (res.success) {
      setOrders((prev) =>
        prev.map((o) =>
          o.id === orderId
            ? { ...o, status, estimatedPrepMinutes: prepMinutes ?? o.estimatedPrepMinutes }
            : o
        )
      );
      setActionAlert(`Order #${orderId} updated to ${status.replace(/_/g, " ")}`);
      setTimeout(() => setActionAlert(null), 4000);
    }
  };

  const pendingOrders = orders.filter((o) => o.status === "PENDING");
  const preparingOrders = orders.filter((o) => o.status === "PREPARING");
  const readyOrders = orders.filter((o) => o.status === "READY_FOR_PICKUP");

  return (
    <div className="flex-1 flex flex-col h-screen overflow-hidden bg-slate-100/75">
      <PartnerHeader
        title="Kitchen Display System (KDS)"
        subtitle="Real-Time Order Queue • Preparation Timers • Thermal KOT Printing & Rider Handoff"
        onRefresh={loadOrders}
        isRefreshing={isRefreshing}
      />

      {/* Action Notification Banner */}
      {actionAlert && (
        <div className="bg-emerald-600 text-white px-6 py-2 text-xs font-bold flex items-center justify-between shrink-0 shadow-sm animate-in fade-in">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 shrink-0" />
            <span>{actionAlert}</span>
          </div>
          <span className="font-mono text-[11px] opacity-90">Synced to Cloud</span>
        </div>
      )}

      {/* KDS 3-Lane Horizontal Board */}
      <div className="flex-1 overflow-x-auto p-4 md:p-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-5 min-w-[900px] h-full items-start">
          {/* LANE 1: NEW / PENDING */}
          <div className="flex flex-col h-full bg-slate-200/60 rounded-2xl p-3 border border-slate-300/70 overflow-hidden">
            <div className="flex items-center justify-between pb-3 px-1 border-b border-slate-300">
              <div className="flex items-center gap-2">
                <span className="w-3 h-3 rounded-full bg-amber-500 animate-ping" />
                <h2 className="text-xs font-black uppercase tracking-wider text-slate-800">
                  New Incoming ({pendingOrders.length})
                </h2>
              </div>
              <span className="text-[10px] font-bold text-amber-700 bg-amber-100 px-2 py-0.5 rounded-md">
                Action Required
              </span>
            </div>

            <div className="flex-1 overflow-y-auto space-y-3 pt-3 pr-1">
              {pendingOrders.length === 0 ? (
                <div className="text-center py-16 text-slate-400 text-xs italic">
                  No new pending orders right now.
                </div>
              ) : (
                pendingOrders.map((order) => (
                  <div
                    key={order.id}
                    className="bg-white rounded-xl border border-amber-300 shadow-sm p-4 space-y-3 ring-2 ring-amber-400/20"
                  >
                    <div className="flex items-center justify-between border-b border-slate-100 pb-2">
                      <div>
                        <span className="font-mono text-sm font-black text-slate-900">
                          {order.displayNumber}
                        </span>
                        <p className="text-[11px] text-slate-500 font-medium">
                          {order.customerName} • {order.createdAt}
                        </p>
                      </div>
                      <span className="px-2 py-0.5 rounded bg-amber-100 text-amber-800 text-[10px] font-bold uppercase">
                        {order.orderType}
                      </span>
                    </div>

                    {/* Order Items */}
                    <div className="space-y-1.5 py-1">
                      {order.items.map((i) => (
                        <div key={i.id} className="text-xs text-slate-800">
                          <div className="flex items-center justify-between font-semibold">
                            <span className="flex items-center gap-1.5">
                              <span className="w-4 h-4 rounded bg-slate-100 text-slate-900 flex items-center justify-center font-mono text-[10px] font-bold">
                                {i.quantity}
                              </span>
                              <span>{i.name}</span>
                            </span>
                            <span className="font-mono text-[11px] text-slate-500">
                              NPR {i.priceNpr * i.quantity}
                            </span>
                          </div>
                          {i.notes && (
                            <p className="text-[11px] text-amber-700 italic pl-5 mt-0.5">
                              Note: &quot;{i.notes}&quot;
                            </p>
                          )}
                        </div>
                      ))}
                    </div>

                    {/* KOT Print & Accept Controls */}
                    <div className="pt-2 border-t border-slate-100 space-y-2">
                      <div className="grid grid-cols-3 gap-1.5">
                        {[15, 20, 30].map((mins) => (
                          <button
                            key={mins}
                            type="button"
                            onClick={() => handleUpdateStatus(order.id, "PREPARING", mins)}
                            className="py-2 bg-brand-600 hover:bg-brand-700 text-white rounded-lg text-xs font-bold transition-all shadow-2xs"
                          >
                            Accept {mins}m
                          </button>
                        ))}
                      </div>

                      <div className="flex items-center justify-between gap-2 pt-1">
                        <button
                          type="button"
                          onClick={() => {
                            setSelectedKdsOrder(order);
                            setShowPrintModal(true);
                          }}
                          className="flex-1 py-1.5 border border-slate-200 hover:bg-slate-50 text-slate-700 rounded-lg text-xs font-semibold flex items-center justify-center gap-1 transition-colors"
                        >
                          <Printer className="w-3.5 h-3.5" />
                          <span>Print KOT</span>
                        </button>

                        <button
                          type="button"
                          onClick={() => handleUpdateStatus(order.id, "REJECTED")}
                          className="px-3 py-1.5 border border-rose-200 hover:bg-rose-50 text-rose-700 rounded-lg text-xs font-semibold transition-colors"
                        >
                          Reject
                        </button>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* LANE 2: COOKING / PREPARING */}
          <div className="flex flex-col h-full bg-slate-200/60 rounded-2xl p-3 border border-slate-300/70 overflow-hidden">
            <div className="flex items-center justify-between pb-3 px-1 border-b border-slate-300">
              <div className="flex items-center gap-2">
                <span className="w-3 h-3 rounded-full bg-blue-500" />
                <h2 className="text-xs font-black uppercase tracking-wider text-slate-800">
                  Kitchen Cooking ({preparingOrders.length})
                </h2>
              </div>
              <span className="text-[10px] font-bold text-blue-700 bg-blue-100 px-2 py-0.5 rounded-md">
                In Preparation
              </span>
            </div>

            <div className="flex-1 overflow-y-auto space-y-3 pt-3 pr-1">
              {preparingOrders.length === 0 ? (
                <div className="text-center py-16 text-slate-400 text-xs italic">
                  No orders currently cooking.
                </div>
              ) : (
                preparingOrders.map((order) => (
                  <div
                    key={order.id}
                    className="bg-white rounded-xl border border-slate-200 shadow-sm p-4 space-y-3"
                  >
                    <div className="flex items-center justify-between border-b border-slate-100 pb-2">
                      <div>
                        <span className="font-mono text-sm font-black text-slate-900">
                          {order.displayNumber}
                        </span>
                        <p className="text-[11px] text-slate-500 font-medium">
                          {order.customerName}
                        </p>
                      </div>
                      <div className="flex items-center gap-1 bg-blue-50 border border-blue-200 text-blue-700 px-2 py-0.5 rounded-md text-xs font-mono font-bold">
                        <Clock className="w-3.5 h-3.5" />
                        <span>{order.estimatedPrepMinutes}m target</span>
                      </div>
                    </div>

                    <div className="space-y-1.5 py-1">
                      {order.items.map((i) => (
                        <div key={i.id} className="text-xs text-slate-800 flex items-center justify-between font-medium">
                          <span className="flex items-center gap-1.5">
                            <span className="w-4 h-4 rounded bg-slate-100 text-slate-900 flex items-center justify-center font-mono text-[10px] font-bold">
                              {i.quantity}
                            </span>
                            <span>{i.name}</span>
                          </span>
                        </div>
                      ))}
                    </div>

                    {/* Mark Ready Button */}
                    <div className="pt-2 border-t border-slate-100">
                      <button
                        type="button"
                        onClick={() => handleUpdateStatus(order.id, "READY_FOR_PICKUP")}
                        className="w-full py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition-all shadow-xs flex items-center justify-center gap-1.5"
                      >
                        <CheckCircle2 className="w-4 h-4" />
                        <span>Mark Ready for Pickup</span>
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* LANE 3: READY FOR PICKUP / RIDER WAITING */}
          <div className="flex flex-col h-full bg-slate-200/60 rounded-2xl p-3 border border-slate-300/70 overflow-hidden">
            <div className="flex items-center justify-between pb-3 px-1 border-b border-slate-300">
              <div className="flex items-center gap-2">
                <span className="w-3 h-3 rounded-full bg-emerald-500" />
                <h2 className="text-xs font-black uppercase tracking-wider text-slate-800">
                  Ready for Pickup ({readyOrders.length})
                </h2>
              </div>
              <span className="text-[10px] font-bold text-emerald-700 bg-emerald-100 px-2 py-0.5 rounded-md">
                Rider Handoff
              </span>
            </div>

            <div className="flex-1 overflow-y-auto space-y-3 pt-3 pr-1">
              {readyOrders.length === 0 ? (
                <div className="text-center py-16 text-slate-400 text-xs italic">
                  No orders waiting for pickup.
                </div>
              ) : (
                readyOrders.map((order) => (
                  <div
                    key={order.id}
                    className="bg-white rounded-xl border border-emerald-200 shadow-sm p-4 space-y-3"
                  >
                    <div className="flex items-center justify-between border-b border-slate-100 pb-2">
                      <div>
                        <span className="font-mono text-sm font-black text-slate-900">
                          {order.displayNumber}
                        </span>
                        <p className="text-[11px] text-slate-500 font-medium">
                          Packed & Ready on Counter
                        </p>
                      </div>
                      <span className="px-2 py-0.5 rounded bg-emerald-100 text-emerald-800 text-[10px] font-bold uppercase">
                        Bagged
                      </span>
                    </div>

                    {/* Assigned Rider Info */}
                    {order.assignedRider && (
                      <div className="p-2.5 rounded-lg bg-slate-50 border border-slate-200 text-xs space-y-1">
                        <div className="flex items-center justify-between font-bold text-slate-900">
                          <span className="flex items-center gap-1.5">
                            <Bike className="w-3.5 h-3.5 text-brand-600" />
                            <span>{order.assignedRider.name}</span>
                          </span>
                          <a
                            href={`tel:${order.assignedRider.phone}`}
                            className="p-1 rounded hover:bg-slate-200 text-slate-600"
                            title="Call Rider"
                          >
                            <Phone className="w-3 h-3" />
                          </a>
                        </div>
                        <p className="text-[11px] text-slate-500 font-mono">
                          {order.assignedRider.vehiclePlate}
                        </p>
                      </div>
                    )}

                    {/* Complete Handoff Button */}
                    <div className="pt-2 border-t border-slate-100">
                      <button
                        type="button"
                        onClick={() => handleUpdateStatus(order.id, "COMPLETED")}
                        className="w-full py-2 bg-slate-800 hover:bg-slate-900 text-white rounded-xl text-xs font-bold transition-all shadow-xs flex items-center justify-center gap-1.5"
                      >
                        <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                        <span>Confirm Rider Handoff</span>
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      </div>

      {/* KOT Thermal Print Modal Simulation */}
      {showPrintModal && selectedKdsOrder && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-slate-200 max-w-sm w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in-95 duration-150">
            <div className="text-center border-b border-dashed border-slate-300 pb-3 space-y-1">
              <h3 className="font-mono text-base font-black tracking-tight text-slate-900">
                KITCHEN ORDER TICKET (KOT)
              </h3>
              <p className="font-mono text-xs text-slate-600">Roadhouse Cafe • Pulchowk</p>
              <p className="font-mono text-[11px] text-slate-400">
                Order {selectedKdsOrder.displayNumber} • {new Date().toLocaleTimeString()}
              </p>
            </div>

            <div className="font-mono text-xs space-y-2 py-2 border-b border-dashed border-slate-300">
              {selectedKdsOrder.items.map((i) => (
                <div key={i.id} className="flex justify-between">
                  <span>
                    {i.quantity}x {i.name}
                  </span>
                  <span>{i.isVeg ? "[VEG]" : "[NON-VEG]"}</span>
                </div>
              ))}
            </div>

            <div className="font-mono text-[11px] text-slate-500 space-y-1">
              <p>Type: {selectedKdsOrder.orderType}</p>
              <p>Customer: {selectedKdsOrder.customerName}</p>
              <p>Address: {selectedKdsOrder.deliveryAddress}</p>
            </div>

            <div className="pt-2 flex items-center gap-2">
              <button
                type="button"
                onClick={() => {
                  window.print();
                  setShowPrintModal(false);
                }}
                className="flex-1 py-2 bg-brand-600 hover:bg-brand-700 text-white rounded-xl text-xs font-bold transition-all shadow-xs flex items-center justify-center gap-1"
              >
                <Printer className="w-4 h-4" />
                <span>Send to Thermal Printer</span>
              </button>
              <button
                type="button"
                onClick={() => setShowPrintModal(false)}
                className="px-4 py-2 border border-slate-200 hover:bg-slate-50 text-slate-700 rounded-xl text-xs font-semibold"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
