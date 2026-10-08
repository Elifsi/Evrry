"use client";

import { useState, useEffect } from "react";
import {
  Hotel,
  Calendar,
  Users,
  CheckCircle2,
  LogOut,
  LogIn,
  Search,
  Filter,
  CreditCard,
  Bed,
} from "lucide-react";
import { PartnerHeader } from "@/components/PartnerHeader";
import {
  fetchHotelBookings,
  updateBookingStatus,
} from "@/lib/partner/api";
import { HotelRoomBooking } from "@/lib/partner/types";

export default function HotelDeskPage() {
  const [bookings, setBookings] = useState<HotelRoomBooking[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [filterStatus, setFilterStatus] = useState<string>("all");
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [actionMessage, setActionMessage] = useState<string | null>(null);

  const loadBookings = async () => {
    setIsRefreshing(true);
    const data = await fetchHotelBookings();
    setBookings(data);
    setIsRefreshing(false);
  };

  useEffect(() => {
    loadBookings();
  }, []);

  const handleStatusChange = async (
    bookingId: string,
    status: "CHECKED_IN" | "CHECKED_OUT" | "CANCELLED"
  ) => {
    const res = await updateBookingStatus(bookingId, status);
    if (res.success) {
      setBookings((prev) =>
        prev.map((b) => (b.id === bookingId ? { ...b, status } : b))
      );
      setActionMessage(`Reservation ${bookingId} updated to ${status.replace(/_/g, " ")}`);
      setTimeout(() => setActionMessage(null), 4000);
    }
  };

  const arrivalsCount = bookings.filter((b) => b.status === "CONFIRMED").length;
  const inHouseCount = bookings.filter((b) => b.status === "CHECKED_IN").length;
  const departuresCount = bookings.filter((b) => b.status === "CHECKED_OUT").length;

  const filteredBookings = bookings.filter((b) => {
    if (filterStatus !== "all" && b.status !== filterStatus) return false;
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      return (
        b.guestName.toLowerCase().includes(q) ||
        b.reservationCode.toLowerCase().includes(q) ||
        b.roomNumber.toLowerCase().includes(q) ||
        b.roomType.toLowerCase().includes(q)
      );
    }
    return true;
  });

  return (
    <div className="flex-1 flex flex-col h-screen overflow-y-auto bg-slate-50">
      <PartnerHeader
        title="Hotel Front Desk & Reservation Calendar"
        subtitle="Guest Check-in Desk • Room Key Allocation • Multi-Night Stay Verifications"
        onRefresh={loadBookings}
        isRefreshing={isRefreshing}
      />

      <div className="p-6 md:p-8 space-y-6 max-w-[1500px] w-full mx-auto">
        {/* Action Alert Banner */}
        {actionMessage && (
          <div className="bg-emerald-600 text-white px-5 py-3 rounded-xl text-xs font-bold flex items-center justify-between shadow-xs animate-in fade-in">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{actionMessage}</span>
            </div>
            <span className="font-mono text-[11px] opacity-90">Desk Record Updated</span>
          </div>
        )}

        {/* 3 Metric Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Arrivals Today
              </span>
              <div className="p-2 bg-indigo-50 rounded-xl text-indigo-600">
                <LogIn className="w-4 h-4" />
              </div>
            </div>
            <p className="text-2xl font-black text-slate-900 mt-2 font-mono">
              {arrivalsCount} Guests
            </p>
            <p className="text-[11px] text-indigo-700 font-medium mt-1">
              Awaiting physical check-in & key issue
            </p>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Currently In-House
              </span>
              <div className="p-2 bg-emerald-50 rounded-xl text-emerald-600">
                <Hotel className="w-4 h-4" />
              </div>
            </div>
            <p className="text-2xl font-black text-slate-900 mt-2 font-mono">
              {inHouseCount} Rooms
            </p>
            <p className="text-[11px] text-emerald-700 font-medium mt-1">
              Checked-in & room occupied
            </p>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Departures
              </span>
              <div className="p-2 bg-slate-100 rounded-xl text-slate-600">
                <LogOut className="w-4 h-4" />
              </div>
            </div>
            <p className="text-2xl font-black text-slate-900 mt-2 font-mono">
              {departuresCount} Checked Out
            </p>
            <p className="text-[11px] text-slate-500 font-medium mt-1">
              Rooms marked for housekeeping
            </p>
          </div>
        </div>

        {/* Filter & Search Bar */}
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex-1 max-w-md relative">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search by guest name, reservation code, or room..."
              className="w-full pl-9 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all"
            />
          </div>

          <div className="flex items-center gap-1.5 overflow-x-auto">
            {["all", "CONFIRMED", "CHECKED_IN", "CHECKED_OUT"].map((st) => (
              <button
                key={st}
                type="button"
                onClick={() => setFilterStatus(st)}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
                  filterStatus === st
                    ? "bg-brand-600 text-white shadow-xs"
                    : "bg-slate-50 text-slate-600 border border-slate-200 hover:bg-slate-100"
                }`}
              >
                {st === "all" ? "All Bookings" : st.replace(/_/g, " ")}
              </button>
            ))}
          </div>
        </div>

        {/* Reservations Table */}
        <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="bg-slate-50/75 border-b border-slate-200/80 text-[11px] font-bold uppercase tracking-wider text-slate-500">
                  <th className="py-3 px-4">Reservation</th>
                  <th className="py-3 px-4">Guest Details</th>
                  <th className="py-3 px-4">Room & Tier</th>
                  <th className="py-3 px-4">Dates & Nights</th>
                  <th className="py-3 px-4">Total Amount</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Desk Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredBookings.map((b) => (
                  <tr key={b.id} className="hover:bg-slate-50/50 transition-colors">
                    <td className="py-3.5 px-4 font-mono font-bold text-slate-900">
                      {b.reservationCode}
                    </td>
                    <td className="py-3.5 px-4">
                      <p className="font-bold text-slate-900">{b.guestName}</p>
                      <p className="text-[11px] text-slate-400 font-mono">{b.guestPhone}</p>
                    </td>
                    <td className="py-3.5 px-4">
                      <div className="flex items-center gap-1.5 text-slate-800 font-semibold">
                        <Bed className="w-3.5 h-3.5 text-indigo-600 shrink-0" />
                        <span>{b.roomType}</span>
                      </div>
                      <span className="text-[11px] text-slate-500 font-mono block mt-0.5">
                        {b.roomNumber}
                      </span>
                    </td>
                    <td className="py-3.5 px-4">
                      <p className="text-slate-800 font-medium">
                        {b.checkInDate} → {b.checkOutDate}
                      </p>
                      <span className="text-[11px] text-slate-400 font-mono">
                        {b.nightsCount} Nights • {b.guestsCount} Guests
                      </span>
                    </td>
                    <td className="py-3.5 px-4">
                      <span className="font-mono font-black text-slate-900 block">
                        NPR {b.totalAmountNpr.toLocaleString()}
                      </span>
                      <span
                        className={`text-[10px] font-bold uppercase ${
                          b.paymentStatus === "PAID"
                            ? "text-emerald-700"
                            : "text-amber-700"
                        }`}
                      >
                        {b.paymentStatus.replace(/_/g, " ")}
                      </span>
                    </td>
                    <td className="py-3.5 px-4">
                      <span
                        className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wide border ${
                          b.status === "CHECKED_IN"
                            ? "bg-emerald-50 text-emerald-800 border-emerald-200"
                            : b.status === "CONFIRMED"
                            ? "bg-blue-50 text-blue-800 border-blue-200"
                            : "bg-slate-50 text-slate-600 border-slate-200"
                        }`}
                      >
                        {b.status.replace(/_/g, " ")}
                      </span>
                    </td>
                    <td className="py-3.5 px-4 text-right">
                      {b.status === "CONFIRMED" ? (
                        <button
                          type="button"
                          onClick={() => handleStatusChange(b.id, "CHECKED_IN")}
                          className="px-3 py-1.5 bg-brand-600 hover:bg-brand-700 text-white rounded-lg text-xs font-bold transition-all shadow-2xs"
                        >
                          Check In Guest
                        </button>
                      ) : b.status === "CHECKED_IN" ? (
                        <button
                          type="button"
                          onClick={() => handleStatusChange(b.id, "CHECKED_OUT")}
                          className="px-3 py-1.5 border border-slate-300 hover:bg-slate-100 text-slate-700 rounded-lg text-xs font-semibold transition-all"
                        >
                          Check Out
                        </button>
                      ) : (
                        <span className="text-slate-400 text-xs italic">Completed</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
