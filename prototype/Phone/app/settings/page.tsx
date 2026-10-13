"use client";

import { useState } from "react";
import Link from "next/link";
import {
  ChevronLeft,
  Volume2,
  Check,
  Sparkles,
  RotateCcw,
  Languages,
  Brain,
  Sliders,
  ShieldCheck,
  Smartphone,
  ExternalLink,
} from "lucide-react";
import { useAppStore } from "@/lib/store/useAppStore";
import {
  getAllVoicePersonas,
  SUPPORTED_LANGUAGES,
  SupportedLanguage,
  VoicePersonaId,
  getPersonaGreeting,
  getVoicePersona,
} from "@/lib/ai/personas";
import { playNaturalIndicSpeech } from "@/lib/ai/ttsAudio";

export default function SettingsPage() {
  const activeVoicePersona = useAppStore((s) => s.activeVoicePersona);
  const setActiveVoicePersona = useAppStore((s) => s.setActiveVoicePersona);
  const selectedLanguage = useAppStore((s) => s.selectedLanguage);
  const setSelectedLanguage = useAppStore((s) => s.setSelectedLanguage);
  const personalizationEnabled = useAppStore((s) => s.personalizationEnabled);
  const setPersonalizationEnabled = useAppStore((s) => s.setPersonalizationEnabled);
  const memory = useAppStore((s) => s.memory);
  const clearMemory = useAppStore((s) => s.clearMemory);
  const clearChat = useAppStore((s) => s.clearChat);

  const [previewingPersona, setPreviewingPersona] = useState<string | null>(null);
  const [pitch, setPitch] = useState(1.0);
  const [speed, setSpeed] = useState(1.0);
  const [actionNotice, setActionNotice] = useState<string | null>(null);

  const personas = getAllVoicePersonas();

  const handlePreviewVoice = (personaId: VoicePersonaId) => {
    if (typeof window === "undefined") return;

    setPreviewingPersona(personaId);
    const persona = getVoicePersona(personaId);
    const greetingText = `Hi, my name is ${persona.name}. ${persona.sampleGreetings.ne || persona.sampleGreeting}`;

    playNaturalIndicSpeech(greetingText, "auto", () => {
      setPreviewingPersona(null);
    });
  };

  const handleSelectPersona = (id: VoicePersonaId) => {
    setActiveVoicePersona(id);
    const persona = getVoicePersona(id);
    setActionNotice(`Active AI Agent set to ${persona.name}`);
    handlePreviewVoice(id);
    setTimeout(() => setActionNotice(null), 3000);
  };

  return (
    <div className="px-5 pt-6 pb-28 space-y-5">
      {/* Top Header */}
      <div className="flex items-center justify-between">
        <Link
          href="/"
          className="inline-flex items-center gap-1 text-xs font-semibold text-ink/60 hover:text-ink transition-colors"
        >
          <ChevronLeft size={16} /> Home
        </Link>
        <span className="text-[10px] font-bold uppercase tracking-wider text-brand bg-brandSoft px-2.5 py-0.5 rounded-full">
          Super App Settings
        </span>
      </div>

      <div>
        <h1 className="text-2xl font-black tracking-tight text-ink">Settings & AI Models</h1>
        <p className="text-xs text-ink/50">
          Customize your voice assistant persona (Ellie, Rony, Jenny, Suka) and intelligence preferences
        </p>
      </div>

      {actionNotice && (
        <div className="bg-brand text-white px-4 py-2.5 rounded-2xl text-xs font-bold shadow-sm animate-in fade-in flex items-center justify-between">
          <span>{actionNotice}</span>
          <Check size={14} />
        </div>
      )}

      {/* SECTION 1: 4 AI PERSONA AGENTS */}
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1.5">
            <Sparkles size={14} className="text-brand" />
            <h2 className="text-xs font-black uppercase tracking-wider text-ink/80">
              Choose AI Agent Model
            </h2>
          </div>
          <span className="text-[10px] font-semibold text-ink/40">4 Distinct Personalities</span>
        </div>

        <div className="space-y-2.5">
          {personas.map((p) => {
            const isSelected = activeVoicePersona === p.id;
            const isSpeaking = previewingPersona === p.id;

            return (
              <div
                key={p.id}
                onClick={() => handleSelectPersona(p.id)}
                className={`p-3.5 rounded-2xl border transition-all cursor-pointer shadow-2xs ${
                  isSelected
                    ? "bg-white border-brand ring-2 ring-brand/15 shadow-sm"
                    : "bg-white border-black/8 hover:border-black/20"
                }`}
              >
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-start gap-3">
                    <div
                      className="w-11 h-11 rounded-2xl flex items-center justify-center text-xl shrink-0 shadow-2xs"
                      style={{ backgroundColor: `${p.color}15`, color: p.color }}
                    >
                      {p.avatarEmoji}
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <h3 className="text-sm font-bold text-ink">{p.name}</h3>
                        <span className="text-[10px] text-ink/40 font-medium capitalize">
                          • {p.gender}
                        </span>
                        {isSelected && (
                          <span className="text-[9px] font-black uppercase tracking-wider bg-brand text-white px-2 py-0.2 rounded-full">
                            Active
                          </span>
                        )}
                      </div>
                      <p className="text-xs text-ink/60 mt-0.5">{p.tagline}</p>
                      <p className="text-[11px] text-ink/40 mt-1 italic leading-tight">
                        &quot;Hi, my name is {p.name}. {p.sampleGreetings.ne}&quot;
                      </p>
                    </div>
                  </div>

                  <button
                    type="button"
                    onClick={(e) => {
                      e.stopPropagation();
                      handlePreviewVoice(p.id);
                    }}
                    className={`w-9 h-9 rounded-xl flex items-center justify-center shrink-0 transition-colors ${
                      isSpeaking
                        ? "bg-brand text-white animate-pulse"
                        : "bg-slate-100 hover:bg-slate-200 text-ink/70"
                    }`}
                    title={`Listen to ${p.name}`}
                  >
                    <Volume2 size={16} />
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* SECTION 2: AUTOMATIC MULTILINGUAL INTELLIGENCE */}
      <div className="space-y-3 pt-2 border-t border-black/5">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1.5">
            <Languages size={14} className="text-brand" />
            <h2 className="text-xs font-black uppercase tracking-wider text-ink/80">
              Automatic Multilingual Recognition
            </h2>
          </div>
          <span className="text-[10px] font-semibold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-100">
            Active · Real-time
          </span>
        </div>

        <div className="rounded-2xl border border-black/8 bg-white p-4">
          <p className="text-xs text-ink/70 leading-relaxed">
            The AI automatically detects and answers in whatever language you speak or write. No manual language switching required:
          </p>
          <div className="grid grid-cols-2 gap-2 mt-3">
            {SUPPORTED_LANGUAGES.map((lang) => (
              <div key={lang.code} className="p-2.5 rounded-xl bg-slate-50 border border-slate-100">
                <p className="text-xs font-bold text-ink">{lang.nativeLabel} ({lang.label})</p>
                <p className="text-[10px] text-ink/50 mt-0.5">{lang.description}</p>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* SECTION 3: VOICE DYNAMICS (PITCH & CADENCE) */}
      <div className="space-y-3 pt-2 border-t border-black/5">
        <div className="flex items-center gap-1.5">
          <Sliders size={14} className="text-brand" />
          <h2 className="text-xs font-black uppercase tracking-wider text-ink/80">
            Voice Cadence & Pitch
          </h2>
        </div>

        <div className="bg-white rounded-2xl border border-black/8 p-4 space-y-4">
          <div className="space-y-1.5">
            <div className="flex justify-between text-xs font-semibold text-ink">
              <span>Speaking Speed</span>
              <span className="font-mono text-ink/50">{speed.toFixed(2)}x</span>
            </div>
            <input
              type="range"
              min="0.8"
              max="1.3"
              step="0.05"
              value={speed}
              onChange={(e) => setSpeed(parseFloat(e.target.value))}
              className="w-full accent-brand"
            />
          </div>

          <div className="space-y-1.5">
            <div className="flex justify-between text-xs font-semibold text-ink">
              <span>Voice Pitch</span>
              <span className="font-mono text-ink/50">{pitch.toFixed(2)}x</span>
            </div>
            <input
              type="range"
              min="0.8"
              max="1.2"
              step="0.05"
              value={pitch}
              onChange={(e) => setPitch(parseFloat(e.target.value))}
              className="w-full accent-brand"
            />
          </div>
        </div>
      </div>

      {/* SECTION 4: AI MEMORY & INTELLIGENCE */}
      <div className="space-y-3 pt-2 border-t border-black/5">
        <div className="flex items-center gap-1.5">
          <Brain size={14} className="text-brand" />
          <h2 className="text-xs font-black uppercase tracking-wider text-ink/80">
            Memory & Personalization
          </h2>
        </div>

        <div className="bg-white rounded-2xl border border-black/8 p-4 space-y-3">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-bold text-ink">Contextual Personalization</p>
              <p className="text-[11px] text-ink/50">
                Remember your diet, address preferences & favorite kitchens
              </p>
            </div>
            <input
              type="checkbox"
              checked={personalizationEnabled}
              onChange={(e) => setPersonalizationEnabled(e.target.checked)}
              className="w-5 h-5 accent-brand rounded-md cursor-pointer"
            />
          </div>

          <div className="flex items-center justify-between pt-2 border-t border-black/5 text-xs text-ink/60">
            <span>Remembered Facts: {memory.length}</span>
            {memory.length > 0 && (
              <button
                type="button"
                onClick={clearMemory}
                className="text-rose-600 hover:underline font-semibold"
              >
                Clear All Facts
              </button>
            )}
          </div>
        </div>
      </div>

      {/* SECTION 5: APP ACTIONS & PARTNER SWITCHER */}
      <div className="space-y-2 pt-2 border-t border-black/5">
        <button
          type="button"
          onClick={() => {
            clearChat();
            setActionNotice("Conversation history cleared");
            setTimeout(() => setActionNotice(null), 3000);
          }}
          className="w-full py-2.5 rounded-xl border border-rose-200 bg-rose-50 hover:bg-rose-100 text-rose-700 text-xs font-bold transition-colors flex items-center justify-center gap-1.5"
        >
          <RotateCcw size={14} />
          <span>Clear Chat History</span>
        </button>

        <Link
          href="/profile"
          className="w-full py-2.5 rounded-xl border border-black/10 bg-slate-50 hover:bg-slate-100 text-ink text-xs font-bold transition-colors flex items-center justify-center gap-1.5"
        >
          <ShieldCheck size={14} className="text-emerald-600" />
          <span>Legal, E2EE Backups &amp; Account Privacy</span>
        </Link>

        <Link
          href="/partner"
          className="w-full py-3 rounded-2xl bg-slate-900 hover:bg-black text-white text-xs font-bold transition-all shadow-sm flex items-center justify-center gap-2"
        >
          <Smartphone size={15} />
          <span>Switch to Partner Mobile App Prototype</span>
          <ExternalLink size={13} className="opacity-60" />
        </Link>
      </div>
    </div>
  );
}
