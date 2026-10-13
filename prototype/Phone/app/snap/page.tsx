"use client";

import { Suspense, useEffect, useRef, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { useSnapStore } from "@/lib/store/useSnapStore";
import { BUILTIN_PRESETS, filterToCss } from "@/lib/filters";
import { FilterSettings, QrScanResult } from "@/lib/types";
import {
  Camera,
  CameraOff,
  Images,
  RefreshCw,
  Zap,
  ZapOff,
  Moon,
  Sparkles,
  QrCode,
  ChevronLeft,
  Music,
  Grid3x3,
  SlidersHorizontal,
} from "lucide-react";
import clsx from "clsx";
import SnapReviewScreen from "@/components/snap/SnapReviewScreen";
import MemoriesScreen from "@/components/snap/MemoriesScreen";
import QrScanCard from "@/components/snap/QrScanCard";
import { detectQrFromCanvas, parseQrPayload, SAMPLE_QR_PRESETS } from "@/lib/camera/qrScanner";

type NamedFilter = FilterSettings & { id: string; name: string };
type Step = "camera" | "review" | "memories";

export default function SnapPage() {
  return (
    <Suspense fallback={null}>
      <SnapPageInner />
    </Suspense>
  );
}

function SnapPageInner() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const forStory = searchParams.get("for") === "story";

  const creatorFilters = useSnapStore((s) => s.filters);
  const snaps = useSnapStore((s) => s.snaps);

  const allFilters: NamedFilter[] = [...BUILTIN_PRESETS, ...creatorFilters];
  const [selectedId, setSelectedId] = useState(allFilters[0].id);
  const selected = allFilters.find((f) => f.id === selectedId) ?? allFilters[0];

  const [permission, setPermission] = useState<"pending" | "granted" | "denied">("pending");
  const [justCaptured, setJustCaptured] = useState(false);
  const [step, setStep] = useState<Step>("camera");
  const [pendingCapture, setPendingCapture] = useState<{ dataUrl: string; filterName: string } | null>(null);
  const [notReadyNudge, setNotReadyNudge] = useState(false);

  // Camera Toolbar States
  const [facingMode, setFacingMode] = useState<"user" | "environment">("user");
  const [flashMode, setFlashMode] = useState<"off" | "on">("off");
  const [nightMode, setNightMode] = useState(false);
  const [showGrid, setShowGrid] = useState(false);
  const [showLenses, setShowLenses] = useState(false);
  const [qrScanningActive, setQrScanningActive] = useState(true);
  const [detectedQr, setDetectedQr] = useState<QrScanResult | null>(null);
  const [showQrDemoMenu, setShowQrDemoMenu] = useState(false);

  // Video recording simulation state
  const [isRecording, setIsRecording] = useState(false);
  const [recordingProgress, setRecordingProgress] = useState(0);

  const videoRef = useRef<HTMLVideoElement>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const qrScanTimerRef = useRef<number | null>(null);

  // Start / restart camera stream with facingMode
  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        if (streamRef.current) {
          streamRef.current.getTracks().forEach((t) => t.stop());
        }
        const stream = await navigator.mediaDevices.getUserMedia({
          video: { facingMode, width: { ideal: 1280 }, height: { ideal: 720 } },
          audio: false,
        });
        if (cancelled) {
          stream.getTracks().forEach((t) => t.stop());
          return;
        }
        streamRef.current = stream;
        if (videoRef.current) videoRef.current.srcObject = stream;
        setPermission("granted");
      } catch {
        if (!cancelled) setPermission("denied");
      }
    })();
    return () => {
      cancelled = true;
      streamRef.current?.getTracks().forEach((t) => t.stop());
    };
  }, [facingMode]);

  // Periodic QR Code Scanner Loop
  useEffect(() => {
    if (!qrScanningActive || step !== "camera") return;

    const interval = window.setInterval(async () => {
      const video = videoRef.current;
      if (!video || video.videoWidth === 0 || detectedQr) return;

      const canvas = document.createElement("canvas");
      canvas.width = Math.min(video.videoWidth, 640);
      canvas.height = Math.min(video.videoHeight, 480);
      const ctx = canvas.getContext("2d");
      if (!ctx) return;
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

      const result = await detectQrFromCanvas(canvas);
      if (result) {
        setDetectedQr(result);
      }
    }, 600);

    qrScanTimerRef.current = interval;
    return () => {
      window.clearInterval(interval);
    };
  }, [qrScanningActive, step, detectedQr]);

  function flipCamera() {
    setFacingMode((prev) => (prev === "user" ? "environment" : "user"));
  }

  function handleCapture() {
    const video = videoRef.current;
    if (!video || video.videoWidth === 0) {
      setNotReadyNudge(true);
      window.setTimeout(() => setNotReadyNudge(false), 900);
      return;
    }
    const canvas = document.createElement("canvas");
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    ctx.filter = filterToCss(selected);
    if (nightMode) {
      ctx.filter += " brightness(125%) contrast(110%)";
    }

    if (facingMode === "user") {
      ctx.translate(canvas.width, 0);
      ctx.scale(-1, 1);
    }
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
    const dataUrl = canvas.toDataURL("image/jpeg", 0.92);

    setJustCaptured(true);
    window.setTimeout(() => setJustCaptured(false), 220);
    setPendingCapture({ dataUrl, filterName: selected.name });
    setStep("review");
  }

  // Shutter Long-Press Video Simulation
  function handleShutterMouseDown() {
    const timer = window.setTimeout(() => {
      setIsRecording(true);
      let p = 0;
      const interval = window.setInterval(() => {
        p += 5;
        setRecordingProgress(p);
        if (p >= 100) {
          window.clearInterval(interval);
          setIsRecording(false);
          setRecordingProgress(0);
          handleCapture();
        }
      }, 100);
    }, 450);

    const handleMouseUp = () => {
      window.clearTimeout(timer);
      setIsRecording(false);
      setRecordingProgress(0);
      window.removeEventListener("mouseup", handleMouseUp);
      window.removeEventListener("touchend", handleMouseUp);
    };
    window.addEventListener("mouseup", handleMouseUp);
    window.addEventListener("touchend", handleMouseUp);
  }

  if (step === "review" && pendingCapture) {
    return (
      <SnapReviewScreen
        dataUrl={pendingCapture.dataUrl}
        filterName={pendingCapture.filterName}
        forStory={forStory}
        onClose={() => {
          setPendingCapture(null);
          setStep("camera");
        }}
      />
    );
  }

  if (step === "memories") {
    return (
      <MemoriesScreen
        onBack={() => setStep("camera")}
        onPick={(dataUrl) => {
          setPendingCapture({ dataUrl, filterName: "Camera Roll" });
          setStep("review");
        }}
      />
    );
  }

  const latestSnap = snaps[0];

  return (
    <div className="fixed inset-0 z-40 bg-black select-none overflow-hidden">
      {/* Camera Permission Denied View */}
      {permission === "denied" && (
        <div className="flex h-full flex-col items-center justify-center gap-3 px-6 text-center text-white/70">
          <CameraOff size={32} className="text-white/40" />
          <p className="text-sm font-semibold">Camera Access Needed</p>
          <p className="text-xs text-white/50">Allow camera access in your browser or device settings to use Evrry Camera.</p>
        </div>
      )}

      {/* Live Video Viewfinder */}
      {permission !== "denied" && (
        <video
          ref={videoRef}
          autoPlay
          playsInline
          muted
          className={clsx(
            "absolute inset-0 h-full w-full object-cover transition duration-150",
            facingMode === "user" && "[transform:scaleX(-1)]"
          )}
          style={{
            filter: nightMode ? `${filterToCss(selected)} brightness(120%) contrast(108%)` : filterToCss(selected),
          }}
        />
      )}

      {/* Grid Alignment Overlay */}
      {showGrid && (
        <div className="pointer-events-none absolute inset-0 grid grid-cols-3 grid-rows-3">
          <div className="border-r border-b border-white/20" />
          <div className="border-r border-b border-white/20" />
          <div className="border-b border-white/20" />
          <div className="border-r border-b border-white/20" />
          <div className="border-r border-b border-white/20" />
          <div className="border-b border-white/20" />
          <div className="border-r border-white/20" />
          <div className="border-r border-white/20" />
          <div />
        </div>
      )}

      {/* Screen Shutter Flash Overlay */}
      {justCaptured && <div className="pointer-events-none absolute inset-0 bg-white opacity-90 transition-opacity" />}

      {/* Center QR Reticle Targeting Frame */}
      {qrScanningActive && !detectedQr && (
        <div className="pointer-events-none absolute inset-0 flex items-center justify-center">
          <div className="relative h-48 w-48 rounded-2xl border-2 border-white/25">
            <div className="absolute -top-1 -left-1 h-5 w-5 border-t-4 border-l-4 border-amber-400 rounded-tl-lg animate-pulse" />
            <div className="absolute -top-1 -right-1 h-5 w-5 border-t-4 border-r-4 border-amber-400 rounded-tr-lg animate-pulse" />
            <div className="absolute -bottom-1 -left-1 h-5 w-5 border-b-4 border-l-4 border-amber-400 rounded-bl-lg animate-pulse" />
            <div className="absolute -bottom-1 -right-1 h-5 w-5 border-b-4 border-r-4 border-amber-400 rounded-br-lg animate-pulse" />
            <p className="absolute -bottom-8 inset-x-0 text-center text-[11px] font-medium text-white/60 tracking-wider">
              Scan QR or Snap
            </p>
          </div>
        </div>
      )}

      {/* Top Header Bar */}
      <div className="absolute inset-x-0 top-0 flex items-center justify-between px-4 pt-5 z-20">
        <button
          onClick={() => router.back()}
          className="flex h-10 w-10 items-center justify-center rounded-full bg-black/40 text-white backdrop-blur-md transition active:scale-90"
          title="Back"
        >
          <ChevronLeft size={22} />
        </button>

        {/* Center Pill: Mode Indicator or Demo QR menu trigger */}
        <div className="relative">
          <button
            onClick={() => setShowQrDemoMenu((prev) => !prev)}
            className="flex items-center gap-1.5 rounded-full bg-black/45 px-3 py-1.5 text-xs font-semibold text-white/95 backdrop-blur-md border border-white/10 active:scale-95"
          >
            <QrCode size={13} className="text-amber-400" />
            <span>{forStory ? "Story Snap" : "Evrry Lens & Scan"}</span>
          </button>

          {/* Quick Demo QR Test Menu */}
          {showQrDemoMenu && (
            <div className="absolute top-10 left-1/2 -translate-x-1/2 w-64 rounded-2xl bg-slate-900/95 p-2 text-white shadow-2xl border border-white/20 backdrop-blur-xl animate-in zoom-in-95 duration-150">
              <p className="px-2.5 py-1 text-[10px] font-bold uppercase tracking-wider text-white/50">Test QR Scanner</p>
              {SAMPLE_QR_PRESETS.map((p) => (
                <button
                  key={p.id}
                  onClick={() => {
                    setDetectedQr(parseQrPayload(p.raw));
                    setShowQrDemoMenu(false);
                  }}
                  className="w-full text-left px-2.5 py-1.5 rounded-xl text-xs font-medium hover:bg-white/10 transition"
                >
                  {p.label}
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Flash Mode Toggle */}
        <button
          onClick={() => setFlashMode((prev) => (prev === "off" ? "on" : "off"))}
          className={clsx(
            "flex h-10 w-10 items-center justify-center rounded-full backdrop-blur-md transition active:scale-90",
            flashMode === "on" ? "bg-amber-400 text-ink" : "bg-black/40 text-white"
          )}
          title="Toggle Flash"
        >
          {flashMode === "on" ? <Zap size={18} /> : <ZapOff size={18} />}
        </button>
      </div>

      {/* Right-Side Vertical Snapchat-Grade Tool Dock */}
      <div className="absolute right-3.5 top-20 flex flex-col items-center gap-3.5 z-20">
        {/* Flip Camera */}
        <button
          onClick={flipCamera}
          className="flex h-10 w-10 items-center justify-center rounded-full bg-black/40 text-white backdrop-blur-md shadow transition active:scale-90 hover:bg-black/60"
          title="Flip camera"
        >
          <RefreshCw size={18} />
        </button>

        {/* Night Mode */}
        <button
          onClick={() => setNightMode((prev) => !prev)}
          className={clsx(
            "flex h-10 w-10 items-center justify-center rounded-full backdrop-blur-md shadow transition active:scale-90",
            nightMode ? "bg-amber-400 text-ink" : "bg-black/40 text-white hover:bg-black/60"
          )}
          title="Night mode"
        >
          <Moon size={18} />
        </button>

        {/* Grid Overlay */}
        <button
          onClick={() => setShowGrid((prev) => !prev)}
          className={clsx(
            "flex h-10 w-10 items-center justify-center rounded-full backdrop-blur-md shadow transition active:scale-90",
            showGrid ? "bg-white text-ink" : "bg-black/40 text-white hover:bg-black/60"
          )}
          title="Toggle grid"
        >
          <Grid3x3 size={18} />
        </button>

        {/* QR Scanner Toggle */}
        <button
          onClick={() => setQrScanningActive((prev) => !prev)}
          className={clsx(
            "flex h-10 w-10 items-center justify-center rounded-full backdrop-blur-md shadow transition active:scale-90",
            qrScanningActive ? "bg-amber-400 text-ink" : "bg-black/40 text-white hover:bg-black/60"
          )}
          title="QR Scanner mode"
        >
          <QrCode size={18} />
        </button>

        {/* Lenses Filter Toggle */}
        <button
          onClick={() => setShowLenses((prev) => !prev)}
          className={clsx(
            "flex h-10 w-10 items-center justify-center rounded-full backdrop-blur-md shadow transition active:scale-90",
            showLenses ? "bg-brand text-white" : "bg-black/40 text-white hover:bg-black/60"
          )}
          title="Toggle lenses"
        >
          <Sparkles size={18} />
        </button>
      </div>

      {/* Floating QR Code Scanner Detection Pill */}
      {detectedQr && (
        <div className="absolute inset-x-4 bottom-32 z-30">
          <QrScanCard result={detectedQr} onDismiss={() => setDetectedQr(null)} />
        </div>
      )}

      {/* Bottom Shutter & Controls Dock */}
      <div className="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/85 via-black/40 to-transparent pb-8 pt-12 z-20">
        {/* Horizontal Lenses / Filters Carousel */}
        {showLenses && (
          <div className="flex gap-2 overflow-x-auto px-4 pb-4 scrollbar-none animate-in fade-in slide-in-from-bottom-2 duration-150">
            {allFilters.map((f) => (
              <button
                key={f.id}
                onClick={() => setSelectedId(f.id)}
                className={clsx(
                  "shrink-0 rounded-full border px-3.5 py-1.5 text-xs font-semibold backdrop-blur-md transition active:scale-95",
                  selectedId === f.id
                    ? "border-white bg-white text-ink shadow-lg"
                    : "border-white/30 bg-black/40 text-white/90 hover:bg-black/60"
                )}
              >
                {f.name}
              </button>
            ))}
          </div>
        )}

        {/* Primary Shutter Row */}
        <div className="flex items-center justify-around px-8">
          {/* Memories Shortcut */}
          <button
            onClick={() => setStep("memories")}
            className="flex flex-col items-center gap-1 text-white transition active:scale-90"
            title="Open Memories"
          >
            <div className="relative flex h-12 w-12 items-center justify-center overflow-hidden rounded-xl border border-white/30 bg-black/40 shadow backdrop-blur-md">
              {latestSnap ? (
                // eslint-disable-next-line @next/next/no-img-element
                <img src={latestSnap.dataUrl} alt="Memories" className="h-full w-full object-cover" />
              ) : (
                <Images size={20} className="text-white/80" />
              )}
            </div>
            <span className="text-[10px] font-semibold text-white/80">Memories</span>
          </button>

          {/* Central Snapchat-Grade Shutter Button */}
          <div className="relative flex items-center justify-center">
            {/* Recording Progress Ring */}
            {isRecording && (
              <svg className="absolute -inset-2 h-20 w-20 -rotate-90">
                <circle
                  cx="40"
                  cy="40"
                  r="36"
                  stroke="currentColor"
                  strokeWidth="4"
                  className="text-rose-500 transition-all duration-100"
                  fill="transparent"
                  strokeDasharray="226"
                  strokeDashoffset={226 - (226 * recordingProgress) / 100}
                />
              </svg>
            )}

            <button
              onClick={handleCapture}
              onMouseDown={handleShutterMouseDown}
              onTouchStart={handleShutterMouseDown}
              disabled={permission !== "granted"}
              className={clsx(
                "flex h-18 w-18 items-center justify-center rounded-full border-4 shadow-2xl transition active:scale-95 disabled:opacity-40",
                isRecording ? "border-rose-500 bg-rose-500 scale-105" : "border-white bg-white/20"
              )}
              title="Capture Photo (or Hold for Video)"
            >
              <div
                className={clsx(
                  "h-14 w-14 rounded-full transition-transform",
                  isRecording ? "bg-white scale-75 rounded-md" : "bg-white"
                )}
              />
            </button>
          </div>

          {/* Lenses Quick Toggle Shortcut */}
          <button
            onClick={() => setShowLenses((prev) => !prev)}
            className="flex flex-col items-center gap-1 text-white transition active:scale-90"
            title="Lenses & Filters"
          >
            <div className="flex h-12 w-12 items-center justify-center rounded-xl border border-white/30 bg-black/40 shadow backdrop-blur-md">
              <Sparkles size={20} className="text-amber-400" />
            </div>
            <span className="text-[10px] font-semibold text-white/80">Lenses</span>
          </button>
        </div>
      </div>
    </div>
  );
}
