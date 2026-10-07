export interface PlatformPricingSettings {
  platformFeePaisa: number; // default 1000 = Rs 10.00
  platformFeeWaiver: boolean; // default false
  ridePlatformFeePaisa: number; // default 0
  freeDeliveryThresholdPaisa: number; // default 100000 = Rs 1000
  baseDeliveryRadiusMeters: number; // default 3000 = 3 km
  baseDeliveryFeePaisa: number; // default 5000 = Rs 50
  extraDistanceFeePerKmPaisa: number; // default 1500 = Rs 15/km
  driverBaseSharePaisa: number; // default 4000 = Rs 40
  driverExtraDistanceShareBps: number; // default 8000 = 80%
  stayServiceFeeBps: number; // default 500 = 5%
  lateNightSurchargePaisa: number; // default 0
  weatherSurgeMultiplier: number; // 1.0 to 2.0
  vatBps: number; // 0 or 1300 (13% VAT)
  commissions: {
    foodBps: number; // 1500 = 15%
    groceryBps: number; // 800 = 8%
    bikeTaxiBps: number; // 1000 = 10%
    cabTaxiBps: number; // 1200 = 12%
    hotelBps: number; // 1000 = 10%
    rentalBps: number; // 500 = 5%
  };
}

export type PartnerType = 'rider' | 'driver' | 'restaurant' | 'grocery' | 'hotel';

export type KycStatus = 'pending' | 'approved' | 'rejected' | 'reupload_requested';

export interface KycDocumentItem {
  id: string;
  partnerName: string;
  partnerType: PartnerType;
  phoneNumber: string;
  submittedAt: string;
  status: KycStatus;
  rejectionReason?: string;
  vehicleInfo?: {
    type: string;
    plateNumber: string;
    model: string;
    taxClearanceYear: number;
  };
  licenseInfo?: {
    number: string;
    expiryDate: string;
    issueDistrict: string;
  };
  panVatInfo?: {
    panNumber: string;
    businessName: string;
    entityType: string;
  };
  documents: Array<{
    type: string;
    title: string;
    imageUrl: string;
  }>;
}

export interface MidnightPayoutBatch {
  batchId: string;
  date: string;
  status: 'pending_approval' | 'processing' | 'completed';
  grossMerchantEarningsPaisa: number;
  platformCommissionsDeductedPaisa: number;
  netPayableMerchantsPaisa: number;
  grossRiderFeesPaisa: number;
  riderCodDeductedPaisa: number;
  netPayableRidersPaisa: number;
  totalDisbursementPaisa: number;
  merchantCount: number;
  riderCount: number;
}

export interface RiderCodBalance {
  riderId: string;
  riderName: string;
  phoneNumber: string;
  cashInHandPaisa: number; // in Paisa
  ordersDeliveredToday: number;
  safetyStatus: 'normal' | 'soft_warning' | 'hard_locked';
  lastDepositTime?: string;
}

export interface VoucherCampaign {
  id: string;
  code: string;
  title: string;
  description: string;
  discountType: 'flat' | 'percentage';
  discountValue: number; // Rs or %
  minOrderValuePaisa: number;
  maxDiscountCapPaisa: number;
  perUserLimit: number;
  totalRedemptionsCap: number;
  redeemedCount: number;
  allocatedBudgetPaisa: number;
  burnedBudgetPaisa: number;
  status: 'active' | 'paused' | 'exhausted' | 'expired';
  validFrom: string;
  validUntil: string;
  verticals: string[];
}

export interface AuditLogEntry {
  id: string;
  timestamp: string;
  adminName: string;
  adminEmail: string;
  actionType: string;
  details: string;
  ipAddress: string;
}

export interface AdminUserItem {
  id: string;
  name: string;
  phone: string;
  role: "consumer" | "driver" | "rider" | "merchant" | "admin" | "superadmin";
  status: "active" | "suspended" | "banned" | "cod_disabled";
  registeredDate: string;
  deviceUuid: string;
  totalOrders: number;
  codViolationCount: number;
  codEnabled?: boolean;
  strikeReason?: string;
}

export interface AdminNotification {
  id: string;
  type: "urgent" | "kyc" | "payout" | "dispute" | "system";
  title: string;
  message: string;
  time: string;
  read: boolean;
  actionUrl: string;
  actionLabel?: string;
}
