"use client";

import { useState, useEffect } from "react";
import {
  Home,
  Plus,
  Users,
  CheckCircle2,
  Calendar,
  MessageSquare,
  Droplets,
  Car,
  Phone,
  Sparkles,
} from "lucide-react";
import { PartnerHeader } from "@/components/PartnerHeader";
import {
  fetchLandlordListings,
  fetchTenantEnquiries,
  toggleListingAvailability,
  updateEnquiryStatus,
} from "@/lib/partner/api";
import { LandlordRentalListing, TenantEnquiry } from "@/lib/partner/types";

export default function LandlordRentalsPage() {
  const [listings, setListings] = useState<LandlordRentalListing[]>([]);
  const [enquiries, setEnquiries] = useState<TenantEnquiry[]>([]);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [actionMessage, setActionMessage] = useState<string | null>(null);

  const loadData = async () => {
    setIsRefreshing(true);
    const [fetchedListings, fetchedEnquiries] = await Promise.all([
      fetchLandlordListings(),
      fetchTenantEnquiries(),
    ]);
    setListings(fetchedListings);
    setEnquiries(fetchedEnquiries);
    setIsRefreshing(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleToggleVacancy = async (id: string, current: boolean) => {
    const res = await toggleListingAvailability(id, !current);
    if (res.success) {
      setListings((prev) =>
        prev.map((l) => (l.id === id ? { ...l, isAvailable: res.isAvailable } : l))
      );
      setActionMessage(
        res.isAvailable
          ? "Property marked AVAILABLE for rent on Evrry App"
          : "Property marked LEASED (Hidden from marketplace)"
      );
      setTimeout(() => setActionMessage(null), 4000);
    }
  };

  const handleEnquiryStatus = async (
    enquiryId: string,
    status: "SCHEDULED_VISIT" | "CONTACTED" | "CLOSED"
  ) => {
    const res = await updateEnquiryStatus(enquiryId, status);
    if (res.success) {
      setEnquiries((prev) =>
        prev.map((e) => (e.id === enquiryId ? { ...e, status } : e))
      );
      setActionMessage(`Tenant inquiry marked as ${status.replace(/_/g, " ")}`);
      setTimeout(() => setActionMessage(null), 4000);
    }
  };

  return (
    <div className="flex-1 flex flex-col h-screen overflow-y-auto bg-slate-50">
      <PartnerHeader
        title="Landlord Stays & Room Rental Management Hub"
        subtitle="Manage Flat Listings • Monthly Rent & Deposit Governance • Tenant Enquiries"
        onRefresh={loadData}
        isRefreshing={isRefreshing}
      />

      <div className="p-6 md:p-8 space-y-8 max-w-[1500px] w-full mx-auto">
        {/* Action Alert Banner */}
        {actionMessage && (
          <div className="bg-brand-600 text-white px-5 py-3 rounded-xl text-xs font-bold flex items-center justify-between shadow-xs animate-in fade-in">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{actionMessage}</span>
            </div>
            <span className="font-mono text-[11px] opacity-90">Live Sync</span>
          </div>
        )}

        {/* Section 1: Property Listings Grid */}
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-sm font-bold text-slate-900 uppercase tracking-wider">
                My Rental Properties ({listings.length})
              </h2>
              <p className="text-xs text-slate-500">
                Flats and rooms visible to house-hunting consumers in Kathmandu Valley.
              </p>
            </div>
            <button
              type="button"
              className="px-4 py-2 bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold rounded-xl transition-all flex items-center gap-1.5 shadow-xs"
            >
              <Plus className="w-4 h-4" />
              <span>List New Property</span>
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
            {listings.map((item) => (
              <div
                key={item.id}
                className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden flex flex-col justify-between hover:border-slate-300 transition-all"
              >
                <div>
                  <div className="relative h-44 w-full bg-slate-100 overflow-hidden">
                    <img
                      src={item.images[0]}
                      alt={item.title}
                      className="w-full h-full object-cover"
                    />
                    <div className="absolute top-3 left-3 flex gap-1.5">
                      <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-white text-slate-900 shadow-sm">
                        {item.propertyType}
                      </span>
                    </div>

                    <div className="absolute top-3 right-3">
                      <span
                        className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold shadow-sm ${
                          item.isAvailable
                            ? "bg-emerald-500 text-white"
                            : "bg-slate-800 text-slate-200"
                        }`}
                      >
                        {item.isAvailable ? "Vacant / Available" : "Leased Out"}
                      </span>
                    </div>
                  </div>

                  <div className="p-5 space-y-3">
                    <div>
                      <h3 className="text-sm font-bold text-slate-900 leading-snug">
                        {item.title}
                      </h3>
                      <p className="text-xs text-slate-500 mt-0.5">{item.location}</p>
                    </div>

                    <div className="grid grid-cols-2 gap-2 pt-2 border-t border-slate-100 text-xs">
                      <div>
                        <span className="text-slate-400 text-[11px] block">Monthly Rent</span>
                        <span className="font-mono font-bold text-slate-900">
                          NPR {item.monthlyRentNpr.toLocaleString()}
                        </span>
                      </div>
                      <div>
                        <span className="text-slate-400 text-[11px] block">Security Deposit</span>
                        <span className="font-mono font-semibold text-slate-700">
                          NPR {item.securityDepositNpr.toLocaleString()}
                        </span>
                      </div>
                    </div>

                    <div className="flex items-center gap-3 text-[11px] text-slate-600 pt-1">
                      <span className="flex items-center gap-1">
                        <Droplets className="w-3.5 h-3.5 text-blue-500" />
                        <span>Melamchi / Boring</span>
                      </span>
                      {item.parkingAvailable && (
                        <span className="flex items-center gap-1">
                          <Car className="w-3.5 h-3.5 text-emerald-500" />
                          <span>Covered Parking</span>
                        </span>
                      )}
                    </div>
                  </div>
                </div>

                <div className="p-4 bg-slate-50 border-t border-slate-100 flex items-center justify-between text-xs font-semibold">
                  <span className="text-slate-500">
                    {item.enquiriesCount} Inquiries Received
                  </span>

                  <button
                    type="button"
                    onClick={() => handleToggleVacancy(item.id, item.isAvailable)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all border ${
                      item.isAvailable
                        ? "bg-white border-slate-300 text-slate-700 hover:bg-slate-100"
                        : "bg-emerald-600 border-emerald-600 text-white hover:bg-emerald-700"
                    }`}
                  >
                    {item.isAvailable ? "Mark Leased" : "Mark Available"}
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Section 2: Prospective Tenant Enquiries */}
        <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs p-6 space-y-5">
          <div className="border-b border-slate-100 pb-3">
            <h2 className="text-sm font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2">
              <MessageSquare className="w-4 h-4 text-brand-600" />
              <span>Prospective Tenant Inquiries ({enquiries.length})</span>
            </h2>
            <p className="text-xs text-slate-500">
              Verified renters interested in visiting or booking your flats.
            </p>
          </div>

          <div className="space-y-3">
            {enquiries.map((enq) => (
              <div
                key={enq.id}
                className="p-4 rounded-xl border border-slate-200 bg-slate-50/50 space-y-3"
              >
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-200/60 pb-2">
                  <div>
                    <span className="text-xs font-black text-slate-900">
                      {enq.tenantName}
                    </span>
                    <span className="text-xs text-slate-500 ml-2">
                      Inquiring for: <strong className="text-slate-700">{enq.listingTitle}</strong>
                    </span>
                  </div>

                  <div className="flex items-center gap-2">
                    <span
                      className={`px-2 py-0.5 rounded text-[10px] font-bold uppercase ${
                        enq.status === "NEW"
                          ? "bg-amber-100 text-amber-800"
                          : enq.status === "SCHEDULED_VISIT"
                          ? "bg-purple-100 text-purple-800"
                          : "bg-emerald-100 text-emerald-800"
                      }`}
                    >
                      {enq.status.replace(/_/g, " ")}
                    </span>
                    <span className="text-[11px] text-slate-400 font-mono">
                      Target Move-in: {enq.moveInDate}
                    </span>
                  </div>
                </div>

                <p className="text-xs text-slate-700 bg-white p-3 rounded-lg border border-slate-200/60">
                  &quot;{enq.message}&quot;
                </p>

                <div className="flex items-center justify-between pt-1">
                  <a
                    href={`tel:${enq.tenantPhone}`}
                    className="inline-flex items-center gap-1.5 text-xs font-bold text-brand-600 hover:text-brand-800"
                  >
                    <Phone className="w-3.5 h-3.5" />
                    <span>Call Tenant ({enq.tenantPhone})</span>
                  </a>

                  <div className="flex items-center gap-2">
                    <button
                      type="button"
                      onClick={() => handleEnquiryStatus(enq.id, "SCHEDULED_VISIT")}
                      className="px-3 py-1 bg-white border border-slate-300 hover:bg-slate-100 text-slate-700 rounded-lg text-xs font-semibold"
                    >
                      Schedule Visit
                    </button>
                    <button
                      type="button"
                      onClick={() => handleEnquiryStatus(enq.id, "CONTACTED")}
                      className="px-3 py-1 bg-brand-600 hover:bg-brand-700 text-white rounded-lg text-xs font-bold"
                    >
                      Mark Contacted
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
