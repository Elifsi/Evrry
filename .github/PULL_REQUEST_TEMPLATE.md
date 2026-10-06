## Summary

<!-- One-line description of what this PR changes. -->

## Motivation

<!-- Why is this change needed? Link to the issue it closes if applicable. -->

Closes #

## Changes

<!-- A brief description of what changed and why. -->

## Component(s) affected

- [ ] `prototype/Phone/` — Mobile phone experience prototype & smartphone UI golden master
- [ ] `apps/consumer/` — Consumer applications (Android / iOS)
- [ ] `apps/partner/` — Partner applications (Android / iOS)
- [ ] `apps/web/` — Production web applications (Consumer / Partner / Admin / Common)
- [ ] `supabase/` — Shared backend (Migrations / Functions / RLS)
- [ ] `docs/` — Architecture / Product / Development documentation
- [ ] `.github/` or `scripts/` — CI / tooling

## Pre-PR checklist

### Prototype (`prototype/Phone/`)

If you modified code in `prototype/Phone/`, run:

```bash
cd prototype/Phone
npm run lint
npx tsc --noEmit
npm run test
npm run build
```

- [ ] `npm run lint` — passes
- [ ] `npx tsc --noEmit` — passes
- [ ] `npm run test` — passes
- [ ] `npm run build` — passes
- [ ] `npm run test:e2e` — passes (if E2E affected; requires Chromium)
- [ ] Manual check for voice/ride/chat/calling changes (if applicable)

## Security Verification

- [ ] No Supabase `service_role` keys are committed or referenced in client applications.
- [ ] No payment gateway secrets (eSewa, Khalti, Fonepay, Card) are committed or placed in client bundles.
- [ ] All new database tables have Row Level Security (RLS) enabled.

## Notes for reviewer

<!-- Anything the reviewer should know: tradeoffs, known gaps, follow-up work. -->
