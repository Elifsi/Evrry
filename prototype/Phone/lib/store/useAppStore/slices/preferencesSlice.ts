import { StateCreator } from "zustand";
import { Preference } from "@/lib/types";
import { AppState, PreferencesSlice } from "../types";

export const createPreferencesSlice: StateCreator<AppState, [], [], PreferencesSlice> = (set) => ({
  preferences: [],
  activeVoicePersona: "ellie",
  selectedLanguage: "ne",

  setActiveVoicePersona: (persona) => set({ activeVoicePersona: persona }),
  setSelectedLanguage: (lang) => set({ selectedLanguage: lang }),

  setPreference: (key, value) =>
    set((s) => {
      const existing = s.preferences.find((p) => p.key === key);
      const updated: Preference = { key, value, updatedAt: Date.now() };
      return {
        preferences: existing ? s.preferences.map((p) => (p.key === key ? updated : p)) : [...s.preferences, updated],
      };
    }),
  deletePreference: (key) => set((s) => ({ preferences: s.preferences.filter((p) => p.key !== key) })),
});
