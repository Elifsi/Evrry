"use client";

import { create } from "zustand";
import { persist } from "zustand/middleware";
import { CreatorFilter, CreatorProfile, FilterSettings, Snap } from "@/lib/types";

function uid(prefix: string) {
  return `${prefix}_${Math.random().toString(36).slice(2, 10)}`;
}

interface SnapState {
  creatorProfile: CreatorProfile | null;
  filters: CreatorFilter[];
  snaps: Snap[];
  cameraRoll: Snap[];
  lockerSnaps: Snap[];
  lockerPin: string | null;
  isLockerUnlocked: boolean;

  registerCreator: (input: { handle: string; category: string; bio: string }) => void;
  unregisterCreator: () => void;

  createFilter: (name: string, settings: FilterSettings) => CreatorFilter;
  deleteFilter: (id: string) => void;

  addSnap: (dataUrl: string, filterName: string, location?: string, caption?: string) => Snap;
  deleteSnap: (id: string) => void;

  addToCameraRoll: (dataUrl: string, caption?: string, location?: string) => Snap;
  deleteCameraRoll: (id: string) => void;

  setLockerPin: (pin: string) => void;
  unlockLocker: (pin: string) => boolean;
  lockLocker: () => void;

  moveToLocker: (id: string, fromTab: "snaps" | "roll") => void;
  restoreFromLocker: (id: string) => void;
  deleteLockerSnap: (id: string) => void;
}

// Curated initial memories for Kathmandu & Nepal
const SEED_SNAPS: Snap[] = [
  {
    id: "snap_patan_golden",
    dataUrl: "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800&auto=format&fit=crop&q=80",
    filterName: "Kathmandu Gold",
    location: "Patan Durbar Square, Lalitpur",
    caption: "Golden hour over Patan temples ✨",
    createdAt: Date.now() - 3600000 * 4,
  },
  {
    id: "snap_himalaya_morning",
    dataUrl: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=80",
    filterName: "Everest Ice",
    location: "Nagarkot Sunrise Viewpoint",
    caption: "Early sunrise over the Himalayan range 🏔️",
    createdAt: Date.now() - 3600000 * 28,
  },
  {
    id: "snap_thamel_neon",
    dataUrl: "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&auto=format&fit=crop&q=80",
    filterName: "Cyberpunk Neon",
    location: "Thamel Night Market, Kathmandu",
    caption: "Late night coffee & live acoustic tunes ☕🎶",
    createdAt: Date.now() - 3600000 * 52,
  },
];

const SEED_ROLL: Snap[] = [
  {
    id: "roll_pokhara_lake",
    dataUrl: "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=800&auto=format&fit=crop&q=80",
    filterName: "Camera Roll",
    location: "Phewa Lake, Pokhara",
    caption: "Wooden boat rides under Machapuchare ⛵",
    createdAt: Date.now() - 3600000 * 72,
  },
  {
    id: "roll_momo_lunch",
    dataUrl: "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=800&auto=format&fit=crop&q=80",
    filterName: "Camera Roll",
    location: "Jhamsikhel, Lalitpur",
    caption: "Steaming hot jhol momo with homemade achar 🥟🔥",
    createdAt: Date.now() - 3600000 * 96,
  },
];

const SEED_LOCKER: Snap[] = [
  {
    id: "locker_private_note",
    dataUrl: "https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=800&auto=format&fit=crop&q=80",
    filterName: "Private Vault",
    location: "Personal Vault",
    caption: "Private travel itinerary & receipts 🔒",
    isLocked: true,
    createdAt: Date.now() - 3600000 * 120,
  },
];

export const useSnapStore = create<SnapState>()(
  persist(
    (set, get) => ({
      creatorProfile: null,
      filters: [],
      snaps: SEED_SNAPS,
      cameraRoll: SEED_ROLL,
      lockerSnaps: SEED_LOCKER,
      lockerPin: "1234", // Default 4-digit PIN for My Locker
      isLockerUnlocked: false,

      registerCreator: ({ handle, category, bio }) =>
        set({
          creatorProfile: { handle: handle.trim(), category: category.trim(), bio: bio.trim(), registeredAt: Date.now() },
        }),
      unregisterCreator: () => set({ creatorProfile: null }),

      createFilter: (name, settings) => {
        const filter: CreatorFilter = { id: uid("filter"), name: name.trim() || "Untitled filter", ...settings, createdAt: Date.now() };
        set((s) => ({ filters: [filter, ...s.filters] }));
        return filter;
      },
      deleteFilter: (id) => set((s) => ({ filters: s.filters.filter((f) => f.id !== id) })),

      addSnap: (dataUrl, filterName, location = "Kathmandu, Nepal", caption = "") => {
        const snap: Snap = {
          id: uid("snap"),
          dataUrl,
          filterName,
          location,
          caption,
          createdAt: Date.now(),
        };
        set((s) => ({ snaps: [snap, ...s.snaps].slice(0, 60) }));
        return snap;
      },
      deleteSnap: (id) => set((s) => ({ snaps: s.snaps.filter((sn) => sn.id !== id) })),

      addToCameraRoll: (dataUrl, caption = "", location = "My Device") => {
        const rollItem: Snap = {
          id: uid("roll"),
          dataUrl,
          filterName: "Camera Roll",
          location,
          caption,
          createdAt: Date.now(),
        };
        set((s) => ({ cameraRoll: [rollItem, ...s.cameraRoll].slice(0, 100) }));
        return rollItem;
      },
      deleteCameraRoll: (id) => set((s) => ({ cameraRoll: s.cameraRoll.filter((r) => r.id !== id) })),

      setLockerPin: (pin) => set({ lockerPin: pin.trim() }),

      unlockLocker: (pin) => {
        const currentPin = get().lockerPin || "1234";
        if (pin.trim() === currentPin) {
          set({ isLockerUnlocked: true });
          return true;
        }
        return false;
      },

      lockLocker: () => set({ isLockerUnlocked: false }),

      moveToLocker: (id, fromTab) => {
        const state = get();
        let target: Snap | undefined;
        if (fromTab === "snaps") {
          target = state.snaps.find((s) => s.id === id);
          if (target) {
            set((s) => ({
              snaps: s.snaps.filter((sn) => sn.id !== id),
              lockerSnaps: [{ ...target!, isLocked: true }, ...s.lockerSnaps],
            }));
          }
        } else {
          target = state.cameraRoll.find((s) => s.id === id);
          if (target) {
            set((s) => ({
              cameraRoll: s.cameraRoll.filter((sn) => sn.id !== id),
              lockerSnaps: [{ ...target!, isLocked: true }, ...s.lockerSnaps],
            }));
          }
        }
      },

      restoreFromLocker: (id) => {
        const target = get().lockerSnaps.find((s) => s.id === id);
        if (target) {
          set((s) => ({
            lockerSnaps: s.lockerSnaps.filter((sn) => sn.id !== id),
            snaps: [{ ...target, isLocked: false }, ...s.snaps],
          }));
        }
      },

      deleteLockerSnap: (id) => set((s) => ({ lockerSnaps: s.lockerSnaps.filter((sn) => sn.id !== id) })),
    }),
    {
      name: "evrry-snap",
      version: 2,
      partialize: (state) => ({
        creatorProfile: state.creatorProfile,
        filters: state.filters,
        snaps: state.snaps,
        cameraRoll: state.cameraRoll,
        lockerSnaps: state.lockerSnaps,
        lockerPin: state.lockerPin,
      }),
    }
  )
);
