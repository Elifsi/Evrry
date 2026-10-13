import { NextRequest, NextResponse } from "next/server";

export const runtime = "nodejs";

export async function GET(req: NextRequest) {
  const { searchParams } = new URL(req.url);
  const text = searchParams.get("text") || "";
  const lang = searchParams.get("lang") || "ne"; // 'ne' | 'hi' | 'en' | 'mai'

  if (!text.trim()) {
    return new NextResponse("Text required", { status: 400 });
  }

  // Map to target TTS locale:
  // Nepali ('ne'), Hindi ('hi'), Maithili ('mai' -> Devanagari phonetics 'hi'/'ne'), English ('en')
  const hasDevanagari = /[\u0900-\u097F]/.test(text);
  let targetLocale = hasDevanagari ? "ne" : "en";
  if (lang === "hi" || lang === "mai") targetLocale = "hi";
  else if (lang === "ne") targetLocale = "ne";
  else if (lang === "en" && !hasDevanagari) targetLocale = "en";

  try {
    const cleanText = text.slice(0, 500); // chunk length limit for audio stream
    const ttsUrl = `https://translate.google.com/translate_tts?ie=UTF-8&q=${encodeURIComponent(cleanText)}&tl=${targetLocale}&client=tw-ob`;

    const res = await fetch(ttsUrl, {
      headers: {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
      },
    });

    if (!res.ok) {
      return new NextResponse("TTS fetch failed", { status: res.status });
    }

    const audioBuffer = await res.arrayBuffer();

    return new NextResponse(audioBuffer, {
      status: 200,
      headers: {
        "Content-Type": "audio/mpeg",
        "Cache-Control": "public, max-age=86400",
      },
    });
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : String(err);
    return new NextResponse(`TTS generation error: ${message}`, { status: 500 });
  }
}
