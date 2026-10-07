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
      <body className="bg-slate-50/75 text-slate-900 h-screen overflow-hidden flex antialiased">
        <AdminAuthProvider>
          <AdminSidebar />
          <div className="flex-1 flex flex-col min-w-0 h-screen overflow-hidden">
            {children}
          </div>
        </AdminAuthProvider>
      </body>
    </html>
  );
}
