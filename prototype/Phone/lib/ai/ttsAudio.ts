/**
 * High-Fidelity Indic Neural Audio Playback
 * Replaces robotic OS browser speech synthesis for Nepali ('ne'), Hindi ('hi'), and Maithili ('mai')
 * with native South Asian neural acoustic models.
 */

let activeAudio: HTMLAudioElement | null = null;

export function stopNaturalIndicSpeech() {
  if (activeAudio) {
    try {
      activeAudio.pause();
      activeAudio.currentTime = 0;
    } catch {}
    activeAudio = null;
  }
  if (typeof window !== "undefined" && "speechSynthesis" in window) {
    window.speechSynthesis.cancel();
  }
}

export function playNaturalIndicSpeech(
  text: string,
  lang: string = "auto",
  onEnd?: () => void,
  onError?: () => void
): boolean {
  if (typeof window === "undefined") return false;

  stopNaturalIndicSpeech();

  // Auto-detect Devanagari script (Nepali, Hindi, Maithili)
  const hasDevanagari = /[\u0900-\u097F]/.test(text);
  const isIndic = hasDevanagari || lang === "ne" || lang === "hi" || lang === "mai";
  const targetLang = lang !== "auto" ? lang : (hasDevanagari ? "ne" : "en");

  if (isIndic) {
    const audioUrl = `/api/ai/tts?text=${encodeURIComponent(text)}&lang=${encodeURIComponent(targetLang)}`;
    const audio = new Audio(audioUrl);
    activeAudio = audio;

    audio.onended = () => {
      activeAudio = null;
      if (onEnd) onEnd();
    };

    audio.onerror = () => {
      activeAudio = null;
      // Graceful fallback to browser speech synthesis if network fails
      fallbackBrowserTts(text, targetLang, onEnd);
    };

    audio.play().catch(() => {
      fallbackBrowserTts(text, targetLang, onEnd);
    });

    return true;
  }

  // Fallback for English or non-Indic
  fallbackBrowserTts(text, targetLang, onEnd);
  return true;
}

function fallbackBrowserTts(text: string, lang: string, onEnd?: () => void) {
  if (typeof window === "undefined" || !("speechSynthesis" in window)) {
    if (onEnd) onEnd();
    return;
  }
  window.speechSynthesis.cancel();
  const utter = new SpeechSynthesisUtterance(text);
  utter.rate = 1.0;
  utter.onend = () => onEnd?.();
  utter.onerror = () => onEnd?.();
  window.speechSynthesis.speak(utter);
}
