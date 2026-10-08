"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import {
  Store,
  Lock,
  Mail,
  Phone,
  ArrowRight,
  ShieldCheck,
  UtensilsCrossed,
  ShoppingBag,
  Hotel,
  Home,
  CheckCircle2,
} from "lucide-react";

export default function PartnerLoginPage() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState<"password" | "otp" | "demo">("password");
  const [email, setEmail] = useState("roadhouse.pulchowk@evrry.com.np");
  const [password, setPassword] = useState("Partner@2026");
  const [phone, setPhone] = useState("9841556677");
  const [otpToken, setOtpToken] = useState("");
  const [otpSent, setOtpSent] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handlePasswordLogin = (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      router.push("/");
    }, 600);
  };

  const handleSendOtp = () => {
    if (!phone || phone.length < 9) {
      setErrorMessage("Enter a valid 10-digit Nepali mobile number.");
      return;
    }
    setIsLoading(true);
    setErrorMessage(null);
    setTimeout(() => {
      setIsLoading(false);
      setOtpSent(true);
    }, 800);
  };

  const handleVerifyOtp = (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      router.push("/");
    }, 600);
  };

  const handleQuickDemoRole = (role: string) => {
    if (role === "restaurant") router.push("/kds");
    else if (role === "grocery") router.push("/grocery");
    else if (role === "hotel") router.push("/hotel");
    else if (role === "landlord") router.push("/rentals");
    else router.push("/");
  };

  return (
    <div className="h-screen overflow-y-auto bg-slate-50 flex flex-col justify-center items-center p-4 antialiased">
      <div className="max-w-md w-full space-y-6">
        {/* Brand Header */}
        <div className="text-center space-y-2">
          <div className="inline-flex items-center justify-center w-12 h-12 rounded-2xl bg-brand-600 text-white font-bold text-2xl shadow-lg shadow-brand-500/20">
            E
          </div>
          <div>
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center justify-center gap-2">
              <span>EVRRY</span>
              <span className="text-[11px] uppercase font-bold px-2 py-0.5 rounded bg-brand-100 text-brand-800 border border-brand-200">
                Partner Portal
              </span>
            </h1>
            <p className="text-xs text-slate-500 mt-1">
              Merchant & Venue Operations • Elifsi Technologies
            </p>
          </div>
        </div>

        {/* Card */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 space-y-5">
          {/* Tabs */}
          <div className="grid grid-cols-3 gap-1 bg-slate-100 p-1 rounded-xl text-xs font-semibold">
            <button
              type="button"
              onClick={() => {
                setActiveTab("password");
                setErrorMessage(null);
              }}
              className={`py-1.5 rounded-lg transition-all ${
                activeTab === "password"
                  ? "bg-white text-slate-900 shadow-xs font-bold"
                  : "text-slate-500 hover:text-slate-900"
              }`}
            >
              Password
            </button>
            <button
              type="button"
              onClick={() => {
                setActiveTab("otp");
                setErrorMessage(null);
              }}
              className={`py-1.5 rounded-lg transition-all ${
                activeTab === "otp"
                  ? "bg-white text-slate-900 shadow-xs font-bold"
                  : "text-slate-500 hover:text-slate-900"
              }`}
            >
              Phone OTP
            </button>
            <button
              type="button"
              onClick={() => {
                setActiveTab("demo");
                setErrorMessage(null);
              }}
              className={`py-1.5 rounded-lg transition-all ${
                activeTab === "demo"
                  ? "bg-white text-brand-700 shadow-xs font-bold"
                  : "text-slate-500 hover:text-slate-900"
              }`}
            >
              Demo Roles
            </button>
          </div>

          {errorMessage && (
            <div className="p-3 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-700 font-medium">
              {errorMessage}
            </div>
          )}

          {/* TAB 1: Password Login */}
          {activeTab === "password" && (
            <form onSubmit={handlePasswordLogin} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">
                  Partner Email / ID
                </label>
                <div className="relative">
                  <Mail className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="email"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:border-brand-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">
                  Password
                </label>
                <div className="relative">
                  <Lock className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="password"
                    required
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:border-brand-500"
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={isLoading}
                className="w-full py-2.5 bg-brand-600 hover:bg-brand-700 text-white rounded-xl text-xs font-bold transition-all shadow-xs flex items-center justify-center gap-1.5"
              >
                <span>{isLoading ? "Signing In..." : "Open Partner Operations Desk"}</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </form>
          )}

          {/* TAB 2: Phone OTP */}
          {activeTab === "otp" && (
            <form onSubmit={handleVerifyOtp} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">
                  Registered Mobile Number
                </label>
                <div className="relative">
                  <span className="absolute left-3 top-1/2 -translate-y-1/2 text-xs font-mono font-bold text-slate-500">
                    +977
                  </span>
                  <input
                    type="tel"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    placeholder="98XXXXXXXX"
                    className="w-full pl-14 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:border-brand-500"
                  />
                </div>
              </div>

              {!otpSent ? (
                <button
                  type="button"
                  onClick={handleSendOtp}
                  disabled={isLoading}
                  className="w-full py-2.5 bg-brand-600 hover:bg-brand-700 text-white rounded-xl text-xs font-bold transition-all shadow-xs"
                >
                  {isLoading ? "Sending OTP..." : "Send Verification Code"}
                </button>
              ) : (
                <>
                  <div>
                    <label className="block text-xs font-bold text-slate-700 mb-1">
                      Enter 6-Digit Code
                    </label>
                    <input
                      type="text"
                      required
                      value={otpToken}
                      onChange={(e) => setOtpToken(e.target.value)}
                      placeholder="123456"
                      className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 font-mono tracking-widest text-center"
                    />
                  </div>
                  <button
                    type="submit"
                    className="w-full py-2.5 bg-brand-600 hover:bg-brand-700 text-white rounded-xl text-xs font-bold transition-all shadow-xs"
                  >
                    Verify & Enter Dashboard
                  </button>
                </>
              )}
            </form>
          )}

          {/* TAB 3: Demo Roles Selector */}
          {activeTab === "demo" && (
            <div className="space-y-2.5">
              <p className="text-xs text-slate-500 mb-2">
                Instantly enter the portal as any of the four verified partner profiles:
              </p>

              <button
                type="button"
                onClick={() => handleQuickDemoRole("restaurant")}
                className="w-full p-3 rounded-xl border border-slate-200 hover:border-brand-300 hover:bg-brand-50/30 text-left transition-all flex items-center justify-between group"
              >
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-brand-50 text-brand-600 rounded-lg group-hover:bg-brand-100">
                    <UtensilsCrossed className="w-4 h-4" />
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-900">
                      Restaurant / Kitchen Operator
                    </p>
                    <p className="text-[11px] text-slate-500">
                      Roadhouse Cafe Pulchowk • KDS & Menu 86
                    </p>
                  </div>
                </div>
                <ArrowRight className="w-4 h-4 text-slate-400 group-hover:text-brand-600 group-hover:translate-x-0.5 transition-all" />
              </button>

              <button
                type="button"
                onClick={() => handleQuickDemoRole("grocery")}
                className="w-full p-3 rounded-xl border border-slate-200 hover:border-emerald-300 hover:bg-emerald-50/30 text-left transition-all flex items-center justify-between group"
              >
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-emerald-50 text-emerald-600 rounded-lg group-hover:bg-emerald-100">
                    <ShoppingBag className="w-4 h-4" />
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-900">
                      Grocery Store Manager
                    </p>
                    <p className="text-[11px] text-slate-500">
                      Bhatbhateni Tangal • Pick & Pack POS
                    </p>
                  </div>
                </div>
                <ArrowRight className="w-4 h-4 text-slate-400 group-hover:text-emerald-600 group-hover:translate-x-0.5 transition-all" />
              </button>

              <button
                type="button"
                onClick={() => handleQuickDemoRole("hotel")}
                className="w-full p-3 rounded-xl border border-slate-200 hover:border-indigo-300 hover:bg-indigo-50/30 text-left transition-all flex items-center justify-between group"
              >
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-indigo-50 text-indigo-600 rounded-lg group-hover:bg-indigo-100">
                    <Hotel className="w-4 h-4" />
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-900">
                      Hotel Front Desk Officer
                    </p>
                    <p className="text-[11px] text-slate-500">
                      Kathmandu Guest House • Room Bookings
                    </p>
                  </div>
                </div>
                <ArrowRight className="w-4 h-4 text-slate-400 group-hover:text-indigo-600 group-hover:translate-x-0.5 transition-all" />
              </button>

              <button
                type="button"
                onClick={() => handleQuickDemoRole("landlord")}
                className="w-full p-3 rounded-xl border border-slate-200 hover:border-amber-300 hover:bg-amber-50/30 text-left transition-all flex items-center justify-between group"
              >
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-amber-50 text-amber-600 rounded-lg group-hover:bg-amber-100">
                    <Home className="w-4 h-4" />
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-900">
                      Property Landlord
                    </p>
                    <p className="text-[11px] text-slate-500">
                      Sanepa Residential Heights • Flat Listings
                    </p>
                  </div>
                </div>
                <ArrowRight className="w-4 h-4 text-slate-400 group-hover:text-amber-600 group-hover:translate-x-0.5 transition-all" />
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
