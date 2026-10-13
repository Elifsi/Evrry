"use client";

import { useState } from "react";
import Link from "next/link";
import {
  ChevronLeft,
  Bike,
  ChefHat,
  ShoppingBag,
  Hotel,
  Home,
  MapPin,
  Navigation,
  Phone,
  CheckCircle2,
  XCircle,
  Clock,
  QrCode,
  ShieldCheck,
  Key,
  Barcode,
  Check,
  AlertCircle,
  User,
  Calendar,
  Sparkles,
  ToggleLeft,
  ToggleRight,
  TrendingUp,
  FileText,
} from "lucide-react";
import clsx from "clsx";

export type PartnerRole = "rider" | "kitchen" | "grocery" | "hotel" | "landlord";

export default function PartnerPrototypePage() {
  const [activeRole, setActiveRole] = useState<PartnerRole>("rider");

  // RIDER STATE
  const [riderOnline, setRiderOnline] = useState(true);
  const [riderTripState, setRiderTripState] = useState<"idle" | "incoming" | "navigating_pickup" | "arrived" | "navigating_dropoff" | "completed">("incoming");
  const [riderOtp, setRiderOtp] = useState("");
  const [riderOtpError, setRiderOtpError] = useState(false);
  const [riderEarningsToday, setRiderEarningsToday] = useState(3420);
  const [riderTripsToday, setRiderTripsToday] = useState(14);

  // KITCHEN KDS STATE
  const [kitchenOrders, setKitchenOrders] = useState([
    {
      id: "ORD-9421",
      customer: "Rahul S.",
      items: [
        { name: "Buff Steam Momo (10 pcs)", qty: 2, price: 280 },
        { name: "Chicken Choila Set", qty: 1, price: 340 },
        { name: "Masala Chiya", qty: 2, price: 80 },
      ],
      total: 1060,
      placedTime: "3 mins ago",
      prepTarget: 15,
      status: "incoming" as "incoming" | "preparing" | "ready",
    },
    {
      id: "ORD-9419",
      customer: "Aayush Sharma",
      items: [
        { name: "Thakali Khana Set (Chicken)", qty: 2, price: 450 },
        { name: "Gundruk Sadeko", qty: 1, price: 160 },
      ],
      total: 1060,
      placedTime: "11 mins ago",
      prepTarget: 20,
      status: "preparing" as "incoming" | "preparing" | "ready",
    },
  ]);
  const [stockItems, setStockItems] = useState([
    { id: "buff_momo", name: "Buff Steam Momo", inStock: true },
    { id: "choila", name: "Chicken Choila Set", inStock: true },
    { id: "jhol_momo", name: "Kathmandu Jhol Momo", inStock: false },
    { id: "thakali", name: "Thakali Khana Set", inStock: true },
    { id: "chiya", name: "Masala Chiya", inStock: true },
  ]);
  const [showRejectModal, setShowRejectModal] = useState<string | null>(null);

  // GROCERY MART STATE
  const [groceryItems, setGroceryItems] = useState([
    { id: "g1", name: "DDC Pure Cow Milk 1L", aisle: "Cold Dairy - A2", qty: 2, picked: true },
    { id: "g2", name: "Wai Wai Quick Chicken Noodles (Pack of 5)", aisle: "Instant Snacks - B1", qty: 1, picked: false },
    { id: "g3", name: "Mustard Oil 1L (Shree Pure)", aisle: "Oils & Spices - C4", qty: 1, picked: false },
    { id: "g4", name: "Red Gala Himalayan Apples 1kg", aisle: "Fresh Produce - D1", qty: 1, picked: false },
  ]);
  const [groceryBagSealed, setGroceryBagSealed] = useState(false);

  // HOTEL FRONT DESK STATE
  const [rooms, setRooms] = useState([
    { id: "101", type: "Deluxe Mountain View", guest: "Aarav Gautam", checkIn: "Today, 14:00", checkOut: "Oct 12", status: "arrived" as "arrived" | "checked_in" | "vacant" },
    { id: "204", type: "Heritage Kathmandu Suite", guest: "Elena Rostova", checkIn: "Today, 15:30", checkOut: "Oct 15", status: "arrived" as "arrived" | "checked_in" | "vacant" },
    { id: "305", type: "Standard Double Room", guest: "Clean & Ready", checkIn: "—", checkOut: "—", status: "vacant" as "arrived" | "checked_in" | "vacant" },
  ]);
  const [checkedInNotice, setCheckedInNotice] = useState<string | null>(null);

  // LANDLORD STATE
  const [propertyAvailable, setPropertyAvailable] = useState(true);
  const [inquiries, setInquiries] = useState([
    {
      id: "inq-1",
      name: "Pooja Gurung",
      phone: "+977 9801234567",
      date: "Saturday, 11:30 AM",
      familySize: "Small family (2 adults, 1 child)",
      status: "pending" as "pending" | "confirmed",
    },
    {
      id: "inq-2",
      name: "Subash Thapa",
      phone: "+977 9841889900",
      date: "Sunday, 2:00 PM",
      familySize: "Tech professional couple",
      status: "confirmed" as "pending" | "confirmed",
    },
  ]);

  // Handler functions
  const handleVerifyOtp = () => {
    if (riderOtp.trim() === "1234" || riderOtp.trim() === "5678") {
      setRiderOtpError(false);
      setRiderTripState("navigating_dropoff");
    } else {
      setRiderOtpError(true);
    }
  };

  const handleCompleteTrip = () => {
    setRiderEarningsToday((prev) => prev + 280);
    setRiderTripsToday((prev) => prev + 1);
    setRiderTripState("completed");
  };

  const toggleStock = (id: string) => {
    setStockItems((prev) =>
      prev.map((item) => (item.id === id ? { ...item, inStock: !item.inStock } : item))
    );
  };

  const updateOrderStatus = (orderId: string, nextStatus: "preparing" | "ready") => {
    setKitchenOrders((prev) =>
      prev.map((o) => (o.id === orderId ? { ...o, status: nextStatus } : o))
    );
  };

  const toggleGroceryItem = (id: string) => {
    setGroceryItems((prev) =>
      prev.map((item) => (item.id === id ? { ...item, picked: !item.picked } : item))
    );
  };

  const handleCheckInGuest = (roomId: string, guestName: string) => {
    setRooms((prev) =>
      prev.map((r) => (r.id === roomId ? { ...r, status: "checked_in" } : r))
    );
    setCheckedInNotice(`Key card issued & Room ${roomId} checked in for ${guestName}`);
    setTimeout(() => setCheckedInNotice(null), 3500);
  };

  const confirmInquiry = (id: string) => {
    setInquiries((prev) =>
      prev.map((inq) => (inq.id === id ? { ...inq, status: "confirmed" } : inq))
    );
  };

  return (
    <div className="min-h-screen bg-[#F8FAFC] pb-24 text-[#0B172A]">
      {/* Top Header */}
      <div className="sticky top-0 z-30 border-b border-slate-200/80 bg-white/95 px-4 pt-3 pb-2 backdrop-blur-md">
        <div className="flex items-center justify-between">
          <Link
            href="/"
            className="flex items-center gap-1.5 text-xs font-semibold text-slate-600 hover:text-slate-900 transition"
          >
            <ChevronLeft size={16} /> Consumer App
          </Link>
          <div className="flex items-center gap-2">
            <span className="flex h-2 w-2 rounded-full bg-emerald-500 animate-pulse" />
            <span className="text-[11px] font-bold tracking-wider uppercase text-[#005EFF]">
              EVRRY PARTNER PROTOTYPE
            </span>
          </div>
        </div>

        {/* 5-Role Switcher Tabs */}
        <div className="mt-2.5 flex items-center gap-1.5 overflow-x-auto pb-1 no-scrollbar">
          <button
            onClick={() => setActiveRole("rider")}
            className={clsx(
              "flex shrink-0 items-center gap-1.5 rounded-full px-3 py-1.5 text-xs font-semibold transition active:scale-95",
              activeRole === "rider"
                ? "bg-[#005EFF] text-white shadow-sm"
                : "bg-slate-100 text-slate-600 hover:bg-slate-200"
            )}
          >
            <Bike size={14} /> Rider / Cab
          </button>
          <button
            onClick={() => setActiveRole("kitchen")}
            className={clsx(
              "flex shrink-0 items-center gap-1.5 rounded-full px-3 py-1.5 text-xs font-semibold transition active:scale-95",
              activeRole === "kitchen"
                ? "bg-[#005EFF] text-white shadow-sm"
                : "bg-slate-100 text-slate-600 hover:bg-slate-200"
            )}
          >
            <ChefHat size={14} /> Kitchen KDS
          </button>
          <button
            onClick={() => setActiveRole("grocery")}
            className={clsx(
              "flex shrink-0 items-center gap-1.5 rounded-full px-3 py-1.5 text-xs font-semibold transition active:scale-95",
              activeRole === "grocery"
                ? "bg-[#005EFF] text-white shadow-sm"
                : "bg-slate-100 text-slate-600 hover:bg-slate-200"
            )}
          >
            <ShoppingBag size={14} /> Grocery Mart
          </button>
          <button
            onClick={() => setActiveRole("hotel")}
            className={clsx(
              "flex shrink-0 items-center gap-1.5 rounded-full px-3 py-1.5 text-xs font-semibold transition active:scale-95",
              activeRole === "hotel"
                ? "bg-[#005EFF] text-white shadow-sm"
                : "bg-slate-100 text-slate-600 hover:bg-slate-200"
            )}
          >
            <Hotel size={14} /> Hotel Desk
          </button>
          <button
            onClick={() => setActiveRole("landlord")}
            className={clsx(
              "flex shrink-0 items-center gap-1.5 rounded-full px-3 py-1.5 text-xs font-semibold transition active:scale-95",
              activeRole === "landlord"
                ? "bg-[#005EFF] text-white shadow-sm"
                : "bg-slate-100 text-slate-600 hover:bg-slate-200"
            )}
          >
            <Home size={14} /> Landlord Flat
          </button>
        </div>
      </div>

      <div className="px-4 pt-4">
        {/* ========================================================= */}
        {/* 1. RIDER / CAB PARTNER MODE */}
        {/* ========================================================= */}
        {activeRole === "rider" && (
          <div className="space-y-4">
            {/* Online Toggle & Earnings Banner */}
            <div className="rounded-2xl bg-white p-4 shadow-sm border border-slate-200">
              <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                <div className="flex items-center gap-2.5">
                  <div
                    className={clsx(
                      "flex h-9 w-9 items-center justify-center rounded-full text-white font-bold",
                      riderOnline ? "bg-emerald-500 shadow-sm shadow-emerald-200" : "bg-slate-400"
                    )}
                  >
                    <Bike size={18} />
                  </div>
                  <div>
                    <h3 className="text-sm font-bold text-slate-900">Rider Partner · BA 02 PA 4521</h3>
                    <p className="text-xs text-slate-500">
                      {riderOnline ? "Online · Receiving trip offers" : "Offline · Tap to go online"}
                    </p>
                  </div>
                </div>
                <button
                  onClick={() => setRiderOnline(!riderOnline)}
                  className={clsx(
                    "flex items-center gap-1 rounded-full px-3 py-1 text-xs font-semibold transition",
                    riderOnline ? "bg-emerald-50 text-emerald-700 border border-emerald-200" : "bg-slate-100 text-slate-600"
                  )}
                >
                  {riderOnline ? "ONLINE" : "OFFLINE"}
                </button>
              </div>

              {/* Ticker Metrics */}
              <div className="grid grid-cols-3 gap-2 pt-3 text-center">
                <div className="rounded-xl bg-slate-50 p-2.5">
                  <p className="text-[11px] font-medium text-slate-500">Today&apos;s Pay</p>
                  <p className="text-sm font-bold text-[#005EFF]">Rs {riderEarningsToday.toLocaleString("en-IN")}</p>
                </div>
                <div className="rounded-xl bg-slate-50 p-2.5">
                  <p className="text-[11px] font-medium text-slate-500">Trips</p>
                  <p className="text-sm font-bold text-slate-800">{riderTripsToday}</p>
                </div>
                <div className="rounded-xl bg-slate-50 p-2.5">
                  <p className="text-[11px] font-medium text-slate-500">Rating</p>
                  <p className="text-sm font-bold text-amber-600">4.92 ★</p>
                </div>
              </div>
            </div>

            {/* Simulated Live GPS Map View */}
            <div className="relative h-44 w-full overflow-hidden rounded-2xl bg-slate-900 shadow-inner border border-slate-200">
              <div className="absolute inset-0 bg-[radial-gradient(#334155_1px,transparent_1px)] [background-size:16px_16px] opacity-40" />
              {/* Route line simulation */}
              <div className="absolute top-12 left-10 right-14 h-1 bg-[#005EFF] rounded-full shadow-[0_0_12px_#005EFF]" />
              {/* Pickup Pin */}
              <div className="absolute top-9 left-8 flex items-center gap-1">
                <div className="flex h-7 w-7 items-center justify-center rounded-full bg-emerald-500 text-white shadow-md">
                  <MapPin size={14} />
                </div>
                <span className="bg-slate-800/90 text-white text-[10px] px-2 py-0.5 rounded-full border border-slate-700">
                  Thamel Marg
                </span>
              </div>
              {/* Dropoff Pin */}
              <div className="absolute top-9 right-8 flex items-center gap-1">
                <div className="flex h-7 w-7 items-center justify-center rounded-full bg-[#005EFF] text-white shadow-md">
                  <Navigation size={14} />
                </div>
                <span className="bg-slate-800/90 text-white text-[10px] px-2 py-0.5 rounded-full border border-slate-700">
                  Patan Durbar
                </span>
              </div>
              {/* Rider current location moving icon */}
              <div className="absolute bottom-4 left-1/2 -translate-x-1/2 flex items-center gap-1.5 rounded-full bg-white/95 px-3 py-1 shadow-md border border-slate-100">
                <Bike size={14} className="text-[#005EFF]" />
                <span className="text-[11px] font-semibold text-slate-800">Kathmandu Central GPS Active</span>
              </div>
            </div>

            {/* Trip Progression State Machine */}
            {riderTripState === "incoming" && (
              <div className="rounded-2xl bg-white p-4 shadow-md border-2 border-[#005EFF] animate-in fade-in slide-in-from-bottom-2">
                <div className="flex items-center justify-between pb-2 border-b border-slate-100">
                  <span className="rounded-full bg-blue-50 px-2.5 py-0.5 text-[11px] font-bold text-[#005EFF]">
                    NEW RIDE OFFER · 4.8 km
                  </span>
                  <span className="text-base font-extrabold text-emerald-600">Rs 280</span>
                </div>
                <div className="mt-3 space-y-2 text-xs">
                  <div className="flex items-start gap-2 text-slate-700">
                    <span className="h-2 w-2 rounded-full bg-emerald-500 mt-1 shrink-0" />
                    <div>
                      <span className="font-semibold text-slate-900">Pickup:</span> Thamel Marg (Near Kathmandu Guest House)
                    </div>
                  </div>
                  <div className="flex items-start gap-2 text-slate-700">
                    <span className="h-2 w-2 rounded-full bg-[#005EFF] mt-1 shrink-0" />
                    <div>
                      <span className="font-semibold text-slate-900">Dropoff:</span> Patan Durbar Square, Mangal Bazaar
                    </div>
                  </div>
                  <p className="text-[11px] text-slate-500 pt-1">Passenger: Rahul S. · Payment: Evrry Digital Wallet</p>
                </div>
                <div className="mt-4 flex gap-2">
                  <button
                    onClick={() => setRiderTripState("idle")}
                    className="flex-1 rounded-xl bg-slate-100 py-2.5 text-xs font-semibold text-slate-600 hover:bg-slate-200 transition"
                  >
                    Decline
                  </button>
                  <button
                    onClick={() => setRiderTripState("navigating_pickup")}
                    className="flex-2 rounded-xl bg-[#005EFF] py-2.5 text-xs font-semibold text-white shadow-sm hover:bg-blue-600 transition"
                  >
                    Accept Trip (15s)
                  </button>
                </div>
              </div>
            )}

            {riderTripState === "navigating_pickup" && (
              <div className="rounded-2xl bg-white p-4 shadow-sm border border-slate-200 space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-amber-600 uppercase">Heading to Pickup</span>
                  <span className="text-xs text-slate-500">ETA: 4 mins</span>
                </div>
                <p className="text-sm font-semibold text-slate-900">Thamel Marg, Gate 2</p>
                <div className="flex gap-2">
                  <a
                    href="tel:+9779800000000"
                    className="flex flex-1 items-center justify-center gap-1.5 rounded-xl border border-slate-200 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50"
                  >
                    <Phone size={14} /> Call Rahul
                  </a>
                  <button
                    onClick={() => setRiderTripState("arrived")}
                    className="flex flex-1 items-center justify-center gap-1.5 rounded-xl bg-[#005EFF] py-2 text-xs font-semibold text-white shadow-sm hover:bg-blue-600"
                  >
                    Arrived at Pickup
                  </button>
                </div>
              </div>
            )}

            {riderTripState === "arrived" && (
              <div className="rounded-2xl bg-white p-4 shadow-md border border-slate-200 space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-emerald-600">Arrived at Passenger Location</span>
                  <span className="text-xs text-slate-400">Waiting</span>
                </div>
                <p className="text-xs text-slate-600">
                  Ask passenger for their 4-digit security PIN before starting the trip (demo PIN is <span className="font-bold text-[#005EFF]">1234</span>).
                </p>
                <div className="flex gap-2">
                  <input
                    type="text"
                    maxLength={4}
                    value={riderOtp}
                    onChange={(e) => {
                      setRiderOtp(e.target.value);
                      setRiderOtpError(false);
                    }}
                    placeholder="Enter 4-digit OTP"
                    className="flex-1 rounded-xl border border-slate-200 px-3 py-2 text-sm text-center font-bold tracking-widest outline-none focus:border-[#005EFF]"
                  />
                  <button
                    onClick={handleVerifyOtp}
                    className="rounded-xl bg-emerald-600 px-4 py-2 text-xs font-semibold text-white shadow-sm hover:bg-emerald-700"
                  >
                    Start Trip
                  </button>
                </div>
                {riderOtpError && (
                  <p className="text-[11px] text-rose-500 font-medium">Incorrect OTP. Please enter 1234 to proceed.</p>
                )}
              </div>
            )}

            {riderTripState === "navigating_dropoff" && (
              <div className="rounded-2xl bg-white p-4 shadow-md border-2 border-emerald-500 space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-emerald-600 uppercase">Trip in Progress</span>
                  <span className="text-xs text-slate-500">Speed: 38 km/h</span>
                </div>
                <p className="text-sm font-semibold text-slate-900">Dropping off at Patan Durbar Square</p>
                <button
                  onClick={handleCompleteTrip}
                  className="w-full rounded-xl bg-emerald-600 py-3 text-xs font-bold text-white shadow-sm hover:bg-emerald-700 transition"
                >
                  Complete Trip & Collect Rs 280
                </button>
              </div>
            )}

            {riderTripState === "completed" && (
              <div className="rounded-2xl bg-emerald-50 p-4 border border-emerald-200 text-center space-y-2">
                <CheckCircle2 size={32} className="mx-auto text-emerald-600" />
                <h4 className="text-sm font-bold text-emerald-900">Trip Completed Successfully!</h4>
                <p className="text-xs text-emerald-700">Rs 280 credited to your Evrry Partner Balance.</p>
                <button
                  onClick={() => {
                    setRiderTripState("idle");
                    setRiderOtp("");
                  }}
                  className="mt-2 rounded-xl bg-emerald-600 px-4 py-2 text-xs font-semibold text-white"
                >
                  Ready for Next Offer
                </button>
              </div>
            )}

            {riderTripState === "idle" && (
              <div className="rounded-2xl bg-white p-6 text-center border border-slate-200 space-y-2">
                <p className="text-sm font-bold text-slate-800">You are in High-Demand Zone (Kathmandu)</p>
                <p className="text-xs text-slate-500">Waiting for incoming rides...</p>
                <button
                  onClick={() => setRiderTripState("incoming")}
                  className="rounded-xl bg-[#005EFF] px-4 py-2 text-xs font-semibold text-white shadow-sm"
                >
                  Simulate New Incoming Ride
                </button>
              </div>
            )}
          </div>
        )}

        {/* ========================================================= */}
        {/* 2. KITCHEN KDS (FOOD DELIVERY PARTNER) */}
        {/* ========================================================= */}
        {activeRole === "kitchen" && (
          <div className="space-y-4">
            {/* Restaurant Profile */}
            <div className="rounded-2xl bg-white p-4 shadow-sm border border-slate-200 flex items-center justify-between">
              <div>
                <h3 className="text-sm font-bold text-slate-900">Bhojan Griha · Durbar Marg Kitchen</h3>
                <p className="text-xs text-slate-500">Live KDS Queue · 2 active orders</p>
              </div>
              <span className="rounded-full bg-emerald-50 px-2.5 py-1 text-[11px] font-bold text-emerald-700 border border-emerald-100">
                Kitchen Open
              </span>
            </div>

            {/* Live Kitchen Order Cards */}
            <div className="space-y-3">
              <h4 className="text-xs font-bold tracking-wider text-slate-500 uppercase">
                Active Order Tickets ({kitchenOrders.length})
              </h4>
              {kitchenOrders.map((ord) => (
                <div key={ord.id} className="rounded-2xl bg-white p-4 shadow-sm border border-slate-200 space-y-3">
                  <div className="flex items-center justify-between pb-2 border-b border-slate-100">
                    <div className="flex items-center gap-2">
                      <span className="text-sm font-black text-slate-900">{ord.id}</span>
                      <span className="text-xs text-slate-500 font-medium">{ord.customer}</span>
                    </div>
                    <span
                      className={clsx(
                        "rounded-full px-2 py-0.5 text-[10px] font-bold uppercase",
                        ord.status === "incoming" && "bg-amber-100 text-amber-800",
                        ord.status === "preparing" && "bg-blue-100 text-[#005EFF]",
                        ord.status === "ready" && "bg-emerald-100 text-emerald-800"
                      )}
                    >
                      {ord.status}
                    </span>
                  </div>

                  {/* Items Ticket */}
                  <div className="space-y-1.5 text-xs">
                    {ord.items.map((it, idx) => (
                      <div key={idx} className="flex justify-between text-slate-700">
                        <span>
                          <strong className="text-slate-900">{it.qty}x</strong> {it.name}
                        </span>
                        <span className="text-slate-500">Rs {it.price * it.qty}</span>
                      </div>
                    ))}
                  </div>

                  <div className="flex items-center justify-between pt-2 border-t border-slate-100 text-xs">
                    <span className="text-slate-500">Total Bill: <strong>Rs {ord.total}</strong></span>
                    <span className="text-slate-400">{ord.placedTime}</span>
                  </div>

                  {/* Kitchen Actions */}
                  <div className="flex gap-2 pt-1">
                    {ord.status === "incoming" && (
                      <>
                        <button
                          onClick={() => setShowRejectModal(ord.id)}
                          className="flex-1 rounded-xl bg-slate-100 py-2 text-xs font-semibold text-rose-600 hover:bg-rose-50 transition"
                        >
                          Reject
                        </button>
                        <button
                          onClick={() => updateOrderStatus(ord.id, "preparing")}
                          className="flex-2 rounded-xl bg-[#005EFF] py-2 text-xs font-semibold text-white shadow-sm hover:bg-blue-600 transition"
                        >
                          Accept & Prep (15m)
                        </button>
                      </>
                    )}
                    {ord.status === "preparing" && (
                      <button
                        onClick={() => updateOrderStatus(ord.id, "ready")}
                        className="w-full rounded-xl bg-emerald-600 py-2.5 text-xs font-bold text-white shadow-sm hover:bg-emerald-700 transition"
                      >
                        Food Ready for Delivery Rider
                      </button>
                    )}
                    {ord.status === "ready" && (
                      <div className="w-full rounded-xl bg-emerald-50 py-2 text-center text-xs font-semibold text-emerald-800 border border-emerald-200">
                        Waiting for Rider Handover
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>

            {/* Quick 86'd Stock Management Toggle */}
            <div className="rounded-2xl bg-white p-4 shadow-sm border border-slate-200 space-y-3">
              <h4 className="text-xs font-bold tracking-wider text-slate-500 uppercase">
                Quick 86&apos;d Item Stock Management
              </h4>
              <p className="text-[11px] text-slate-500">
                Instantly disable sold-out dishes so customers cannot order them.
              </p>
              <div className="divide-y divide-slate-100 text-xs">
                {stockItems.map((item) => (
                  <div key={item.id} className="flex items-center justify-between py-2">
                    <span className={clsx("font-medium", !item.inStock && "line-through text-slate-400")}>
                      {item.name}
                    </span>
                    <button
                      onClick={() => toggleStock(item.id)}
                      className={clsx(
                        "rounded-full px-2.5 py-1 text-[10px] font-bold transition",
                        item.inStock
                          ? "bg-emerald-50 text-emerald-700 border border-emerald-200"
                          : "bg-rose-50 text-rose-700 border border-rose-200"
                      )}
                    >
                      {item.inStock ? "IN STOCK" : "86'd (OUT)"}
                    </button>
                  </div>
                ))}
              </div>
            </div>

            {/* Reject Modal */}
            {showRejectModal && (
              <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
                <div className="w-full max-w-sm rounded-2xl bg-white p-5 shadow-xl space-y-3">
                  <h4 className="text-sm font-bold text-slate-900">Select Cancellation Reason</h4>
                  <p className="text-xs text-slate-500">Structured rejection for order {showRejectModal}:</p>
                  <div className="space-y-1.5 text-xs">
                    {["Ingredients Out of Stock", "Kitchen Overloaded (Peak Rush)", "Store Closing Soon", "Item Discontinued"].map((reason) => (
                      <button
                        key={reason}
                        onClick={() => {
                          setKitchenOrders((prev) => prev.filter((o) => o.id !== showRejectModal));
                          setShowRejectModal(null);
                        }}
                        className="w-full text-left rounded-xl p-2.5 hover:bg-slate-50 border border-slate-100 text-slate-700"
                      >
                        {reason}
                      </button>
                    ))}
                  </div>
                  <button
                    onClick={() => setShowRejectModal(null)}
                    className="w-full rounded-xl bg-slate-100 py-2 text-xs font-semibold text-slate-600 mt-2"
                  >
                    Dismiss
                  </button>
                </div>
              </div>
            )}
          </div>
        )}

        {/* ========================================================= */}
        {/* 3. GROCERY MART PARTNER (DARK STORE CLERK) */}
        {/* ========================================================= */}
        {activeRole === "grocery" && (
          <div className="space-y-4">
            <div className="rounded-2xl bg-white p-4 shadow-sm border border-slate-200">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-sm font-bold text-slate-900">Evrry Dark Store · New Baneshwor #102</h3>
                  <p className="text-xs text-slate-500">10-Minute Instant Delivery Dispatch Hub</p>
                </div>
                <span className="rounded-full bg-blue-50 px-2.5 py-1 text-[11px] font-bold text-[#005EFF] border border-blue-100">
                  Target: 4m Pack
                </span>
              </div>
            </div>

            {/* Pick & Pack Checklist */}
            <div className="rounded-2xl bg-white p-4 shadow-sm border border-slate-200 space-y-3">
              <div className="flex items-center justify-between pb-2 border-b border-slate-100">
                <span className="text-xs font-bold text-slate-900">Order #MART-5192 Checklist</span>
                <span className="text-xs text-slate-500">
                  {groceryItems.filter((g) => g.picked).length} / {groceryItems.length} items picked
                </span>
              </div>

              <div className="space-y-2 text-xs">
                {groceryItems.map((item) => (
                  <div
                    key={item.id}
                    onClick={() => toggleGroceryItem(item.id)}
                    className={clsx(
                      "flex items-center justify-between p-3 rounded-xl border cursor-pointer transition",
                      item.picked ? "bg-emerald-50/70 border-emerald-200" : "bg-slate-50 border-slate-200"
                    )}
                  >
                    <div className="flex items-center gap-2.5">
                      <div
                        className={clsx(
                          "flex h-5 w-5 items-center justify-center rounded-md border",
                          item.picked ? "bg-emerald-600 border-emerald-600 text-white" : "border-slate-300 bg-white"
                        )}
                      >
                        {item.picked && <Check size={12} strokeWidth={3} />}
                      </div>
                      <div>
                        <p className={clsx("font-semibold text-slate-900", item.picked && "line-through text-slate-400")}>
                          {item.name}
                        </p>
                        <p className="text-[10px] text-slate-400">Location: {item.aisle} · Qty: {item.qty}</p>
                      </div>
                    </div>
                    <span className="text-[10px] font-semibold text-slate-500">
                      {item.picked ? "Packed" : "Pick now"}
                    </span>
                  </div>
                ))}
              </div>

              {/* Barcode Simulation */}
              <div className="pt-2">
                <button
                  onClick={() => {
                    setGroceryItems((prev) => prev.map((item) => ({ ...item, picked: true })));
                  }}
                  className="flex w-full items-center justify-center gap-2 rounded-xl border border-dashed border-slate-300 py-2.5 text-xs font-semibold text-slate-600 hover:bg-slate-50"
                >
                  <Barcode size={16} /> Scan Next Barcode (Auto-verify)
                </button>
              </div>

              <button
                disabled={!groceryItems.every((g) => g.picked)}
                onClick={() => setGroceryBagSealed(true)}
                className="w-full rounded-xl bg-[#005EFF] py-3 text-xs font-bold text-white shadow-sm hover:bg-blue-600 disabled:opacity-40 transition"
              >
                Seal Bag & Hand to Express Rider
              </button>

              {groceryBagSealed && (
                <div className="rounded-xl bg-emerald-50 p-3 text-center border border-emerald-200 text-xs text-emerald-800 font-semibold">
                  Bag #MART-5192 sealed with barcode tag. Handover dispatched!
                </div>
              )}
            </div>
          </div>
        )}

        {/* ========================================================= */}
        {/* 4. HOTEL FRONT DESK PARTNER */}
        {/* ========================================================= */}
        {activeRole === "hotel" && (
          <div className="space-y-4">
            <div className="rounded-2xl bg-white p-4 shadow-sm border border-slate-200">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-sm font-bold text-slate-900">Kathmandu Guest House · Thamel</h3>
                  <p className="text-xs text-slate-500">Front Desk Manager · Daily Folio</p>
                </div>
                <span className="rounded-full bg-blue-50 px-2.5 py-1 text-[11px] font-bold text-[#005EFF] border border-blue-100">
                  88% Occupancy
                </span>
              </div>
            </div>

            {checkedInNotice && (
              <div className="rounded-xl bg-emerald-50 p-3 text-center border border-emerald-200 text-xs text-emerald-800 font-semibold animate-in fade-in">
                {checkedInNotice}
              </div>
            )}

            {/* Room Availability & Today's Check-ins */}
            <div className="space-y-3">
              <h4 className="text-xs font-bold tracking-wider text-slate-500 uppercase">
                Today&apos;s Room Manifest
              </h4>
              {rooms.map((room) => (
                <div key={room.id} className="rounded-2xl bg-white p-4 shadow-sm border border-slate-200 space-y-2.5">
                  <div className="flex items-center justify-between">
                    <div>
                      <span className="text-sm font-black text-slate-900">Room {room.id}</span>
                      <p className="text-xs text-slate-500 font-medium">{room.type}</p>
                    </div>
                    <span
                      className={clsx(
                        "rounded-full px-2.5 py-0.5 text-[10px] font-bold uppercase",
                        room.status === "arrived" && "bg-amber-100 text-amber-800",
                        room.status === "checked_in" && "bg-emerald-100 text-emerald-800",
                        room.status === "vacant" && "bg-slate-100 text-slate-600"
                      )}
                    >
                      {room.status.replace("_", " ")}
                    </span>
                  </div>

                  <div className="flex items-center justify-between text-xs text-slate-600 bg-slate-50 p-2.5 rounded-xl">
                    <span>Guest: <strong>{room.guest}</strong></span>
                    <span>Check-in: {room.checkIn}</span>
                  </div>

                  {room.status === "arrived" && (
                    <button
                      onClick={() => handleCheckInGuest(room.id, room.guest)}
                      className="flex w-full items-center justify-center gap-1.5 rounded-xl bg-[#005EFF] py-2 text-xs font-semibold text-white shadow-sm hover:bg-blue-600 transition"
                    >
                      <Key size={14} /> Verify ID & Issue Room Key
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* ========================================================= */}
        {/* 5. LANDLORD / ROOM RENTAL PARTNER */}
        {/* ========================================================= */}
        {activeRole === "landlord" && (
          <div className="space-y-4">
            <div className="rounded-2xl bg-white p-4 shadow-sm border border-slate-200 space-y-3">
              <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                <div>
                  <h3 className="text-sm font-bold text-slate-900">Jhamsikhel 2BHK Furnished Apartment</h3>
                  <p className="text-xs text-slate-500">Lalitpur Ward 3 · Lalpurja Verified Property</p>
                </div>
                <button
                  onClick={() => setPropertyAvailable(!propertyAvailable)}
                  className={clsx(
                    "rounded-full px-3 py-1 text-xs font-semibold transition",
                    propertyAvailable ? "bg-emerald-50 text-emerald-700 border border-emerald-200" : "bg-slate-100 text-slate-600"
                  )}
                >
                  {propertyAvailable ? "AVAILABLE" : "LEASED"}
                </button>
              </div>

              <div className="grid grid-cols-2 gap-2 text-xs">
                <div className="rounded-xl bg-slate-50 p-2.5">
                  <span className="text-[11px] text-slate-400">Monthly Rent</span>
                  <p className="text-sm font-bold text-slate-900">Rs 42,000 / mo</p>
                </div>
                <div className="rounded-xl bg-slate-50 p-2.5">
                  <span className="text-[11px] text-slate-400">Verification</span>
                  <p className="text-xs font-bold text-emerald-600 flex items-center gap-1">
                    <ShieldCheck size={14} /> Ward 3 Certified
                  </p>
                </div>
              </div>
            </div>

            {/* Scheduled Visit Requests */}
            <div className="space-y-3">
              <h4 className="text-xs font-bold tracking-wider text-slate-500 uppercase">
                Scheduled Tenant Visits ({inquiries.length})
              </h4>
              {inquiries.map((inq) => (
                <div key={inq.id} className="rounded-2xl bg-white p-4 shadow-sm border border-slate-200 space-y-2.5">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm font-bold text-slate-900">{inq.name}</p>
                      <p className="text-xs text-slate-500">{inq.familySize}</p>
                    </div>
                    <span
                      className={clsx(
                        "rounded-full px-2.5 py-0.5 text-[10px] font-bold uppercase",
                        inq.status === "confirmed" ? "bg-emerald-100 text-emerald-800" : "bg-amber-100 text-amber-800"
                      )}
                    >
                      {inq.status}
                    </span>
                  </div>

                  <div className="flex items-center justify-between text-xs text-slate-600 bg-slate-50 p-2 rounded-xl">
                    <span className="flex items-center gap-1">
                      <Calendar size={13} className="text-[#005EFF]" /> {inq.date}
                    </span>
                    <span className="text-slate-500">{inq.phone}</span>
                  </div>

                  <div className="flex gap-2 pt-1">
                    <a
                      href={`tel:${inq.phone}`}
                      className="flex-1 flex items-center justify-center gap-1 rounded-xl border border-slate-200 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50"
                    >
                      <Phone size={13} /> Call
                    </a>
                    {inq.status === "pending" ? (
                      <button
                        onClick={() => confirmInquiry(inq.id)}
                        className="flex-2 rounded-xl bg-[#005EFF] py-2 text-xs font-semibold text-white shadow-sm hover:bg-blue-600 transition"
                      >
                        Confirm Scheduled Visit
                      </button>
                    ) : (
                      <button
                        className="flex-2 rounded-xl bg-slate-100 py-2 text-xs font-semibold text-slate-600"
                        disabled
                      >
                        Visit Confirmed
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
