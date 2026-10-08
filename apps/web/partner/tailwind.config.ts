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
        // Canonical EVRRY Core Brand Tokens (UI_DESIGN_SYSTEM.md)
        brand: {
          50: "#eff6ff",
          100: "#dbeafe",
          200: "#bfdbfe",
          300: "#93c5fd",
          400: "#60a5fa",
          500: "#2f82f6", // secondary brand blue
          600: "#005eff", // CANONICAL EVRRY ELECTRIC BLUE
          700: "#004ed6",
          800: "#003fae",
          900: "#0646a7",
          DEFAULT: "#005eff",
          soft: "#eff6ff",
        },
        ink: {
          DEFAULT: "#0b172a", // text.primary deep navy slate
          muted: "#64758b",   // text.secondary
          subtle: "#94a3b8",  // text.disabled
        },
        surface: {
          subtle: "#f8fafc",  // background canvas
          card: "#ffffff",    // card background
          muted: "#f1f5f9",
          soft: "#eff6ff",
        },
        border: {
          subtle: "#f1f5f9",
          DEFAULT: "#e2e8f0",
          strong: "#cbd5e1",
        },
        // Semantic Operations Status
        status: {
          success: "#10b981",
          successText: "#047857",
          warning: "#f59e0b",
          warningText: "#b45309",
          error: "#ef4444",
          errorText: "#b91c1c",
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
