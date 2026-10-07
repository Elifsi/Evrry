import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
    "./lib/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "var(--background)",
        foreground: "var(--foreground)",
        // Canonical EVRRY Brand Palette (UI_DESIGN_SYSTEM.md)
        brand: {
          50: "#eff6ff",
          100: "#dbeafe",
          200: "#bfdbfe",
          300: "#93c5fd", // brand.accentLight
          400: "#60a5fa",
          500: "#2f82f6", // brand.secondary
          600: "#005eff", // brand.primary (Canonical EVRRY Electric Blue)
          700: "#004ed6",
          800: "#003fae",
          900: "#0646a7", // brand.primaryDeep
          DEFAULT: "#005eff",
        },
        ink: {
          DEFAULT: "#0b172a", // text.primary (Deep Navy Slate)
          muted: "#64758b",   // text.secondary
          subtle: "#94a3b8",  // text.disabled
        },
        surface: {
          subtle: "#f8fafc",  // background.subtle
          muted: "#e2e8f0",   // surface.muted
          card: "#ffffff",    // background.main
          soft: "#eff6ff",    // surface.primarySoft
        },
        border: {
          subtle: "#e2e8f0",
          DEFAULT: "#cbd5e1", // border.default
          strong: "#64758b",  // border.strong
        },
        // Strict Semantic Status Colors
        status: {
          success: "#10b981",
          successText: "#047857",
          error: "#ef4444",
          errorText: "#b91c1c",
          warning: "#f59e0b",
          warningText: "#b45309",
        },
      },
      fontFamily: {
        sans: ["Inter", "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "sans-serif"],
      },
      boxShadow: {
        card: "0 1px 3px 0 rgba(11, 23, 42, 0.05), 0 1px 2px -1px rgba(11, 23, 42, 0.05)",
        raised: "0 4px 16px rgba(11, 23, 42, 0.08)",
      },
    },
  },
  plugins: [],
};

export default config;
