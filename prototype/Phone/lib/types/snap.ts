// Camera filters + creator registration (Snap feature). FilterSettings is
// the raw slider values; CreatorFilter is one saved/named instance of it.

export interface FilterSettings {
  brightness: number; // 100 = unchanged
  contrast: number;
  saturation: number;
  hueRotate: number; // degrees, -180..180
  sepia: number; // 0-100
  grayscale: number; // 0-100
}

export interface CreatorFilter extends FilterSettings {
  id: string;
  name: string;
  createdAt: number;
}

export interface CreatorProfile {
  handle: string;
  category: string;
  bio: string;
  registeredAt: number;
}

export interface Snap {
  id: string;
  dataUrl: string;
  filterName: string;
  createdAt: number;
  location?: string;
  caption?: string;
  isLocked?: boolean;
}

export type QrActionType = "fonepay" | "contact" | "restaurant" | "url" | "text";

export interface QrScanResult {
  raw: string;
  type: QrActionType;
  title: string;
  subtitle: string;
  actionLabel: string;
  target?: string;
  amount?: number;
}

export type MemoryTab = "snaps" | "roll" | "locker";

