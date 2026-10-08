export interface Driver {
  name: string;
  vehicleModel: string;
  vehicleNumber: string;
  rating: number;
  /** Fraction of ride offers this driver accepts — used by the matching simulation. */
  acceptanceRate: number;
}

// Purely mock — no real dispatch/matching, just enough to make the ride
// matching + "driver assigned" flow feel real.
const ROSTER: Record<string, Driver[]> = {
  bike: [
    { name: "Bikash Tamang", vehicleModel: "Bajaj Pulsar 150", vehicleNumber: "BA 02 PA 4521", rating: 4.8, acceptanceRate: 0.92 },
    { name: "Dipesh Magar", vehicleModel: "TVS NTorq 125", vehicleNumber: "BA 04 PA 7789", rating: 4.7, acceptanceRate: 0.88 },
    { name: "Sagar Gurung", vehicleModel: "Yamaha FZ-S", vehicleNumber: "BA 01 PA 1023", rating: 4.9, acceptanceRate: 0.95 },
  ],
  auto: [
    { name: "Sunil Maharjan", vehicleModel: "Safa Tempo (Electric)", vehicleNumber: "BA 1 SA 6634", rating: 4.6, acceptanceRate: 0.85 },
    { name: "Ramesh Prajapati", vehicleModel: "Piaggio Ape Electric", vehicleNumber: "BA 2 HA 2210", rating: 4.7, acceptanceRate: 0.82 },
  ],
  standard: [
    { name: "Raju Thapa", vehicleModel: "Tata Tigor EV", vehicleNumber: "BA 2 JA 3345", rating: 4.8, acceptanceRate: 0.89 },
    { name: "Bibek Shrestha", vehicleModel: "BYD e6 Electric", vehicleNumber: "BA 3 JA 9982", rating: 4.9, acceptanceRate: 0.94 },
  ],
  premium: [
    { name: "Dawa Sherpa", vehicleModel: "BYD Atto 3 EV", vehicleNumber: "BA 1 JA 5567", rating: 4.9, acceptanceRate: 0.96 },
    { name: "Purna Bahadur KC", vehicleModel: "MG ZS EV", vehicleNumber: "BA 4 JA 8814", rating: 4.9, acceptanceRate: 0.95 },
  ],
};

export function driversForType(rideTypeId: string): Driver[] {
  return ROSTER[rideTypeId] ?? ROSTER.standard;
}
