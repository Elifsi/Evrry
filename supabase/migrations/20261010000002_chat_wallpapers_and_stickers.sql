-- =============================================================================
-- 0022 / 20261010000002 — Chat Wallpapers, Stickers & Rich Message Types
-- Adds per-chat wallpaper persistence, sticker URL support, and expands
-- message_type to cover WhatsApp-grade stickers, animated emojis, and location.
-- =============================================================================

-- 1. Add wallpaper_url column to public.chats
ALTER TABLE public.chats
  ADD COLUMN IF NOT EXISTS wallpaper_url TEXT;

-- 2. Add sticker_url column to public.messages for CDN/Lottie sticker assets
ALTER TABLE public.messages
  ADD COLUMN IF NOT EXISTS sticker_url TEXT;

-- 3. Update message_type check constraint to include all modern rich media types
ALTER TABLE public.messages
  DROP CONSTRAINT IF EXISTS messages_message_type_check;

ALTER TABLE public.messages
  ADD CONSTRAINT messages_message_type_check
  CHECK (message_type IN (
    'TEXT',
    'IMAGE',
    'VOICE_NOTE',
    'LIVE_RIDE_SHARE',
    'STICKER',
    'ANIMATED_EMOJI',
    'LIVE_LOCATION',
    'GIFT_ENVELOPE',
    'CALL_LOG'
  ));

-- 4. Secure RPC to update chat wallpaper for members
CREATE OR REPLACE FUNCTION public.set_chat_wallpaper(
  p_chat_id UUID,
  p_wallpaper_url TEXT
)
RETURNS VOID
LANGUAGE plpgsql SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
  IF NOT public.is_chat_member(p_chat_id) THEN
    RAISE EXCEPTION 'not a member of this chat' USING ERRCODE = '42501';
  END IF;

  UPDATE public.chats
  SET wallpaper_url = p_wallpaper_url
  WHERE id = p_chat_id;
END;
$$;
