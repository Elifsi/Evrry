import type { Metadata } from "next";
import "./globals.css";
import { AdminSidebar } from "@/components/AdminSidebar";

export const metadata: Metadata = {
  title: "EVRRY Superadmin Operations & Control Center",
  description: "Zero-SQL Administrative Governance Platform for EVRRY Super App Ecosystem",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className="bg-surface-subtle text-ink min-h-screen flex antialiased">
        <AdminSidebar />
        <main className="flex-1 flex flex-col min-w-0 overflow-y-auto">
          {children}
        </main>
      </body>
    </html>
  );
}
