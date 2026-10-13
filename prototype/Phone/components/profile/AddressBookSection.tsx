"use client";

/**
 * AddressBookSection — Profile accordion section for managing saved addresses.
 *
 * Features:
 * - Displays saved Home, Office, and custom addresses.
 * - Add/edit addresses using the shared LocationPickerSheet.
 * - Delete with a confirmation step.
 * - Persisted via the Zustand addressBookSlice (localStorage).
 *
 * Important: The device's current location does NOT auto-overwrite Home or Office.
 * Users must explicitly tap "Set as Home" or "Set as Office" to change those.
 */
import { useState } from "react";
import {
  MapPin,
  Home,
  Building2,
  Plus,
  Pencil,
  Trash2,
  ChevronDown,
  ChevronUp,
  Navigation,
  X,
  Check,
} from "lucide-react";
import clsx from "clsx";
import { useAppStore } from "@/lib/store/useAppStore";
import LocationPickerSheet from "@/components/location/LocationPickerSheet";
import type { ResolvedLocation } from "@/lib/location/geoService";
import type { SavedAddress } from "@/lib/types/addresses";

interface AddressBookSectionProps {
  open: boolean;
  onToggle: () => void;
}

type AddressTag = "home" | "office" | "custom";

export default function AddressBookSection({ open, onToggle }: AddressBookSectionProps) {
  const savedAddresses = useAppStore((s) => s.savedAddresses);
  const saveAddress = useAppStore((s) => s.saveAddress);
  const updateAddress = useAppStore((s) => s.updateAddress);
  const deleteAddress = useAppStore((s) => s.deleteAddress);

  const [pickerOpen, setPickerOpen] = useState(false);
  const [pickerTag, setPickerTag] = useState<AddressTag>("custom");
  const [pickerName, setPickerName] = useState("Home");
  const [editingId, setEditingId] = useState<string | null>(null);
  const [confirmDeleteId, setConfirmDeleteId] = useState<string | null>(null);
  const [customNameInput, setCustomNameInput] = useState("");
  const [showCustomNameForm, setShowCustomNameForm] = useState(false);
  const [pendingLocation, setPendingLocation] = useState<ResolvedLocation | null>(null);

  // Sorted: home first, office second, then custom alphabetically
  const sortedAddresses = [...savedAddresses].sort((a, b) => {
    const order = { home: 0, office: 1, custom: 2 };
    const o = order[a.tag] - order[b.tag];
    if (o !== 0) return o;
    return a.name.localeCompare(b.name);
  });

  const homeAddr = savedAddresses.find((a) => a.tag === "home");
  const officeAddr = savedAddresses.find((a) => a.tag === "office");

  function startAdd(tag: AddressTag, name: string) {
    setPickerTag(tag);
    setPickerName(name);
    setEditingId(null);
    setPickerOpen(true);
  }

  function startEdit(addr: SavedAddress) {
    setPickerTag(addr.tag);
    setPickerName(addr.name);
    setEditingId(addr.id);
    setPickerOpen(true);
  }

  function handleLocationSelected(loc: ResolvedLocation) {
    if (pickerTag === "custom") {
      // For custom addresses, we need a name first
      setPendingLocation(loc);
      setShowCustomNameForm(true);
      return;
    }
    // Home or Office — save immediately
    if (editingId) {
      updateAddress(editingId, { label: loc.label, lat: loc.coords.lat, lng: loc.coords.lng });
    } else {
      saveAddress({
        tag: pickerTag,
        name: pickerName,
        label: loc.label,
        lat: loc.coords.lat,
        lng: loc.coords.lng,
      });
    }
  }

  function handleCustomNameSave() {
    const name = customNameInput.trim();
    if (!name || !pendingLocation) return;
    if (editingId) {
      updateAddress(editingId, {
        name,
        label: pendingLocation.label,
        lat: pendingLocation.coords.lat,
        lng: pendingLocation.coords.lng,
      });
    } else {
      saveAddress({
        tag: "custom",
        name,
        label: pendingLocation.label,
        lat: pendingLocation.coords.lat,
        lng: pendingLocation.coords.lng,
      });
    }
    setCustomNameInput("");
    setPendingLocation(null);
    setShowCustomNameForm(false);
  }

  function handleDelete(id: string) {
    if (confirmDeleteId === id) {
      deleteAddress(id);
      setConfirmDeleteId(null);
    } else {
      setConfirmDeleteId(id);
    }
  }

  const tagIcon = (tag: AddressTag) => {
    if (tag === "home") return <Home size={14} className="text-brand" />;
    if (tag === "office") return <Building2 size={14} className="text-indigo-600" />;
    return <MapPin size={14} className="text-ink/50" />;
  };

  const tagColor = (tag: AddressTag) => {
    if (tag === "home") return "bg-brandSoft";
    if (tag === "office") return "bg-indigo-50";
    return "bg-black/5";
  };

  return (
    <div className="rounded-xl2 border border-black/5 bg-white shadow-sm">
      <button
        onClick={onToggle}
        className="flex w-full items-center gap-3 px-4 py-3.5 text-left"
      >
        <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-brandSoft text-brand">
          <Navigation size={16} />
        </span>
        <div className="flex-1">
          <p className="text-sm font-semibold text-ink">Saved Addresses</p>
          <p className="text-xs text-ink/45">
            {savedAddresses.length > 0
              ? `${savedAddresses.length} saved place${savedAddresses.length > 1 ? "s" : ""}`
              : "Home, Office & more"}
          </p>
        </div>
        {open ? <ChevronUp size={16} className="text-ink/40" /> : <ChevronDown size={16} className="text-ink/40" />}
      </button>

      {open && (
        <div className="border-t border-black/5 px-4 pb-4">
          {/* Quick add shortcuts for Home & Office if not set */}
          <div className="mt-3 flex gap-2">
            {!homeAddr && (
              <button
                onClick={() => startAdd("home", "Home")}
                className="flex flex-1 items-center gap-1.5 rounded-xl border border-dashed border-brand/30 bg-brandSoft px-3 py-2 text-xs font-medium text-brand hover:bg-brand hover:text-white transition"
              >
                <Home size={12} /> Add Home
              </button>
            )}
            {!officeAddr && (
              <button
                onClick={() => startAdd("office", "Office")}
                className="flex flex-1 items-center gap-1.5 rounded-xl border border-dashed border-indigo-300 bg-indigo-50 px-3 py-2 text-xs font-medium text-indigo-700 hover:bg-indigo-600 hover:text-white transition"
              >
                <Building2 size={12} /> Add Office
              </button>
            )}
          </div>

          {/* Saved address list */}
          {sortedAddresses.length > 0 && (
            <div className="mt-3 space-y-2">
              {sortedAddresses.map((addr) => (
                <div
                  key={addr.id}
                  className="flex items-center gap-3 rounded-xl border border-black/5 bg-white p-3"
                >
                  <span
                    className={clsx(
                      "flex h-9 w-9 shrink-0 items-center justify-center rounded-full",
                      tagColor(addr.tag)
                    )}
                  >
                    {tagIcon(addr.tag)}
                  </span>
                  <div className="min-w-0 flex-1">
                    <p className="text-sm font-semibold text-ink">{addr.name}</p>
                    <p className="truncate text-xs text-ink/50">{addr.label}</p>
                    <p className="text-[10px] text-ink/30">
                      {addr.lat.toFixed(4)}, {addr.lng.toFixed(4)}
                    </p>
                  </div>
                  <div className="flex items-center gap-1 shrink-0">
                    <button
                      onClick={() => startEdit(addr)}
                      className="flex h-7 w-7 items-center justify-center rounded-full text-ink/40 hover:bg-black/5 hover:text-ink"
                      title="Edit"
                    >
                      <Pencil size={13} />
                    </button>
                    <button
                      onClick={() => handleDelete(addr.id)}
                      className={clsx(
                        "flex h-7 w-7 items-center justify-center rounded-full transition",
                        confirmDeleteId === addr.id
                          ? "bg-red-500 text-white"
                          : "text-ink/40 hover:bg-red-50 hover:text-red-500"
                      )}
                      title={confirmDeleteId === addr.id ? "Confirm delete" : "Delete"}
                    >
                      {confirmDeleteId === addr.id ? <Check size={13} /> : <Trash2 size={13} />}
                    </button>
                    {confirmDeleteId === addr.id && (
                      <button
                        onClick={() => setConfirmDeleteId(null)}
                        className="flex h-7 w-7 items-center justify-center rounded-full text-ink/40 hover:bg-black/5"
                        title="Cancel"
                      >
                        <X size={13} />
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}

          {sortedAddresses.length === 0 && (
            <p className="mt-3 text-center text-xs text-ink/40 py-2">
              No saved addresses yet. Add Home or Office above.
            </p>
          )}

          {/* Add another custom place */}
          <button
            onClick={() => startAdd("custom", "Custom")}
            className="mt-3 flex w-full items-center justify-center gap-1.5 rounded-xl border border-dashed border-black/15 py-2 text-xs font-medium text-ink/50 hover:border-brand hover:text-brand transition"
          >
            <Plus size={13} /> Add another place
          </button>
        </div>
      )}

      {/* Location picker sheet */}
      <LocationPickerSheet
        isOpen={pickerOpen}
        title={`Set ${pickerName} location`}
        onSelect={handleLocationSelected}
        onClose={() => setPickerOpen(false)}
      />

      {/* Custom place name form */}
      {showCustomNameForm && pendingLocation && (
        <div className="fixed inset-0 z-50 flex items-end">
          <div className="absolute inset-0 bg-black/40" onClick={() => setShowCustomNameForm(false)} />
          <div className="relative w-full rounded-t-3xl bg-paper px-5 pb-8 pt-6 shadow-2xl">
            <h2 className="text-base font-bold text-ink">Name this place</h2>
            <p className="mt-1 text-xs text-ink/50 truncate">{pendingLocation.label}</p>
            <input
              autoFocus
              value={customNameInput}
              onChange={(e) => setCustomNameInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleCustomNameSave()}
              placeholder='e.g. "Gym", "Mom\'s house"'
              className="mt-4 w-full rounded-xl border border-black/10 px-3 py-2.5 text-sm focus:outline-none focus:border-brand"
            />
            <button
              onClick={handleCustomNameSave}
              disabled={!customNameInput.trim()}
              className="mt-3 w-full rounded-full bg-brand py-3 text-sm font-semibold text-white disabled:opacity-40"
            >
              Save place
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
