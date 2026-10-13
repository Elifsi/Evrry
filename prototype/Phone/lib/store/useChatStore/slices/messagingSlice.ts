import { StateCreator } from "zustand";
import { transport } from "../transport";
import { ChatState, MessagingSlice } from "../types";
import { appendMessage, attemptDeliver, updateMessage, uid } from "../internal";

export const createMessagingSlice: StateCreator<ChatState, [], [], MessagingSlice> = (set, get) => ({
  messagesByContact: {},
  unreadByContact: {},
  wallpaperByContact: {},

  sendMessage: async (contactId, text, imageDataUrl, audio, meta) => {
    const trimmed = text.trim();
    // Allow sticker messages that have no text/image/audio
    if (!trimmed && !imageDataUrl && !audio && !meta) return;
    const msgId = uid("msg");
    appendMessage(set, contactId, {
      id: msgId,
      direction: "out",
      text: trimmed,
      type: meta?.type,
      stickerUrl: meta?.stickerUrl,
      imageDataUrl,
      audioDataUrl: audio?.dataUrl,
      audioDurationMs: audio?.durationMs,
      locationMeta: meta?.locationMeta,
      envelopeMeta: meta?.envelopeMeta,
      at: Date.now(),
      status: "sending",
    });
    const contact = get().contacts.find((c) => c.id === contactId);
    if (contact?.isMock) {
      // Not a real peer — no relay round trip (a fake publicKeyJwk would
      // fail deriveSharedKey anyway). Simulate a delivery.
      window.setTimeout(() => updateMessage(set, contactId, msgId, { status: "delivered" }), 500);
      return;
    }
    await attemptDeliver(set, get, contactId, msgId);
  },

  markRead: (contactId) => {
    const hadUnread = (get().unreadByContact[contactId] ?? 0) > 0;
    set((s) => ({ unreadByContact: { ...s.unreadByContact, [contactId]: 0 } }));
    if (hadUnread && transport.socket && transport.socket.readyState === WebSocket.OPEN) {
      transport.socket.send(JSON.stringify({ type: "read", to: contactId, at: Date.now() }));
    }
  },

  setWallpaper: (contactId, url) => {
    set((s) => ({ wallpaperByContact: { ...s.wallpaperByContact, [contactId]: url } }));
  },
});
