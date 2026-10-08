"use client";

import { use, useState } from "react";
import Link from "next/link";
import { notFound } from "next/navigation";
import {
  ChevronLeft,
  MapPin,
  ShieldCheck,
  Droplets,
  Car,
  Bed,
  Bath,
  Calendar,
  Phone,
  MessageSquare,
  CheckCircle2,
  Sparkles,
  FileText,
  Clock,
  Layers,
} from "lucide-react";
import { findRentalProperty } from "@/lib/data/rentals";

export default function RentalDetailPage({
  params,
}: {
  params: Promise<{ rentalId: string }>;
}) {
  const { rentalId } = use(params);
  const property = findRentalProperty(rentalId);

  const [showVisitModal, setShowVisitModal] = useState(false);
  const [showApplyModal, setShowApplyModal] = useState(false);
  const [visitDate, setVisitDate] = useState("");
  const [visitSlot, setVisitSlot] = useState("Morning (10:00 AM - 12:00 PM)");
  const [isSuccess, setIsSuccess] = useState<string | null>(null);

  if (!property) {
    notFound();
  }

  return (
    <div className="pb-32 space-y-4">
      {/* Top Banner */}
      <div
        className="relative flex h-44 w-full items-start justify-between p-4"
        style={{
          background: `linear-gradient(135deg, ${property.gradientFrom}, ${property.gradientTo})`,
        }}
      >
        <Link
          href="/explore/rentals"
          className="flex h-8 w-8 items-center justify-center rounded-full bg-white/20 text-white backdrop-blur transition hover:bg-white/30"
        >
          <ChevronLeft size={18} />
        </Link>

        <div className="flex items-center gap-1 bg-white/90 backdrop-blur-xs px-2.5 py-1 rounded-full text-xs font-bold text-rose-700 shadow-sm">
          <ShieldCheck size={13} />
          <span>Lalpurja Verified</span>
        </div>
      </div>

      <div className="px-5 space-y-4 -mt-6">
        {/* Main Title & Price Card */}
        <div className="bg-white rounded-2xl border border-black/8 p-4 shadow-sm space-y-3">
          <div className="flex items-start justify-between gap-3">
            <div>
              <span className="text-[10px] font-bold uppercase tracking-wider text-rose-600 bg-rose-50 px-2 py-0.5 rounded-md">
                {property.propertyType} • {property.furnishing}
              </span>
              <h1 className="text-lg font-bold text-ink mt-1.5">{property.title}</h1>
              <p className="text-xs text-ink/60 flex items-center gap-1 mt-0.5">
                <MapPin size={12} className="text-rose-600 shrink-0" />
                <span>{property.address}</span>
              </p>
            </div>

            <div className="text-right shrink-0">
              <p className="text-xl font-black text-ink">
                Rs {property.monthlyRentNpr.toLocaleString()}
              </p>
              <p className="text-[10px] text-ink/40 font-medium">per month</p>
            </div>
          </div>

          {/* Deposit & Terms Pill */}
          <div className="flex items-center justify-between p-2.5 rounded-xl bg-slate-50 border border-black/5 text-xs">
            <span className="text-ink/60">Security Deposit:</span>
            <span className="font-bold text-ink">
              Rs {property.depositNpr.toLocaleString()} (1 Month Advance)
            </span>
          </div>
        </div>

        {/* Key Specifications Grid */}
        <div className="bg-white rounded-2xl border border-black/8 p-4 shadow-sm space-y-3">
          <h2 className="text-xs font-black uppercase tracking-wider text-ink/60">
            Property Specifications
          </h2>
          <div className="grid grid-cols-2 gap-3">
            <div className="flex items-center gap-2 p-2.5 rounded-xl bg-slate-50 border border-black/5">
              <Bed size={16} className="text-ink/50" />
              <div>
                <p className="text-[10px] text-ink/40 uppercase font-bold">Bedrooms</p>
                <p className="text-xs font-bold text-ink">{property.bedrooms} Bed</p>
              </div>
            </div>

            <div className="flex items-center gap-2 p-2.5 rounded-xl bg-slate-50 border border-black/5">
              <Bath size={16} className="text-ink/50" />
              <div>
                <p className="text-[10px] text-ink/40 uppercase font-bold">Bathrooms</p>
                <p className="text-xs font-bold text-ink">{property.bathrooms} Bath</p>
              </div>
            </div>

            <div className="flex items-center gap-2 p-2.5 rounded-xl bg-slate-50 border border-black/5">
              <Layers size={16} className="text-ink/50" />
              <div>
                <p className="text-[10px] text-ink/40 uppercase font-bold">Floor Level</p>
                <p className="text-xs font-bold text-ink">{property.floor}</p>
              </div>
            </div>

            <div className="flex items-center gap-2 p-2.5 rounded-xl bg-slate-50 border border-black/5">
              <Clock size={16} className="text-ink/50" />
              <div>
                <p className="text-[10px] text-ink/40 uppercase font-bold">Availability</p>
                <p className="text-xs font-bold text-ink">{property.availableFrom}</p>
              </div>
            </div>
          </div>

          <div className="space-y-2 pt-2 border-t border-black/5 text-xs text-ink/80">
            <div className="flex items-center gap-2">
              <Droplets size={14} className="text-emerald-600 shrink-0" />
              <span className="font-semibold text-ink">Water Supply:</span>
              <span>{property.waterSupply}</span>
            </div>
            <div className="flex items-center gap-2">
              <Car size={14} className="text-blue-600 shrink-0" />
              <span className="font-semibold text-ink">Parking:</span>
              <span>{property.parking}</span>
            </div>
          </div>
        </div>

        {/* Description */}
        <div className="bg-white rounded-2xl border border-black/8 p-4 shadow-sm space-y-2">
          <h2 className="text-xs font-black uppercase tracking-wider text-ink/60">
            About the Property
          </h2>
          <p className="text-xs text-ink/70 leading-relaxed">{property.description}</p>
        </div>

        {/* Included Amenities */}
        <div className="bg-white rounded-2xl border border-black/8 p-4 shadow-sm space-y-3">
          <h2 className="text-xs font-black uppercase tracking-wider text-ink/60">
            Included Amenities & Utilities
          </h2>
          <div className="grid grid-cols-2 gap-2">
            {property.amenities.map((a) => (
              <div key={a} className="flex items-center gap-1.5 text-xs text-ink/80">
                <CheckCircle2 size={13} className="text-emerald-600 shrink-0" />
                <span>{a}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Landlord Contact Card */}
        <div className="bg-white rounded-2xl border border-black/8 p-4 shadow-sm flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-rose-100 text-rose-700 flex items-center justify-center font-bold text-sm">
              {property.landlordName.charAt(0)}
            </div>
            <div>
              <p className="text-xs font-bold text-ink">{property.landlordName}</p>
              <p className="text-[11px] text-ink/50">Verified Property Owner</p>
              <p className="text-[11px] text-ink/60 font-mono">{property.landlordPhone}</p>
            </div>
          </div>

          <div className="flex items-center gap-1.5">
            <a
              href={`tel:${property.landlordPhone}`}
              className="p-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-ink/80 transition-colors"
              title="Call Landlord"
            >
              <Phone size={14} />
            </a>
            <Link
              href="/chat"
              className="p-2 rounded-xl bg-rose-50 hover:bg-rose-100 text-rose-700 transition-colors"
              title="Message on EVRRY Chat"
            >
              <MessageSquare size={14} />
            </Link>
          </div>
        </div>
      </div>

      {/* Sticky Bottom Action Tray */}
      <div className="fixed bottom-0 left-0 right-0 max-w-md mx-auto p-4 bg-white/95 backdrop-blur-md border-t border-black/10 z-40 flex items-center gap-2">
        <button
          type="button"
          onClick={() => {
            setShowVisitModal(true);
            setIsSuccess(null);
          }}
          className="flex-1 py-3 bg-rose-600 hover:bg-rose-700 text-white rounded-2xl text-xs font-bold transition-all shadow-sm flex items-center justify-center gap-1.5"
        >
          <Calendar size={14} />
          <span>Schedule Physical Visit</span>
        </button>

        <button
          type="button"
          onClick={() => {
            setShowApplyModal(true);
            setIsSuccess(null);
          }}
          className="px-4 py-3 border border-black/15 hover:bg-slate-50 text-ink rounded-2xl text-xs font-bold transition-colors"
        >
          Apply
        </button>
      </div>

      {/* Visit Schedule Modal */}
      {showVisitModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-black/10 max-w-sm w-full p-5 shadow-2xl space-y-4 animate-in fade-in zoom-in-95">
            {isSuccess ? (
              <div className="text-center py-4 space-y-2">
                <div className="w-12 h-12 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center mx-auto">
                  <CheckCircle2 size={24} />
                </div>
                <h3 className="text-base font-bold text-ink">Visit Request Sent!</h3>
                <p className="text-xs text-ink/60">
                  {property.landlordName} will call you at {property.landlordPhone} to confirm your inspection.
                </p>
                <button
                  type="button"
                  onClick={() => setShowVisitModal(false)}
                  className="mt-3 w-full py-2 bg-rose-600 text-white rounded-xl text-xs font-bold"
                >
                  Close
                </button>
              </div>
            ) : (
              <>
                <div className="flex items-center justify-between border-b border-black/5 pb-2">
                  <h3 className="text-sm font-bold text-ink">Book Physical Visit</h3>
                  <button
                    type="button"
                    onClick={() => setShowVisitModal(false)}
                    className="text-xs text-ink/40 hover:text-ink"
                  >
                    ✕
                  </button>
                </div>

                <div className="space-y-3">
                  <div>
                    <label className="text-[10px] font-bold uppercase tracking-wider text-ink/50 block mb-1">
                      Inspection Date
                    </label>
                    <input
                      type="date"
                      value={visitDate}
                      onChange={(e) => setVisitDate(e.target.value)}
                      className="w-full text-xs p-2.5 bg-slate-50 border border-black/10 rounded-xl font-medium outline-none"
                    />
                  </div>

                  <div>
                    <label className="text-[10px] font-bold uppercase tracking-wider text-ink/50 block mb-1">
                      Inspection Time Slot
                    </label>
                    <select
                      value={visitSlot}
                      onChange={(e) => setVisitSlot(e.target.value)}
                      className="w-full text-xs p-2.5 bg-slate-50 border border-black/10 rounded-xl font-medium outline-none"
                    >
                      <option>Morning (10:00 AM - 12:00 PM)</option>
                      <option>Afternoon (2:00 PM - 4:00 PM)</option>
                      <option>Evening (4:30 PM - 6:30 PM)</option>
                    </select>
                  </div>
                </div>

                <div className="flex items-center gap-2 pt-2">
                  <button
                    type="button"
                    onClick={() => setIsSuccess("visit")}
                    className="flex-1 py-2.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl text-xs font-bold transition-all shadow-xs"
                  >
                    Confirm Visit
                  </button>
                  <button
                    type="button"
                    onClick={() => setShowVisitModal(false)}
                    className="px-4 py-2.5 border border-black/10 hover:bg-slate-50 text-ink/70 rounded-xl text-xs font-semibold"
                  >
                    Cancel
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      {/* Tenancy Application Modal */}
      {showApplyModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-black/10 max-w-sm w-full p-5 shadow-2xl space-y-4 animate-in fade-in zoom-in-95">
            {isSuccess ? (
              <div className="text-center py-4 space-y-2">
                <div className="w-12 h-12 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center mx-auto">
                  <CheckCircle2 size={24} />
                </div>
                <h3 className="text-base font-bold text-ink">Application Submitted!</h3>
                <p className="text-xs text-ink/60">
                  Your tenant profile has been dispatched to {property.landlordName}. You can track status in Activity.
                </p>
                <button
                  type="button"
                  onClick={() => setShowApplyModal(false)}
                  className="mt-3 w-full py-2 bg-rose-600 text-white rounded-xl text-xs font-bold"
                >
                  Done
                </button>
              </div>
            ) : (
              <>
                <div className="flex items-center justify-between border-b border-black/5 pb-2">
                  <h3 className="text-sm font-bold text-ink">Apply for Tenancy</h3>
                  <button
                    type="button"
                    onClick={() => setShowApplyModal(false)}
                    className="text-xs text-ink/40 hover:text-ink"
                  >
                    ✕
                  </button>
                </div>

                <div className="space-y-2 text-xs text-ink/70">
                  <p>
                    You are applying for: <strong className="text-ink">{property.title}</strong>
                  </p>
                  <p>
                    Monthly Rent: <strong className="text-ink">Rs {property.monthlyRentNpr.toLocaleString()}</strong>
                  </p>
                  <p>
                    Deposit: <strong className="text-ink">Rs {property.depositNpr.toLocaleString()}</strong>
                  </p>
                  <p className="text-[11px] text-ink/50 pt-1">
                    Your verified citizenship / KYC will be securely shared with {property.landlordName} upon preliminary acceptance.
                  </p>
                </div>

                <div className="flex items-center gap-2 pt-2">
                  <button
                    type="button"
                    onClick={() => setIsSuccess("apply")}
                    className="flex-1 py-2.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl text-xs font-bold transition-all shadow-xs"
                  >
                    Submit 1-Click Application
                  </button>
                  <button
                    type="button"
                    onClick={() => setShowApplyModal(false)}
                    className="px-4 py-2.5 border border-black/10 hover:bg-slate-50 text-ink/70 rounded-xl text-xs font-semibold"
                  >
                    Cancel
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
