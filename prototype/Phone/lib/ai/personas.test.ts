import { describe, it, expect } from 'vitest';
import {
  getAllVoicePersonas,
  getVoicePersona,
  VOICE_PERSONAS,
  VoicePersonaId,
} from './personas';

describe('AI Voice Personas (Ellie, Rony, Jenny, Suka)', () => {
  it('registers exactly four distinct voice personas', () => {
    const personas = getAllVoicePersonas();
    expect(personas).toHaveLength(4);
    const ids = personas.map((p) => p.id);
    expect(ids).toEqual(['ellie', 'rony', 'jenny', 'suka']);
  });

  it('correctly configures Ellie as the flagship energetic female concierge', () => {
    const ellie = getVoicePersona('ellie');
    expect(ellie.name).toBe('Ellie');
    expect(ellie.gender).toBe('female');
    expect(ellie.pitch).toBeGreaterThan(0);
    expect(ellie.speed).toBeGreaterThan(1.0);
    expect(ellie.sampleGreeting).toContain('Ellie here');
    expect(ellie.systemInstruction).toContain('energetic');
  });

  it('correctly configures Rony as the deep authoritative male concierge', () => {
    const rony = getVoicePersona('rony');
    expect(rony.name).toBe('Rony');
    expect(rony.gender).toBe('male');
    expect(rony.pitch).toBeLessThan(0); // Deep baritone pitch
    expect(rony.speed).toBeLessThan(1.0); // Measured cadence
    expect(rony.sampleGreeting).toContain('Rony');
    expect(rony.systemInstruction).toContain('authoritative');
  });

  it('correctly configures Jenny as the sweet cheerful female concierge', () => {
    const jenny = getVoicePersona('jenny');
    expect(jenny.name).toBe('Jenny');
    expect(jenny.gender).toBe('female');
    expect(jenny.pitch).toBeGreaterThan(0);
    expect(jenny.speed).toBe(1.0);
    expect(jenny.sampleGreeting).toContain('Jenny');
    expect(jenny.systemInstruction).toContain('cheerful');
  });

  it('correctly configures Suka as the calm and soothing male concierge', () => {
    const suka = getVoicePersona('suka');
    expect(suka.name).toBe('Suka');
    expect(suka.gender).toBe('male');
    expect(suka.pitch).toBeLessThan(0); // Mellow pitch
    expect(suka.speed).toBeLessThan(1.0); // Relaxed gentle pace
    expect(suka.sampleGreeting).toContain('Suka');
    expect(suka.sampleGreeting).toContain('Take a breath');
    expect(suka.systemInstruction).toContain('calm');
  });

  it('gracefully falls back to Ellie when persona is invalid or missing', () => {
    expect(getVoicePersona(null).id).toBe('ellie');
    expect(getVoicePersona(undefined).id).toBe('ellie');
    expect(getVoicePersona('unknown_voice').id).toBe('ellie');
    expect(getVoicePersona('eli').id).toBe('ellie');
    expect(getVoicePersona('  SUKA  ').id).toBe('suka');
  });

  it('ensures each persona defines a valid TTS voice code and non-empty tagline', () => {
    const all = getAllVoicePersonas();
    for (const p of all) {
      expect(p.ttsVoiceCode).toMatch(/^ne_NP-/);
      expect(p.tagline.length).toBeGreaterThan(15);
      expect(p.sampleGreeting.length).toBeGreaterThan(20);
    }
  });
});
