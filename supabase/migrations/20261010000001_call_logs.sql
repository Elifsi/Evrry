-- =============================================================================
-- 0017 / 20261010000001 — Call Logs & VoIP Session History
-- Supports WebRTC audio & video call history for direct users, delivery
-- partners, and ride captains with full Row Level Security (RLS).
-- =============================================================================

CREATE TABLE public.call_logs (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  caller_id         UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
  callee_id         UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
  chat_id           UUID REFERENCES public.chats(id) ON DELETE SET NULL,
  call_type         TEXT NOT NULL CHECK (call_type IN ('AUDIO', 'VIDEO')),
  status            TEXT NOT NULL CHECK (status IN ('COMPLETED', 'MISSED', 'REJECTED', 'FAILED', 'CANCELLED')),
  duration_seconds  INTEGER NOT NULL DEFAULT 0,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  CHECK (caller_id <> callee_id)
);

CREATE INDEX call_logs_caller_idx ON public.call_logs (caller_id, created_at DESC);
CREATE INDEX call_logs_callee_idx ON public.call_logs (callee_id, created_at DESC);
CREATE INDEX call_logs_chat_idx   ON public.call_logs (chat_id);

ALTER TABLE public.call_logs ENABLE ROW LEVEL SECURITY;

-- Caller and callee can view their own call records
CREATE POLICY call_logs_select ON public.call_logs
  FOR SELECT TO authenticated
  USING (caller_id = auth.uid() OR callee_id = auth.uid());

-- Authenticated users can log calls initiated by them
CREATE POLICY call_logs_insert ON public.call_logs
  FOR INSERT TO authenticated
  WITH CHECK (caller_id = auth.uid());

-- Helper function to log a call securely
CREATE OR REPLACE FUNCTION public.log_call(
  p_callee UUID,
  p_chat UUID,
  p_type TEXT,
  p_status TEXT,
  p_duration INTEGER DEFAULT 0
)
RETURNS UUID
LANGUAGE plpgsql SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
  v_me UUID := auth.uid();
  v_log_id UUID;
BEGIN
  IF v_me IS NULL THEN
    RAISE EXCEPTION 'authentication required' USING ERRCODE = '28000';
  END IF;

  INSERT INTO public.call_logs (caller_id, callee_id, chat_id, call_type, status, duration_seconds)
  VALUES (v_me, p_callee, p_chat, p_type, p_status, GREATEST(0, p_duration))
  RETURNING id INTO v_log_id;

  RETURN v_log_id;
END;
$$;
