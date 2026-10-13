import { describe, it, expect } from "vitest";
import { parseQrPayload } from "./qrScanner";

describe("QR Scanner Payload Parser", () => {
  it("correctly identifies and parses a Nepal Fonepay QR payload", () => {
    const raw = "nepalqr://fonepay?merchant=Himalayan+Java+Coffee&amount=450";
    const result = parseQrPayload(raw);

    expect(result.type).toBe("fonepay");
    expect(result.title).toBe("Fonepay Merchant QR");
    expect(result.subtitle).toContain("Himalayan Java Coffee");
    expect(result.subtitle).toContain("450");
    expect(result.actionLabel).toBe("Pay with Fonepay");
    expect(result.amount).toBe(450);
  });

  it("identifies EMVCo standardized QR as Fonepay / Nepal QR", () => {
    const raw = "00020101021226580010com.fonepay5204599953035245802NP";
    const result = parseQrPayload(raw);

    expect(result.type).toBe("fonepay");
    expect(result.actionLabel).toBe("Pay with Fonepay");
  });

  it("correctly identifies and parses an Evrry Friend Contact QR", () => {
    const raw = "evrry://user/@aarav";
    const result = parseQrPayload(raw);

    expect(result.type).toBe("contact");
    expect(result.title).toBe("Evrry User · @aarav");
    expect(result.subtitle).toContain("Aarav");
    expect(result.actionLabel).toBe("Open Chat");
    expect(result.target).toBe("/chat/contact-aarav");
  });

  it("parses an Evrry dining table QR code", () => {
    const raw = "evrry://restaurant/roadhouse-cafe?table=12";
    const result = parseQrPayload(raw);

    expect(result.type).toBe("restaurant");
    expect(result.title).toBe("Table Service Order");
    expect(result.subtitle).toContain("Table #12");
    expect(result.actionLabel).toBe("View Digital Menu");
    expect(result.target).toBe("/explore/food/roadhouse-cafe");
  });

  it("parses a standard website URL", () => {
    const raw = "https://elifsi.com/evrry";
    const result = parseQrPayload(raw);

    expect(result.type).toBe("url");
    expect(result.title).toBe("Website Link");
    expect(result.subtitle).toBe("elifsi.com/evrry");
    expect(result.actionLabel).toBe("Open in Browser");
    expect(result.target).toBe("https://elifsi.com/evrry");
  });

  it("handles arbitrary text payload gracefully", () => {
    const raw = "Sample Wi-Fi password: EverestPassword123";
    const result = parseQrPayload(raw);

    expect(result.type).toBe("text");
    expect(result.title).toBe("QR Code Content");
    expect(result.actionLabel).toBe("Copy Text");
  });
});
