/**
 * Web Map & Routing Helper (Next.js / TypeScript)
 * Elifsi Technologies Private Limited
 *
 * Connects Web Apps (Consumer, Partner, and Superadmin Admin Console) to the
 * self-hosted OSRM engine via the Supabase Routing Edge Function.
 *
 * Provides polyline decoding for Mapbox GL / Leaflet / Google Maps JavaScript API.
 */

import { SupabaseClient } from '@supabase/supabase-js';

export interface LatLng {
  lat: number;
  lng: number;
}

export interface RouteStep {
  name: string;
  distance_meters: number;
  duration_seconds: number;
  instruction: string;
  modifier?: string;
}

export interface RouteResult {
  ok: boolean;
  provider: string;
  distance_meters: number;
  duration_seconds: number;
  duration_minutes: number;
  polyline: string;
  steps: RouteStep[];
  coordinates?: LatLng[];
}

export interface SnapResult {
  ok: boolean;
  provider: string;
  snapped: {
    lat: number;
    lng: number;
    distance_from_point_m: number;
    street_name: string;
  };
}

export class EvrryWebRoutingClient {
  private supabase: SupabaseClient;

  constructor(supabaseClient: SupabaseClient) {
    this.supabase = supabaseClient;
  }

  /**
   * Calculates driving road route, distance, ETA, and decoded coordinates.
   */
  async getRoute(
    origin: LatLng,
    destination: LatLng,
    waypoints: LatLng[] = [],
    mode: 'driving' | 'motorcycle' | 'walking' = 'driving'
  ): Promise<RouteResult> {
    const { data, error } = await this.supabase.functions.invoke('routing', {
      body: {
        action: 'route',
        origin,
        destination,
        waypoints,
        mode,
      },
    });

    if (error || !data) {
      throw new Error(error?.message || 'Failed to compute route');
    }

    const coordinates = data.polyline ? EvrryWebRoutingClient.decodePolyline(data.polyline) : [];
    return {
      ...data,
      coordinates,
    };
  }

  /**
   * Snaps a coordinate to the nearest road network.
   */
  async snapToRoad(point: LatLng): Promise<SnapResult> {
    const { data, error } = await this.supabase.functions.invoke('routing', {
      body: {
        action: 'nearest',
        point,
      },
    });

    if (error || !data) {
      throw new Error(error?.message || 'Failed to snap point to road');
    }

    return data;
  }

  /**
   * Decodes an OSRM / Google encoded polyline string into an array of LatLng points.
   */
  static decodePolyline(encoded: string): LatLng[] {
    const points: LatLng[] = [];
    let index = 0;
    const len = encoded.length;
    let lat = 0;
    let lng = 0;

    while (index < len) {
      let b: number;
      let shift = 0;
      let result = 0;
      do {
        b = encoded.charCodeAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);
      const dlat = (result & 1) !== 0 ? ~(result >> 1) : result >> 1;
      lat += dlat;

      shift = 0;
      result = 0;
      do {
        b = encoded.charCodeAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);
      const dlng = (result & 1) !== 0 ? ~(result >> 1) : result >> 1;
      lng += dlng;

      points.push({ lat: lat / 1e5, lng: lng / 1e5 });
    }
    return points;
  }

  /**
   * Computes the heading bearing (0° to 360°) from point A to point B.
   * Used for smooth 360° rotation of 3D vehicle icons (Scooter, Motorcycle, Cab).
   */
  static calculateBearing(from: LatLng, to: LatLng): number {
    const rad = Math.PI / 180;
    const lat1 = from.lat * rad;
    const lat2 = to.lat * rad;
    const dLng = (to.lng - from.lng) * rad;
    const y = Math.sin(dLng) * Math.cos(lat2);
    const x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng);
    const bearing = (Math.atan2(y, x) * (180 / Math.PI) + 360) % 360;
    return Math.round(bearing);
  }
}

/**
 * 3D Isometric Vehicle Types supported across mobile apps & web tracking
 */
export type VehicleType = 'scooter' | 'motorcycle' | 'cab';

export interface VehicleTrackingState {
  orderId: string;
  driverId: string;
  vehicleType: VehicleType;
  position: LatLng;
  bearingDegrees: number; // 0 to 360 degrees
  speedKmh?: number;
  etaMinutes: number;
}

/**
 * Constant prompt banner displayed on web order/ride tracking views
 */
export const WEB_LIVE_TRACKING_PROMPT = {
  bannerTitle: 'For real-time 3D vehicle tracking and turn-by-turn navigation, open in the Evrry Phone App',
  ctaText: 'Open in App',
  deepLinkScheme: 'evrry://order-tracking',
  mapProviderWeb: 'maplibre-gl',
  mapTileSource: 'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
} as const;

