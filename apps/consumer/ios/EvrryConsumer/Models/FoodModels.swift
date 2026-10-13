//
//  FoodModels.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Food Delivery Vertical Domain Models & Authentic Nepal Catalog
//

import Foundation
import SwiftUI

public struct Restaurant: Identifiable, Hashable {
    public let id: String
    public let name: String
    public let cuisines: [String]
    public let rating: Double
    public let ratingCount: Int
    public let deliveryEtaMinutes: Int
    public let priceForTwo: Int
    public let area: String
    public let isPureVeg: Bool
    public let offer: String?
    public let gradientFrom: Color
    public let gradientTo: Color
    public let fullAddress: String
    public let phone: String
    public let operatingHours: String
    public let pickupEtaMinutes: Int
    public let hygieneCert: String
    
    public init(
        id: String,
        name: String,
        cuisines: [String],
        rating: Double,
        ratingCount: Int,
        deliveryEtaMinutes: Int,
        priceForTwo: Int,
        area: String,
        isPureVeg: Bool = false,
        offer: String? = nil,
        gradientFrom: Color = Color(red: 0.98, green: 0.45, blue: 0.09),
        gradientTo: Color = Color(red: 0.71, green: 0.33, blue: 0.04),
        fullAddress: String = "Ward 3, Pulchowk Road, Lalitpur, Nepal",
        phone: String = "+977-1-5542199",
        operatingHours: String = "10:00 AM – 10:30 PM (Sun–Sat)",
        pickupEtaMinutes: Int = 15,
        hygieneCert: String = "DFTQC Nepal Grade A · Certified Clean Kitchen"
    ) {
        self.id = id
        self.name = name
        self.cuisines = cuisines
        self.rating = rating
        self.ratingCount = ratingCount
        self.deliveryEtaMinutes = deliveryEtaMinutes
        self.priceForTwo = priceForTwo
        self.area = area
        self.isPureVeg = isPureVeg
        self.offer = offer
        self.gradientFrom = gradientFrom
        self.gradientTo = gradientTo
        self.fullAddress = fullAddress
        self.phone = phone
        self.operatingHours = operatingHours
        self.pickupEtaMinutes = pickupEtaMinutes
        self.hygieneCert = hygieneCert
    }
}

public struct FoodMenuItemOption: Identifiable, Hashable, Codable {
    public let id: String
    public let name: String
    public let priceNpr: Double
    public var extraPriceNpr: Double { priceNpr }
    
    public init(id: String, name: String, priceNpr: Double = 0.0) {
        self.id = id
        self.name = name
        self.priceNpr = priceNpr
    }
}

public struct FoodMenuItemAddon: Identifiable, Hashable, Codable {
    public let id: String
    public let name: String
    public let priceNpr: Double
    
    public init(id: String, name: String, priceNpr: Double = 0.0) {
        self.id = id
        self.name = name
        self.priceNpr = priceNpr
    }
}

public struct FoodMenuItem: Identifiable, Hashable {
    public let id: String
    public let restaurantId: String
    public let title: String
    public let subtitle: String?
    public let priceNpr: Double
    public let veg: Bool
    public let isBestseller: Bool
    public let menuSection: String
    public let imageUrl: String?
    public let options: [FoodMenuItemOption]
    public let addons: [FoodMenuItemAddon]
    
    public init(
        id: String,
        restaurantId: String,
        title: String,
        subtitle: String? = nil,
        priceNpr: Double,
        veg: Bool = true,
        isBestseller: Bool = false,
        menuSection: String = "Mains",
        imageUrl: String? = nil,
        options: [FoodMenuItemOption] = [],
        addons: [FoodMenuItemAddon] = []
    ) {
        self.id = id
        self.restaurantId = restaurantId
        self.title = title
        self.subtitle = subtitle
        self.priceNpr = priceNpr
        self.veg = veg
        self.isBestseller = isBestseller
        self.menuSection = menuSection
        self.imageUrl = imageUrl
        self.options = options
        self.addons = addons
    }
    
    public func toCatalogItem(restaurant: Restaurant) -> CatalogItem {
        CatalogItem(
            id: id,
            vertical: "food",
            title: title,
            subtitle: subtitle ?? "",
            priceNpr: priceNpr,
            originalPriceNpr: nil,
            vendorName: restaurant.name,
            vendorId: restaurant.id,
            rating: restaurant.rating,
            etaMinutes: restaurant.deliveryEtaMinutes,
            imageUrl: imageUrl,
            tags: isBestseller ? ["Bestseller"] : []
        )
    }
}

public struct FoodCatalogData {
    public static let restaurants: [Restaurant] = [
        Restaurant(
            id: "rest-roadhouse",
            name: "Roadhouse Cafe",
            cuisines: ["Woodfired Pizza", "Italian", "Bakery"],
            rating: 4.8,
            ratingCount: 3200,
            deliveryEtaMinutes: 28,
            priceForTwo: 1200,
            area: "Pulchowk, Lalitpur",
            isPureVeg: false,
            offer: "20% OFF up to Rs 250",
            gradientFrom: Color(red: 0.98, green: 0.45, blue: 0.09),
            gradientTo: Color(red: 0.71, green: 0.33, blue: 0.04)
        ),
        Restaurant(
            id: "rest-jimbuthakali",
            name: "Jimbu Thakali Kitchen",
            cuisines: ["Nepali", "Thakali Thali", "Himalayan"],
            rating: 4.9,
            ratingCount: 4800,
            deliveryEtaMinutes: 30,
            priceForTwo: 950,
            area: "Jhamsikhel, Lalitpur",
            isPureVeg: false,
            offer: "Free Jimbu Ghee & Timur Achar",
            gradientFrom: Color(red: 0.09, green: 0.64, blue: 0.29),
            gradientTo: Color(red: 0.08, green: 0.33, blue: 0.18)
        ),
        Restaurant(
            id: "rest-dalle",
            name: "Dalle Restaurant",
            cuisines: ["Momo", "Newari", "Spicy Asian"],
            rating: 4.6,
            ratingCount: 5200,
            deliveryEtaMinutes: 22,
            priceForTwo: 650,
            area: "Kamaladi, Kathmandu",
            isPureVeg: false,
            offer: "Flat Rs 100 OFF above Rs 600",
            gradientFrom: Color(red: 0.92, green: 0.70, blue: 0.03),
            gradientTo: Color(red: 0.52, green: 0.30, blue: 0.05)
        ),
        Restaurant(
            id: "rest-fireandice",
            name: "Fire & Ice Pizzeria",
            cuisines: ["Italian", "Woodfired Pizza", "Desserts"],
            rating: 4.8,
            ratingCount: 6100,
            deliveryEtaMinutes: 35,
            priceForTwo: 1400,
            area: "Thamel, Kathmandu",
            isPureVeg: false,
            offer: "Buy 1 Get 1 on Large Pizza",
            gradientFrom: Color(red: 0.86, green: 0.15, blue: 0.15),
            gradientTo: Color(red: 0.49, green: 0.18, blue: 0.07)
        ),
        Restaurant(
            id: "rest-burgerhouse",
            name: "The Burger House & CFC",
            cuisines: ["Burgers", "Fried Chicken", "Fast Food"],
            rating: 4.4,
            ratingCount: 7800,
            deliveryEtaMinutes: 20,
            priceForTwo: 550,
            area: "New Baneshwor, Kathmandu",
            isPureVeg: false,
            offer: "Crunchy Combo Rs 120 OFF",
            gradientFrom: Color(red: 0.57, green: 0.25, blue: 0.05),
            gradientTo: Color(red: 0.27, green: 0.10, blue: 0.01)
        ),
        Restaurant(
            id: "rest-mithomitho",
            name: "Mitho Mitho Organic Pure Veg",
            cuisines: ["Pure Veg", "South Indian", "Nepali Khaja"],
            rating: 4.5,
            ratingCount: 2900,
            deliveryEtaMinutes: 24,
            priceForTwo: 450,
            area: "Patan Durbar Square, Lalitpur",
            isPureVeg: true,
            offer: "15% OFF on Family Thali",
            gradientFrom: Color(red: 0.05, green: 0.58, blue: 0.53),
            gradientTo: Color(red: 0.07, green: 0.37, blue: 0.35)
        )
    ]
    
    public static let menuItems: [FoodMenuItem] = [
        // Roadhouse Cafe Items
        FoodMenuItem(
            id: "food-rd-01",
            restaurantId: "rest-roadhouse",
            title: "Woodfired Margherita Pizza",
            subtitle: "San Marzano tomato sauce, fresh buffalo mozzarella, fresh basil",
            priceNpr: 650.0,
            veg: true,
            isBestseller: true,
            menuSection: "Woodfired Pizza",
            options: [
                FoodMenuItemOption(id: "opt-rd-crust-1", name: "Thin Hand-Tossed Crust", priceNpr: 0.0),
                FoodMenuItemOption(id: "opt-rd-crust-2", name: "Stuffed Yak Cheese Crust", priceNpr: 120.0),
                FoodMenuItemOption(id: "opt-rd-crust-3", name: "Gluten-Free Base", priceNpr: 100.0)
            ],
            addons: [
                FoodMenuItemAddon(id: "add-rd-01", name: "Extra Buffalo Mozzarella", priceNpr: 90.0),
                FoodMenuItemAddon(id: "add-rd-02", name: "Mustang Chili Honey Drizzle", priceNpr: 45.0),
                FoodMenuItemAddon(id: "add-rd-03", name: "Fresh Garlic Butter Dip", priceNpr: 40.0)
            ]
        ),
        FoodMenuItem(
            id: "food-rd-02",
            restaurantId: "rest-roadhouse",
            title: "Smoked Chicken & Salami Pizza",
            subtitle: "Roasted chicken breast, spicy chicken salami, mozzarella, oregano",
            priceNpr: 850.0,
            veg: false,
            isBestseller: true,
            menuSection: "Woodfired Pizza",
            options: [
                FoodMenuItemOption(id: "opt-rd-sz-1", name: "10-inch Medium (Serves 1-2)", priceNpr: 0.0),
                FoodMenuItemOption(id: "opt-rd-sz-2", name: "12-inch Large (Serves 2-3)", priceNpr: 220.0)
            ],
            addons: [
                FoodMenuItemAddon(id: "add-rd-sc-1", name: "Extra Smoked Chicken Topping", priceNpr: 110.0),
                FoodMenuItemAddon(id: "add-rd-sc-2", name: "Jalapeno & Red Paprika", priceNpr: 50.0)
            ]
        ),
        FoodMenuItem(
            id: "food-rd-03",
            restaurantId: "rest-roadhouse",
            title: "Bruschetta al Pomodoro",
            subtitle: "Toasted sourdough, garlic, olive oil, vine ripe tomatoes, parmesan",
            priceNpr: 380.0,
            veg: true,
            menuSection: "Starters"
        ),
        FoodMenuItem(
            id: "food-rd-04",
            restaurantId: "rest-roadhouse",
            title: "Spaghetti Carbonara",
            subtitle: "Crispy bacon, egg yolk emulsion, black pepper, aged pecorino",
            priceNpr: 720.0,
            veg: false,
            menuSection: "Pasta & Mains"
        ),
        FoodMenuItem(
            id: "food-rd-05",
            restaurantId: "rest-roadhouse",
            title: "Signature Warm Apple Pie",
            subtitle: "Mustang organic apples, cinnamon glaze, vanilla custard drizzle",
            priceNpr: 420.0,
            veg: true,
            menuSection: "Desserts"
        ),
        
        // Jimbu Thakali Kitchen Items
        FoodMenuItem(
            id: "food-jt-01",
            restaurantId: "rest-jimbuthakali",
            title: "Authentic Mutton Thakali Thali Set",
            subtitle: "Tender hill mutton curry, black Mustang dal, local ghee, gundruk, radish achar",
            priceNpr: 680.0,
            veg: false,
            isBestseller: true,
            menuSection: "Thakali Thali",
            options: [
                FoodMenuItemOption(id: "opt-jt-rc-1", name: "Steamed Marpha Basmati Rice", priceNpr: 0.0),
                FoodMenuItemOption(id: "opt-jt-rc-2", name: "Traditional Buckwheat Dhindo", priceNpr: 50.0)
            ],
            addons: [
                FoodMenuItemAddon(id: "add-jt-01", name: "Extra Jimbu Fried Clarified Butter (Ghee)", priceNpr: 60.0),
                FoodMenuItemAddon(id: "add-jt-02", name: "Spicy Timur Chili Achar (Side)", priceNpr: 30.0),
                FoodMenuItemAddon(id: "add-jt-03", name: "Crispy Gundruk Sadeko Bowl", priceNpr: 80.0)
            ]
        ),
        FoodMenuItem(
            id: "food-jt-02",
            restaurantId: "rest-jimbuthakali",
            title: "Organic Vegetable Thakali Thali",
            subtitle: "Mustang daal, seasonal saag, aloo tareko, timur chutney, papad, curd",
            priceNpr: 480.0,
            veg: true,
            isBestseller: true,
            menuSection: "Thakali Thali"
        ),
        FoodMenuItem(
            id: "food-jt-03",
            restaurantId: "rest-jimbuthakali",
            title: "Thakali Chicken Sukuti Fry",
            subtitle: "Sun-dried local chicken pan-fried with green chilies, onions & garlic",
            priceNpr: 450.0,
            veg: false,
            menuSection: "Starters & Khaja"
        ),
        FoodMenuItem(
            id: "food-jt-04",
            restaurantId: "rest-jimbuthakali",
            title: "Kodo ko Dhindo with Ghee",
            subtitle: "Traditional buckwheat porridge cooked with organic clarified butter",
            priceNpr: 350.0,
            veg: true,
            menuSection: "Traditional Khaja"
        ),
        
        // Dalle Restaurant Items
        FoodMenuItem(
            id: "food-dl-01",
            restaurantId: "rest-dalle",
            title: "Fiery C-Momo (Buff / Chicken)",
            subtitle: "10 deep fried dumplings tossed in ultra-spicy Dalle Khursani chili gravy",
            priceNpr: 290.0,
            veg: false,
            isBestseller: true,
            menuSection: "Momo Specials",
            options: [
                FoodMenuItemOption(id: "opt-dl-cm-1", name: "Chicken (Medium Spicy)", priceNpr: 0.0),
                FoodMenuItemOption(id: "opt-dl-cm-2", name: "Buff (Extra Hot)", priceNpr: 20.0),
                FoodMenuItemOption(id: "opt-dl-cm-3", name: "Paneer & Veg (Medium)", priceNpr: 10.0)
            ],
            addons: [
                FoodMenuItemAddon(id: "add-dl-cm-1", name: "Extra Hot Dalle Chili Sauce", priceNpr: 30.0),
                FoodMenuItemAddon(id: "add-dl-cm-2", name: "Creamy Peanut Chutney Dip", priceNpr: 35.0),
                FoodMenuItemAddon(id: "add-dl-cm-3", name: "Grated Yak Cheese Topping", priceNpr: 60.0)
            ]
        ),
        FoodMenuItem(
            id: "food-dl-02",
            restaurantId: "rest-dalle",
            title: "Steamed Chicken Momo (10 Pcs)",
            subtitle: "Served with spicy yellow peanut chutney and red Dalle sauce",
            priceNpr: 240.0,
            veg: false,
            isBestseller: true,
            menuSection: "Momo Specials",
            options: [
                FoodMenuItemOption(id: "opt-dl-st-1", name: "Standard Steamed (10 Pcs)", priceNpr: 0.0),
                FoodMenuItemOption(id: "opt-dl-st-2", name: "Jhol Momo Gravy Style", priceNpr: 40.0),
                FoodMenuItemOption(id: "opt-dl-st-3", name: "Crispy Kothey Fried", priceNpr: 30.0)
            ],
            addons: [
                FoodMenuItemAddon(id: "add-dl-st-1", name: "Extra Yellow Peanut Jhol", priceNpr: 40.0),
                FoodMenuItemAddon(id: "add-dl-st-2", name: "Spicy Sesame Chili Oil", priceNpr: 30.0)
            ]
        ),
        FoodMenuItem(
            id: "food-dl-03",
            restaurantId: "rest-dalle",
            title: "Steamed Paneer & Veg Momo",
            subtitle: "Minced fresh cottage cheese, cabbage, ginger, scallions",
            priceNpr: 220.0,
            veg: true,
            menuSection: "Momo Specials"
        ),
        FoodMenuItem(
            id: "food-dl-04",
            restaurantId: "rest-dalle",
            title: "Dalle Special Chowmein",
            subtitle: "Wok tossed handmade noodles with shredded chicken, peppers & hot chili",
            priceNpr: 270.0,
            veg: false,
            menuSection: "Noodles & Rice"
        ),
        
        // Fire & Ice Pizzeria Items
        FoodMenuItem(
            id: "food-fi-01",
            restaurantId: "rest-fireandice",
            title: "Fire & Ice Classic Pizza",
            subtitle: "Tomato, mozzarella, ham, salami, mushrooms, chili oil",
            priceNpr: 890.0,
            veg: false,
            isBestseller: true,
            menuSection: "Pizzas"
        ),
        FoodMenuItem(
            id: "food-fi-02",
            restaurantId: "rest-fireandice",
            title: "Quattro Formaggi (4 Cheeses)",
            subtitle: "Mozzarella, gorgonzola, parmesan, yak cheese blend",
            priceNpr: 820.0,
            veg: true,
            menuSection: "Pizzas"
        ),
        FoodMenuItem(
            id: "food-fi-03",
            restaurantId: "rest-fireandice",
            title: "Homemade Italian Gelato (2 Scoops)",
            subtitle: "Choice of Dark Chocolate, Wild Pistachio, or Strawberry Cream",
            priceNpr: 350.0,
            veg: true,
            menuSection: "Desserts"
        ),
        
        // The Burger House Items
        FoodMenuItem(
            id: "food-bh-01",
            restaurantId: "rest-burgerhouse",
            title: "Crispy Fried Chicken Burger",
            subtitle: "Golden crumbed breast fillet, melted cheddar, lettuce, spicy mayo, toasted bun",
            priceNpr: 320.0,
            veg: false,
            isBestseller: true,
            menuSection: "Burgers"
        ),
        FoodMenuItem(
            id: "food-bh-02",
            restaurantId: "rest-burgerhouse",
            title: "CFC Bucket (4 Pcs Drumsticks)",
            subtitle: "Crispy pressure fried local chicken with garlic dip & chili sauce",
            priceNpr: 480.0,
            veg: false,
            isBestseller: true,
            menuSection: "Fried Chicken"
        ),
        FoodMenuItem(
            id: "food-bh-03",
            restaurantId: "rest-burgerhouse",
            title: "Cheesy Fries Basket",
            subtitle: "Crinkle cut potatoes smothered with warm cheese sauce and jalapeños",
            priceNpr: 210.0,
            veg: true,
            menuSection: "Sides"
        ),
        
        // Mitho Mitho Organic Pure Veg Items
        FoodMenuItem(
            id: "food-mm-01",
            restaurantId: "rest-mithomitho",
            title: "Masala Dosa with Sambar & 3 Chutneys",
            subtitle: "Crispy fermented crepe filled with spiced potato masala, coconut & tomato chutneys",
            priceNpr: 220.0,
            veg: true,
            isBestseller: true,
            menuSection: "South Indian"
        ),
        FoodMenuItem(
            id: "food-mm-02",
            restaurantId: "rest-mithomitho",
            title: "Special Chhoila Set (Organic Mushroom)",
            subtitle: "Smoky grilled oyster mushrooms tossed in mustard oil, fenugreek, beaten rice",
            priceNpr: 260.0,
            veg: true,
            isBestseller: true,
            menuSection: "Nepali Khaja"
        ),
        FoodMenuItem(
            id: "food-mm-03",
            restaurantId: "rest-mithomitho",
            title: "Kathmandu Lassi with Cashews & Kismis",
            subtitle: "Rich sweet churned organic yogurt, rose water, crushed nuts",
            priceNpr: 140.0,
            veg: true,
            menuSection: "Beverages"
        )
    ]
    
    public static func findRestaurant(id: String) -> Restaurant? {
        restaurants.first { $0.id == id }
    }
    
    public static func menuByRestaurant(restaurantId: String) -> [FoodMenuItem] {
        menuItems.filter { $0.restaurantId == restaurantId }
    }
}
