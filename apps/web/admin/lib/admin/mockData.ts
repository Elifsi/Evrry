import {
  PlatformPricingSettings,
  KycDocumentItem,
  MidnightPayoutBatch,
  RiderCodBalance,
  VoucherCampaign,
  AuditLogEntry,
} from './types';

export const initialPricingSettings: PlatformPricingSettings = {
  platformFeePaisa: 1000, // Rs 10.00
  platformFeeWaiver: false,
  ridePlatformFeePaisa: 0,
  freeDeliveryThresholdPaisa: 100000, // Rs 1,000.00
  baseDeliveryRadiusMeters: 3000, // 3 km
  baseDeliveryFeePaisa: 5000, // Rs 50.00
  extraDistanceFeePerKmPaisa: 1500, // Rs 15.00 / km
  driverBaseSharePaisa: 4000, // Rs 40.00
  driverExtraDistanceShareBps: 8000, // 80%
  stayServiceFeeBps: 500, // 5%
  lateNightSurchargePaisa: 0,
  weatherSurgeMultiplier: 1.0,
  vatBps: 0, // 0% pre-registration, toggleable to 1300 (13%)
  commissions: {
    foodBps: 1500, // 15%
    groceryBps: 800, // 8%
    bikeTaxiBps: 1000, // 10%
    cabTaxiBps: 1200, // 12%
    hotelBps: 1000, // 10%
    rentalBps: 500, // 5%
  },
};

export const sampleKycQueue: KycDocumentItem[] = [
  {
    id: 'kyc-001',
    partnerName: 'Ram Bahadur Shrestha',
    partnerType: 'rider',
    phoneNumber: '+977 9841234567',
    submittedAt: '2026-10-07 09:30 AM',
    status: 'pending',
    vehicleInfo: {
      type: 'Motorcycle (Two-Wheeler)',
      plateNumber: 'Ba 85 Pa 4921',
      model: 'Bajaj Pulsar 150 (2022)',
      taxClearanceYear: 2082,
    },
    licenseInfo: {
      number: '01-06-00294812',
      expiryDate: '2029-08-14',
      issueDistrict: 'Kathmandu (Ekantakuna)',
    },
    documents: [
      {
        type: 'license_front',
        title: 'Nepali Smart Driving License (Front)',
        imageUrl: 'https://images.unsplash.com/photo-1589829545856-d10d557cf95f?auto=format&fit=crop&w=800&q=80',
      },
      {
        type: 'bluebook_tax',
        title: 'Vehicle Blue Book (Tax Clearance & Ownership)',
        imageUrl: 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=800&q=80',
      },
      {
        type: 'nagarikta',
        title: 'Nepali Citizenship (Nagarikta)',
        imageUrl: 'https://images.unsplash.com/photo-1450133064473-71024230f91b?auto=format&fit=crop&w=800&q=80',
      },
    ],
  },
  {
    id: 'kyc-002',
    partnerName: 'Himalayan Java Coffee Thamel',
    partnerType: 'restaurant',
    phoneNumber: '+977 9801987654',
    submittedAt: '2026-10-07 10:15 AM',
    status: 'pending',
    panVatInfo: {
      panNumber: '601293847',
      businessName: 'Himalayan Java Pvt Ltd',
      entityType: 'Private Limited Company',
    },
    documents: [
      {
        type: 'pan_certificate',
        title: 'Inland Revenue Department PAN/VAT Certificate',
        imageUrl: 'https://images.unsplash.com/photo-1554224154-26032ffc0d07?auto=format&fit=crop&w=800&q=80',
      },
      {
        type: 'food_license',
        title: 'Food Hygiene & Department of Food Tech License',
        imageUrl: 'https://images.unsplash.com/photo-1450133064473-71024230f91b?auto=format&fit=crop&w=800&q=80',
      },
    ],
  },
  {
    id: 'kyc-003',
    partnerName: 'Bikash Tamang',
    partnerType: 'driver',
    phoneNumber: '+977 9813009988',
    submittedAt: '2026-10-06 04:45 PM',
    status: 'approved',
    vehicleInfo: {
      type: 'Cab / Sedan (Four-Wheeler Taxi)',
      plateNumber: 'Ba 1 Ja 7812',
      model: 'Suzuki Dzire Tour (2023)',
      taxClearanceYear: 2083,
    },
    licenseInfo: {
      number: '03-08-00511893',
      expiryDate: '2030-01-20',
      issueDistrict: 'Lalitpur',
    },
    documents: [
      {
        type: 'license_front',
        title: 'Smart Driving License (Category B - Light Vehicle)',
        imageUrl: 'https://images.unsplash.com/photo-1589829545856-d10d557cf95f?auto=format&fit=crop&w=800&q=80',
      },
    ],
  },
  {
    id: 'kyc-004',
    partnerName: 'Everest Kirana & Departmental Mart',
    partnerType: 'grocery',
    phoneNumber: '+977 9851022334',
    submittedAt: '2026-10-06 02:10 PM',
    status: 'reupload_requested',
    rejectionReason: 'VAT certificate photo was blurry and corner was cut off. Please re-upload full page.',
    panVatInfo: {
      panNumber: '302918274',
      businessName: 'Everest Grocery Traders',
      entityType: 'Sole Proprietorship',
    },
    documents: [
      {
        type: 'pan_certificate',
        title: 'VAT Certificate (Blurry sample)',
        imageUrl: 'https://images.unsplash.com/photo-1554224154-26032ffc0d07?auto=format&fit=crop&w=800&q=80',
      },
    ],
  },
];

export const samplePayoutBatch: MidnightPayoutBatch = {
  batchId: 'BATCH-2026-10-07-MIDNIGHT',
  date: '2026-10-07',
  status: 'pending_approval',
  grossMerchantEarningsPaisa: 184500000, // NPR 1,845,000
  platformCommissionsDeductedPaisa: 27675000, // NPR 276,750 (15%)
  netPayableMerchantsPaisa: 156825000, // NPR 1,568,250
  grossRiderFeesPaisa: 42100000, // NPR 421,000
  riderCodDeductedPaisa: 28540000, // NPR 285,400 (cash retained by riders)
  netPayableRidersPaisa: 13560000, // NPR 135,600
  totalDisbursementPaisa: 170385000, // NPR 1,703,850 total
  merchantCount: 48,
  riderCount: 112,
};

export const sampleRiderCodBalances: RiderCodBalance[] = [
  {
    riderId: 'r-01',
    riderName: 'Sunil Gurung',
    phoneNumber: '+977 9841887766',
    cashInHandPaisa: 520000, // Rs 5,200 -> Exceeds Rs 5,000 safety lock
    ordersDeliveredToday: 14,
    safetyStatus: 'hard_locked',
    lastDepositTime: 'Yesterday 07:45 PM',
  },
  {
    riderId: 'r-02',
    riderName: 'Anil Khadka',
    phoneNumber: '+977 9801223344',
    cashInHandPaisa: 385000, // Rs 3,850 -> Exceeds Rs 3,500 soft warning
    ordersDeliveredToday: 9,
    safetyStatus: 'soft_warning',
    lastDepositTime: 'Today 11:30 AM',
  },
  {
    riderId: 'r-03',
    riderName: 'Dawa Sherpa',
    phoneNumber: '+977 9860112233',
    cashInHandPaisa: 140000, // Rs 1,400 -> Normal
    ordersDeliveredToday: 6,
    safetyStatus: 'normal',
    lastDepositTime: 'Today 02:15 PM',
  },
  {
    riderId: 'r-04',
    riderName: 'Prakash Maharjan',
    phoneNumber: '+977 9841554433',
    cashInHandPaisa: 85000, // Rs 850 -> Normal
    ordersDeliveredToday: 4,
    safetyStatus: 'normal',
    lastDepositTime: 'Today 01:00 PM',
  },
];

export const sampleVouchers: VoucherCampaign[] = [
  {
    id: 'v-01',
    code: 'EVRRYNEW100',
    title: 'Welcome to EVRRY! Flat Rs 100 Off',
    description: 'Flat Rs 100 discount on your first food or mart delivery order over Rs 300.',
    discountType: 'flat',
    discountValue: 100,
    minOrderValuePaisa: 30000, // Rs 300
    maxDiscountCapPaisa: 10000, // Rs 100
    perUserLimit: 1,
    totalRedemptionsCap: 5000,
    redeemedCount: 3420,
    allocatedBudgetPaisa: 50000000, // Rs 500,000
    burnedBudgetPaisa: 34200000, // Rs 342,000
    status: 'active',
    validFrom: '2026-10-01',
    validUntil: '2026-11-30',
    verticals: ['food', 'grocery'],
  },
  {
    id: 'v-02',
    code: 'DASHAIN50',
    title: 'Dashain Festival Feast: 50% Off',
    description: '50% off dining and restaurants up to Rs 150 for the festive season.',
    discountType: 'percentage',
    discountValue: 50,
    minOrderValuePaisa: 25000,
    maxDiscountCapPaisa: 15000,
    perUserLimit: 2,
    totalRedemptionsCap: 10000,
    redeemedCount: 8950,
    allocatedBudgetPaisa: 150000000,
    burnedBudgetPaisa: 134250000,
    status: 'active',
    validFrom: '2026-10-05',
    validUntil: '2026-10-25',
    verticals: ['food'],
  },
  {
    id: 'v-03',
    code: 'FREESHIP',
    title: 'Free Delivery Special',
    description: 'Waives delivery fee completely on all Kirana grocery orders.',
    discountType: 'flat',
    discountValue: 50,
    minOrderValuePaisa: 50000,
    maxDiscountCapPaisa: 5000,
    perUserLimit: 5,
    totalRedemptionsCap: 2000,
    redeemedCount: 2000,
    allocatedBudgetPaisa: 10000000,
    burnedBudgetPaisa: 10000000,
    status: 'exhausted',
    validFrom: '2026-09-15',
    validUntil: '2026-10-05',
    verticals: ['grocery'],
  },
];

export const sampleAuditLogs: AuditLogEntry[] = [
  {
    id: 'log-101',
    timestamp: '2026-10-07 11:45 AM',
    adminName: 'Suman Adhikari (VP Operations)',
    adminEmail: 'suman@evrry.com.np',
    actionType: 'PRICING_UPDATED',
    details: 'Set standard platform fee to Rs 10.00 and free delivery threshold to Rs 1,000.',
    ipAddress: '103.10.29.14 (Kathmandu)',
  },
  {
    id: 'log-102',
    timestamp: '2026-10-07 10:20 AM',
    adminName: 'Pratima Sharma (KYC Compliance)',
    adminEmail: 'pratima@evrry.com.np',
    actionType: 'KYC_APPROVED',
    details: 'Approved Bikash Tamang (Dzire Tour Ba 1 Ja 7812). SMS confirmation triggered.',
    ipAddress: '103.10.29.18 (Kathmandu)',
  },
  {
    id: 'log-103',
    timestamp: '2026-10-07 00:05 AM',
    adminName: 'System Automated pg_cron',
    adminEmail: 'cron@internal.evrry.com.np',
    actionType: 'SETTLEMENT_BATCH_GENERATED',
    details: 'Midnight settlement batch BATCH-2026-10-07 created with 48 merchants and 112 riders.',
    ipAddress: '10.0.0.5 (OCI Core Server)',
  },
];
