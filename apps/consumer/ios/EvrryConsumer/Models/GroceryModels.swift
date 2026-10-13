//
//  GroceryModels.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Domain Models for Grocery Quick-Commerce Vertical (Express Quick Mart)
//  Matching Android 1:1 Parity
//

import Foundation

public enum GrocerySubstitutionPreference: String, Codable, CaseIterable {
    case allowStoreSubstitution = "ALLOW_STORE_SUBSTITUTION"
    case callCustomer = "CALL_CUSTOMER"
    case refundItem = "REFUND_ITEM"
    
    public var label: String {
        switch self {
        case .allowStoreSubstitution:
            return "Allow closest item replacement"
        case .callCustomer:
            return "Call me before replacement"
        case .refundItem:
            return "Refund item if out of stock"
        }
    }
}

public enum GroceryDeliveryMode: String, Codable, CaseIterable {
    case expressDarkstore = "EXPRESS_DARKSTORE"
    case groceryStores = "GROCERY_STORES"
}

public struct GroceryStore: Identifiable, Codable, Equatable {
    public let id: String
    public let name: String
    public let tagline: String
    public let storeType: String
    public let area: String
    public let distanceKm: Double
    public let etaRange: String
    public let rating: Double
    public let reviewCount: Int
    public let minOrderNpr: Double
    public let freeDeliveryAboveNpr: Double
    public let isOpen: Bool
    public let openingHours: String
    public let badge: String
    public let emoji: String
    public let featuredTags: [String]
    public let aisles: [String]
    
    public init(
        id: String,
        name: String,
        tagline: String,
        storeType: String,
        area: String,
        distanceKm: Double,
        etaRange: String = "25-40 min",
        rating: Double = 4.8,
        reviewCount: Int = 350,
        minOrderNpr: Double = 300.0,
        freeDeliveryAboveNpr: Double = 1200.0,
        isOpen: Bool = true,
        openingHours: String = "7:00 AM - 9:30 PM",
        badge: String = "PARTNER",
        emoji: String = "🏪",
        featuredTags: [String] = [],
        aisles: [String] = []
    ) {
        self.id = id
        self.name = name
        self.tagline = tagline
        self.storeType = storeType
        self.area = area
        self.distanceKm = distanceKm
        self.etaRange = etaRange
        self.rating = rating
        self.reviewCount = reviewCount
        self.minOrderNpr = minOrderNpr
        self.freeDeliveryAboveNpr = freeDeliveryAboveNpr
        self.isOpen = isOpen
        self.openingHours = openingHours
        self.badge = badge
        self.emoji = emoji
        self.featuredTags = featuredTags
        self.aisles = aisles
    }
}

public struct GroceryHub: Identifiable, Codable {
    public let id: String
    public let name: String
    public let area: String
    public let etaMinutes: Int
    public let distanceKm: Double
    public let isOpen: BooleanLiteralType
    public let minOrderNpr: Double
    public let freeDeliveryAboveNpr: Double
    
    public init(
        id: String,
        name: String,
        area: String,
        etaMinutes: Int = 12,
        distanceKm: Double = 1.2,
        isOpen: Bool = true,
        minOrderNpr: Double = 150.0,
        freeDeliveryAboveNpr: Double = 1000.0
    ) {
        self.id = id
        self.name = name
        self.area = area
        self.etaMinutes = etaMinutes
        self.distanceKm = distanceKm
        self.isOpen = isOpen
        self.minOrderNpr = minOrderNpr
        self.freeDeliveryAboveNpr = freeDeliveryAboveNpr
    }
}

public struct GroceryCategory: Identifiable, Codable {
    public let id: String
    public let name: String
    public let nameNe: String
    public let emoji: String
    public let subcategories: [String]
    
    public init(id: String, name: String, nameNe: String, emoji: String, subcategories: [String] = []) {
        self.id = id
        self.name = name
        self.nameNe = nameNe
        self.emoji = emoji
        self.subcategories = subcategories
    }
}

public struct GroceryProductVariant: Identifiable, Codable, Equatable {
    public let id: String
    public let packSize: String
    public let unitQuantity: Double
    public let unitType: String
    public let priceNpr: Double
    public let mrpNpr: Double
    public let discountPercent: Int
    public let stockQuantity: Int
    
    public init(
        id: String,
        packSize: String,
        unitQuantity: Double,
        unitType: String,
        priceNpr: Double,
        mrpNpr: Double,
        discountPercent: Int = 0,
        stockQuantity: Int = 20
    ) {
        self.id = id
        self.packSize = packSize
        self.unitQuantity = unitQuantity
        self.unitType = unitType
        self.priceNpr = priceNpr
        self.mrpNpr = mrpNpr
        self.discountPercent = discountPercent
        self.stockQuantity = stockQuantity
    }
}

public struct GroceryProduct: Identifiable, Codable {
    public let id: String
    public let hubId: String
    public let storeId: String?
    public let categoryId: String
    public let subCategory: String
    public let title: String
    public let titleNe: String?
    public let brand: String
    public let unitQuantity: Double
    public let unitType: String
    public let packSize: String
    public let priceNpr: Double
    public let mrpNpr: Double
    public let discountPercent: Int
    public let stockQuantity: Int
    public let maxPerOrder: Int
    public let isPerishable: Bool
    public let shelfLife: String?
    public let imageUrl: String?
    public let tags: [String]
    public let variants: [GroceryProductVariant]
    
    public init(
        id: String,
        hubId: String = "hub-jhamsikhel-01",
        storeId: String? = nil,
        categoryId: String,
        subCategory: String,
        title: String,
        titleNe: String? = nil,
        brand: String,
        unitQuantity: Double,
        unitType: String,
        packSize: String,
        priceNpr: Double,
        mrpNpr: Double,
        discountPercent: Int = 0,
        stockQuantity: Int = 20,
        maxPerOrder: Int = 6,
        isPerishable: Bool = false,
        shelfLife: String? = nil,
        imageUrl: String? = nil,
        tags: [String] = [],
        variants: [GroceryProductVariant] = []
    ) {
        self.id = id
        self.hubId = hubId
        self.storeId = storeId
        self.categoryId = categoryId
        self.subCategory = subCategory
        self.title = title
        self.titleNe = titleNe
        self.brand = brand
        self.unitQuantity = unitQuantity
        self.unitType = unitType
        self.packSize = packSize
        self.priceNpr = priceNpr
        self.mrpNpr = mrpNpr
        self.discountPercent = discountPercent
        self.stockQuantity = stockQuantity
        self.maxPerOrder = maxPerOrder
        self.isPerishable = isPerishable
        self.shelfLife = shelfLife
        self.imageUrl = imageUrl
        self.tags = tags
        self.variants = variants
    }
}

public struct GroceryCatalogData {
    public static let defaultHub = GroceryHub(
        id: "hub-jhamsikhel-01",
        name: "Evrry Quick Mart • Jhamsikhel Hub",
        area: "Jhamsikhel, Lalitpur",
        etaMinutes: 12,
        distanceKm: 1.2,
        isOpen: true,
        minOrderNpr: 150.0,
        freeDeliveryAboveNpr: 1000.0
    )
    
    public static let categories: [GroceryCategory] = [
        GroceryCategory(
            id: "dairy",
            name: "Dairy & Breakfast",
            nameNe: "दूध र नास्ता",
            emoji: "🥛",
            subcategories: ["All", "Milk", "Curd & Yogurt", "Paneer & Tofu", "Butter & Ghee", "Eggs & Bread"]
        ),
        GroceryCategory(
            id: "produce",
            name: "Fresh Veggies & Fruits",
            nameNe: "तरकारी तथा फलफूल",
            emoji: "🥦",
            subcategories: ["All", "Daily Veggies", "Leafy Greens", "Potatoes & Onions", "Fresh Fruits"]
        ),
        GroceryCategory(
            id: "munchies",
            name: "Munchies & Snacks",
            nameNe: "खाजा तथा स्न्याक्स",
            emoji: "🥨",
            subcategories: ["All", "Chips & Crisps", "Bhujia & Dalmoth", "Biscuits & Cookies", "Chocolates"]
        ),
        GroceryCategory(
            id: "instant",
            name: "Instant Noodles & Soups",
            nameNe: "चाउचाउ तथा सुप",
            emoji: "🍜",
            subcategories: ["All", "Wai Wai & Chowchow", "Spicy Korean Ramen", "Instant Pasta", "Soups"]
        ),
        GroceryCategory(
            id: "staples",
            name: "Atta, Rice & Dal",
            nameNe: "चामल, दाल र पिठो",
            emoji: "🍚",
            subcategories: ["All", "Basmati Rice", "Chamal & Chiura", "Atta & Maida", "Lentils & Dal", "Cooking Oils"]
        ),
        GroceryCategory(
            id: "beverages",
            name: "Cold Drinks & Juices",
            nameNe: "पेय पदार्थ",
            emoji: "🧃",
            subcategories: ["All", "Soft Drinks", "Real Juices", "Tea & Coffee", "Energy Drinks"]
        ),
        GroceryCategory(
            id: "bakery",
            name: "Bakery & Breads",
            nameNe: "पाउरोटी र बेकरी",
            emoji: "🍞",
            subcategories: ["All", "Sliced Breads", "Buns & Pav", "Cakes & Pastries", "Cookies"]
        ),
        GroceryCategory(
            id: "cleaning",
            name: "Cleaning & Household",
            nameNe: "सरसफाई तथा घरायसी",
            emoji: "🧼",
            subcategories: ["All", "Detergents", "Dishwash", "Floor Cleaners", "Tissue Papers"]
        ),
        GroceryCategory(
            id: "personal",
            name: "Personal Care",
            nameNe: "व्यक्तिगत स्याहार",
            emoji: "🧴",
            subcategories: ["All", "Soaps & Handwash", "Hair Care", "Oral Care", "Skin Care"]
        )
    ]
    
    public static let products: [GroceryProduct] = [
        // DAIRY & BREAKFAST
        GroceryProduct(
            id: "groc-ddc-milk-500",
            hubId: "hub-jhamsikhel-01",
            categoryId: "dairy",
            subCategory: "Milk",
            title: "DDC Standardized Blue Milk",
            titleNe: "डीडीसी प्रशोधित निलो दुध",
            brand: "DDC",
            unitQuantity: 500.0,
            unitType: "ml",
            packSize: "500 ml Pouch",
            priceNpr: 50.0,
            mrpNpr: 55.0,
            discountPercent: 9,
            stockQuantity: 45,
            maxPerOrder: 6,
            isPerishable: true,
            shelfLife: "Use within 2 days of delivery",
            tags: ["Bestseller", "Daily Essential", "Morning Fresh"],
            variants: [
                GroceryProductVariant(
                    id: "groc-ddc-milk-500",
                    packSize: "500 ml",
                    unitQuantity: 500.0,
                    unitType: "ml",
                    priceNpr: 50.0,
                    mrpNpr: 55.0,
                    discountPercent: 9,
                    stockQuantity: 45
                ),
                GroceryProductVariant(
                    id: "groc-ddc-cow-milk-1l",
                    packSize: "1 Litre",
                    unitQuantity: 1.0,
                    unitType: "L",
                    priceNpr: 110.0,
                    mrpNpr: 120.0,
                    discountPercent: 8,
                    stockQuantity: 28
                )
            ]
        ),
        GroceryProduct(
            id: "groc-local-eggs-6",
            hubId: "hub-jhamsikhel-01",
            categoryId: "dairy",
            subCategory: "Eggs & Bread",
            title: "Fresh Farm Brown Eggs",
            titleNe: "ताजा फार्म अण्डा (६ वटा)",
            brand: "Valley Poultry",
            unitQuantity: 6.0,
            unitType: "pcs",
            packSize: "Pack of 6 Eggs",
            priceNpr: 110.0,
            mrpNpr: 120.0,
            discountPercent: 8,
            stockQuantity: 32,
            maxPerOrder: 4,
            isPerishable: true,
            shelfLife: "Best before 14 days",
            tags: ["Daily Pack", "Farm Fresh"],
            variants: [
                GroceryProductVariant(
                    id: "groc-local-eggs-6",
                    packSize: "6 Pcs",
                    unitQuantity: 6.0,
                    unitType: "pcs",
                    priceNpr: 110.0,
                    mrpNpr: 120.0,
                    discountPercent: 8,
                    stockQuantity: 32
                ),
                GroceryProductVariant(
                    id: "groc-local-eggs-crate",
                    packSize: "Crate of 30",
                    unitQuantity: 30.0,
                    unitType: "pcs",
                    priceNpr: 480.0,
                    mrpNpr: 520.0,
                    discountPercent: 8,
                    stockQuantity: 16
                )
            ]
        ),
        GroceryProduct(
            id: "groc-patan-sliced-bread",
            hubId: "hub-jhamsikhel-01",
            categoryId: "dairy",
            subCategory: "Eggs & Bread",
            title: "Patan Bakery White Sliced Bread",
            titleNe: "पाटन बेकरी सेतो पाउरोटी",
            brand: "Patan Bakery",
            unitQuantity: 400.0,
            unitType: "g",
            packSize: "400 g Loaf",
            priceNpr: 65.0,
            mrpNpr: 70.0,
            discountPercent: 7,
            stockQuantity: 22,
            maxPerOrder: 3,
            isPerishable: true,
            shelfLife: "Best before 3 days",
            tags: ["Oven Fresh"]
        ),
        GroceryProduct(
            id: "groc-cow-ghee-500",
            hubId: "hub-jhamsikhel-01",
            categoryId: "dairy",
            subCategory: "Butter & Ghee",
            title: "Nepal Dairy Pure Cow Ghee",
            titleNe: "नेपाल डेरी शुद्ध गाईको घ्यु",
            brand: "Nepal Dairy",
            unitQuantity: 500.0,
            unitType: "ml",
            packSize: "500 ml Glass Jar",
            priceNpr: 620.0,
            mrpNpr: 675.0,
            discountPercent: 8,
            stockQuantity: 14,
            maxPerOrder: 2,
            isPerishable: false,
            shelfLife: "Best before 6 months",
            tags: ["Pure Himalayan", "Aroma"]
        ),

        // PRODUCE
        GroceryProduct(
            id: "groc-red-potatoes-2kg",
            hubId: "hub-jhamsikhel-01",
            categoryId: "produce",
            subCategory: "Potatoes & Onions",
            title: "Kathmandu Valley Red Potatoes",
            titleNe: "काठमाडौं उपत्यका रातो आलु",
            brand: "Mude Fresh",
            unitQuantity: 2.0,
            unitType: "kg",
            packSize: "2 Kg Mesh Bag",
            priceNpr: 135.0,
            mrpNpr: 160.0,
            discountPercent: 16,
            stockQuantity: 40,
            maxPerOrder: 3,
            isPerishable: true,
            shelfLife: "Store in a cool dry place",
            tags: ["Pahari Alu", "Kitchen Base"]
        ),
        GroceryProduct(
            id: "groc-red-onions-1kg",
            hubId: "hub-jhamsikhel-01",
            categoryId: "produce",
            subCategory: "Potatoes & Onions",
            title: "Fresh Red Onions (Pyaj)",
            titleNe: "ताजा रातो प्याज",
            brand: "Farm Fresh",
            unitQuantity: 1.0,
            unitType: "kg",
            packSize: "1 Kg Bag",
            priceNpr: 85.0,
            mrpNpr: 95.0,
            discountPercent: 11,
            stockQuantity: 35,
            maxPerOrder: 3,
            isPerishable: true,
            tags: ["Daily Essential"]
        ),
        GroceryProduct(
            id: "groc-palungo-saag",
            hubId: "hub-jhamsikhel-01",
            categoryId: "produce",
            subCategory: "Leafy Greens",
            title: "Organic Palung Spinach (Palungo)",
            titleNe: "अर्ग्यानिक पालुङ्गो साग",
            brand: "Palung Organic",
            unitQuantity: 500.0,
            unitType: "g",
            packSize: "500 g Bundle",
            priceNpr: 60.0,
            mrpNpr: 75.0,
            discountPercent: 20,
            stockQuantity: 18,
            maxPerOrder: 2,
            isPerishable: true,
            shelfLife: "Freshly harvested this morning",
            tags: ["Organic", "Rich in Iron"]
        ),
        GroceryProduct(
            id: "groc-jumla-apples-1kg",
            hubId: "hub-jhamsikhel-01",
            categoryId: "produce",
            subCategory: "Fresh Fruits",
            title: "Jumla Royal Delicious Apples",
            titleNe: "जुम्लाको अर्ग्यानिक स्याउ",
            brand: "Himalayan Organic",
            unitQuantity: 1.0,
            unitType: "kg",
            packSize: "1 Kg Pack (4-5 Apples)",
            priceNpr: 240.0,
            mrpNpr: 280.0,
            discountPercent: 14,
            stockQuantity: 20,
            maxPerOrder: 3,
            isPerishable: true,
            tags: ["Direct from Jumla", "Crisp & Sweet"]
        ),

        // INSTANT NOODLES
        GroceryProduct(
            id: "groc-waiwai-chicken-5pack",
            hubId: "hub-jhamsikhel-01",
            categoryId: "instant",
            subCategory: "Wai Wai & Chowchow",
            title: "Wai Wai Quick Chicken Noodles (5 Pack)",
            titleNe: "वाइ वाइ चिकेन चाउचाउ (५ प्याक)",
            brand: "CG Foods",
            unitQuantity: 5.0,
            unitType: "pack",
            packSize: "75 g x 5 Packets",
            priceNpr: 125.0,
            mrpNpr: 135.0,
            discountPercent: 7,
            stockQuantity: 60,
            maxPerOrder: 4,
            isPerishable: false,
            shelfLife: "Best before 9 months",
            tags: ["National Favorite", "Ready to Eat"]
        ),
        GroceryProduct(
            id: "groc-rara-noodles-5pack",
            hubId: "hub-jhamsikhel-01",
            categoryId: "instant",
            subCategory: "Wai Wai & Chowchow",
            title: "Rara Classic White Noodles (5 Pack)",
            titleNe: "रारा सेतो चाउचाउ (५ प्याक)",
            brand: "Gandaki Noodles",
            unitQuantity: 5.0,
            unitType: "pack",
            packSize: "70 g x 5 Packets",
            priceNpr: 115.0,
            mrpNpr: 125.0,
            discountPercent: 8,
            stockQuantity: 40,
            maxPerOrder: 4,
            isPerishable: false,
            shelfLife: "Best before 9 months",
            tags: ["Classic Taste"]
        ),
        GroceryProduct(
            id: "groc-current-hot-spicy-noodles",
            hubId: "hub-jhamsikhel-01",
            categoryId: "instant",
            subCategory: "Spicy Korean Ramen",
            title: "Current 2X Hot & Spicy 2PM Noodles",
            titleNe: "करेन्ट टु-एक्स पिरो चाउचाउ",
            brand: "Yashoda Foods",
            unitQuantity: 100.0,
            unitType: "g",
            packSize: "100 g Pouch",
            priceNpr: 55.0,
            mrpNpr: 60.0,
            discountPercent: 8,
            stockQuantity: 50,
            maxPerOrder: 6,
            isPerishable: false,
            tags: ["Extra Spicy", "Trending"]
        ),

        // STAPLES & OILS
        GroceryProduct(
            id: "groc-hetauda-mustard-oil-1l",
            hubId: "hub-jhamsikhel-01",
            categoryId: "staples",
            subCategory: "Cooking Oils",
            title: "Hetauda Cold-Pressed Mustard Oil",
            titleNe: "हेटौंडा शुद्ध तोरीको तेल",
            brand: "Hetauda Agro",
            unitQuantity: 1.0,
            unitType: "L",
            packSize: "1 Litre Bottle",
            priceNpr: 275.0,
            mrpNpr: 310.0,
            discountPercent: 11,
            stockQuantity: 25,
            maxPerOrder: 3,
            isPerishable: false,
            shelfLife: "Best before 12 months",
            tags: ["Pure Kacchi Ghani", "Traditional Aroma"]
        ),
        GroceryProduct(
            id: "groc-mansuli-rice-5kg",
            hubId: "hub-jhamsikhel-01",
            categoryId: "staples",
            subCategory: "Basmati Rice",
            title: "Himalayan Mansuli Premium Rice (5 Kg)",
            titleNe: "मनसुली चामल (५ केजी)",
            brand: "Himalayan Grains",
            unitQuantity: 5.0,
            unitType: "kg",
            packSize: "5 Kg Sealed Bag",
            priceNpr: 490.0,
            mrpNpr: 540.0,
            discountPercent: 9,
            stockQuantity: 18,
            maxPerOrder: 2,
            isPerishable: false,
            shelfLife: "Best before 12 months",
            tags: ["Daily Rice", "Aged Grains"]
        ),
        GroceryProduct(
            id: "groc-tokla-tea-500g",
            hubId: "hub-jhamsikhel-01",
            categoryId: "staples",
            subCategory: "Tea & Coffee",
            title: "Tokla Gold CTC Leaf Tea",
            titleNe: "तोक्ला गोल्ड सिटिसि चिया",
            brand: "Nepal Tea",
            unitQuantity: 500.0,
            unitType: "g",
            packSize: "500 g Pack",
            priceNpr: 260.0,
            mrpNpr: 285.0,
            discountPercent: 9,
            stockQuantity: 30,
            maxPerOrder: 3,
            isPerishable: false,
            tags: ["Jhapa Gardens", "Rich Liquor"]
        ),

        // CLEANING & PERSONAL
        GroceryProduct(
            id: "groc-dettol-handwash-200",
            hubId: "hub-jhamsikhel-01",
            categoryId: "personal",
            subCategory: "Soaps & Handwash",
            title: "Dettol Original Germ Protect Handwash",
            titleNe: "डेटोल ह्यान्डवास",
            brand: "Dettol",
            unitQuantity: 200.0,
            unitType: "ml",
            packSize: "200 ml Pump Bottle",
            priceNpr: 145.0,
            mrpNpr: 160.0,
            discountPercent: 9,
            stockQuantity: 35,
            maxPerOrder: 3,
            isPerishable: false,
            tags: ["Antibacterial", "Family Hygiene"]
        ),
        GroceryProduct(
            id: "groc-surf-excel-1kg",
            hubId: "hub-jhamsikhel-01",
            categoryId: "cleaning",
            subCategory: "Detergents",
            title: "Surf Excel Easy Wash Detergent Powder",
            titleNe: "सर्फ एक्सेल पाउडर",
            brand: "Unilever",
            unitQuantity: 1.0,
            unitType: "kg",
            packSize: "1 Kg Pack",
            priceNpr: 230.0,
            mrpNpr: 250.0,
            discountPercent: 8,
            stockQuantity: 24,
            maxPerOrder: 2,
            isPerishable: false,
            tags: ["Tough Stain Removal"]
        )
    ]

    public static let partnerStores: [GroceryStore] = [
        GroceryStore(
            id: "store-bhatbhateni-pulchowk",
            name: "Bhatbhateni Supermarket",
            tagline: "Nepal's Premier Supermarket • Everything under one roof",
            storeType: "Supermarket",
            area: "Pulchowk, Lalitpur",
            distanceKm: 1.8,
            etaRange: "30–45 mins",
            rating: 4.9,
            reviewCount: 2480,
            minOrderNpr: 250.0,
            freeDeliveryAboveNpr: 1000.0,
            isOpen: true,
            openingHours: "7:30 AM – 9:30 PM",
            badge: "🌟 Flagship Supermarket",
            emoji: "🏬",
            featuredTags: ["Supermarket", "Imported Goods", "Bakery", "Full Grocery"],
            aisles: ["Fresh Produce", "Dairy & Bakery", "Pantry & Grains", "Snacks & Drinks", "Household"]
        ),
        GroceryStore(
            id: "store-bigmart-jhamsikhel",
            name: "Big Mart",
            tagline: "Fresh local produce & everyday grocery essentials",
            storeType: "Mart Chain",
            area: "Jhamsikhel Ward 3, Lalitpur",
            distanceKm: 0.8,
            etaRange: "20–30 mins",
            rating: 4.8,
            reviewCount: 1320,
            minOrderNpr: 150.0,
            freeDeliveryAboveNpr: 1000.0,
            isOpen: true,
            openingHours: "7:00 AM – 9:00 PM",
            badge: "⚡ Fast Neighborhood Mart",
            emoji: "🏪",
            featuredTags: ["Neighborhood Mart", "Daily Produce", "Fast Dispatch"],
            aisles: ["Fresh Produce", "Dairy & Bakery", "Pantry & Grains", "Snacks & Drinks", "Household"]
        ),
        GroceryStore(
            id: "store-kathmandu-organics",
            name: "Kathmandu Organics",
            tagline: "100% Certified Organic Farm Produce, Honey & Himalayan Grains",
            storeType: "Organic Farm Store",
            area: "Sanepa Heights, Lalitpur",
            distanceKm: 1.4,
            etaRange: "25–35 mins",
            rating: 4.9,
            reviewCount: 640,
            minOrderNpr: 200.0,
            freeDeliveryAboveNpr: 1000.0,
            isOpen: true,
            openingHours: "8:00 AM – 8:00 PM",
            badge: "🥬 100% Organic Certified",
            emoji: "🌱",
            featuredTags: ["Organic", "Himalayan Grains", "Raw Honey", "Pesticide Free"],
            aisles: ["Fresh Produce", "Dairy & Bakery", "Pantry & Grains", "Snacks & Drinks", "Household"]
        ),
        GroceryStore(
            id: "store-saleways-jawalakhel",
            name: "Saleways Department Store",
            tagline: "Gourmet groceries, European cheese, spices & confectionery",
            storeType: "Department Store",
            area: "Jawalakhel Chowk, Lalitpur",
            distanceKm: 2.2,
            etaRange: "35–45 mins",
            rating: 4.8,
            reviewCount: 910,
            minOrderNpr: 300.0,
            freeDeliveryAboveNpr: 1000.0,
            isOpen: true,
            openingHours: "8:30 AM – 9:00 PM",
            badge: "🧀 Gourmet & Imported",
            emoji: "🛒",
            featuredTags: ["Gourmet", "Artisanal Cheese", "Bakery", "Imported"],
            aisles: ["Fresh Produce", "Dairy & Bakery", "Pantry & Grains", "Snacks & Drinks", "Household"]
        ),
        GroceryStore(
            id: "store-mitho-kirana",
            name: "Mitho Fresh Kirana",
            tagline: "Friendly local neighborhood Kirana • Instant cold drinks & snacks",
            storeType: "Local Kirana",
            area: "Kumaripati, Lalitpur",
            distanceKm: 0.5,
            etaRange: "15–20 mins",
            rating: 4.7,
            reviewCount: 410,
            minOrderNpr: 100.0,
            freeDeliveryAboveNpr: 1000.0,
            isOpen: true,
            openingHours: "6:30 AM – 10:00 PM",
            badge: "🥛 Instant Local Kirana",
            emoji: "🥤",
            featuredTags: ["Local Kirana", "Quick Snacks", "Cold Dairy", "Low Min Order"],
            aisles: ["Fresh Produce", "Dairy & Bakery", "Pantry & Grains", "Snacks & Drinks", "Household"]
        )
    ]

    public static let storeProducts: [GroceryProduct] = [
        // 1. BHATBHATENI SUPERMARKET PRODUCTS
        GroceryProduct(
            id: "groc-bb-kelloggs-cornflakes",
            storeId: "store-bhatbhateni-pulchowk",
            categoryId: "pantry",
            subCategory: "Pantry & Grains",
            title: "Kellogg's Corn Flakes (Real Almond & Honey)",
            titleNe: "केलोग्स कर्नफ्लेक्स",
            brand: "Kellogg's",
            unitQuantity: 500.0,
            unitType: "g",
            packSize: "500g Box",
            priceNpr: 480.0,
            mrpNpr: 520.0,
            discountPercent: 8,
            stockQuantity: 40,
            tags: ["Breakfast", "Imported", "Healthy"]
        ),
        GroceryProduct(
            id: "groc-bb-barilla-spaghetti",
            storeId: "store-bhatbhateni-pulchowk",
            categoryId: "pantry",
            subCategory: "Pantry & Grains",
            title: "Barilla Spaghetti No. 5 Durum Wheat Pasta",
            titleNe: "बरिला स्प्यागेटी पास्ता",
            brand: "Barilla",
            unitQuantity: 500.0,
            unitType: "g",
            packSize: "500g Pack",
            priceNpr: 340.0,
            mrpNpr: 375.0,
            discountPercent: 9,
            stockQuantity: 30,
            tags: ["Italian", "Pasta", "Durum Wheat"]
        ),
        GroceryProduct(
            id: "groc-bb-borges-olive-oil",
            storeId: "store-bhatbhateni-pulchowk",
            categoryId: "pantry",
            subCategory: "Pantry & Grains",
            title: "Borges Extra Virgin Olive Oil Cold-Extracted",
            titleNe: "बोर्गेस जैतूनको तेल",
            brand: "Borges",
            unitQuantity: 1.0,
            unitType: "L",
            packSize: "1 Litre Glass Bottle",
            priceNpr: 1650.0,
            mrpNpr: 1850.0,
            discountPercent: 11,
            stockQuantity: 20,
            tags: ["Cold Pressed", "Heart Healthy", "Cooking"]
        ),
        GroceryProduct(
            id: "groc-bb-daawat-basmati",
            storeId: "store-bhatbhateni-pulchowk",
            categoryId: "pantry",
            subCategory: "Pantry & Grains",
            title: "Daawat Rozana Gold Long Grain Basmati Rice",
            titleNe: "दावत बासमती चामल",
            brand: "Daawat",
            unitQuantity: 5.0,
            unitType: "kg",
            packSize: "5 Kg Bag",
            priceNpr: 1120.0,
            mrpNpr: 1250.0,
            discountPercent: 10,
            stockQuantity: 50,
            tags: ["Basmati", "Long Grain", "Bestseller"]
        ),
        GroceryProduct(
            id: "groc-bb-dairy-milk-silk",
            storeId: "store-bhatbhateni-pulchowk",
            categoryId: "snacks",
            subCategory: "Snacks & Drinks",
            title: "Cadbury Dairy Milk Silk Hazelnut Chocolate",
            titleNe: "डेयरी मिल्क सिल्क",
            brand: "Cadbury",
            unitQuantity: 143.0,
            unitType: "g",
            packSize: "143g Bar",
            priceNpr: 320.0,
            mrpNpr: 350.0,
            discountPercent: 9,
            stockQuantity: 60,
            tags: ["Chocolate", "Silk", "Sweet"]
        ),
        GroceryProduct(
            id: "groc-bb-ariel-matic",
            storeId: "store-bhatbhateni-pulchowk",
            categoryId: "cleaning",
            subCategory: "Household",
            title: "Ariel Matic Top Load Washing Powder",
            titleNe: "एरियल मेटिक पाउडर",
            brand: "Ariel",
            unitQuantity: 2.0,
            unitType: "kg",
            packSize: "2 Kg Pack",
            priceNpr: 680.0,
            mrpNpr: 750.0,
            discountPercent: 9,
            stockQuantity: 35,
            tags: ["Detergent", "Washing", "Fabric Care"]
        ),
        GroceryProduct(
            id: "groc-bb-amul-ghee",
            storeId: "store-bhatbhateni-pulchowk",
            categoryId: "dairy",
            subCategory: "Dairy & Bakery",
            title: "Amul Pure Cow Ghee Aroma Tin",
            titleNe: "अमुल गाईको घ्यु",
            brand: "Amul",
            unitQuantity: 1.0,
            unitType: "L",
            packSize: "1 Litre Tin",
            priceNpr: 1250.0,
            mrpNpr: 1380.0,
            discountPercent: 9,
            stockQuantity: 25,
            tags: ["Pure Ghee", "Cow Milk", "Aromatic"]
        ),
        GroceryProduct(
            id: "groc-bb-avocado-imported",
            storeId: "store-bhatbhateni-pulchowk",
            categoryId: "produce",
            subCategory: "Fresh Produce",
            title: "Hass Creamy Avocado (Imported)",
            titleNe: "एभोकाडो फल",
            brand: "Fresh Produce",
            unitQuantity: 1.0,
            unitType: "kg",
            packSize: "1 Kg (3-4 pcs)",
            priceNpr: 650.0,
            mrpNpr: 720.0,
            discountPercent: 10,
            stockQuantity: 15,
            tags: ["Creamy", "Salad", "Superfood"]
        ),

        // 2. BIG MART PRODUCTS
        GroceryProduct(
            id: "groc-bm-mude-potato",
            storeId: "store-bigmart-jhamsikhel",
            categoryId: "produce",
            subCategory: "Fresh Produce",
            title: "Mude Fresh Red Mountain Potatoes",
            titleNe: "मुडेको रातो आलु",
            brand: "Local Farm",
            unitQuantity: 2.0,
            unitType: "kg",
            packSize: "2 Kg Net Bag",
            priceNpr: 130.0,
            mrpNpr: 150.0,
            discountPercent: 13,
            stockQuantity: 50,
            tags: ["Fresh", "Local", "Staple"]
        ),
        GroceryProduct(
            id: "groc-bm-dalle-chilli",
            storeId: "store-bigmart-jhamsikhel",
            categoryId: "produce",
            subCategory: "Fresh Produce",
            title: "Dalle Khursani Fresh Round Hot Chilli",
            titleNe: "डल्ले खुर्सानी ताजा",
            brand: "Local Farm",
            unitQuantity: 200.0,
            unitType: "g",
            packSize: "200g Pouch",
            priceNpr: 90.0,
            mrpNpr: 100.0,
            discountPercent: 10,
            stockQuantity: 25,
            tags: ["Hot & Spicy", "Achar Essential"]
        ),
        GroceryProduct(
            id: "groc-bm-brown-bread",
            storeId: "store-bigmart-jhamsikhel",
            categoryId: "dairy",
            subCategory: "Dairy & Bakery",
            title: "Patan Bakery Whole Wheat Brown Bread",
            titleNe: "ब्राउन पाउरोटी",
            brand: "Patan Bakery",
            unitQuantity: 400.0,
            unitType: "g",
            packSize: "400g Sliced Loaf",
            priceNpr: 95.0,
            mrpNpr: 105.0,
            discountPercent: 10,
            stockQuantity: 30,
            tags: ["Daily Fresh", "Whole Wheat"]
        ),
        GroceryProduct(
            id: "groc-bm-ddc-cow-milk",
            storeId: "store-bigmart-jhamsikhel",
            categoryId: "dairy",
            subCategory: "Dairy & Bakery",
            title: "DDC Pure Cow Milk Pouch (1 Litre)",
            titleNe: "डिडिसी गाईको दुध १ लि",
            brand: "DDC Nepal",
            unitQuantity: 1.0,
            unitType: "L",
            packSize: "1 Litre (2x 500ml)",
            priceNpr: 115.0,
            mrpNpr: 120.0,
            discountPercent: 4,
            stockQuantity: 45,
            tags: ["Cow Milk", "Pasteurized", "Daily Essential"]
        ),
        GroceryProduct(
            id: "groc-bm-valley-eggs-30",
            storeId: "store-bigmart-jhamsikhel",
            categoryId: "dairy",
            subCategory: "Dairy & Bakery",
            title: "Valley Fresh Farm Brown Eggs (Crate)",
            titleNe: "भ्याली ताजा अण्डा ३० गोटा",
            brand: "Valley Poultry",
            unitQuantity: 30.0,
            unitType: "pcs",
            packSize: "Crate of 30 Eggs",
            priceNpr: 490.0,
            mrpNpr: 540.0,
            discountPercent: 9,
            stockQuantity: 20,
            tags: ["Farm Eggs", "Protein", "Crate"]
        ),
        GroceryProduct(
            id: "groc-bm-tokla-tea",
            storeId: "store-bigmart-jhamsikhel",
            categoryId: "snacks",
            subCategory: "Snacks & Drinks",
            title: "Tokla Gold CTC Himalayan Black Tea",
            titleNe: "तोक्ला चिया ५०० ग्रा",
            brand: "Tokla",
            unitQuantity: 500.0,
            unitType: "g",
            packSize: "500g Box",
            priceNpr: 290.0,
            mrpNpr: 320.0,
            discountPercent: 9,
            stockQuantity: 30,
            tags: ["Chiya", "Jhapa CTC", "Strong Brew"]
        ),
        GroceryProduct(
            id: "groc-bm-waiwai-5pack",
            storeId: "store-bigmart-jhamsikhel",
            categoryId: "snacks",
            subCategory: "Snacks & Drinks",
            title: "Wai Wai Quick Chicken Noodles Bundle",
            titleNe: "वाइवाइ चाउचाउ ५ प्याक",
            brand: "Chaudhary Group",
            unitQuantity: 375.0,
            unitType: "g",
            packSize: "Pack of 5 (375g)",
            priceNpr: 125.0,
            mrpNpr: 135.0,
            discountPercent: 7,
            stockQuantity: 75,
            tags: ["Instant Khaja", "Ready to Eat"]
        ),

        // 3. KATHMANDU ORGANICS PRODUCTS
        GroceryProduct(
            id: "groc-ko-mustang-rajma",
            storeId: "store-kathmandu-organics",
            categoryId: "pantry",
            subCategory: "Pantry & Grains",
            title: "Mustang Simikot Organic Red Rajma Beans",
            titleNe: "मुस्ताङ सिमिकोट रातो राजमा",
            brand: "Kathmandu Organics",
            unitQuantity: 1.0,
            unitType: "kg",
            packSize: "1 Kg Pouch",
            priceNpr: 360.0,
            mrpNpr: 400.0,
            discountPercent: 10,
            stockQuantity: 35,
            tags: ["Himalayan", "High Protein", "Organic"]
        ),
        GroceryProduct(
            id: "groc-ko-jumla-apples",
            storeId: "store-kathmandu-organics",
            categoryId: "produce",
            subCategory: "Fresh Produce",
            title: "Jumla Royal Organic Crunchy Red Apples",
            titleNe: "जुम्लाको अर्गानिक स्याउ",
            brand: "Kathmandu Organics",
            unitQuantity: 1.0,
            unitType: "kg",
            packSize: "1 Kg Bag",
            priceNpr: 240.0,
            mrpNpr: 280.0,
            discountPercent: 14,
            stockQuantity: 25,
            tags: ["Jumla Orchard", "Sweet & Juicy"]
        ),
        GroceryProduct(
            id: "groc-ko-hetauda-mustard-oil",
            storeId: "store-kathmandu-organics",
            categoryId: "pantry",
            subCategory: "Pantry & Grains",
            title: "Hetauda Wood-Pressed Pure Mustard Oil",
            titleNe: "हेटौंडा काठको तोरीको तेल",
            brand: "Kathmandu Organics",
            unitQuantity: 1.0,
            unitType: "L",
            packSize: "1 Litre Glass Bottle",
            priceNpr: 380.0,
            mrpNpr: 420.0,
            discountPercent: 10,
            stockQuantity: 25,
            tags: ["Wood Pressed", "Unrefined", "Pungent"]
        ),
        GroceryProduct(
            id: "groc-ko-raw-honey",
            storeId: "store-kathmandu-organics",
            categoryId: "pantry",
            subCategory: "Pantry & Grains",
            title: "Wild Himalayan Raw Multiflora Forest Honey",
            titleNe: "हिमाली जङ्गली काँचो मह",
            brand: "Kathmandu Organics",
            unitQuantity: 500.0,
            unitType: "g",
            packSize: "500g Glass Jar",
            priceNpr: 650.0,
            mrpNpr: 750.0,
            discountPercent: 13,
            stockQuantity: 20,
            tags: ["Raw Honey", "Forest Foraged", "Pure"]
        ),
        GroceryProduct(
            id: "groc-ko-buckwheat-flour",
            storeId: "store-kathmandu-organics",
            categoryId: "pantry",
            subCategory: "Pantry & Grains",
            title: "High Altitude Organic Phapar (Buckwheat) Flour",
            titleNe: "अर्गानिक फापरको पिठो",
            brand: "Kathmandu Organics",
            unitQuantity: 1.0,
            unitType: "kg",
            packSize: "1 Kg Pouch",
            priceNpr: 195.0,
            mrpNpr: 220.0,
            discountPercent: 11,
            stockQuantity: 30,
            tags: ["Gluten Free", "Dhido", "Roti"]
        ),
        GroceryProduct(
            id: "groc-ko-palungo-saag",
            storeId: "store-kathmandu-organics",
            categoryId: "produce",
            subCategory: "Fresh Produce",
            title: "Farm Fresh Organic Palungo Saag (Spinach)",
            titleNe: "पालुङ्गोको ताजा साग",
            brand: "Organic Farm",
            unitQuantity: 500.0,
            unitType: "g",
            packSize: "500g Fresh Bunch",
            priceNpr: 60.0,
            mrpNpr: 75.0,
            discountPercent: 20,
            stockQuantity: 40,
            tags: ["Pesticide Free", "Iron Rich", "Harvested Today"]
        ),
        GroceryProduct(
            id: "groc-ko-local-gundruk",
            storeId: "store-kathmandu-organics",
            categoryId: "produce",
            subCategory: "Fresh Produce",
            title: "Himalayan Handmade Gundruk Sun-Dried",
            titleNe: "नेपाली हातले बनाएको गुन्द्रुक",
            brand: "Kathmandu Organics",
            unitQuantity: 200.0,
            unitType: "g",
            packSize: "200g Pack",
            priceNpr: 140.0,
            mrpNpr: 160.0,
            discountPercent: 12,
            stockQuantity: 30,
            tags: ["Traditional", "Tangy Jhol", "Mustang Sun-Dried"]
        ),

        // 4. SALEWAYS DEPARTMENT STORE PRODUCTS
        GroceryProduct(
            id: "groc-sw-yak-cheese",
            storeId: "store-saleways-jawalakhel",
            categoryId: "dairy",
            subCategory: "Dairy & Bakery",
            title: "Kanchanpur Aged Himalayan Yak Cheese",
            titleNe: "हिमाली याक चिज",
            brand: "Dairy Development Corp",
            unitQuantity: 250.0,
            unitType: "g",
            packSize: "250g Vacuum Block",
            priceNpr: 420.0,
            mrpNpr: 460.0,
            discountPercent: 9,
            stockQuantity: 15,
            tags: ["Yak Milk", "Artisanal", "Aged"]
        ),
        GroceryProduct(
            id: "groc-sw-french-croissant",
            storeId: "store-saleways-jawalakhel",
            categoryId: "dairy",
            subCategory: "Dairy & Bakery",
            title: "French Butter Croissant Fresh Baked Box",
            titleNe: "फ्रेन्च बटर क्रोइसन्ट",
            brand: "Saleways Bakery",
            unitQuantity: 4.0,
            unitType: "pcs",
            packSize: "Box of 4 Pcs",
            priceNpr: 320.0,
            mrpNpr: 360.0,
            discountPercent: 11,
            stockQuantity: 20,
            tags: ["Flaky Butter", "Fresh Baked"]
        ),
        GroceryProduct(
            id: "groc-sw-trail-mix",
            storeId: "store-saleways-jawalakhel",
            categoryId: "snacks",
            subCategory: "Snacks & Drinks",
            title: "Premium Roasted Almonds & Walnuts Trail Mix",
            titleNe: "रोस्टेड बदाम र ओखर मिक्स",
            brand: "Nutty Delights",
            unitQuantity: 400.0,
            unitType: "g",
            packSize: "400g Zip Pack",
            priceNpr: 780.0,
            mrpNpr: 880.0,
            discountPercent: 11,
            stockQuantity: 25,
            tags: ["Nuts", "Healthy Snack", "Energy"]
        ),
        GroceryProduct(
            id: "groc-sw-twinings-tea",
            storeId: "store-saleways-jawalakhel",
            categoryId: "snacks",
            subCategory: "Snacks & Drinks",
            title: "Twinings Pure English Breakfast Tea Bags",
            titleNe: "ट्विनिङ्स ब्रेकफास्ट चिया",
            brand: "Twinings London",
            unitQuantity: 25.0,
            unitType: "pcs",
            packSize: "25 Tea Bags Box",
            priceNpr: 390.0,
            mrpNpr: 430.0,
            discountPercent: 9,
            stockQuantity: 30,
            tags: ["British Tea", "Premium Aroma"]
        ),
        GroceryProduct(
            id: "groc-sw-heinz-ketchup",
            storeId: "store-saleways-jawalakhel",
            categoryId: "pantry",
            subCategory: "Pantry & Grains",
            title: "Heinz Tomato Ketchup Classic Glass Bottle",
            titleNe: "हेन्ज टोमेटो सस",
            brand: "Heinz",
            unitQuantity: 500.0,
            unitType: "g",
            packSize: "500g Glass Bottle",
            priceNpr: 290.0,
            mrpNpr: 320.0,
            discountPercent: 9,
            stockQuantity: 30,
            tags: ["Rich Tomato", "Thick Recipe"]
        ),
        GroceryProduct(
            id: "groc-sw-nutella",
            storeId: "store-saleways-jawalakhel",
            categoryId: "snacks",
            subCategory: "Snacks & Drinks",
            title: "Nutella Hazelnut Cocoa Spread Jar",
            titleNe: "नुटेला हेजलनट स्प्रेड",
            brand: "Ferrero",
            unitQuantity: 350.0,
            unitType: "g",
            packSize: "350g Glass Jar",
            priceNpr: 520.0,
            mrpNpr: 570.0,
            discountPercent: 9,
            stockQuantity: 25,
            tags: ["Breakfast Spread", "Hazelnut", "Kids Favorite"]
        ),

        // 5. MITHO FRESH KIRANA PRODUCTS
        GroceryProduct(
            id: "groc-mk-ddc-milk-blue",
            storeId: "store-mitho-kirana",
            categoryId: "dairy",
            subCategory: "Dairy & Bakery",
            title: "DDC Standard Blue Milk Fresh Pouch",
            titleNe: "डिडिसी निलो दुध ५०० मिलि",
            brand: "DDC Nepal",
            unitQuantity: 500.0,
            unitType: "ml",
            packSize: "500 ml Pouch",
            priceNpr: 55.0,
            mrpNpr: 55.0,
            discountPercent: 0,
            stockQuantity: 60,
            tags: ["Morning Milk", "Chiya Ready", "Fresh Pouch"]
        ),
        GroceryProduct(
            id: "groc-mk-current-noodles",
            storeId: "store-mitho-kirana",
            categoryId: "snacks",
            subCategory: "Snacks & Drinks",
            title: "Current 2X Super Spicy Hot Ramen Bundle",
            titleNe: "करन्ट २X पिरो चाउचाउ ५ प्याक",
            brand: "Yashoda Foods",
            unitQuantity: 500.0,
            unitType: "g",
            packSize: "5-Pack Bundle (500g)",
            priceNpr: 250.0,
            mrpNpr: 275.0,
            discountPercent: 9,
            stockQuantity: 50,
            tags: ["Super Spicy", "Nepali Viral Khaja"]
        ),
        GroceryProduct(
            id: "groc-mk-local-eggs-6",
            storeId: "store-mitho-kirana",
            categoryId: "dairy",
            subCategory: "Dairy & Bakery",
            title: "Local Farm Fresh Brown Eggs (Small Box)",
            titleNe: "ताजा लोकल अण्डा ६ गोटा",
            brand: "Kirana Fresh",
            unitQuantity: 6.0,
            unitType: "pcs",
            packSize: "Pack of 6 Eggs",
            priceNpr: 110.0,
            mrpNpr: 120.0,
            discountPercent: 8,
            stockQuantity: 40,
            tags: ["Fresh Eggs", "Protein"]
        ),
        GroceryProduct(
            id: "groc-mk-haldirams-bhujia",
            storeId: "store-mitho-kirana",
            categoryId: "snacks",
            subCategory: "Snacks & Drinks",
            title: "Haldiram's Nagpur Spicy Aloo Bhujia Sev",
            titleNe: "हल्दीराम आलु भुजिया",
            brand: "Haldiram's",
            unitQuantity: 400.0,
            unitType: "g",
            packSize: "400g Foil Pack",
            priceNpr: 210.0,
            mrpNpr: 230.0,
            discountPercent: 9,
            stockQuantity: 35,
            tags: ["Crispy Khaja", "Tea Snack"]
        ),
        GroceryProduct(
            id: "groc-mk-real-mixed-juice",
            storeId: "store-mitho-kirana",
            categoryId: "snacks",
            subCategory: "Snacks & Drinks",
            title: "Real Active 100% Mixed Fruit Juice",
            titleNe: "रियल मिक्स फ्रुट जुस १ लि",
            brand: "Dabur Nepal",
            unitQuantity: 1.0,
            unitType: "L",
            packSize: "1 Litre Tetra Pack",
            priceNpr: 215.0,
            mrpNpr: 240.0,
            discountPercent: 10,
            stockQuantity: 30,
            tags: ["Fruit Juice", "Vitamin C", "Refreshing"]
        ),
        GroceryProduct(
            id: "groc-mk-good-day-butter",
            storeId: "store-mitho-kirana",
            categoryId: "snacks",
            subCategory: "Snacks & Drinks",
            title: "Britannia Good Day Rich Butter Cookies",
            titleNe: "गुड डे बटर बिस्कुट",
            brand: "Britannia",
            unitQuantity: 200.0,
            unitType: "g",
            packSize: "200g Pack",
            priceNpr: 65.0,
            mrpNpr: 75.0,
            discountPercent: 13,
            stockQuantity: 40,
            tags: ["Butter Cookies", "Chiya Dip"]
        )
    ]

}
