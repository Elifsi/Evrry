/**
 * Supabase Edge Function: create-call-session
 * WhatsApp-Grade WebRTC P2P Voice & Video Call Signaling Engine
 * Platform: Evrry Super App
 * Elifsi Technologies Private Limited
 */

import { createClient } from 'https://esm.sh/@supabase/supabase-js@2.39.0';

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
  'Access-Control-Allow-Methods': 'POST, OPTIONS',
};

interface CreateCallSessionRequest {
  chat_id: string;
  callee_id: string;
  call_type: 'AUDIO' | 'VIDEO';
  caller_name?: string;
  caller_avatar?: string;
}

Deno.serve(async (req: Request) => {
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders });
  }

  try {
    if (req.method !== 'POST') {
      return new Response(JSON.stringify({ error: 'Method not allowed. Use POST.' }), {
        status: 405,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }

    const authHeader = req.headers.get('Authorization') ?? '';
    const supabaseUrl = Deno.env.get('SUPABASE_URL') || '';
    const supabaseAnonKey = Deno.env.get('SUPABASE_ANON_KEY') || '';
    const supabaseServiceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') || '';

    const supabase = createClient(supabaseUrl, supabaseAnonKey, {
      global: { headers: { Authorization: authHeader } },
    });

    const { data: { user }, error: authError } = await supabase.auth.getUser();
    if (authError || !user) {
      return new Response(JSON.stringify({ error: 'Unauthorized. Authentication required.' }), {
        status: 401,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }

    const payload: CreateCallSessionRequest = await req.json();
    const { chat_id, callee_id, call_type, caller_name, caller_avatar } = payload;

    if (!chat_id || !callee_id || !call_type) {
      return new Response(
        JSON.stringify({ error: 'Missing required parameters: chat_id, callee_id, and call_type are required.' }),
        { status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      );
    }

    // Verify caller is a member of the chat
    const { data: isMember } = await supabase.rpc('is_chat_member', { p_chat: chat_id });
    if (isMember === false) {
      return new Response(JSON.stringify({ error: 'Caller is not a member of this chat.' }), {
        status: 403,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }

    const sessionId = `call_${crypto.randomUUID().replace(/-/g, '').slice(0, 16)}`;
    const roomName = `evrry_${chat_id}_${Date.now()}`;

    // Standard low-latency WebRTC STUN/TURN ICE configuration
    const iceServers = [
      { urls: ['stun:stun.l.google.com:19302', 'stun:stun1.l.google.com:19302'] },
      {
        urls: ['stun:stun.services.mozilla.com', 'stun:stun.relay.metered.ca:80'],
      },
    ];

    // Dispatch high-priority VoIP / FCM push alert to Callee
    const adminSupabase = createClient(supabaseUrl, supabaseServiceKey);
    const callerDisplayName = caller_name || 'Evrry Contact';

    try {
      await adminSupabase.functions.invoke('push-notify', {
        body: {
          target_type: 'user',
          target_id: callee_id,
          title: `Incoming ${call_type === 'VIDEO' ? 'Video' : 'Voice'} Call`,
          body: `${callerDisplayName} is calling you...`,
          priority: 'high',
          sound: 'ringtone.mp3',
          app_variant: 'consumer',
          data: {
            type: 'incoming_call',
            call_id: sessionId,
            room_name: roomName,
            chat_id: chat_id,
            caller_id: user.id,
            caller_name: callerDisplayName,
            caller_avatar: caller_avatar || '',
            call_type: call_type,
          },
        },
      });
    } catch (_pushErr) {
      console.warn('Call push notification dispatch logged notice:', _pushErr);
    }

    return new Response(
      JSON.stringify({
        success: true,
        session_id: sessionId,
        room_name: roomName,
        call_type: call_type,
        caller_id: user.id,
        callee_id: callee_id,
        ice_servers: iceServers,
        created_at: new Date().toISOString(),
      }),
      { status: 200, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
    );
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : 'Internal Server Error';
    return new Response(JSON.stringify({ error: message }), {
      status: 500,
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
    });
  }
});
