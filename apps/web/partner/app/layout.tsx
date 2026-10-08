import type { Metadata } from "next";
import "./globals.css";
import { PartnerSidebar } from "@/components/PartnerSidebar";

export const metadata: Metadata = {
  title: "EVRRY Partner Operations & Venue Dashboard",
  description: "Merchant & Host Operations Portal for Dining, Groceries, Stays, and Rentals",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className="bg-slate-50 text-slate-900 h-screen overflow-hidden flex antialiased">
        <PartnerSidebar />
        <div className="flex-1 flex flex-col min-w-0 h-screen overflow-hidden">
          {children}
        </div>
      </body>
    </html>
  );
}
