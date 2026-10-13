// Hotels & Stays are out of scope for the current release.
// This page is preserved to avoid a 404 for any existing links/bookmarks.
import Link from "next/link";
import { Hotel, ArrowLeft, Clock } from "lucide-react";

export default function HotelsComingSoonPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center px-6 text-center">
      <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-amber-50 text-amber-600 mb-4">
        <Hotel size={28} strokeWidth={1.5} />
      </div>
      <h1 className="text-xl font-bold text-ink">Hotels coming soon</h1>
      <p className="mt-2 text-sm text-ink/50 max-w-xs">
        Hotel and stay bookings are not available in this release. We&apos;re working on bringing
        verified stays across Nepal soon.
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
