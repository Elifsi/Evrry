import React from "react";
import clsx from "clsx";

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "default" | "secondary" | "outline" | "ghost" | "destructive" | "success";
  size?: "sm" | "default" | "lg" | "icon";
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant = "default", size = "default", ...props }, ref) => {
    const variantStyles = {
      default: "bg-teal-600 text-white hover:bg-teal-700 shadow-sm",
      secondary: "bg-slate-100 text-slate-800 hover:bg-slate-200 border border-slate-200",
      outline: "bg-white text-slate-700 hover:bg-slate-50 border border-slate-200/80 shadow-sm",
      ghost: "text-slate-700 hover:bg-slate-100",
      destructive: "bg-rose-600 text-white hover:bg-rose-700 shadow-sm",
      success: "bg-emerald-600 text-white hover:bg-emerald-700 shadow-sm",
    };

    const sizeStyles = {
      sm: "h-8 px-3 text-xs rounded-lg",
      default: "h-9 px-4 py-2 text-xs rounded-xl",
      lg: "h-10 px-5 text-sm rounded-xl",
      icon: "h-8 w-8 rounded-lg p-0 flex items-center justify-center",
    };

    return (
      <button
        ref={ref}
        className={clsx(
          "inline-flex items-center justify-center gap-1.5 font-semibold transition-all disabled:opacity-50 disabled:pointer-events-none select-none focus:outline-none focus:ring-2 focus:ring-teal-500/20",
          variantStyles[variant],
          sizeStyles[size],
          className
        )}
        {...props}
      />
    );
  }
);

Button.displayName = "Button";
