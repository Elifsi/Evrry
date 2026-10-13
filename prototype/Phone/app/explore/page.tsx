// Rides-only release: Hotels & Room Rentals are hidden from this page.
// The underlying pages, data files, and store slices are preserved for future
// development; only the user-facing navigation entry points are removed here.
import Link from "next/link";
import {
  UtensilsCrossed,
  ShoppingCart,
  Car,
  Laptop,
  Shirt,
  Wrench,
  Search,
  MapPin,
  ChevronRight,
  Sparkles,
  Zap,
} from "lucide-react";

export default function ExplorePage() {
  // Hotels & Rentals removed for current release. Rides is the active mobility vertical.
  const CORE_VERTICALS = [
    {
      id: "rides",
      label: "City Rides",
      tagline: "Pathao & Uber style · Bike, Auto & Cab",
      eta: "3-5 min pickup",
      badge: "EV & Moto",
      color: "from-blue-600 to-indigo-700",
      lightBg: "bg-blue-50",
      textColor: "text-blue-700",
      borderColor: "border-blue-200",
      icon: Car,
      href: "/explore/rides",
    },
    {
      id: "food",
      label: "Food Delivery",
      tagline: "DoorDash & Zomato style",
      eta: "20-30 min",
      badge: "Popular",
      color: "from-orange-500 to-amber-600",
      lightBg: "bg-orange-50",
      textColor: "text-orange-700",
      borderColor: "border-orange-200",
      icon: UtensilsCrossed,
      href: "/explore/food",
    },
    {
      id: "grocery",
      label: "Grocery Marts",
      tagline: "Blinkit quick commerce",
      eta: "10-15 min",
      badge: "Fastest",
      color: "from-emerald-600 to-teal-700",
      lightBg: "bg-emerald-50",
      textColor: "text-emerald-700",
      borderColor: "border-emerald-200",
      icon: ShoppingCart,
      href: "/explore/grocery",
    },
  ];

  const SECONDARY_VERTICALS = [
    { id: "electronics", label: "Electronics", icon: Laptop, href: "/explore/electronics" },
    { id: "fashion", label: "Fashion / Retail", icon: Shirt, href: "/explore/fashion" },
    { id: "services", label: "Home Services", icon: Wrench, href: "/explore/services" },
  ];

  return (
    <div className="px-5 pt-6 pb-28 space-y-5">
      {/* Location Bar */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-1.5 text-xs text-ink/70">
          <MapPin size={14} className="text-brand shrink-0" />
          <span className="font-semibold text-ink">Kathmandu Valley</span>
          <span className="text-[10px] text-ink/40">(Doorstep Delivery)</span>
        </div>
        <Link
          href="/"
          className="inline-flex items-center gap-1 rounded-full bg-brandSoft px-2.5 py-1 text-[11px] font-semibold text-brand transition hover:brightness-95"
        >
          <Sparkles size={11} /> AI Concierge
        </Link>
      </div>

      <div>
        <h1 className="text-2xl font-bold tracking-tight text-ink">Explore Services</h1>
        <p className="mt-0.5 text-xs text-ink/50">
          Rides · Food · Groceries — and more coming soon across Nepal.
        </p>
      </div>

      {/* Global Search Bar */}
      <div className="relative">
        <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink/35" />
        <input
          type="text"
          placeholder="Search food, groceries, rides..."
          className="w-full pl-10 pr-4 py-2.5 bg-white border border-black/10 rounded-2xl text-xs text-ink placeholder:text-ink/35 shadow-2xs focus:outline-none focus:border-brand"
        />
      </div>

      {/* Active Verticals */}
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <h2 className="text-xs font-black uppercase tracking-wider text-ink/60">
            Available Now
          </h2>
          <span className="text-[10px] font-bold text-brand bg-brandSoft px-2 py-0.5 rounded-full">
            Live
          </span>
        </div>

        <div className="space-y-2.5">
          {CORE_VERTICALS.map((v) => {
            const Icon = v.icon;
            return (
              <Link
                key={v.id}
                href={v.href}
                className={`flex items-center justify-between p-3.5 rounded-2xl bg-white border ${v.borderColor} shadow-2xs transition-all hover:scale-[1.01] hover:shadow-xs group`}
              >
                <div className="flex items-center gap-3.5">
                  <div
                    className={`w-12 h-12 rounded-xl flex items-center justify-center text-white bg-gradient-to-br ${v.color} shadow-2xs shrink-0 group-hover:rotate-2 transition-transform`}
                  >
                    <Icon size={22} strokeWidth={2} />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <h3 className="text-sm font-bold text-ink group-hover:text-brand transition-colors">
                        {v.label}
                      </h3>
                      <span
                        className={`text-[9px] font-bold px-1.5 py-0.5 rounded-md ${v.lightBg} ${v.textColor}`}
                      >
                        {v.badge}
                      </span>
                    </div>
                    <p className="text-[11px] text-ink/50 mt-0.5">{v.tagline}</p>
                    <div className="flex items-center gap-2 mt-1 text-[10px] text-ink/60 font-medium">
                      <span className="flex items-center gap-1">
                        <Zap size={10} className="text-amber-500 fill-amber-500" />
                        {v.eta}
                      </span>
                    </div>
                  </div>
                </div>

                <div className="w-8 h-8 rounded-full bg-slate-50 flex items-center justify-center text-ink/40 group-hover:text-brand group-hover:bg-brandSoft transition-colors shrink-0">
                  <ChevronRight size={16} />
                </div>
              </Link>
            );
          })}
        </div>
      </div>

      {/* Quick Search Chips */}
      <div className="space-y-2">
        <h3 className="text-xs font-bold text-ink/70">Popular in Kathmandu Valley</h3>
        <div className="flex flex-wrap gap-1.5">
          {[
            { label: "🛵 EVRRY Moto Ride", href: "/explore/rides" },
            { label: "🚗 AC Cab to Airport", href: "/explore/rides" },
            { label: "🥟 Momo & Chowmein", href: "/explore/food" },
            { label: "🍲 Jimbu Thakali", href: "/explore/food" },
            { label: "🥛 DDC Cow Milk", href: "/explore/grocery" },
            { label: "⚡ EV Auto Ride", href: "/explore/rides" },
          ].map((chip) => (
            <Link
              key={chip.label}
              href={chip.href}
              className="text-[11px] px-2.5 py-1 bg-white border border-black/8 rounded-full text-ink/70 hover:border-brand hover:text-brand transition-colors"
            >
              {chip.label}
            </Link>
          ))}
        </div>
      </div>

      {/* Other Categories */}
      <div className="space-y-2 pt-2 border-t border-black/5">
        <h3 className="text-xs font-semibold text-ink/50 uppercase tracking-wider">
          More Categories
        </h3>
        <div className="grid grid-cols-3 gap-2">
          {SECONDARY_VERTICALS.map((s) => {
            const Icon = s.icon;
            return (
              <Link
                key={s.id}
                href={s.href}
                className="flex flex-col items-center gap-1.5 p-3 rounded-xl border border-black/5 bg-white shadow-2xs hover:border-brand transition-colors text-center"
              >
                <div className="w-8 h-8 rounded-lg bg-brandSoft text-brand flex items-center justify-center">
                  <Icon size={16} strokeWidth={1.8} />
                </div>
                <span className="text-[11px] font-semibold text-ink">{s.label}</span>
              </Link>
            );
          })}
        </div>
      </div>
    </div>
  );
}
