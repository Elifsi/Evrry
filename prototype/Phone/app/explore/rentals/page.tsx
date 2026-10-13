// Room & Flat Rentals are out of scope for the current release.
// This page is preserved to avoid a 404 for any existing links/bookmarks.
import Link from "next/link";
import { Home, ArrowLeft, Clock } from "lucide-react";

export default function RentalsComingSoonPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center px-6 text-center">
      <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-rose-50 text-rose-600 mb-4">
        <Home size={28} strokeWidth={1.5} />
      </div>
      <h1 className="text-xl font-bold text-ink">Rentals coming soon</h1>
      <p className="mt-2 text-sm text-ink/50 max-w-xs">
        Room and flat rentals are not available in this release. Verified landlord listings across
        Nepal are planned for an upcoming version.
      </p>
      <div className="mt-4 flex items-center gap-1 text-xs text-ink/40">
        <Clock size={12} /> Next release
      </div>
      <Link
        href="/explore"
        className="mt-6 inline-flex items-center gap-2 rounded-full bg-brand px-5 py-2.5 text-sm font-semibold text-white"
      >
        <ArrowLeft size={14} /> Back to Services
      </Link>
    </div>
  );
}
