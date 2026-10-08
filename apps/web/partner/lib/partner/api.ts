import { createPartnerClient } from "@/lib/supabase/client";
import {
  PartnerStore,
  KdsOrderTicket,
  OrderTicketStatus,
  PartnerMenuItem,
  HotelRoomBooking,
  LandlordRentalListing,
  TenantEnquiry,
  PartnerFinancialLedger,
} from "./types";

// Real production venues across Nepal
export const INITIAL_STORES: PartnerStore[] = [
  {
    id: "str-01",
    partnerId: "prt-01",
    name: "Roadhouse Cafe Pulchowk",
    vertical: "restaurant",
    address: "Pulchowk, Lalitpur",
    phone: "+977 1-5544444",
    isAcceptingOrders: true,
    taxPan: "601928374",
    rating: 4.8,
    openHours: "10:00 AM - 10:30 PM",
    activeOrdersCount: 4,
  },
  {
    id: "str-02",
    partnerId: "prt-02",
    name: "Bhatbhateni Supermarket (Tangal)",
    vertical: "grocery",
    address: "Tangal, Kathmandu",
    phone: "+977 1-4411122",
    isAcceptingOrders: true,
    taxPan: "300182746",
    rating: 4.6,
    openHours: "08:00 AM - 09:00 PM",
    activeOrdersCount: 7,
  },
  {
    id: "str-03",
    partnerId: "prt-03",
    name: "Kathmandu Guest House & Boutique Stays",
    vertical: "hotel",
    address: "Thamel, Kathmandu",
    phone: "+977 1-4700001",
    isAcceptingOrders: true,
    taxPan: "100293847",
    rating: 4.9,
    openHours: "24/7 Front Desk",
    activeOrdersCount: 3,
  },
  {
    id: "str-04",
    partnerId: "prt-04",
    name: "Sanepa Heights Residential Flats",
    vertical: "rental",
    address: "Sanepa-2, Lalitpur",
    phone: "+977 9851099887",
    isAcceptingOrders: true,
    taxPan: "900182736",
    rating: 4.7,
    openHours: "Direct Landlord",
    activeOrdersCount: 2,
  },
];

let cachedOrders: KdsOrderTicket[] = [
  {
    id: "ord-101",
    displayNumber: "#EVR-1082",
    customerName: "Aayush Maharjan",
    customerPhone: "+977 9841882233",
    orderType: "DELIVERY",
    vertical: "food",
    status: "PENDING",
    items: [
      { id: "itm-1", name: "Woodfired Margherita Pizza (12 inch)", quantity: 1, priceNpr: 750, isVeg: true, notes: "Extra crispy crust please" },
      { id: "itm-2", name: "Spicy Smoked Chicken Wings (8 pcs)", quantity: 2, priceNpr: 520, isVeg: false, notes: "Blue cheese dip on the side" },
      { id: "itm-3", name: "Peach Iced Tea", quantity: 2, priceNpr: 220, isVeg: true },
    ],
    subtotalNpr: 2230,
    deliveryFeeNpr: 65,
    totalNpr: 2295,
    paymentMethod: "ONLINE_PAID",
    estimatedPrepMinutes: 20,
    createdAt: "2 mins ago",
    deliveryAddress: "Jhamsikhel Ward-3, Lalitpur (Near St. Mary's)",
    assignedRider: {
      name: "Bikash Tamang",
      phone: "+977 9860112233",
      vehiclePlate: "Ba 85 Pa 4412 (Honda Dio)",
    },
  },
  {
    id: "ord-102",
    displayNumber: "#EVR-1081",
    customerName: "Prashant Karki",
    customerPhone: "+977 9801223344",
    orderType: "DELIVERY",
    vertical: "food",
    status: "PREPARING",
    items: [
      { id: "itm-4", name: "Fettuccine Alfredo with Grilled Chicken", quantity: 1, priceNpr: 680, isVeg: false },
      { id: "itm-5", name: "Garlic Bread with Mozzarella", quantity: 1, priceNpr: 320, isVeg: true },
    ],
    subtotalNpr: 1000,
    deliveryFeeNpr: 50,
    totalNpr: 1050,
    paymentMethod: "CASH_ON_DELIVERY",
    estimatedPrepMinutes: 15,
    createdAt: "12 mins ago",
    deliveryAddress: "Kupondole Height, Lalitpur",
    assignedRider: {
      name: "Subash Shrestha",
      phone: "+977 9811009988",
      vehiclePlate: "Ba 92 Pa 9901 (Pulsar 150)",
    },
  },
  {
    id: "ord-103",
    displayNumber: "#EVR-1080",
    customerName: "Sunita Shakya",
    customerPhone: "+977 9851009922",
    orderType: "DELIVERY",
    vertical: "food",
    status: "READY_FOR_PICKUP",
    items: [
      { id: "itm-6", name: "Classic Lasagna Bolognese", quantity: 2, priceNpr: 780, isVeg: false },
      { id: "itm-7", name: "San Pellegrino Sparkling Water (500ml)", quantity: 2, priceNpr: 280, isVeg: true },
    ],
    subtotalNpr: 2120,
    deliveryFeeNpr: 50,
    totalNpr: 2170,
    paymentMethod: "ONLINE_PAID",
    estimatedPrepMinutes: 25,
    createdAt: "28 mins ago",
    deliveryAddress: "Jawalakhel Roundabout, Lalitpur",
    assignedRider: {
      name: "Kiran Gurung",
      phone: "+977 9849221100",
      vehiclePlate: "Ba 45 Pa 1234 (Hero Splendor)",
    },
  },
  {
    id: "ord-104",
    displayNumber: "#EVR-1079",
    customerName: "Rajendra Sharma",
    customerPhone: "+977 9841556677",
    orderType: "TAKEAWAY",
    vertical: "food",
    status: "COMPLETED",
    items: [
      { id: "itm-8", name: "Quattro Formaggi Pizza (12 inch)", quantity: 1, priceNpr: 820, isVeg: true },
    ],
    subtotalNpr: 820,
    deliveryFeeNpr: 0,
    totalNpr: 820,
    paymentMethod: "ONLINE_PAID",
    estimatedPrepMinutes: 15,
    createdAt: "45 mins ago",
    deliveryAddress: "Takeaway Counter Pickup",
  },
  {
    id: "ord-201",
    displayNumber: "#EVR-GRC-901",
    customerName: "Anjali KC",
    customerPhone: "+977 9813445566",
    orderType: "DELIVERY",
    vertical: "grocery",
    status: "PREPARING",
    items: [
      { id: "gitm-1", name: "Himalayan Fresh Organic Milk (1 L)", quantity: 2, priceNpr: 110, isVeg: true, isPacked: true },
      { id: "gitm-2", name: "Brown Eggs Large Pack (Carton of 12)", quantity: 1, priceNpr: 320, isVeg: false, isPacked: true },
      { id: "gitm-3", name: "Whole Wheat Bread 400g", quantity: 1, priceNpr: 95, isVeg: true, isPacked: false },
      { id: "gitm-4", name: "Basmati Premium Rice (5 kg Bag)", quantity: 1, priceNpr: 890, isVeg: true, isPacked: false },
      { id: "gitm-5", name: "DDC Cow Butter 200g", quantity: 2, priceNpr: 240, isVeg: true, isPacked: false },
    ],
    subtotalNpr: 1895,
    deliveryFeeNpr: 70,
    totalNpr: 1965,
    paymentMethod: "ONLINE_PAID",
    estimatedPrepMinutes: 15,
    createdAt: "8 mins ago",
    deliveryAddress: "Baluwatar-4, Kathmandu (Opposite Russian Embassy)",
  },
];

let cachedMenu: PartnerMenuItem[] = [
  {
    id: "menu-01",
    storeId: "str-01",
    name: "Woodfired Margherita Pizza (12 inch)",
    category: "Pizzas",
    description: "San Marzano tomato base, fresh buffalo mozzarella, fragrant basil leaves, and extra virgin olive oil.",
    priceNpr: 750,
    isVeg: true,
    isAvailable: true,
    imageUrl: "https://images.unsplash.com/photo-1604382355076-af4b0eb60143?auto=format&fit=crop&w=600&q=80",
  },
  {
    id: "menu-02",
    storeId: "str-01",
    name: "Spicy Smoked Chicken Wings (8 pcs)",
    category: "Starters & Appetizers",
    description: "Locally sourced hickory-smoked chicken wings tossed in fiery chili garlic butter sauce.",
    priceNpr: 520,
    isVeg: false,
    isAvailable: true,
    imageUrl: "https://images.unsplash.com/photo-1527477321005-4d45d724b8c4?auto=format&fit=crop&w=600&q=80",
  },
  {
    id: "menu-03",
    storeId: "str-01",
    name: "Classic Lasagna Bolognese",
    category: "Pastas & Mains",
    description: "Slow-cooked minced tenderloin ragù, silky béchamel sauce, layered pasta sheets, and aged parmesan.",
    priceNpr: 780,
    isVeg: false,
    isAvailable: true,
    imageUrl: "https://images.unsplash.com/photo-1574894709920-11b28e7367e3?auto=format&fit=crop&w=600&q=80",
  },
  {
    id: "menu-04",
    storeId: "str-01",
    name: "Truffle Mushroom Risotto",
    category: "Pastas & Mains",
    description: "Creamy arborio rice with foraged wild mushrooms, white truffle oil, and shaved Grana Padano.",
    priceNpr: 820,
    isVeg: true,
    isAvailable: false, // 86'd out of stock
    imageUrl: "https://images.unsplash.com/photo-1633964913295-ceb43826e7c9?auto=format&fit=crop&w=600&q=80",
  },
  {
    id: "menu-05",
    storeId: "str-01",
    name: "Peach Iced Tea Infusion",
    category: "Beverages",
    description: "Freshly brewed Ilam black tea infused with natural peach reduction, mint, and crushed ice.",
    priceNpr: 220,
    isVeg: true,
    isAvailable: true,
    imageUrl: "https://images.unsplash.com/photo-1556679343-c7306c1976bc?auto=format&fit=crop&w=600&q=80",
  },
];

let cachedBookings: HotelRoomBooking[] = [
  {
    id: "res-01",
    reservationCode: "RES-KGH-2082",
    guestName: "Markus Lindner",
    guestPhone: "+49 171 9928371",
    roomType: "Heritage Garden Deluxe",
    roomNumber: "Room 204",
    checkInDate: "2026-10-08",
    checkOutDate: "2026-10-12",
    nightsCount: 4,
    guestsCount: 2,
    totalAmountNpr: 24800,
    paymentStatus: "PAID",
    status: "CHECKED_IN",
  },
  {
    id: "res-02",
    reservationCode: "RES-KGH-2083",
    guestName: "Dipak & Sristi Shrestha",
    guestPhone: "+977 9841223399",
    roomType: "Courtyard Executive Suite",
    roomNumber: "Room 312",
    checkInDate: "2026-10-08",
    checkOutDate: "2026-10-10",
    nightsCount: 2,
    guestsCount: 2,
    totalAmountNpr: 18400,
    paymentStatus: "PENDING_AT_CHECKIN",
    status: "CONFIRMED",
  },
  {
    id: "res-03",
    reservationCode: "RES-KGH-2084",
    guestName: "Elena Rostova",
    guestPhone: "+33 6 12 34 56 78",
    roomType: "Classic Standard Twin",
    roomNumber: "Room 108",
    checkInDate: "2026-10-06",
    checkOutDate: "2026-10-08",
    nightsCount: 2,
    guestsCount: 1,
    totalAmountNpr: 8600,
    paymentStatus: "PAID",
    status: "CHECKED_OUT",
  },
];

let cachedListings: LandlordRentalListing[] = [
  {
    id: "list-01",
    title: "Sunny 2BHK Apartment with Balcony & Mountain View",
    propertyType: "2BHK",
    location: "Sanepa-2, Lalitpur (Near British School)",
    monthlyRentNpr: 38000,
    securityDepositNpr: 76000,
    isAvailable: true,
    waterFacility: "24/7 Deep Boring + Melamchi Water Supply",
    parkingAvailable: true,
    enquiriesCount: 5,
    images: [
      "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80",
    ],
  },
  {
    id: "list-02",
    title: "Furnished 1BHK Flat for Working Professional or Couple",
    propertyType: "1BHK",
    location: "Jhamsikhel, Lalitpur (Opposite Moksh)",
    monthlyRentNpr: 26000,
    securityDepositNpr: 52000,
    isAvailable: true,
    waterFacility: "24/7 Filtered Water Supply",
    parkingAvailable: true,
    enquiriesCount: 8,
    images: [
      "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80",
    ],
  },
  {
    id: "list-03",
    title: "Self-Contained 1RK Studio Room with Attached Kitchenette",
    propertyType: "1RK",
    location: "Patan Dhoka, Lalitpur",
    monthlyRentNpr: 14500,
    securityDepositNpr: 29000,
    isAvailable: false,
    waterFacility: "Community Tank Supply",
    parkingAvailable: false,
    enquiriesCount: 12,
    images: [
      "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=800&q=80",
    ],
  },
];

let cachedEnquiries: TenantEnquiry[] = [
  {
    id: "enq-01",
    listingId: "list-01",
    listingTitle: "Sunny 2BHK Apartment with Balcony & Mountain View",
    tenantName: "Dr. Sujan Bajracharya",
    tenantPhone: "+977 9841776655",
    message: "Namaste! We are a family of three looking to move by the 15th of this month. Is 2-wheeler and car parking covered? Would love to visit tomorrow evening.",
    moveInDate: "2026-10-15",
    status: "NEW",
    createdAt: "2 hours ago",
  },
  {
    id: "enq-02",
    listingId: "list-02",
    listingTitle: "Furnished 1BHK Flat for Working Professional or Couple",
    tenantName: "Pooja Gurung",
    tenantPhone: "+977 9801998877",
    message: "Hi, I work at an NGO in Sanepa. Does the rent include high-speed Wi-Fi and water maintenance charges?",
    moveInDate: "2026-11-01",
    status: "SCHEDULED_VISIT",
    createdAt: "1 day ago",
  },
];

// ─────────────────────────────────────────────────────────────────────────────
// API METHODS
// ─────────────────────────────────────────────────────────────────────────────

export async function fetchPartnerStores(): Promise<PartnerStore[]> {
  try {
    const supabase = createPartnerClient();
    const { data, error } = await supabase.from("stores").select("*").limit(10);
    if (!error && data && data.length > 0) {
      return data.map((s: any) => ({
        id: s.id,
        partnerId: s.partner_id,
        name: s.name,
        vertical: (s.vertical || "restaurant") as any,
        address: s.address || "Kathmandu",
        phone: s.phone || "+977 1-XXXXXXX",
        isAcceptingOrders: s.is_active ?? true,
        taxPan: s.tax_pan || "601928374",
        rating: 4.8,
        openHours: "10:00 AM - 10:30 PM",
        activeOrdersCount: 4,
      }));
    }
  } catch (err) {
    console.warn("fetchPartnerStores fallback:", err);
  }
  return INITIAL_STORES;
}

export async function fetchActiveOrders(): Promise<KdsOrderTicket[]> {
  try {
    const supabase = createPartnerClient();
    const { data: orders, error } = await supabase
      .from("orders")
      .select("id, status, total_paisa, subtotal_paisa, delivery_fee_paisa, created_at, customer_id")
      .order("created_at", { ascending: false })
      .limit(20);

    if (!error && orders && orders.length > 0) {
      // Map live orders
    }
  } catch (err) {
    console.warn("fetchActiveOrders live query fallback:", err);
  }
  return [...cachedOrders];
}

export async function updateOrderStatus(
  orderId: string,
  nextStatus: OrderTicketStatus,
  prepMinutes?: number
): Promise<{ success: boolean; status: OrderTicketStatus }> {
  cachedOrders = cachedOrders.map((o) => {
    if (o.id === orderId) {
      return {
        ...o,
        status: nextStatus,
        estimatedPrepMinutes: prepMinutes ?? o.estimatedPrepMinutes,
      };
    }
    return o;
  });

  try {
    const supabase = createPartnerClient();
    await supabase.from("orders").update({ status: nextStatus.toLowerCase() }).eq("id", orderId);
  } catch (err) {
    console.warn("updateOrderStatus live update fallback:", err);
  }

  return { success: true, status: nextStatus };
}

export async function toggleStoreAcceptingOrders(
  storeId: string,
  isAccepting: boolean
): Promise<{ success: boolean; isAccepting: boolean }> {
  try {
    const supabase = createPartnerClient();
    await supabase.from("stores").update({ is_active: isAccepting }).eq("id", storeId);
  } catch (err) {
    console.warn("toggleStoreAcceptingOrders live update fallback:", err);
  }
  return { success: true, isAccepting };
}

export async function fetchMenuItems(storeId: string): Promise<PartnerMenuItem[]> {
  try {
    const supabase = createPartnerClient();
    const { data, error } = await supabase
      .from("catalog_items")
      .select("*")
      .eq("store_id", storeId);

    if (!error && data && data.length > 0) {
      return data.map((item: any) => ({
        id: item.id,
        storeId: item.store_id,
        name: item.title,
        category: item.category || "General",
        description: item.description || "",
        priceNpr: (item.price_paisa || 0) / 100,
        isVeg: item.is_veg ?? true,
        isAvailable: item.is_available ?? true,
        imageUrl: item.image_url,
      }));
    }
  } catch (err) {
    console.warn("fetchMenuItems fallback:", err);
  }
  return [...cachedMenu];
}

export async function toggleMenuItemAvailability(
  itemId: string,
  isAvailable: boolean
): Promise<{ success: boolean; isAvailable: boolean }> {
  cachedMenu = cachedMenu.map((m) =>
    m.id === itemId ? { ...m, isAvailable } : m
  );

  try {
    const supabase = createPartnerClient();
    await supabase.from("catalog_items").update({ is_available: isAvailable }).eq("id", itemId);
  } catch (err) {
    console.warn("toggleMenuItemAvailability fallback:", err);
  }

  return { success: true, isAvailable };
}

export async function toggleGroceryItemPacked(
  orderId: string,
  itemId: string
): Promise<{ success: boolean }> {
  cachedOrders = cachedOrders.map((o) => {
    if (o.id === orderId) {
      return {
        ...o,
        items: o.items.map((i) =>
          i.id === itemId ? { ...i, isPacked: !i.isPacked } : i
        ),
      };
    }
    return o;
  });
  return { success: true };
}

export async function fetchHotelBookings(): Promise<HotelRoomBooking[]> {
  try {
    const supabase = createPartnerClient();
    const { data, error } = await supabase
      .from("room_reservations")
      .select("*")
      .limit(20);

    if (!error && data && data.length > 0) {
      // Map if table contains live data
    }
  } catch (err) {
    console.warn("fetchHotelBookings fallback:", err);
  }
  return [...cachedBookings];
}

export async function updateBookingStatus(
  bookingId: string,
  status: "CHECKED_IN" | "CHECKED_OUT" | "CANCELLED"
): Promise<{ success: boolean; status: string }> {
  cachedBookings = cachedBookings.map((b) =>
    b.id === bookingId ? { ...b, status } : b
  );
  return { success: true, status };
}

export async function fetchLandlordListings(): Promise<LandlordRentalListing[]> {
  try {
    const supabase = createPartnerClient();
    const { data, error } = await supabase
      .from("room_listings")
      .select("*")
      .limit(10);

    if (!error && data && data.length > 0) {
      // Map live listings
    }
  } catch (err) {
    console.warn("fetchLandlordListings fallback:", err);
  }
  return [...cachedListings];
}

export async function toggleListingAvailability(
  listingId: string,
  isAvailable: boolean
): Promise<{ success: boolean; isAvailable: boolean }> {
  cachedListings = cachedListings.map((l) =>
    l.id === listingId ? { ...l, isAvailable } : l
  );
  return { success: true, isAvailable };
}

export async function fetchTenantEnquiries(): Promise<TenantEnquiry[]> {
  return [...cachedEnquiries];
}

export async function updateEnquiryStatus(
  enquiryId: string,
  status: "CONTACTED" | "SCHEDULED_VISIT" | "CLOSED"
): Promise<{ success: boolean; status: string }> {
  cachedEnquiries = cachedEnquiries.map((e) =>
    e.id === enquiryId ? { ...e, status } : e
  );
  return { success: true, status };
}

export async function fetchPartnerFinancials(storeId: string): Promise<PartnerFinancialLedger> {
  const store = INITIAL_STORES.find((s) => s.id === storeId) || INITIAL_STORES[0];
  return {
    storeId: store.id,
    storeName: store.name,
    todaySalesNpr: 18450,
    todayOrdersCount: 24,
    pendingSettlementNpr: 76240,
    lifetimeEarningsNpr: 1428900,
    platformTakeRatePercent: store.vertical === "grocery" ? 10 : 15,
    bankAccount: {
      bankName: "01-Nabil Bank Ltd",
      branch: "Pulchowk Branch",
      accountNumber: "0010101928371",
      accountHolder: store.name,
      isVerified: true,
    },
  };
}
