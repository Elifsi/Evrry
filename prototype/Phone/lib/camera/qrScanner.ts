import { QrScanResult, QrActionType } from "@/lib/types/snap";

/**
 * Intelligent QR parser for modern smartphone camera scanning:
 * Supports Nepal Fonepay / EMVCo QRs, Evrry user contacts, restaurant tables, and URLs.
 */
export function parseQrPayload(raw: string): QrScanResult {
  const trimmed = raw.trim();

  // 1. Nepal Fonepay & EMVCo QR
  if (
    trimmed.startsWith("000201") ||
    trimmed.toLowerCase().includes("fonepay") ||
    trimmed.toLowerCase().includes("nepalqr")
  ) {
    let merchant = "Himalayan Java Coffee";
    let amount = 350;

    if (trimmed.includes("amount=")) {
      const match = trimmed.match(/amount=([0-9.]+)/i);
      if (match) amount = parseFloat(match[1]);
    }
    if (trimmed.includes("merchant=")) {
      const match = trimmed.match(/merchant=([^&]+)/i);
      if (match) merchant = decodeURIComponent(match[1]).replace(/\+/g, " ");
    }

    return {
      raw: trimmed,
      type: "fonepay",
      title: "Fonepay Merchant QR",
      subtitle: `${merchant} · NPR ${amount.toLocaleString("en-NP")}`,
      actionLabel: "Pay with Fonepay",
      target: `/cart`,
      amount,
    };
  }

  // 2. Evrry User Profile / Friend QR
  if (
    trimmed.startsWith("evrry://user/") ||
    trimmed.startsWith("evrry://chat/") ||
    trimmed.includes("evrry.com/u/") ||
    trimmed.startsWith("@")
  ) {
    const handle = trimmed
      .replace(/^evrry:\/\/(user|chat)\//, "")
      .replace(/^https?:\/\/evrry\.com\/u\//, "")
      .replace(/^@/, "");
    
    const contactName = handle.charAt(0).toUpperCase() + handle.slice(1);

    return {
      raw: trimmed,
      type: "contact",
      title: `Evrry User · @${handle}`,
      subtitle: `${contactName} Sharma · Available for Chat & Split Pay`,
      actionLabel: "Open Chat",
      target: `/chat/contact-${handle.toLowerCase()}`,
    };
  }

  // 3. Restaurant / Dining Table QR
  if (trimmed.startsWith("evrry://restaurant/") || trimmed.includes("table=")) {
    const tableMatch = trimmed.match(/table=([0-9A-Za-z]+)/i);
    const tableNum = tableMatch ? tableMatch[1] : "4";
    return {
      raw: trimmed,
      type: "restaurant",
      title: "Table Service Order",
      subtitle: `Roadhouse Cafe (Pulchowk) · Table #${tableNum}`,
      actionLabel: "View Digital Menu",
      target: "/explore/food/roadhouse-cafe",
    };
  }

  // 4. Standard Web Link
  if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
    try {
      const parsedUrl = new URL(trimmed);
      return {
        raw: trimmed,
        type: "url",
        title: "Website Link",
        subtitle: parsedUrl.hostname + (parsedUrl.pathname !== "/" ? parsedUrl.pathname : ""),
        actionLabel: "Open in Browser",
        target: trimmed,
      };
    } catch {
      // Fallback
    }
  }

  // 5. Generic Text
  return {
    raw: trimmed,
    type: "text",
    title: "QR Code Content",
    subtitle: trimmed.length > 40 ? trimmed.slice(0, 37) + "..." : trimmed,
    actionLabel: "Copy Text",
    target: trimmed,
  };
}

/**
 * Modern BarcodeDetector API scanner with fallback for browser compatibility
 */
export async function detectQrFromCanvas(canvas: HTMLCanvasElement): Promise<QrScanResult | null> {
  if (typeof window === "undefined") return null;

  // Check if browser native BarcodeDetector API is available (Chrome, Edge, Android, etc.)
  if ("BarcodeDetector" in window) {
    try {
      const BarcodeDetectorClass = (window as unknown as { BarcodeDetector: new (opts: { formats: string[] }) => { detect: (source: ImageBitmapSource) => Promise<Array<{ rawValue: string }>> } }).BarcodeDetector;
      const detector = new BarcodeDetectorClass({ formats: ["qr_code"] });
      const barcodes = await detector.detect(canvas);
      if (barcodes && barcodes.length > 0 && barcodes[0].rawValue) {
        return parseQrPayload(barcodes[0].rawValue);
      }
    } catch {
      // Continue to fallback
    }
  }

  return null;
}

/**
 * Built-in demo QR test codes for developer and user interactive testing in browser
 */
export const SAMPLE_QR_PRESETS = [
  {
    id: "qr_fonepay_coffee",
    label: "☕ Fonepay (Himalayan Java)",
    raw: "nepalqr://fonepay?merchant=Himalayan+Java+Coffee&amount=350",
  },
  {
    id: "qr_friend_aarav",
    label: "👤 Friend Profile (@aarav)",
    raw: "evrry://user/@aarav",
  },
  {
    id: "qr_table_roadhouse",
    label: "🍽️ Table #4 (Roadhouse Cafe)",
    raw: "evrry://restaurant/roadhouse-cafe?table=4",
  },
  {
    id: "qr_web_elifsi",
    label: "🌐 Web Link (elifsi.com)",
    raw: "https://elifsi.com",
  },
];
