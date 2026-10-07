"use client";

import { useState } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import {
  Search,
  CheckCircle2,
  AlertTriangle,
  Smartphone,
  Phone,
  CreditCard,
  Ban,
} from "lucide-react";
import { recordAdminAuditLog } from "@/lib/admin/api";

interface AdminUserItem {
  id: string;
  name: string;
  phone: string;
  role: "consumer" | "driver" | "merchant";
  status: "active" | "suspended" | "banned" | "cod_disabled";
  registeredDate: string;
  deviceUuid: string;
  totalOrders: number;
  codViolationCount: number;
  strikeReason?: string;
}

const sampleUsers: AdminUserItem[] = [
  {
    id: "usr-01",
    name: "Aayush Maharjan",
    phone: "+977 9841998877",
    role: "consumer",
    status: "active",
    registeredDate: "2026-08-14",
    deviceUuid: "a8f3-88bc-9102-ef01",
    totalOrders: 38,
    codViolationCount: 0,
  },
  {
    id: "usr-02",
    name: "Ramesh Bogati",
    phone: "+977 9801445566",
    role: "consumer",
    status: "cod_disabled",
    registeredDate: "2026-09-02",
    deviceUuid: "77cd-12da-44ef-0092",
    totalOrders: 12,
    codViolationCount: 3,
    strikeReason: "Rejected 3 consecutive Cash-on-Delivery food orders after driver arrived at doorstep.",
  },
  {
    id: "usr-03",
    name: "Sandip Thapa",
    phone: "+977 9860112244",
    role: "driver",
    status: "suspended",
    registeredDate: "2026-07-20",
    deviceUuid: "33ba-99ef-8812-7711",
    totalOrders: 142,
    codViolationCount: 1,
    strikeReason: "Failed to remit cash-in-hand within 48 hours. Temporarily suspended from ride dispatch.",
  },
  {
    id: "usr-04",
    name: "Unknown Bad Actor",
    phone: "+977 9812990011",
    role: "consumer",
    status: "banned",
    registeredDate: "2026-10-01",
    deviceUuid: "0000-ffaa-3321-bbee",
    totalOrders: 2,
    codViolationCount: 4,
    strikeReason: "Abusive chat messages to restaurant partner and repeated fake delivery orders.",
  },
];

export default function UsersModerationPage() {
  const [users, setUsers] = useState<AdminUserItem[]>(sampleUsers);
  const [searchQuery, setSearchQuery] = useState("");
  const [filterRole, setFilterRole] = useState<string>("all");
  const [selectedUser, setSelectedUser] = useState<AdminUserItem | null>(null);
  const [actionAlert, setActionAlert] = useState<string | null>(null);

  const filteredUsers = users.filter((u) => {
    const matchesSearch =
      u.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.phone.includes(searchQuery) ||
      u.deviceUuid.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesRole = filterRole === "all" || u.role === filterRole;
    return matchesSearch && matchesRole;
  });

  const handleUpdateStatus = async (
    userId: string,
    newStatus: "active" | "suspended" | "banned" | "cod_disabled",
    reason: string
  ) => {
    setUsers((prev) =>
      prev.map((u) =>
        u.id === userId ? { ...u, status: newStatus, strikeReason: reason } : u
      )
    );

    await recordAdminAuditLog({
      adminName: "Suman Adhikari (VP Operations)",
      adminEmail: "suman@evrry.com.np",
      actionType: `USER_${newStatus.toUpperCase()}`,
      details: `User ${userId} status changed to ${newStatus}. Reason: ${reason}`,
      ipAddress: "103.10.29.14 (Kathmandu)",
    });

    setSelectedUser(null);
    setActionAlert(`User ${userId} status changed to ${newStatus.toUpperCase()}.`);
    setTimeout(() => setActionAlert(null), 4000);
  };

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      <AdminHeader
        title="User & Driver Moderation Hub"
        subtitle="Fraud Prevention • Account Actions • Device & SIM Blacklisting"
      />

      <div className="p-8 space-y-6 flex-1 max-w-6xl">
        {/* Alert Feedback */}
        {actionAlert && (
          <div className="bg-brand-50 border border-brand-200 p-4 rounded-xl text-brand-900 text-xs flex items-center justify-between shadow-sm">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-brand-600" />
              <span className="font-semibold">{actionAlert}</span>
            </div>
            <span className="text-[11px] font-mono text-brand-600 bg-white px-2 py-0.5 rounded border border-brand-200">
              Logged to admin_audit_logs
            </span>
          </div>
        )}

        {/* Search & Filter Toolbar */}
        <div className="bg-white p-5 rounded-2xl border border-border shadow-card flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="relative flex-1 max-w-md">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
            <input
              type="text"
              placeholder="Search by Name, Nepali Phone (+977), or Device UUID..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2 text-xs border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-600 text-ink bg-surface-subtle"
            />
          </div>

          <div className="flex items-center gap-2 text-xs">
            <span className="text-ink-muted font-semibold">Filter:</span>
            {["all", "consumer", "driver", "merchant"].map((role) => (
              <button
                key={role}
                onClick={() => setFilterRole(role)}
                className={`px-3 py-1.5 rounded-lg font-semibold uppercase text-[10px] transition-all border ${
                  filterRole === role
                    ? "bg-slate-900 text-white border-slate-900 shadow-sm"
                    : "bg-surface-subtle text-ink-muted border-border hover:bg-slate-100"
                }`}
              >
                {role}
              </button>
            ))}
          </div>
        </div>

        {/* Users Table */}
        <div className="bg-white rounded-2xl border border-border shadow-card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="border-b border-border text-ink-subtle font-semibold uppercase text-[10px] bg-surface-subtle">
                  <th className="py-3 px-4">User Details</th>
                  <th className="py-3 px-4">Phone & Device</th>
                  <th className="py-3 px-4">Role</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4">Orders / Activity</th>
                  <th className="py-3 px-4 text-right">Moderation Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {filteredUsers.map((user) => (
                  <tr key={user.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="py-3.5 px-4">
                      <div className="font-semibold text-ink">{user.name}</div>
                      <div className="text-[10px] font-mono text-ink-subtle">{user.id}</div>
                    </td>

                    <td className="py-3.5 px-4">
                      <div className="font-mono text-ink font-medium flex items-center gap-1.5">
                        <Phone className="w-3 h-3 text-slate-400" />
                        {user.phone}
                      </div>
                      <div className="text-[10px] font-mono text-ink-subtle flex items-center gap-1 mt-0.5">
                        <Smartphone className="w-3 h-3 text-slate-400" />
                        UUID: {user.deviceUuid}
                      </div>
                    </td>

                    <td className="py-3.5 px-4">
                      <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-surface-subtle text-ink uppercase font-mono border border-border">
                        {user.role}
                      </span>
                    </td>

                    <td className="py-3.5 px-4">
                      {user.status === "active" ? (
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-emerald-100 text-emerald-800">
                          Active
                        </span>
                      ) : user.status === "cod_disabled" ? (
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-amber-100 text-amber-800">
                          COD Disabled
                        </span>
                      ) : user.status === "suspended" ? (
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-orange-100 text-orange-800">
                          Suspended
                        </span>
                      ) : (
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-rose-100 text-rose-800">
                          Banned
                        </span>
                      )}
                    </td>

                    <td className="py-3.5 px-4 text-ink-muted">
                      <div>{user.totalOrders} total completed</div>
                      {user.codViolationCount > 0 && (
                        <div className="text-[11px] text-rose-600 font-semibold">
                          {user.codViolationCount} rejected COD deliveries
                        </div>
                      )}
                    </td>

                    <td className="py-3.5 px-4 text-right">
                      <button
                        onClick={() => setSelectedUser(user)}
                        className="px-3 py-1.5 bg-surface-subtle hover:bg-slate-100 text-ink font-semibold text-xs rounded-lg transition-colors border border-border"
                      >
                        Manage & Moderate
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Moderation Action Modal */}
        {selectedUser && (
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl max-w-md w-full p-6 space-y-4 shadow-2xl border border-border text-xs">
              <div className="flex items-center justify-between border-b border-border pb-3">
                <h3 className="text-sm font-bold text-ink">
                  Moderate Account: {selectedUser.name}
                </h3>
                <span className="font-mono text-ink-subtle">{selectedUser.phone}</span>
              </div>

              <div className="space-y-2">
                <button
                  onClick={() =>
                    handleUpdateStatus(
                      selectedUser.id,
                      "active",
                      "Account restored to normal standing by Superadmin."
                    )
                  }
                  className="w-full text-left p-3 rounded-xl border border-emerald-200 bg-emerald-50/50 hover:bg-emerald-100/70 text-emerald-900 font-semibold transition-all flex items-center justify-between"
                >
                  <div>
                    <div className="flex items-center gap-1.5 font-bold">
                      <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                      Restore Account (Active)
                    </div>
                    <div className="text-[11px] font-normal text-emerald-700 mt-0.5">
                      Clears restrictions; enables COD, ordering, and dispatching.
                    </div>
                  </div>
                </button>

                <button
                  onClick={() =>
                    handleUpdateStatus(
                      selectedUser.id,
                      "cod_disabled",
                      "Disabled Cash-on-Delivery due to repeated fake order rejections."
                    )
                  }
                  className="w-full text-left p-3 rounded-xl border border-amber-200 bg-amber-50/50 hover:bg-amber-100/70 text-amber-900 font-semibold transition-all flex items-center justify-between"
                >
                  <div>
                    <div className="flex items-center gap-1.5 font-bold">
                      <CreditCard className="w-4 h-4 text-amber-600" />
                      Disable Cash-on-Delivery (COD)
                    </div>
                    <div className="text-[11px] font-normal text-amber-700 mt-0.5">
                      Forces prepayment via eSewa, Khalti, or Cards for all orders.
                    </div>
                  </div>
                </button>

                <button
                  onClick={() =>
                    handleUpdateStatus(
                      selectedUser.id,
                      "suspended",
                      "Temporary suspension for policy investigation."
                    )
                  }
                  className="w-full text-left p-3 rounded-xl border border-orange-200 bg-orange-50/50 hover:bg-orange-100/70 text-orange-900 font-semibold transition-all flex items-center justify-between"
                >
                  <div>
                    <div className="flex items-center gap-1.5 font-bold">
                      <AlertTriangle className="w-4 h-4 text-orange-600" />
                      Temporary 7-Day Suspension
                    </div>
                    <div className="text-[11px] font-normal text-orange-700 mt-0.5">
                      Temporarily blocks access to all booking and order services.
                    </div>
                  </div>
                </button>

                <button
                  onClick={() =>
                    handleUpdateStatus(
                      selectedUser.id,
                      "banned",
                      "Permanent ban and Device UUID blacklisted for fraud."
                    )
                  }
                  className="w-full text-left p-3 rounded-xl border border-rose-200 bg-rose-50/50 hover:bg-rose-100/70 text-rose-900 font-semibold transition-all flex items-center justify-between"
                >
                  <div>
                    <div className="flex items-center gap-1.5 font-bold">
                      <Ban className="w-4 h-4 text-rose-600" />
                      Permanent Ban & Blacklist Device
                    </div>
                    <div className="text-[11px] font-normal text-rose-700 mt-0.5">
                      Permanently revokes account and blacklists device UUID.
                    </div>
                  </div>
                </button>
              </div>

              <div className="flex justify-end pt-2 border-t border-border">
                <button
                  onClick={() => setSelectedUser(null)}
                  className="px-4 py-1.5 bg-surface-subtle hover:bg-slate-100 text-ink rounded-lg font-semibold border border-border"
                >
                  Close
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
