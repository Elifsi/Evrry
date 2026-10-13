import { StateCreator } from "zustand";
import { AppState } from "../types";
import { SavedAddress } from "@/lib/types/addresses";

function uid(prefix: string) {
  return `${prefix}_${Date.now().toString(36)}_${Math.random().toString(36).slice(2, 7)}`;
}

export interface AddressBookSlice {
  savedAddresses: SavedAddress[];

  /** Upserts an address. If an address with tag "home"/"office" already exists, it is replaced. */
  saveAddress: (
    input: Omit<SavedAddress, "id" | "createdAt" | "updatedAt">
  ) => SavedAddress;

  /** Updates mutable fields (name, label, lat, lng) of an existing address. */
  updateAddress: (
    id: string,
    changes: Partial<Pick<SavedAddress, "name" | "label" | "lat" | "lng" | "tag">>
  ) => void;

  /** Permanently deletes a saved address. */
  deleteAddress: (id: string) => void;

  /** Returns the saved Home address, if any. */
  homeAddress: () => SavedAddress | null;

  /** Returns the saved Office address, if any. */
  officeAddress: () => SavedAddress | null;
}

export function createAddressBookSlice(
  ...a: Parameters<StateCreator<AppState, [], [], AddressBookSlice>>
): AddressBookSlice {
  const [set, get] = a;
  return {
    savedAddresses: [],

    saveAddress: (input) => {
      const now = Date.now();

      // For home/office tags, replace any existing address with the same tag
      if (input.tag === "home" || input.tag === "office") {
        const existing = get().savedAddresses.find((a) => a.tag === input.tag);
        if (existing) {
          const updated: SavedAddress = { ...existing, ...input, updatedAt: now };
          set((s) => ({
            savedAddresses: s.savedAddresses.map((a) => (a.id === existing.id ? updated : a)),
          }));
          return updated;
        }
      }

      const address: SavedAddress = {
        ...input,
        id: uid("addr"),
        createdAt: now,
        updatedAt: now,
      };
      set((s) => ({ savedAddresses: [...s.savedAddresses, address] }));
      return address;
    },

    updateAddress: (id, changes) => {
      const now = Date.now();
      set((s) => ({
        savedAddresses: s.savedAddresses.map((a) =>
          a.id === id ? { ...a, ...changes, updatedAt: now } : a
        ),
      }));
    },

    deleteAddress: (id) => {
      set((s) => ({
        savedAddresses: s.savedAddresses.filter((a) => a.id !== id),
      }));
    },

    homeAddress: () => get().savedAddresses.find((a) => a.tag === "home") ?? null,

    officeAddress: () => get().savedAddresses.find((a) => a.tag === "office") ?? null,
  };
}
