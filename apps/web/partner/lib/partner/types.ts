export type PartnerRole =
  | "owner"
  | "manager"
  | "kitchen_staff"
  | "cashier"
  | "front_desk"
  | "landlord";

export type PartnerVertical =
  | "restaurant"
  | "grocery"
  | "hotel"
  | "rental"
  | "rider";

export interface PartnerStore {
  id: string;
  partnerId: string;
  name: string;
  vertical: PartnerVertical;
  address: string;
  phone: string;
  isAcceptingOrders: boolean;
  taxPan: string;
  rating: number;
  openHours: string;
  activeOrdersCount: number;
}

export type OrderTicketStatus =
  | "PENDING"
  | "PREPARING"
  | "READY_FOR_PICKUP"
  | "DISPATCHED"
  | "COMPLETED"
  | "REJECTED";

export interface OrderTicketItem {
  id: string;
  name: string;
  quantity: number;
  notes?: string;
  priceNpr: number;
  isVeg?: boolean;
  options?: string[];
  isPacked?: boolean; // For grocery pick-and-pack
  binRackLocation?: string; // e.g. "Rack A1 • Shelf 2 • Bin 04"
  barcode?: string;
  isSubstituted?: boolean;
  substitutedWithName?: string;
  isRefunded?: boolean;
}

export type GroceryFulfillmentType = "DARK_STORE_EXPRESS" | "SUPERMARKET_RETAIL";
export type SubstitutionPreference = "callCustomer" | "allowStoreSubstitution" | "refundItem";

export interface KdsOrderTicket {
  id: string;
  displayNumber: string;
  customerName: string;
  customerPhone: string;
  orderType: "DELIVERY" | "DINE_IN" | "TAKEAWAY";
  vertical: "food" | "grocery";
  fulfillmentType?: GroceryFulfillmentType;
  storeId?: string;
  storeName?: string;
  substitutionPreference?: SubstitutionPreference;
  status: OrderTicketStatus;
  items: OrderTicketItem[];
  subtotalNpr: number;
  deliveryFeeNpr: number;
  totalNpr: number;
  paymentMethod: "CASH_ON_DELIVERY" | "ONLINE_PAID";
  estimatedPrepMinutes: number;
  createdAt: string;
  deliveryAddress: string;
  bagSealCode?: string;
  assignedRider?: {
    name: string;
    phone: string;
    vehiclePlate: string;
  };
}

export interface SupermarketShelfItem {
  id: string;
  storeId: string;
  name: string;
  category: string;
  packSize: string;
  priceNpr: number;
  mrpNpr: number;
  inStock: boolean;
  stockCount: number;
  barcode: string;
  imageUrl?: string;
}

export interface PartnerMenuItem {
  id: string;
  storeId: string;
  name: string;
  category: string;
  description: string;
  priceNpr: number;
  mrpNpr?: number;
  isVeg: boolean;
  isAvailable: boolean; // 86 status
  packSize?: string;
  stockCount?: number;
  imageUrl?: string;
}

export interface HotelRoomBooking {
  id: string;
  reservationCode: string;
  guestName: string;
  guestPhone: string;
  roomType: string;
  roomNumber: string;
  checkInDate: string;
  checkOutDate: string;
  nightsCount: number;
  guestsCount: number;
  totalAmountNpr: number;
  paymentStatus: "PAID" | "PENDING_AT_CHECKIN";
  status: "CONFIRMED" | "CHECKED_IN" | "CHECKED_OUT" | "CANCELLED";
}

export interface LandlordRentalListing {
  id: string;
  title: string;
  propertyType: "1BHK" | "2BHK" | "1RK" | "Single Room" | "Commercial Space";
  location: string;
  monthlyRentNpr: number;
  securityDepositNpr: number;
  isAvailable: boolean;
  waterFacility: string;
  parkingAvailable: boolean;
  enquiriesCount: number;
  images: string[];
}

export interface TenantEnquiry {
  id: string;
  listingId: string;
  listingTitle: string;
  tenantName: string;
  tenantPhone: string;
  message: string;
  moveInDate: string;
  status: "NEW" | "CONTACTED" | "SCHEDULED_VISIT" | "CLOSED";
  createdAt: string;
}

export interface PartnerFinancialLedger {
  storeId: string;
  storeName: string;
  todaySalesNpr: number;
  todayOrdersCount: number;
  pendingSettlementNpr: number;
  lifetimeEarningsNpr: number;
  platformTakeRatePercent: number;
  bankAccount: {
    bankName: string;
    branch: string;
    accountNumber: string;
    accountHolder: string;
    isVerified: boolean;
  };
}
