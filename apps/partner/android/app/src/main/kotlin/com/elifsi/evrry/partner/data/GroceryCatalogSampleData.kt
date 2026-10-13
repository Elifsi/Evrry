package com.elifsi.evrry.partner.data

import com.elifsi.evrry.partner.model.GroceryOrderItem
import com.elifsi.evrry.partner.model.GroceryPartnerItem
import com.elifsi.evrry.partner.model.GroceryPartnerOrder
import com.elifsi.evrry.partner.model.SubstitutionStatus

/**
 * Sample Catalog & Active Orders Data for Grocery Supermarket Partner
 * Grounded in Bhatbhateni Supermarket (Pulchowk) catalog.
 * Elifsi Technologies Private Limited
 */
object GroceryCatalogSampleData {

    val aisles = listOf(
        "All",
        "Fresh Produce",
        "Dairy & Bakery",
        "Pantry & Grains",
        "Snacks & Drinks",
        "Household"
    )

    val initialCatalog: List<GroceryPartnerItem> = listOf(
        // FRESH PRODUCE
        GroceryPartnerItem(
            id = "prod-alu-01",
            name = "Kathmandu Valley Red Potatoes (Rato Alu)",
            nameNe = "काठमाडौं उपत्यका रातो आलु",
            brand = "Mude Fresh",
            section = "Fresh Produce",
            pricePaisa = 13500, // NPR 135.00
            stockQuantity = 45,
            isAvailable = true,
            barcode = "8901000000018",
            sku = "BBN-PROD-ALU",
            unit = "kg",
            isLooseWeight = true,
            isPerishable = true
        ),
        GroceryPartnerItem(
            id = "prod-pyaj-02",
            name = "Fresh Red Onions (Pyaj)",
            nameNe = "ताजा रातो प्याज",
            brand = "Terai Harvest",
            section = "Fresh Produce",
            pricePaisa = 8500, // NPR 85.00
            stockQuantity = 38,
            isAvailable = true,
            barcode = "8901000000025",
            sku = "BBN-PROD-PYAJ",
            unit = "kg",
            isLooseWeight = true,
            isPerishable = true
        ),
        GroceryPartnerItem(
            id = "prod-apple-03",
            name = "Jumla Royal Delicious Apples",
            nameNe = "जुम्लाको अर्ग्यानिक स्याउ",
            brand = "Himalayan Organic",
            section = "Fresh Produce",
            pricePaisa = 24000, // NPR 240.00
            stockQuantity = 22,
            isAvailable = true,
            barcode = "8901000000032",
            sku = "BBN-PROD-APPL",
            unit = "kg",
            isLooseWeight = true,
            isPerishable = true
        ),
        GroceryPartnerItem(
            id = "prod-palung-04",
            name = "Organic Palung Spinach (Palungo Saag)",
            nameNe = "अर्ग्यानिक पालुङ्गो साग",
            brand = "Palung Organic",
            section = "Fresh Produce",
            pricePaisa = 6000, // NPR 60.00
            stockQuantity = 14,
            isAvailable = true,
            barcode = "8901000000049",
            sku = "BBN-PROD-SAAG",
            unit = "bundle",
            isLooseWeight = false,
            isPerishable = true
        ),

        // DAIRY & BAKERY
        GroceryPartnerItem(
            id = "dairy-ddc-blue-01",
            name = "DDC Standardized Blue Milk (500 ml)",
            nameNe = "डीडीसी प्रशोधित निलो दुध",
            brand = "DDC",
            section = "Dairy & Bakery",
            pricePaisa = 5000, // NPR 50.00
            stockQuantity = 60,
            isAvailable = true,
            barcode = "8902000000015",
            sku = "BBN-DAIRY-DDC-500",
            unit = "pack",
            isLooseWeight = false,
            isPerishable = true
        ),
        GroceryPartnerItem(
            id = "dairy-ddc-cow-02",
            name = "DDC Pure Cow Milk (1 Litre)",
            nameNe = "डीडीसी शुद्ध गाईको दुध (१ लि)",
            brand = "DDC",
            section = "Dairy & Bakery",
            pricePaisa = 11000, // NPR 110.00
            stockQuantity = 25,
            isAvailable = true,
            barcode = "8902000000022",
            sku = "BBN-DAIRY-COW-1L",
            unit = "pack",
            isLooseWeight = false,
            isPerishable = true
        ),
        GroceryPartnerItem(
            id = "dairy-eggs-03",
            name = "Fresh Farm Brown Eggs (Pack of 6)",
            nameNe = "ताजा फार्म अण्डा (६ वटा)",
            brand = "Valley Poultry",
            section = "Dairy & Bakery",
            pricePaisa = 11000, // NPR 110.00
            stockQuantity = 35,
            isAvailable = true,
            barcode = "8902000000039",
            sku = "BBN-DAIRY-EGG-6",
            unit = "pack",
            isLooseWeight = false,
            isPerishable = true
        ),
        GroceryPartnerItem(
            id = "dairy-bread-04",
            name = "Patan Bakery White Sliced Bread (400g)",
            nameNe = "पाटन बेकरी सेतो पाउरोटी",
            brand = "Patan Bakery",
            section = "Dairy & Bakery",
            pricePaisa = 6500, // NPR 65.00
            stockQuantity = 18,
            isAvailable = true,
            barcode = "8902000000046",
            sku = "BBN-DAIRY-BREAD",
            unit = "pack",
            isLooseWeight = false,
            isPerishable = true
        ),
        GroceryPartnerItem(
            id = "dairy-ghee-05",
            name = "Nepal Dairy Pure Cow Ghee (500 ml)",
            nameNe = "नेपाल डेरी शुद्ध गाईको घ्यु",
            brand = "Nepal Dairy",
            section = "Dairy & Bakery",
            pricePaisa = 62000, // NPR 620.00
            stockQuantity = 15,
            isAvailable = true,
            barcode = "8902000000053",
            sku = "BBN-DAIRY-GHEE",
            unit = "jar",
            isLooseWeight = false,
            isPerishable = false
        ),

        // PANTRY & GRAINS
        GroceryPartnerItem(
            id = "pantry-waiwai-01",
            name = "Wai Wai Quick Chicken Noodles (5 Pack)",
            nameNe = "वाइ वाइ चिकेन चाउचाउ (५ प्याक)",
            brand = "CG Foods",
            section = "Pantry & Grains",
            pricePaisa = 12500, // NPR 125.00
            stockQuantity = 50,
            isAvailable = true,
            barcode = "8903000000012",
            sku = "BBN-PANT-WAI-5PK",
            unit = "pack",
            isLooseWeight = false,
            isPerishable = false
        ),
        GroceryPartnerItem(
            id = "pantry-rara-02",
            name = "Rara Classic White Noodles (5 Pack)",
            nameNe = "रारा सेतो चाउचाउ (५ प्याक)",
            brand = "Gandaki Noodles",
            section = "Pantry & Grains",
            pricePaisa = 11500, // NPR 115.00
            stockQuantity = 40,
            isAvailable = true,
            barcode = "8903000000029",
            sku = "BBN-PANT-RARA-5PK",
            unit = "pack",
            isLooseWeight = false,
            isPerishable = false
        ),
        GroceryPartnerItem(
            id = "pantry-rice-03",
            name = "Himalayan Mansuli Premium Rice (5 Kg)",
            nameNe = "मनसुली चामल (५ केजी)",
            brand = "Himalayan Grains",
            section = "Pantry & Grains",
            pricePaisa = 49000, // NPR 490.00
            stockQuantity = 20,
            isAvailable = true,
            barcode = "8903000000036",
            sku = "BBN-PANT-RICE-5KG",
            unit = "bag",
            isLooseWeight = false,
            isPerishable = false
        ),
        GroceryPartnerItem(
            id = "pantry-oil-04",
            name = "Hetauda Cold-Pressed Mustard Oil (1L)",
            nameNe = "हेटौंडा शुद्ध तोरीको तेल (१ लि)",
            brand = "Hetauda Agro",
            section = "Pantry & Grains",
            pricePaisa = 27500, // NPR 275.00
            stockQuantity = 30,
            isAvailable = true,
            barcode = "8903000000043",
            sku = "BBN-PANT-OIL-1L",
            unit = "bottle",
            isLooseWeight = false,
            isPerishable = false
        ),

        // SNACKS & DRINKS
        GroceryPartnerItem(
            id = "snack-current-01",
            name = "Current 2X Hot & Spicy 2PM Noodles",
            nameNe = "करेन्ट टु-एक्स पिरो चाउचाउ",
            brand = "Yashoda Foods",
            section = "Snacks & Drinks",
            pricePaisa = 5500, // NPR 55.00
            stockQuantity = 45,
            isAvailable = true,
            barcode = "8904000000019",
            sku = "BBN-SNAK-CURR",
            unit = "pack",
            isLooseWeight = false,
            isPerishable = false
        ),
        GroceryPartnerItem(
            id = "snack-tokla-02",
            name = "Tokla Gold CTC Leaf Tea (500g)",
            nameNe = "तोक्ला गोल्ड सिटिसि चिया",
            brand = "Nepal Tea",
            section = "Snacks & Drinks",
            pricePaisa = 26000, // NPR 260.00
            stockQuantity = 28,
            isAvailable = true,
            barcode = "8904000000026",
            sku = "BBN-SNAK-TEA",
            unit = "pack",
            isLooseWeight = false,
            isPerishable = false
        ),

        // HOUSEHOLD
        GroceryPartnerItem(
            id = "house-surf-01",
            name = "Surf Excel Easy Wash Detergent Powder (1 Kg)",
            nameNe = "सर्फ एक्सेल पाउडर",
            brand = "Unilever",
            section = "Household",
            pricePaisa = 23000, // NPR 230.00
            stockQuantity = 24,
            isAvailable = true,
            barcode = "8905000000016",
            sku = "BBN-HOUS-SURF",
            unit = "pack",
            isLooseWeight = false,
            isPerishable = false
        ),
        GroceryPartnerItem(
            id = "house-dettol-02",
            name = "Dettol Original Germ Protect Handwash (200ml)",
            nameNe = "डेटोल ह्यान्डवास",
            brand = "Dettol",
            section = "Household",
            pricePaisa = 14500, // NPR 145.00
            stockQuantity = 32,
            isAvailable = true,
            barcode = "8905000000023",
            sku = "BBN-HOUS-DETT",
            unit = "bottle",
            isLooseWeight = false,
            isPerishable = false
        )
    )

    fun createInitialOrders(): List<GroceryPartnerOrder> = listOf(
        GroceryPartnerOrder(
            id = "ord-bbn-10042",
            orderNo = 10042,
            consumerName = "Aayush Sharma",
            consumerPhone = "+977 9841234567",
            deliveryAddress = "Jhamsikhel Road, Lalitpur (Ward 3, near St. Mary's)",
            status = "preparing",
            totalPaisa = 64500, // NPR 645.00
            placedAt = "12 mins ago",
            customerNote = "Please pack leafy greens and dairy in separate bags.",
            riderName = "Santosh KC",
            riderPhone = "+977 9813009988",
            riderVehiclePlate = "Ba 88 Pa 4321",
            riderEtaMinutes = 5,
            pickupBayCode = "Bay #3 (Front Express Counter)",
            items = listOf(
                GroceryOrderItem(
                    id = "item-01",
                    orderId = "ord-bbn-10042",
                    itemId = "prod-alu-01",
                    name = "Kathmandu Valley Red Potatoes",
                    section = "Fresh Produce",
                    unitPricePaisa = 13500,
                    quantity = 1,
                    lineTotalPaisa = 13500,
                    unit = "kg",
                    isLooseWeight = true,
                    measuredWeight = 2.05,
                    isPacked = true
                ),
                GroceryOrderItem(
                    id = "item-02",
                    orderId = "ord-bbn-10042",
                    itemId = "dairy-ddc-blue-01",
                    name = "DDC Standardized Blue Milk (500 ml)",
                    section = "Dairy & Bakery",
                    unitPricePaisa = 5000,
                    quantity = 2,
                    lineTotalPaisa = 10000,
                    unit = "pack",
                    isLooseWeight = false,
                    isPacked = false
                ),
                GroceryOrderItem(
                    id = "item-03",
                    orderId = "ord-bbn-10042",
                    itemId = "dairy-eggs-03",
                    name = "Fresh Farm Brown Eggs (Pack of 6)",
                    section = "Dairy & Bakery",
                    unitPricePaisa = 11000,
                    quantity = 1,
                    lineTotalPaisa = 11000,
                    unit = "pack",
                    isLooseWeight = false,
                    isPacked = false
                ),
                GroceryOrderItem(
                    id = "item-04",
                    orderId = "ord-bbn-10042",
                    itemId = "pantry-waiwai-01",
                    name = "Wai Wai Quick Chicken Noodles (5 Pack)",
                    section = "Pantry & Grains",
                    unitPricePaisa = 12500,
                    quantity = 1,
                    lineTotalPaisa = 12500,
                    unit = "pack",
                    isLooseWeight = false,
                    isPacked = false
                ),
                GroceryOrderItem(
                    id = "item-05",
                    orderId = "ord-bbn-10042",
                    itemId = "house-surf-01",
                    name = "Surf Excel Easy Wash Detergent (1 Kg)",
                    section = "Household",
                    unitPricePaisa = 23000,
                    quantity = 1,
                    lineTotalPaisa = 23000,
                    unit = "pack",
                    isLooseWeight = false,
                    isPacked = false
                )
            )
        ),
        GroceryPartnerOrder(
            id = "ord-bbn-10043",
            orderNo = 10043,
            consumerName = "Prashant Karki",
            consumerPhone = "+977 9801887766",
            deliveryAddress = "Sanepa Heights, Lalitpur (House #42)",
            status = "acknowledged",
            totalPaisa = 115500, // NPR 1,155.00
            placedAt = "4 mins ago",
            customerNote = "Call gate before arrival.",
            riderName = "Rajan Shrestha",
            riderPhone = "+977 9849112233",
            riderVehiclePlate = "Pradesh 3-02-001 Pa 7788",
            riderEtaMinutes = 15,
            pickupBayCode = "Bay #1 (Main Loading Dock)",
            items = listOf(
                GroceryOrderItem(
                    id = "item-11",
                    orderId = "ord-bbn-10043",
                    itemId = "pantry-rice-03",
                    name = "Himalayan Mansuli Premium Rice (5 Kg)",
                    section = "Pantry & Grains",
                    unitPricePaisa = 49000,
                    quantity = 1,
                    lineTotalPaisa = 49000,
                    unit = "bag",
                    isLooseWeight = false,
                    isPacked = false
                ),
                GroceryOrderItem(
                    id = "item-12",
                    orderId = "ord-bbn-10043",
                    itemId = "pantry-oil-04",
                    name = "Hetauda Cold-Pressed Mustard Oil (1L)",
                    section = "Pantry & Grains",
                    unitPricePaisa = 27500,
                    quantity = 1,
                    lineTotalPaisa = 27500,
                    unit = "bottle",
                    isLooseWeight = false,
                    isPacked = false
                ),
                GroceryOrderItem(
                    id = "item-13",
                    orderId = "ord-bbn-10043",
                    itemId = "snack-tokla-02",
                    name = "Tokla Gold CTC Leaf Tea (500g)",
                    section = "Snacks & Drinks",
                    unitPricePaisa = 26000,
                    quantity = 1,
                    lineTotalPaisa = 26000,
                    unit = "pack",
                    isLooseWeight = false,
                    isPacked = false
                )
            )
        )
    )
}
