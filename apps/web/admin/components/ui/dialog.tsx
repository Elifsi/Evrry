"use client";

import React from "react";
import clsx from "clsx";
import { X } from "lucide-react";

interface DialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  children: React.ReactNode;
}

export function Dialog({ open, onOpenChange, children }: DialogProps) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity"
        onClick={() => onOpenChange(false)}
      />
      {/* Modal Dialog Content */}
      <div className="relative z-50 w-full max-w-lg bg-white rounded-2xl border border-slate-200/80 p-6 shadow-2xl animate-in fade-in zoom-in-95 duration-150">
        {children}
      </div>
    </div>
  );
}

export function DialogHeader({
  className,
  children,
}: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div className={clsx("flex flex-col space-y-1.5 pb-3 border-b border-slate-100", className)}>
      {children}
    </div>
  );
}

export function DialogTitle({
  className,
  children,
}: React.HTMLAttributes<HTMLHeadingElement>) {
  return (
    <h3 className={clsx("text-base font-bold text-slate-900 tracking-tight", className)}>
      {children}
    </h3>
  );
}

export function DialogDescription({
  className,
  children,
}: React.HTMLAttributes<HTMLParagraphElement>) {
  return (
    <p className={clsx("text-xs text-slate-500", className)}>
      {children}
    </p>
  );
}

export function DialogFooter({
  className,
  children,
}: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div className={clsx("flex items-center justify-end gap-2.5 pt-4 border-t border-slate-100 mt-4", className)}>
      {children}
    </div>
  );
}
