"use client";

import { useState, useEffect, useMemo } from "react";
import { AdminHeader } from "@/components/AdminHeader";
import { DataTable } from "@/components/shared/DataTable";
import { ColumnDef } from "@tanstack/react-table";
import {
  Search,
  CheckCircle2,
  AlertTriangle,
  Smartphone,
  Phone,
  CreditCard,
  Ban,
  Loader2,
  MoreVertical,
  ShieldAlert,
  UserCheck,
  UserX,
  Eye,
  History,
  Lock,
  Unlock,
} from "lucide-react";
import { fetchAdminUsers, updateAdminUserStatus } from "@/lib/admin/api";
import { AdminUserItem } from "@/lib/admin/types";

export default function UsersModerationPage() {
  const [users, setUsers] = useState<AdminUserItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [actionAlert, setActionAlert] = useState<string | null>(null);

  // User Action Modal
  const [selectedUserForAction, setSelectedUserForAction] = useState<AdminUserItem | null>(null);
  const [modalActionType, setModalActionType] = useState<"suspend" | "ban" | "view" | null>(null);
  const [actionReason, setActionReason] = useState("");

  const loadUsers = async () => {
    setLoading(true);
    const data = await fetchAdminUsers();
    setUsers(data);
    setLoading(false);
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const handleToggleCod = async (user: AdminUserItem) => {
    const nextStatus = user.codEnabled ? "cod_disabled" : "active";
    const reason = user.codEnabled
      ? "COD blocked due to repeated order returns / fraud safety."
      : "COD privileges restored by Superadmin.";

    setUsers((prev) =>
      prev.map((u) =>
        u.id === user.id ? { ...u, codEnabled: !u.codEnabled, status: nextStatus } : u
      )
    );

    await updateAdminUserStatus(user.id, nextStatus, reason);
    setActionAlert(
      `COD privilege ${user.codEnabled ? "DISABLED" : "ENABLED"} for ${user.name}.`
    );
    setTimeout(() => setActionAlert(null), 4000);
  };

  const handleExecuteModalAction = async () => {
    if (!selectedUserForAction || !modalActionType) return;

    if (modalActionType === "suspend") {
      const reason = actionReason.trim() || "7-Day temporary cooldown applied by compliance.";
      await updateAdminUserStatus(selectedUserForAction.id, "suspended", reason);
      setUsers((prev) =>
        prev.map((u) => (u.id === selectedUserForAction.id ? { ...u, status: "suspended" } : u))
      );
      setActionAlert(`User ${selectedUserForAction.name} suspended for 7 days.`);
    } else if (modalActionType === "ban") {
      const reason = actionReason.trim() || "Permanent account termination for policy breach.";
      await updateAdminUserStatus(selectedUserForAction.id, "banned", reason);
      setUsers((prev) =>
        prev.map((u) => (u.id === selectedUserForAction.id ? { ...u, status: "banned" } : u))
      );
      setActionAlert(`User ${selectedUserForAction.name} permanently banned.`);
    }

    setSelectedUserForAction(null);
    setModalActionType(null);
    setActionReason("");
    setTimeout(() => setActionAlert(null), 4000);
  };

  // TanStack Table Columns
  const columns: ColumnDef<AdminUserItem>[] = useMemo(
    () => [
      {
        accessorKey: "name",
        header: "User / Partner",
        cell: ({ row }) => (
          <div>
            <div className="font-bold text-slate-900">{row.original.name}</div>
            <div className="text-[10px] font-mono text-slate-400">{row.original.id}</div>
          </div>
        ),
      },
      {
        accessorKey: "role",
        header: "Role",
        cell: ({ row }) => (
          <span className="px-2 py-0.5 rounded-md text-[10px] font-bold uppercase font-mono bg-slate-100 text-slate-700 border border-slate-200">
            {row.original.role}
          </span>
        ),
      },
      {
        accessorKey: "phone",
        header: "Contact & Device",
        cell: ({ row }) => (
          <div className="font-mono text-xs text-slate-600">
            <div className="flex items-center gap-1">
              <Phone className="w-3 h-3 text-slate-400" />
              <span>{row.original.phone}</span>
            </div>
            <div className="text-[10px] text-slate-400 flex items-center gap-1 mt-0.5">
              <Smartphone className="w-3 h-3 text-slate-400" />
              <span>UUID: {row.original.deviceUuid}</span>
            </div>
          </div>
        ),
      },
      {
        accessorKey: "totalOrders",
        header: "Orders / Activity",
        cell: ({ row }) => (
          <span className="font-mono text-xs font-medium text-slate-700">
            {row.original.totalOrders} completed
          </span>
        ),
      },
      {
        accessorKey: "codEnabled",
        header: "COD Privilege",
        cell: ({ row }) => {
          const isEnabled = row.original.codEnabled !== false;
          return (
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={() => handleToggleCod(row.original)}
                className={`relative inline-flex h-5 w-9 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                  isEnabled ? "bg-brand-600" : "bg-slate-300"
                }`}
                title={isEnabled ? "Click to Disable COD" : "Click to Enable COD"}
              >
                <span
                  className={`pointer-events-none inline-block h-4 w-4 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                    isEnabled ? "translate-x-4" : "translate-x-0"
                  }`}
                />
              </button>
              <span
                className={`text-[11px] font-bold ${
                  isEnabled ? "text-emerald-700" : "text-rose-600"
                }`}
              >
                {isEnabled ? "Enabled" : "Blocked"}
              </span>
            </div>
          );
        },
      },
      {
        accessorKey: "status",
        header: "Account Status",
        cell: ({ row }) => {
          const status = row.original.status;
          return (
            <span
              className={`px-2 py-0.5 rounded-full text-[10px] font-bold uppercase ${
                status === "active"
                  ? "bg-emerald-50 text-emerald-700 border border-emerald-200"
                  : status === "suspended"
                  ? "bg-amber-50 text-amber-700 border border-amber-200"
                  : status === "banned"
                  ? "bg-rose-50 text-rose-700 border border-rose-200"
                  : "bg-slate-100 text-slate-700 border border-slate-200"
              }`}
            >
              {status.replace("_", " ")}
            </span>
          );
        },
      },
      {
        id: "actions",
        header: "Actions",
        cell: ({ row }) => {
          const user = row.original;
          return (
            <div className="flex items-center gap-1.5 justify-end">
              <button
                type="button"
                onClick={() => {
                  setSelectedUserForAction(user);
                  setModalActionType("view");
                }}
                className="p-1 rounded-lg border border-slate-200 hover:bg-slate-50 text-slate-600 transition-colors"
                title="View Profile Snapshot"
              >
                <Eye className="w-3.5 h-3.5" />
              </button>

              <button
                type="button"
                onClick={() => {
                  setSelectedUserForAction(user);
                  setModalActionType("suspend");
                  setActionReason("");
                }}
                className="p-1 rounded-lg border border-amber-200 bg-amber-50 hover:bg-amber-100 text-amber-700 transition-colors"
                title="7-Day Temporary Suspension"
              >
                <ShieldAlert className="w-3.5 h-3.5" />
              </button>

              <button
                type="button"
                onClick={() => {
                  setSelectedUserForAction(user);
                  setModalActionType("ban");
                  setActionReason("");
                }}
                className="p-1 rounded-lg border border-rose-200 bg-rose-50 hover:bg-rose-100 text-rose-700 transition-colors"
                title="Permanent Account Ban"
              >
                <Ban className="w-3.5 h-3.5" />
              </button>
            </div>
          );
        },
      },
    ],
    []
  );

  return (
    <div className="flex-1 flex flex-col h-screen overflow-y-auto bg-slate-50/75">
      <AdminHeader
        title="User & Partner Moderation Directory"
        subtitle="Fraud Prevention • Account Status • COD Privilege Switches & Blacklisting"
        onRefresh={loadUsers}
        isRefreshing={loading}
      />

      <div className="p-6 md:p-8 space-y-6 flex-1 max-w-[1500px] w-full mx-auto">
        {/* Feedback Alert */}
        {actionAlert && (
          <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-xl text-emerald-800 text-xs flex items-center justify-between shadow-sm animate-fade-in">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
              <span className="font-semibold">{actionAlert}</span>
            </div>
            <span className="text-[11px] font-mono text-emerald-700 bg-white px-2.5 py-1 rounded-md border border-emerald-200">
              RPC Synced
            </span>
          </div>
        )}

        {/* Directory Card */}
        <div className="space-y-4">
          <DataTable
            columns={columns}
            data={users}
            searchPlaceholder="Search by name, Nepali phone (+977), or device UUID..."
            facetedFilters={{
              columnId: "role",
              title: "Role",
              options: [
                { label: "Consumers", value: "consumer", count: users.filter((u) => u.role === "consumer").length },
                { label: "Riders", value: "rider", count: users.filter((u) => u.role === "rider").length },
                { label: "Drivers", value: "driver", count: users.filter((u) => u.role === "driver").length },
                { label: "Merchants", value: "merchant", count: users.filter((u) => u.role === "merchant").length },
              ],
            }}
          />
        </div>
      </div>

      {/* Action Modal (View, Suspend, Ban) */}
      {selectedUserForAction && modalActionType && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-slate-200 max-w-md w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-sm font-bold text-slate-900">
                {modalActionType === "view"
                  ? `User Profile: ${selectedUserForAction.name}`
                  : modalActionType === "suspend"
                  ? `7-Day Suspension for ${selectedUserForAction.name}`
                  : `Permanent Ban: ${selectedUserForAction.name}`}
              </h3>
              <button
                onClick={() => {
                  setSelectedUserForAction(null);
                  setModalActionType(null);
                }}
                className="text-slate-400 hover:text-slate-700 text-sm"
              >
                ✕
              </button>
            </div>

            {modalActionType === "view" ? (
              <div className="space-y-3 text-xs">
                <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 space-y-2 font-mono">
                  <div className="flex justify-between">
                    <span className="text-slate-500">User ID:</span>
                    <span className="font-bold text-slate-900">{selectedUserForAction.id}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-500">Phone:</span>
                    <span className="font-bold text-slate-900">{selectedUserForAction.phone}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-500">Device UUID:</span>
                    <span className="text-slate-800">{selectedUserForAction.deviceUuid}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-500">Role:</span>
                    <span className="font-bold text-brand-700 uppercase">{selectedUserForAction.role}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-500">Registered:</span>
                    <span className="text-slate-800">{selectedUserForAction.registeredDate}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-500">COD Privilege:</span>
                    <span className={selectedUserForAction.codEnabled ? "text-emerald-700 font-bold" : "text-rose-600 font-bold"}>
                      {selectedUserForAction.codEnabled ? "Enabled" : "Blocked"}
                    </span>
                  </div>
                </div>

                <div className="flex justify-end pt-2">
                  <button
                    onClick={() => {
                      setSelectedUserForAction(null);
                      setModalActionType(null);
                    }}
                    className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-xl"
                  >
                    Close
                  </button>
                </div>
              </div>
            ) : (
              <div className="space-y-3 text-xs">
                <p className="text-slate-500">
                  {modalActionType === "suspend"
                    ? "Applying a 7-day cooldown prevents this user from placing orders or accepting trips. The account unlocks automatically after 7 days."
                    : "Permanent termination revokes all auth tokens, blocks the device UUID and phone number across the Evrry ecosystem."}
                </p>

                <div>
                  <label className="block font-semibold text-slate-800 mb-1">
                    Audit Justification & Policy Reason:
                  </label>
                  <textarea
                    rows={3}
                    required
                    value={actionReason}
                    onChange={(e) => setActionReason(e.target.value)}
                    placeholder="e.g. Excessive COD delivery refusals resulting in vendor loss."
                    className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500/20"
                  />
                </div>

                <div className="flex items-center justify-end gap-2.5 pt-2 border-t border-slate-100">
                  <button
                    onClick={() => {
                      setSelectedUserForAction(null);
                      setModalActionType(null);
                    }}
                    className="px-4 py-2 font-semibold text-slate-500 hover:text-slate-800"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={handleExecuteModalAction}
                    className={`px-5 py-2 font-bold text-white rounded-xl shadow-sm ${
                      modalActionType === "suspend"
                        ? "bg-amber-600 hover:bg-amber-700"
                        : "bg-rose-600 hover:bg-rose-700"
                    }`}
                  >
                    {modalActionType === "suspend" ? "Confirm 7-Day Suspension" : "Confirm Permanent Ban"}
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
