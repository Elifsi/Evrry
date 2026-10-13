"use client";

import { useState, useEffect } from "react";
import {
  ShoppingBag,
  Zap,
  Store,
  Clock,
  CheckSquare,
  Square,
  PackageCheck,
  CheckCircle2,
  ScanBarcode,
  Bike,
  Phone,
  AlertTriangle,
  Search,
  DollarSign,
  ShieldCheck,
  ArrowRight,
  XCircle,
  Plus,
  Minus,
  RefreshCw,
  SlidersHorizontal,
  ChevronDown,
} from "lucide-react";
import { PartnerHeader } from "@/components/PartnerHeader";
import {
  fetchActiveOrders,
  fetchSupermarketShelfItems,
  toggleGroceryItemPacked,
  toggleSupermarketShelfItemInStock,
  updateSupermarketShelfItemStock,
  resolveItemSubstitution,
  sealDarkStoreBag,
  updateOrderStatus,
  toggleStoreAcceptingOrders,
  fetchPartnerFinancials,
  INITIAL_STORES,
} from "@/lib/partner/api";
import {
  KdsOrderTicket,
  SupermarketShelfItem,
  PartnerStore,
  PartnerFinancialLedger,
} from "@/lib/partner/types";

type GroceryDeskMode = "DARK_STORE" | "SUPERMARKET";
type SupermarketSubTab = "ORDERS" | "SHELF_INVENTORY" | "SETTINGS";

export default function GroceryOperationsPage() {
  // Top Fulfillment Desk Mode
  const [deskMode, setDeskMode] = useState<GroceryDeskMode>("DARK_STORE");

  // Orders State
  const [orders, setOrders] = useState<KdsOrderTicket[]>([]);
  const [selectedOrderId, setSelectedOrderId] = useState<string>("");
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [actionAlert, setActionAlert] = useState<string | null>(null);

  // Supermarket Sub-tab and Selected Retail Venue
  const [supermarketTab, setSupermarketTab] = useState<SupermarketSubTab>("ORDERS");
  const [selectedStoreId, setSelectedStoreId] = useState<string>("store_bb_pulchowk");
  const [stores, setStores] = useState<PartnerStore[]>([]);
  const [shelfItems, setShelfItems] = useState<SupermarketShelfItem[]>([]);
  const [shelfCategoryFilter, setShelfCategoryFilter] = useState<string>("ALL");
  const [shelfSearchQuery, setShelfSearchQuery] = useState<string>("");
  const [financials, setFinancials] = useState<PartnerFinancialLedger | null>(null);

  // Substitution Resolution Modal state
  const [substitutionModal, setSubstitutionModal] = useState<{
    orderId: string;
    itemId: string;
    itemName: string;
    unitPrice: number;
    quantity: number;
    preference: string;
  } | null>(null);
  const [customReplacementName, setCustomReplacementName] = useState("");

  // Dark store seal code state
  const [generatedSealCode, setGeneratedSealCode] = useState<string>("SEAL-JH-8821");

  const loadData = async () => {
    setIsRefreshing(true);
    const groceryStores = INITIAL_STORES.filter((s) => s.vertical === "grocery");
    setStores(groceryStores);

    const activeOrders = await fetchActiveOrders();
    const groceryOrders = activeOrders.filter((o) => o.vertical === "grocery");
    setOrders(groceryOrders);

    const inventory = await fetchSupermarketShelfItems(selectedStoreId);
    setShelfItems(inventory);

    const fin = await fetchPartnerFinancials(selectedStoreId);
    setFinancials(fin);

    setIsRefreshing(false);
  };

  useEffect(() => {
    loadData();
  }, [selectedStoreId]);

  // Filter orders according to active desk mode
  const filteredOrders = orders.filter((o) => {
    if (deskMode === "DARK_STORE") {
      return o.fulfillmentType === "DARK_STORE_EXPRESS";
    } else {
      return (
        o.fulfillmentType === "SUPERMARKET_RETAIL" &&
        (!selectedStoreId || o.storeId === selectedStoreId)
      );
    }
  });

  // Keep a valid selected order
  useEffect(() => {
    if (filteredOrders.length > 0) {
      if (!filteredOrders.some((o) => o.id === selectedOrderId)) {
        setSelectedOrderId(filteredOrders[0].id);
      }
    } else {
      setSelectedOrderId("");
    }
  }, [deskMode, selectedStoreId, orders]);

  const currentOrder =
    filteredOrders.find((o) => o.id === selectedOrderId) || filteredOrders[0] || null;

  const currentStore =
    stores.find((s) => s.id === selectedStoreId) || stores[0] || null;

  // Handlers for Dark Store Pick & Pack
  const handleToggleItemPacked = async (orderId: string, itemId: string) => {
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

  const handleSealDarkStoreBag = async (orderId: string) => {
    const sealCode = `SEAL-JH-${Math.floor(1000 + Math.random() * 9000)}`;
    await sealDarkStoreBag(orderId, sealCode);
    setOrders((prev) =>
      prev.map((o) =>
        o.id === orderId
          ? { ...o, status: "READY_FOR_PICKUP", bagSealCode: sealCode }
          : o
      )
    );
    setActionAlert(`Order #${orderId} bagged with tamper seal ${sealCode}! Express rider alerted.`);
    setTimeout(() => setActionAlert(null), 4500);
  };

  // Handlers for Supermarket Retail Operations
  const handleToggleStoreStatus = async () => {
    if (!currentStore) return;
    const nextStatus = !currentStore.isAcceptingOrders;
    await toggleStoreAcceptingOrders(currentStore.id, nextStatus);
    setStores((prev) =>
      prev.map((s) => (s.id === currentStore.id ? { ...s, isAcceptingOrders: nextStatus } : s))
    );
    setActionAlert(
      nextStatus
        ? `${currentStore.name} is now OPEN and accepting online orders.`
        : `${currentStore.name} is PAUSED due to counter rush.`
    );
    setTimeout(() => setActionAlert(null), 4000);
  };

  const handleUpdatePrepBuffer = async (orderId: string, prepMins: number) => {
    await updateOrderStatus(orderId, "PREPARING", prepMins);
    setOrders((prev) =>
      prev.map((o) =>
        o.id === orderId
          ? { ...o, status: "PREPARING", estimatedPrepMinutes: prepMins }
          : o
      )
    );
    setActionAlert(`Prep time set to ${prepMins} mins. Customer notified.`);
    setTimeout(() => setActionAlert(null), 3500);
  };

  const handleMarkOrderReadyForRider = async (orderId: string) => {
    await updateOrderStatus(orderId, "READY_FOR_PICKUP");
    setOrders((prev) =>
      prev.map((o) => (o.id === orderId ? { ...o, status: "READY_FOR_PICKUP" } : o))
    );
    setActionAlert(`Order #${orderId} marked ready! Rider pickup requested.`);
    setTimeout(() => setActionAlert(null), 4000);
  };

  // Handlers for Substitution Resolution
  const handleExecuteSubstitution = async (action: "replace" | "refund") => {
    if (!substitutionModal) return;
    const { orderId, itemId, itemName } = substitutionModal;
    const replacement =
      action === "replace"
        ? customReplacementName || "Store Selected Equivalent"
        : undefined;

    const res = await resolveItemSubstitution(orderId, itemId, action, replacement);
    setOrders((prev) =>
      prev.map((o) => {
        if (o.id === orderId) {
          return {
            ...o,
            totalNpr: res.newTotal,
            items: o.items.map((i) => {
              if (i.id === itemId) {
                return action === "replace"
                  ? {
                      ...i,
                      isSubstituted: true,
                      substitutedWithName: replacement,
                      isPacked: true,
                    }
                  : {
                      ...i,
                      isRefunded: true,
                      isPacked: false,
                    };
              }
              return i;
            }),
          };
        }
        return o;
      })
    );

    setActionAlert(
      action === "replace"
        ? `Replaced "${itemName}" with "${replacement}". Customer cart updated.`
        : `Deducted "${itemName}" and initiated instant refund of NPR ${substitutionModal.unitPrice * substitutionModal.quantity}.`
    );
    setSubstitutionModal(null);
    setCustomReplacementName("");
    setTimeout(() => setActionAlert(null), 4500);
  };

  // Handlers for Shelf Inventory 86's
  const handleToggleShelfInStock = async (itemId: string, inStock: boolean) => {
    await toggleSupermarketShelfItemInStock(itemId, inStock);
    setShelfItems((prev) =>
      prev.map((i) => (i.id === itemId ? { ...i, inStock } : i))
    );
    const item = shelfItems.find((i) => i.id === itemId);
    setActionAlert(
      inStock
        ? `Marked "${item?.name}" IN STOCK.`
        : `86'd "${item?.name}". Synced across Evrry Consumer App.`
    );
    setTimeout(() => setActionAlert(null), 3000);
  };

  const handleAdjustShelfStock = async (itemId: string, delta: number) => {
    const item = shelfItems.find((i) => i.id === itemId);
    if (!item) return;
    const newStock = Math.max(0, item.stockCount + delta);
    await updateSupermarketShelfItemStock(itemId, newStock);
    setShelfItems((prev) =>
      prev.map((i) => (i.id === itemId ? { ...i, stockCount: newStock, inStock: newStock > 0 } : i))
    );
  };

  // Filter shelf items
  const filteredShelfItems = shelfItems.filter((item) => {
    const matchesCategory =
      shelfCategoryFilter === "ALL" ||
      item.category.toLowerCase().includes(shelfCategoryFilter.toLowerCase());
    const matchesSearch =
      item.name.toLowerCase().includes(shelfSearchQuery.toLowerCase()) ||
      item.barcode.includes(shelfSearchQuery);
    return matchesCategory && matchesSearch;
  });

  const packedCount = currentOrder?.items.filter((i) => i.isPacked || i.isSubstituted).length || 0;
  const activeItemsCount = currentOrder?.items.filter((i) => !i.isRefunded).length || 0;
  const progressPercent =
    activeItemsCount > 0 ? Math.round((packedCount / activeItemsCount) * 100) : 0;

  const darkStoreOrdersCount = orders.filter((o) => o.fulfillmentType === "DARK_STORE_EXPRESS").length;
  const retailOrdersCount = orders.filter((o) => o.fulfillmentType === "SUPERMARKET_RETAIL").length;

  return (
    <div className="flex-1 flex flex-col h-screen overflow-hidden bg-slate-50">
      <PartnerHeader
        title="Grocery Operations & Merchant Desk"
        subtitle="Dual-Model: ⚡ 10m Express Dark Store Hub & 🏪 Physical Supermarket / Kirana Fulfillment"
        onRefresh={loadData}
        isRefreshing={isRefreshing}
      />

      {/* Top Dual-Model Switcher Banner */}
      <div className="bg-slate-900 text-white px-6 py-2.5 flex flex-wrap items-center justify-between gap-3 shrink-0 shadow-sm">
        <div className="flex items-center gap-1.5 p-1 bg-slate-800 rounded-xl border border-slate-700/60">
          <button
            type="button"
            onClick={() => setDeskMode("DARK_STORE")}
            className={`flex items-center gap-2 px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all ${
              deskMode === "DARK_STORE"
                ? "bg-amber-500 text-slate-950 shadow-sm"
                : "text-slate-300 hover:text-white"
            }`}
          >
            <Zap className="w-3.5 h-3.5 fill-current" />
            <span>⚡ Dark Store Hub Desk (10m SLA)</span>
            <span
              className={`text-[10px] px-1.5 py-0.2 rounded-full font-mono font-black ${
                deskMode === "DARK_STORE"
                  ? "bg-slate-950 text-amber-400"
                  : "bg-slate-700 text-slate-300"
              }`}
            >
              {darkStoreOrdersCount}
            </span>
          </button>

          <button
            type="button"
            onClick={() => setDeskMode("SUPERMARKET")}
            className={`flex items-center gap-2 px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all ${
              deskMode === "SUPERMARKET"
                ? "bg-emerald-600 text-white shadow-sm"
                : "text-slate-300 hover:text-white"
            }`}
          >
            <Store className="w-3.5 h-3.5" />
            <span>🏪 Supermarket & Kirana Desk</span>
            <span
              className={`text-[10px] px-1.5 py-0.2 rounded-full font-mono font-black ${
                deskMode === "SUPERMARKET"
                  ? "bg-emerald-800 text-white"
                  : "bg-slate-700 text-slate-300"
              }`}
            >
              {retailOrdersCount}
            </span>
          </button>
        </div>

        {/* Dynamic Context Tag */}
        <div className="flex items-center gap-3 text-xs">
          {deskMode === "DARK_STORE" ? (
            <div className="flex items-center gap-2 text-amber-300 font-mono">
              <Clock className="w-3.5 h-3.5 animate-pulse" />
              <span>PST SLA: &lt; 180s Bin-to-Bag Target</span>
            </div>
          ) : (
            <div className="flex items-center gap-2 text-emerald-400 font-mono">
              <ShieldCheck className="w-3.5 h-3.5" />
              <span>NRB NRB Direct Settlement: ConnectIPS / Fonepay</span>
            </div>
          )}
        </div>
      </div>

      {/* Action Notification Banner */}
      {actionAlert && (
        <div className="bg-emerald-600 text-white px-6 py-2.5 text-xs font-bold flex items-center justify-between shrink-0 shadow-xs animate-in fade-in">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 shrink-0" />
            <span>{actionAlert}</span>
          </div>
          <span className="font-mono text-[11px] opacity-90">Live Sync</span>
        </div>
      )}

      {/* ========================================================================= */}
      {/* MODE 1: ⚡ DARK STORE HUB PICKING DESK                                    */}
      {/* ========================================================================= */}
      {deskMode === "DARK_STORE" && (
        <div className="flex-1 flex overflow-hidden">
          {/* Left Pane: Express Picklists Queue */}
          <div className="w-80 md:w-96 border-r border-slate-200 bg-white flex flex-col shrink-0">
            <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-amber-50/40">
              <div className="flex items-center gap-2">
                <Zap className="w-4 h-4 text-amber-600 fill-amber-600" />
                <h2 className="text-xs font-black uppercase tracking-wider text-slate-800">
                  Express Hub Queue ({filteredOrders.length})
                </h2>
              </div>
              <span className="text-[10px] font-mono font-bold bg-amber-100 text-amber-800 px-2 py-0.5 rounded-full">
                Jhamsikhel Hub
              </span>
            </div>

            <div className="flex-1 overflow-y-auto p-3 space-y-2.5">
              {filteredOrders.length === 0 ? (
                <div className="text-center py-16 text-slate-400 text-xs italic">
                  No active express dark-store picklists.
                </div>
              ) : (
                filteredOrders.map((o) => {
                  const isSelected = o.id === selectedOrderId;
                  const pCount = o.items.filter((i) => i.isPacked).length;
                  return (
                    <button
                      key={o.id}
                      type="button"
                      onClick={() => setSelectedOrderId(o.id)}
                      className={`w-full text-left p-3.5 rounded-xl border transition-all space-y-2 ${
                        isSelected
                          ? "border-amber-500 bg-amber-50/40 ring-1 ring-amber-500 shadow-xs"
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
                              : "bg-amber-100 text-amber-800 animate-pulse"
                          }`}
                        >
                          {o.status === "READY_FOR_PICKUP" ? "SEALED & READY" : "PICKING IN PROGRESS"}
                        </span>
                      </div>

                      <div className="flex items-center justify-between text-xs text-slate-600">
                        <span className="font-medium">{o.customerName}</span>
                        <span className="text-[11px] font-mono text-amber-700 bg-amber-50 px-1.5 py-0.5 rounded">
                          {o.createdAt}
                        </span>
                      </div>

                      <div className="flex items-center justify-between text-[11px] text-slate-500 pt-1 border-t border-slate-100">
                        <span>
                          {pCount} / {o.items.length} items scanned
                        </span>
                        <span className="font-mono font-semibold">NPR {o.totalNpr}</span>
                      </div>
                    </button>
                  );
                })
              )}
            </div>
          </div>

          {/* Right Pane: Active Express Picklist Details */}
          <div className="flex-1 flex flex-col h-full bg-slate-50 overflow-y-auto p-6 md:p-8 space-y-6">
            {currentOrder ? (
              <div className="max-w-3xl w-full mx-auto space-y-6">
                {/* Top Hub Card */}
                <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-xs space-y-4">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 pb-4">
                    <div>
                      <div className="flex items-center gap-2.5">
                        <h2 className="text-lg font-black text-slate-900 font-mono">
                          {currentOrder.displayNumber}
                        </h2>
                        <span className="text-xs px-2.5 py-0.5 bg-amber-100 text-amber-900 font-bold rounded-md border border-amber-300 flex items-center gap-1">
                          <Zap className="w-3 h-3 fill-amber-700 text-amber-700" />
                          10m Express Hub Fulfillment
                        </span>
                      </div>
                      <p className="text-xs text-slate-500 mt-1">
                        Customer: {currentOrder.customerName} ({currentOrder.customerPhone})
                      </p>
                      <p className="text-xs text-slate-500">
                        Delivery Hub: Evrry Jhamsikhel Hub 01 &bull; Destination: {currentOrder.deliveryAddress}
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

                  {/* Pick Progress Bar with SLA Countdown */}
                  <div className="space-y-1.5">
                    <div className="flex items-center justify-between text-xs font-semibold">
                      <span className="text-slate-700 flex items-center gap-1.5">
                        <span>Packing Progress: {packedCount} of {activeItemsCount} Items Picked</span>
                        {packedCount === activeItemsCount && (
                          <span className="text-emerald-600 font-bold flex items-center gap-0.5">
                            <CheckCircle2 className="w-3.5 h-3.5" /> Complete
                          </span>
                        )}
                      </span>
                      <span className="font-mono text-amber-600 font-bold">{progressPercent}%</span>
                    </div>
                    <div className="w-full h-2.5 rounded-full bg-slate-100 overflow-hidden">
                      <div
                        className="h-full bg-amber-500 transition-all duration-300 rounded-full"
                        style={{ width: `${progressPercent}%` }}
                      />
                    </div>
                  </div>
                </div>

                {/* Items Bin-to-Bag Checklist */}
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
                  <div className="p-4 bg-slate-50/80 border-b border-slate-200/80 flex items-center justify-between">
                    <div>
                      <h3 className="text-xs font-black uppercase tracking-wider text-slate-800">
                        Rack &amp; Bin Picklist (Organized by Warehouse Walkway)
                      </h3>
                      <p className="text-[11px] text-slate-400">
                        Tap item or scan barcode to verify rack location and pack into tote bag
                      </p>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-mono font-bold text-slate-500 bg-slate-200/70 px-2 py-0.5 rounded">
                        Express Bag #JH-1
                      </span>
                    </div>
                  </div>

                  <div className="divide-y divide-slate-100">
                    {currentOrder.items.map((item) => (
                      <div
                        key={item.id}
                        onClick={() => handleToggleItemPacked(currentOrder.id, item.id)}
                        className={`p-4 flex items-center justify-between cursor-pointer transition-colors ${
                          item.isPacked ? "bg-emerald-50/40" : "hover:bg-slate-50"
                        }`}
                      >
                        <div className="flex items-center gap-3.5">
                          <button
                            type="button"
                            className="text-slate-400 hover:text-amber-600 transition-colors"
                          >
                            {item.isPacked ? (
                              <CheckSquare className="w-5 h-5 text-emerald-600" />
                            ) : (
                              <Square className="w-5 h-5 text-slate-300" />
                            )}
                          </button>
                          <div>
                            <div className="flex items-center gap-2">
                              <p
                                className={`text-xs font-bold ${
                                  item.isPacked ? "text-slate-500 line-through" : "text-slate-900"
                                }`}
                              >
                                {item.quantity}x {item.name}
                              </p>
                              {item.binRackLocation && (
                                <span className="text-[10px] font-mono font-bold bg-amber-100 text-amber-900 px-2 py-0.5 rounded">
                                  {item.binRackLocation}
                                </span>
                              )}
                            </div>
                            <div className="flex items-center gap-3 text-[11px] text-slate-400 font-mono mt-0.5">
                              <span>Unit: NPR {item.priceNpr}</span>
                              {item.barcode && <span>Barcode: {item.barcode}</span>}
                            </div>
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
                    <div>
                      {currentOrder.bagSealCode ? (
                        <span className="text-xs font-mono font-bold text-emerald-700 bg-emerald-100 px-2.5 py-1 rounded-md">
                          Security Bag Seal: {currentOrder.bagSealCode}
                        </span>
                      ) : (
                        <span className="text-xs text-slate-500">
                          {packedCount === activeItemsCount
                            ? "All items scanned. Ready to apply tamper-evident seal."
                            : "Scan all bin items before sealing bag."}
                        </span>
                      )}
                    </div>

                    <button
                      type="button"
                      disabled={
                        packedCount !== activeItemsCount ||
                        currentOrder.status === "READY_FOR_PICKUP"
                      }
                      onClick={() => handleSealDarkStoreBag(currentOrder.id)}
                      className="px-5 py-2.5 bg-amber-500 hover:bg-amber-600 disabled:bg-slate-300 disabled:cursor-not-allowed text-slate-950 text-xs font-black rounded-xl transition-all shadow-xs flex items-center gap-2"
                    >
                      <PackageCheck className="w-4 h-4" />
                      <span>
                        {currentOrder.status === "READY_FOR_PICKUP"
                          ? "Bag Sealed & Rider Assigned"
                          : "Seal Bag & Alert Express Rider"}
                      </span>
                    </button>
                  </div>
                </div>
              </div>
            ) : (
              <div className="text-center py-24 text-slate-400 text-xs italic">
                Select a dark-store picklist from the left pane to begin fulfillment.
              </div>
            )}
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* MODE 2: 🏪 SUPERMARKET & KIRANA MERCHANT DESK                             */}
      {/* ========================================================================= */}
      {deskMode === "SUPERMARKET" && (
        <div className="flex-1 flex flex-col overflow-hidden">
          {/* Sub-Header: Store Switcher + Operating Status + Tabs */}
          <div className="bg-white border-b border-slate-200 px-6 py-3 flex flex-wrap items-center justify-between gap-4 shrink-0">
            {/* Store Venue Selector */}
            <div className="flex items-center gap-3">
              <Store className="w-5 h-5 text-emerald-600" />
              <div>
                <label className="text-[10px] font-bold uppercase tracking-wider text-slate-400 block">
                  Active Retail Merchant Store
                </label>
                <select
                  value={selectedStoreId}
                  onChange={(e) => setSelectedStoreId(e.target.value)}
                  className="text-xs font-black text-slate-900 bg-transparent border-none p-0 focus:ring-0 cursor-pointer"
                >
                  <option value="store_bb_pulchowk">Bhatbhateni Supermarket (Pulchowk)</option>
                  <option value="store_bigmart_jham">Big Mart Express (Jhamsikhel)</option>
                  <option value="store_mitho_kirana">Mitho Kirana &amp; Daily Needs (Bakhundole)</option>
                </select>
              </div>

              {/* Master Open / Pause Store Toggle */}
              {currentStore && (
                <button
                  type="button"
                  onClick={handleToggleStoreStatus}
                  className={`ml-4 px-3 py-1.5 rounded-lg text-xs font-bold flex items-center gap-1.5 transition-all ${
                    currentStore.isAcceptingOrders
                      ? "bg-emerald-100 text-emerald-800 hover:bg-emerald-200"
                      : "bg-red-100 text-red-800 hover:bg-red-200"
                  }`}
                >
                  <span
                    className={`w-2 h-2 rounded-full ${
                      currentStore.isAcceptingOrders ? "bg-emerald-600 animate-pulse" : "bg-red-600"
                    }`}
                  />
                  <span>
                    {currentStore.isAcceptingOrders ? "Store Open (Accepting)" : "Store Paused (Busy Counter)"}
                  </span>
                </button>
              )}
            </div>

            {/* Sub-Tab Navigation */}
            <div className="flex items-center gap-1 bg-slate-100 p-1 rounded-xl">
              <button
                type="button"
                onClick={() => setSupermarketTab("ORDERS")}
                className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                  supermarketTab === "ORDERS"
                    ? "bg-white text-slate-900 shadow-xs"
                    : "text-slate-500 hover:text-slate-900"
                }`}
              >
                📋 Orders &amp; Prep ({filteredOrders.length})
              </button>
              <button
                type="button"
                onClick={() => setSupermarketTab("SHELF_INVENTORY")}
                className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                  supermarketTab === "SHELF_INVENTORY"
                    ? "bg-white text-slate-900 shadow-xs"
                    : "text-slate-500 hover:text-slate-900"
                }`}
              >
                📦 Shelf Inventory (Quick 86&apos;s)
              </button>
              <button
                type="button"
                onClick={() => setSupermarketTab("SETTINGS")}
                className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                  supermarketTab === "SETTINGS"
                    ? "bg-white text-slate-900 shadow-xs"
                    : "text-slate-500 hover:text-slate-900"
                }`}
              >
                ⚙️ Settlement &amp; Settings
              </button>
            </div>
          </div>

          {/* Sub-Tab 1: Orders & Prep Desk */}
          {supermarketTab === "ORDERS" && (
            <div className="flex-1 flex overflow-hidden">
              {/* Left Pane: Supermarket Order Queue */}
              <div className="w-80 md:w-96 border-r border-slate-200 bg-white flex flex-col shrink-0">
                <div className="p-4 border-b border-slate-100 flex items-center justify-between">
                  <h2 className="text-xs font-black uppercase tracking-wider text-slate-800 flex items-center gap-2">
                    <ShoppingBag className="w-4 h-4 text-emerald-600" />
                    <span>Active Orders Queue ({filteredOrders.length})</span>
                  </h2>
                  <span className="text-[10px] font-mono text-slate-400">Live POS</span>
                </div>

                <div className="flex-1 overflow-y-auto p-3 space-y-2.5">
                  {filteredOrders.length === 0 ? (
                    <div className="text-center py-16 text-slate-400 text-xs italic">
                      No incoming retail orders for {currentStore?.name || "this store"}.
                    </div>
                  ) : (
                    filteredOrders.map((o) => {
                      const isSelected = o.id === selectedOrderId;
                      return (
                        <button
                          key={o.id}
                          type="button"
                          onClick={() => setSelectedOrderId(o.id)}
                          className={`w-full text-left p-3.5 rounded-xl border transition-all space-y-2 ${
                            isSelected
                              ? "border-emerald-500 bg-emerald-50/40 ring-1 ring-emerald-500 shadow-xs"
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

                          <div className="flex items-center justify-between text-xs text-slate-600">
                            <span className="font-medium">{o.customerName}</span>
                            <span className="text-[11px] font-mono text-slate-400">
                              {o.createdAt}
                            </span>
                          </div>

                          {/* Substitution preference pill on card */}
                          <div className="flex items-center justify-between text-[11px] pt-1 border-t border-slate-100">
                            <span className="text-slate-500">
                              {o.items.length} items &bull; Prep: {o.estimatedPrepMinutes}m
                            </span>
                            <span className="font-mono font-bold text-slate-900">
                              NPR {o.totalNpr}
                            </span>
                          </div>
                        </button>
                      );
                    })
                  )}
                </div>
              </div>

              {/* Right Pane: Order Inspection & Substitution Handling */}
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
                            <span className="text-xs px-2.5 py-0.5 bg-emerald-50 text-emerald-800 font-bold rounded-md border border-emerald-200">
                              Retail Supermarket Order
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
                          <span className="text-xs text-slate-400 block">Total Invoice</span>
                          <span className="text-xl font-black text-slate-900 font-mono">
                            NPR {currentOrder.totalNpr}
                          </span>
                          <span className="text-[11px] font-bold text-emerald-600 block mt-0.5">
                            {currentOrder.paymentMethod.replace(/_/g, " ")}
                          </span>
                        </div>
                      </div>

                      {/* Prep Buffer Adjuster */}
                      <div className="flex flex-wrap items-center justify-between gap-3 pt-1">
                        <div className="flex items-center gap-2">
                          <span className="text-xs font-bold text-slate-700">Prep Buffer:</span>
                          <div className="flex items-center gap-1.5">
                            {[15, 25, 35].map((mins) => (
                              <button
                                key={mins}
                                type="button"
                                onClick={() => handleUpdatePrepBuffer(currentOrder.id, mins)}
                                className={`px-2.5 py-1 text-xs font-mono font-bold rounded-md border transition-all ${
                                  currentOrder.estimatedPrepMinutes === mins
                                    ? "bg-emerald-600 text-white border-emerald-600"
                                    : "bg-white text-slate-700 border-slate-200 hover:border-slate-300"
                                }`}
                              >
                                {mins}m
                              </button>
                            ))}
                          </div>
                        </div>

                        {/* Customer Substitution Preference Badge */}
                        <div className="flex items-center gap-1.5">
                          {currentOrder.substitutionPreference === "callCustomer" && (
                            <span className="text-[11px] font-bold bg-blue-50 text-blue-800 border border-blue-200 px-2.5 py-1 rounded-lg flex items-center gap-1.5">
                              <Phone className="w-3 h-3 text-blue-600" />
                              Preference: Call Customer Before Replacing
                            </span>
                          )}
                          {currentOrder.substitutionPreference === "allowStoreSubstitution" && (
                            <span className="text-[11px] font-bold bg-amber-50 text-amber-900 border border-amber-200 px-2.5 py-1 rounded-lg flex items-center gap-1.5">
                              <RefreshCw className="w-3 h-3 text-amber-600" />
                              Preference: Allow Store Substitution
                            </span>
                          )}
                          {currentOrder.substitutionPreference === "refundItem" && (
                            <span className="text-[11px] font-bold bg-purple-50 text-purple-900 border border-purple-200 px-2.5 py-1 rounded-lg flex items-center gap-1.5">
                              <DollarSign className="w-3 h-3 text-purple-600" />
                              Preference: Refund Unavailable Items Directly
                            </span>
                          )}
                        </div>
                      </div>
                    </div>

                    {/* Items Checklist with Out-of-Stock Substitution Action */}
                    <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
                      <div className="p-4 bg-slate-50/80 border-b border-slate-200/80 flex items-center justify-between">
                        <div>
                          <h3 className="text-xs font-black uppercase tracking-wider text-slate-800">
                            Store Items Verification
                          </h3>
                          <p className="text-[11px] text-slate-400">
                            Mark packed or resolve missing shelf items via substitution
                          </p>
                        </div>
                      </div>

                      <div className="divide-y divide-slate-100">
                        {currentOrder.items.map((item) => (
                          <div
                            key={item.id}
                            className={`p-4 flex items-center justify-between transition-colors ${
                              item.isRefunded
                                ? "bg-red-50/40 opacity-70"
                                : item.isSubstituted
                                ? "bg-amber-50/40"
                                : item.isPacked
                                ? "bg-emerald-50/30"
                                : "hover:bg-slate-50"
                            }`}
                          >
                            <div className="flex items-center gap-3.5">
                              {!item.isRefunded && (
                                <button
                                  type="button"
                                  onClick={() => handleToggleItemPacked(currentOrder.id, item.id)}
                                  className="text-slate-400 hover:text-emerald-600 transition-colors"
                                >
                                  {item.isPacked ? (
                                    <CheckSquare className="w-5 h-5 text-emerald-600" />
                                  ) : (
                                    <Square className="w-5 h-5 text-slate-300" />
                                  )}
                                </button>
                              )}
                              {item.isRefunded && (
                                <XCircle className="w-5 h-5 text-red-500 shrink-0" />
                              )}

                              <div>
                                <p
                                  className={`text-xs font-bold ${
                                    item.isRefunded
                                      ? "text-red-700 line-through"
                                      : item.isPacked
                                      ? "text-slate-500"
                                      : "text-slate-900"
                                  }`}
                                >
                                  {item.quantity}x {item.name}
                                </p>

                                {item.isSubstituted && (
                                  <span className="text-[11px] font-bold text-amber-700 block">
                                    Substituted with: {item.substitutedWithName}
                                  </span>
                                )}

                                {item.isRefunded && (
                                  <span className="text-[11px] font-bold text-red-600 block">
                                    Out of stock &bull; NPR {item.priceNpr * item.quantity} refunded to customer
                                  </span>
                                )}

                                <span className="text-[11px] text-slate-400 font-mono">
                                  Unit Price: NPR {item.priceNpr}
                                </span>
                              </div>
                            </div>

                            <div className="flex items-center gap-3">
                              <span className="font-mono text-xs font-bold text-slate-700">
                                NPR {item.priceNpr * item.quantity}
                              </span>

                              {/* Substitution Action Button if missing on shelf */}
                              {!item.isPacked && !item.isRefunded && !item.isSubstituted && (
                                <button
                                  type="button"
                                  onClick={() =>
                                    setSubstitutionModal({
                                      orderId: currentOrder.id,
                                      itemId: item.id,
                                      itemName: item.name,
                                      unitPrice: item.priceNpr,
                                      quantity: item.quantity,
                                      preference: currentOrder.substitutionPreference || "allowStoreSubstitution",
                                    })
                                  }
                                  className="px-2.5 py-1 text-[11px] font-bold text-amber-700 bg-amber-50 hover:bg-amber-100 border border-amber-200 rounded-lg transition-all"
                                >
                                  Missing on Shelf?
                                </button>
                              )}
                            </div>
                          </div>
                        ))}
                      </div>

                      {/* Bottom Ready for Rider Button */}
                      <div className="p-4 bg-slate-50 border-t border-slate-100 flex items-center justify-between">
                        <span className="text-xs text-slate-500">
                          {packedCount === activeItemsCount
                            ? "All items verified. Ready for delivery dispatch."
                            : "Check off items before dispatch."}
                        </span>

                        <button
                          type="button"
                          disabled={
                            packedCount !== activeItemsCount ||
                            currentOrder.status === "READY_FOR_PICKUP"
                          }
                          onClick={() => handleMarkOrderReadyForRider(currentOrder.id)}
                          className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-700 disabled:bg-slate-300 disabled:cursor-not-allowed text-white text-xs font-bold rounded-xl transition-all shadow-xs flex items-center gap-1.5"
                        >
                          <Bike className="w-4 h-4" />
                          <span>
                            {currentOrder.status === "READY_FOR_PICKUP"
                              ? "Rider Dispatched"
                              : "Handover to Delivery Rider"}
                          </span>
                        </button>
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="text-center py-24 text-slate-400 text-xs italic">
                    Select a retail order to inspect and pack.
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Sub-Tab 2: Shelf Inventory & Quick 86's */}
          {supermarketTab === "SHELF_INVENTORY" && (
            <div className="flex-1 flex flex-col p-6 md:p-8 overflow-y-auto space-y-6">
              <div className="max-w-5xl w-full mx-auto space-y-6">
                {/* Search & Category Filter Bar */}
                <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex flex-col md:flex-row items-center justify-between gap-4">
                  <div className="relative flex-1 w-full">
                    <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                    <input
                      type="text"
                      placeholder="Search store shelf SKUs by name or barcode..."
                      value={shelfSearchQuery}
                      onChange={(e) => setShelfSearchQuery(e.target.value)}
                      className="w-full pl-9 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-1 focus:ring-emerald-500 font-medium"
                    />
                  </div>

                  {/* Aisle Category Filter */}
                  <div className="flex items-center gap-1.5 overflow-x-auto w-full md:w-auto">
                    {["ALL", "Produce", "Dairy", "Noodles", "Oils"].map((cat) => (
                      <button
                        key={cat}
                        type="button"
                        onClick={() => setShelfCategoryFilter(cat)}
                        className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all whitespace-nowrap ${
                          shelfCategoryFilter === cat
                            ? "bg-slate-900 text-white"
                            : "bg-slate-100 text-slate-600 hover:bg-slate-200"
                        }`}
                      >
                        {cat === "ALL" ? "All Aisles" : cat}
                      </button>
                    ))}
                  </div>
                </div>

                {/* Shelf Items Table */}
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
                  <div className="p-4 bg-slate-50/80 border-b border-slate-200/80 flex items-center justify-between">
                    <div>
                      <h3 className="text-xs font-black uppercase tracking-wider text-slate-800">
                        Live Store Shelf Inventory ({filteredShelfItems.length} SKUs)
                      </h3>
                      <p className="text-[11px] text-slate-400">
                        Toggle &ldquo;In Stock&rdquo; to instantly prevent consumer orders when walk-in customers deplete shelves
                      </p>
                    </div>
                  </div>

                  <div className="divide-y divide-slate-100">
                    {filteredShelfItems.map((item) => (
                      <div
                        key={item.id}
                        className="p-4 flex items-center justify-between gap-4 hover:bg-slate-50/80 transition-colors"
                      >
                        <div className="flex items-center gap-3.5">
                          <div className="w-12 h-12 rounded-xl bg-slate-100 border border-slate-200 overflow-hidden shrink-0 flex items-center justify-center">
                            {item.imageUrl ? (
                              <img
                                src={item.imageUrl}
                                alt={item.name}
                                className="w-full h-full object-cover"
                              />
                            ) : (
                              <ShoppingBag className="w-5 h-5 text-slate-400" />
                            )}
                          </div>
                          <div>
                            <div className="flex items-center gap-2">
                              <h4 className="text-xs font-bold text-slate-900">{item.name}</h4>
                              <span className="text-[10px] font-mono px-2 py-0.5 rounded-md bg-slate-100 text-slate-600 font-medium">
                                {item.packSize}
                              </span>
                            </div>
                            <div className="flex items-center gap-3 text-[11px] text-slate-400 font-mono mt-0.5">
                              <span>Aisle: {item.category}</span>
                              <span>Barcode: {item.barcode}</span>
                            </div>
                          </div>
                        </div>

                        <div className="flex items-center gap-6">
                          {/* Price */}
                          <div className="text-right">
                            <span className="text-xs font-black text-slate-900 font-mono block">
                              NPR {item.priceNpr}
                            </span>
                            {item.mrpNpr > item.priceNpr && (
                              <span className="text-[10px] text-slate-400 line-through font-mono">
                                MRP {item.mrpNpr}
                              </span>
                            )}
                          </div>

                          {/* Stock Counter Stepper */}
                          <div className="flex items-center gap-1.5 bg-slate-100 px-2 py-1 rounded-lg">
                            <button
                              type="button"
                              onClick={() => handleAdjustShelfStock(item.id, -5)}
                              className="w-5 h-5 rounded flex items-center justify-center text-slate-600 hover:bg-white transition-all text-xs font-bold"
                            >
                              -
                            </button>
                            <span className="font-mono text-xs font-bold text-slate-800 w-8 text-center">
                              {item.stockCount}
                            </span>
                            <button
                              type="button"
                              onClick={() => handleAdjustShelfStock(item.id, 5)}
                              className="w-5 h-5 rounded flex items-center justify-center text-slate-600 hover:bg-white transition-all text-xs font-bold"
                            >
                              +
                            </button>
                          </div>

                          {/* 1-Click 86 Toggle */}
                          <button
                            type="button"
                            onClick={() => handleToggleShelfInStock(item.id, !item.inStock)}
                            className={`px-3 py-1.5 rounded-xl text-xs font-black transition-all ${
                              item.inStock
                                ? "bg-emerald-100 text-emerald-800 hover:bg-emerald-200"
                                : "bg-red-100 text-red-800 hover:bg-red-200"
                            }`}
                          >
                            {item.inStock ? "IN STOCK" : "86'd (OUT)"}
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Sub-Tab 3: Settings & Daily Settlement */}
          {supermarketTab === "SETTINGS" && (
            <div className="flex-1 flex flex-col p-6 md:p-8 overflow-y-auto space-y-6">
              <div className="max-w-4xl w-full mx-auto space-y-6">
                {/* Settlement Cards */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs space-y-2">
                    <span className="text-xs text-slate-400 font-medium">Today&apos;s Gross Sales</span>
                    <h3 className="text-2xl font-black text-slate-900 font-mono">
                      NPR {financials?.todaySalesNpr || 18450}
                    </h3>
                    <span className="text-[11px] font-bold text-emerald-600">
                      {financials?.todayOrdersCount || 24} Retail Orders Processed
                    </span>
                  </div>

                  <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs space-y-2">
                    <span className="text-xs text-slate-400 font-medium">Platform Fee &bull; NRB Compliant</span>
                    <h3 className="text-2xl font-black text-slate-900 font-mono">
                      {financials?.platformTakeRatePercent || 10}%
                    </h3>
                    <span className="text-[11px] text-slate-500">
                      Zero stored-value wallet retention
                    </span>
                  </div>

                  <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs space-y-2">
                    <span className="text-xs text-slate-400 font-medium">Pending Bank Settlement</span>
                    <h3 className="text-2xl font-black text-emerald-600 font-mono">
                      NPR {financials?.pendingSettlementNpr || 76240}
                    </h3>
                    <span className="text-[11px] text-slate-500 font-mono">
                      Direct Credit: ConnectIPS / Fonepay
                    </span>
                  </div>
                </div>

                {/* Bank Account Verification & Store Details */}
                <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-xs space-y-4">
                  <h3 className="text-xs font-black uppercase tracking-wider text-slate-800 border-b border-slate-100 pb-3 flex items-center gap-2">
                    <ShieldCheck className="w-4 h-4 text-emerald-600" />
                    <span>Verified Merchant Settlement Ledger</span>
                  </h3>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
                    <div className="space-y-1">
                      <span className="text-slate-400">Beneficiary Bank</span>
                      <p className="font-bold text-slate-900">
                        {financials?.bankAccount.bankName || "01-Nabil Bank Ltd"}
                      </p>
                    </div>
                    <div className="space-y-1">
                      <span className="text-slate-400">Branch</span>
                      <p className="font-bold text-slate-900">
                        {financials?.bankAccount.branch || "Pulchowk Branch"}
                      </p>
                    </div>
                    <div className="space-y-1">
                      <span className="text-slate-400">Account Number</span>
                      <p className="font-mono font-bold text-slate-900">
                        {financials?.bankAccount.accountNumber || "0010101928371"}
                      </p>
                    </div>
                    <div className="space-y-1">
                      <span className="text-slate-400">Tax PAN Number</span>
                      <p className="font-mono font-bold text-slate-900">
                        {currentStore?.taxPan || "300182746"}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* ========================================================================= */}
      {/* MODAL: SUBSTITUTION RESOLUTION DRAWER                                     */}
      {/* ========================================================================= */}
      {substitutionModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl space-y-5 animate-in zoom-in-95">
            <div className="flex items-start justify-between border-b border-slate-100 pb-3">
              <div>
                <span className="text-[10px] font-bold uppercase tracking-wider text-amber-600 block">
                  Out of Stock Substitution Resolution
                </span>
                <h3 className="text-sm font-black text-slate-900 mt-0.5">
                  Resolve: {substitutionModal.itemName}
                </h3>
              </div>
              <button
                type="button"
                onClick={() => setSubstitutionModal(null)}
                className="text-slate-400 hover:text-slate-600 text-sm font-bold"
              >
                ✕
              </button>
            </div>

            {/* Customer Preference Explanation */}
            <div className="p-3 bg-amber-50 rounded-xl border border-amber-200/80 text-xs space-y-1">
              <span className="font-bold text-amber-900 block">Customer Instruction:</span>
              <p className="text-amber-800">
                {substitutionModal.preference === "callCustomer" &&
                  "Customer requested a phone call prior to making any replacement."}
                {substitutionModal.preference === "allowStoreSubstitution" &&
                  "Customer authorized the store to pick the best-match equivalent product."}
                {substitutionModal.preference === "refundItem" &&
                  "Customer prefers a direct refund instead of replacement."}
              </p>
            </div>

            {/* Action Option 1: Call Customer */}
            {substitutionModal.preference === "callCustomer" && (
              <a
                href={`tel:${currentOrder?.customerPhone}`}
                className="w-full py-2.5 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-xs font-bold flex items-center justify-center gap-2 transition-all shadow-xs"
              >
                <Phone className="w-4 h-4" />
                <span>Call Customer ({currentOrder?.customerPhone})</span>
              </a>
            )}

            {/* Action Option 2: Select Replacement SKU */}
            <div className="space-y-2">
              <label className="text-xs font-bold text-slate-700 block">
                Option A: Replace with Equivalent SKU
              </label>
              <input
                type="text"
                placeholder="e.g. Kamdhenu Cow Milk (500ml) or Fresh Eggs 6pcs"
                value={customReplacementName}
                onChange={(e) => setCustomReplacementName(e.target.value)}
                className="w-full px-3.5 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-1 focus:ring-emerald-500 font-medium"
              />
              <button
                type="button"
                onClick={() => handleExecuteSubstitution("replace")}
                className="w-full py-2 px-4 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition-all"
              >
                Confirm Replacement &amp; Pack
              </button>
            </div>

            {/* Action Option 3: Strike & Refund */}
            <div className="pt-2 border-t border-slate-100">
              <button
                type="button"
                onClick={() => handleExecuteSubstitution("refund")}
                className="w-full py-2 px-4 bg-red-50 hover:bg-red-100 text-red-700 border border-red-200 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5"
              >
                <XCircle className="w-4 h-4" />
                <span>
                  Strike Item &amp; Refund NPR {substitutionModal.unitPrice * substitutionModal.quantity}
                </span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
