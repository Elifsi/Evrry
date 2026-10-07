import type { Metadata } from "next";
import "./globals.css";
import { AdminSidebar } from "@/components/AdminSidebar";
import { AdminAuthProvider } from "@/lib/admin/auth";

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
      <body className="bg-surface-subtle text-ink h-screen overflow-hidden flex antialiased">
        <AdminAuthProvider>
          <AdminSidebar />
          <main className="flex-1 flex flex-col min-w-0 h-screen overflow-y-auto">
            {children}
          </main>
        </AdminAuthProvider>
      </body>
    </html>
  );
}
