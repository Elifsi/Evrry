package com.elifsi.evrry.consumer.model

import kotlinx.serialization.Serializable

/**
 * Domain Models for Food Delivery Vertical
 * Platform: Evrry Super App (Consumer)
 * Elifsi Technologies Private Limited
 */

@Serializable
data class Restaurant(
    val id: String,
    val name: String,
    val cuisines: List<String>,
    val rating: Double,
    val ratingCount: Int,
    val deliveryEtaMinutes: Int,
    val priceForTwo: Int,
    val area: String,
    val isPureVeg: Boolean = false,
    val offer: String? = null,
    val gradientFrom: Long = 0xFFF97316,
    val gradientTo: Long = 0xFFB45309,
    val fullAddress: String = "Ward 3, Pulchowk Road, Lalitpur, Nepal",
    val phone: String = "+977-1-5542199",
    val operatingHours: String = "10:00 AM – 10:30 PM (Sun–Sat)",
    val pickupEtaMinutes: Int = 15,
    val hygieneCert: String = "Certified Clean Kitchen · DFTQC Nepal Grade A",
    val imageUrl: String? = null
)

@Serializable
data class FoodMenuItemOption(
    val id: String,
    val name: String,
    val extraPriceNpr: Double = 0.0
)

@Serializable
data class FoodMenuItemAddon(
    val id: String,
    val name: String,
    val priceNpr: Double
)

@Serializable
data class FoodMenuItem(
    val id: String,
    val restaurantId: String,
    val title: String,
    val subtitle: String? = null,
    val priceNpr: Double,
    val veg: Boolean = true,
    val isBestseller: Boolean = false,
    val menuSection: String = "Mains",
    val imageUrl: String? = null,
    val options: List<FoodMenuItemOption> = emptyList(),
    val addons: List<FoodMenuItemAddon> = emptyList()
)

object FoodCatalogData {
    val restaurants = listOf(
        Restaurant(
            id = "rest-roadhouse",
            name = "Roadhouse Cafe",
            cuisines = listOf("Woodfired Pizza", "Italian", "Bakery"),
            rating = 4.8,
            ratingCount = 3200,
            deliveryEtaMinutes = 28,
            priceForTwo = 1200,
            area = "Pulchowk, Lalitpur",
            isPureVeg = false,
            offer = "20% OFF up to Rs 250",
            gradientFrom = 0xFFF97316,
            gradientTo = 0xFFB45309
        ),
        Restaurant(
            id = "rest-jimbuthakali",
            name = "Jimbu Thakali Kitchen",
            cuisines = listOf("Nepali", "Thakali Thali", "Himalayan"),
            rating = 4.9,
            ratingCount = 4800,
            deliveryEtaMinutes = 30,
            priceForTwo = 950,
            area = "Jhamsikhel, Lalitpur",
            isPureVeg = false,
            offer = "Free Jimbu Ghee & Timur Achar",
            gradientFrom = 0xFF16A34A,
            gradientTo = 0xFF14532D
        ),
        Restaurant(
            id = "rest-dalle",
            name = "Dalle Restaurant",
            cuisines = listOf("Momo", "Newari", "Spicy Asian"),
            rating = 4.6,
            ratingCount = 5200,
            deliveryEtaMinutes = 22,
            priceForTwo = 650,
            area = "Kamaladi, Kathmandu",
            isPureVeg = false,
            offer = "Flat Rs 100 OFF above Rs 600",
            gradientFrom = 0xFFEAB308,
            gradientTo = 0xFF854D0E
        ),
        Restaurant(
            id = "rest-fireandice",
            name = "Fire & Ice Pizzeria",
            cuisines = listOf("Italian", "Woodfired Pizza", "Desserts"),
            rating = 4.8,
            ratingCount = 6100,
            deliveryEtaMinutes = 35,
            priceForTwo = 1400,
            area = "Thamel, Kathmandu",
            isPureVeg = false,
            offer = "Buy 1 Get 1 on Large Pizza",
            gradientFrom = 0xFFDC2626,
            gradientTo = 0xFF7C2D12
        ),
        Restaurant(
            id = "rest-burgerhouse",
            name = "The Burger House & CFC",
            cuisines = listOf("Burgers", "Fried Chicken", "Fast Food"),
            rating = 4.4,
            ratingCount = 7800,
            deliveryEtaMinutes = 20,
            priceForTwo = 550,
            area = "New Baneshwor, Kathmandu",
            isPureVeg = false,
            offer = "Crunchy Combo Rs 120 OFF",
            gradientFrom = 0xFF92400E,
            gradientTo = 0xFF451A03
        ),
        Restaurant(
            id = "rest-mithomitho",
            name = "Mitho Mitho Organic Pure Veg",
            cuisines = listOf("Pure Veg", "South Indian", "Nepali Khaja"),
            rating = 4.5,
            ratingCount = 2900,
            deliveryEtaMinutes = 24,
            priceForTwo = 450,
            area = "Patan Durbar Square, Lalitpur",
            isPureVeg = true,
            offer = "15% OFF on Family Thali",
            gradientFrom = 0xFF0D9488,
            gradientTo = 0xFF115E59
        )
    )

    val menuItems = listOf(
        // Roadhouse Cafe Items
        FoodMenuItem(
            id = "food-rd-01",
            restaurantId = "rest-roadhouse",
            title = "Woodfired Margherita Pizza",
            subtitle = "San Marzano tomato sauce, fresh buffalo mozzarella, fresh basil",
            priceNpr = 650.0,
            veg = true,
            isBestseller = true,
            menuSection = "Woodfired Pizza",
            options = listOf(
                FoodMenuItemOption("opt-rd-mar-1", "Regular Hand-Tossed Crust", 0.0),
                FoodMenuItemOption("opt-rd-mar-2", "Thin & Crispy Crust", 50.0),
                FoodMenuItemOption("opt-rd-mar-3", "Cheese Burst Crust", 120.0)
            ),
            addons = listOf(
                FoodMenuItemAddon("add-rd-mar-1", "Extra Buffalo Mozzarella", 90.0),
                FoodMenuItemAddon("add-rd-mar-2", "Black Olives & Jalapenos", 60.0),
                FoodMenuItemAddon("add-rd-mar-3", "Fresh Basil Pesto Drizzle", 50.0)
            )
        ),
        FoodMenuItem(
            id = "food-rd-02",
            restaurantId = "rest-roadhouse",
            title = "Smoked Chicken & Salami Pizza",
            subtitle = "Roasted chicken breast, spicy chicken salami, mozzarella, oregano",
            priceNpr = 850.0,
            veg = false,
            isBestseller = true,
            menuSection = "Woodfired Pizza",
            options = listOf(
                FoodMenuItemOption("opt-rd-smk-1", "Standard Crust", 0.0),
                FoodMenuItemOption("opt-rd-smk-2", "Thin Crust", 50.0),
                FoodMenuItemOption("opt-rd-smk-3", "Cheese Stuffed Crust", 120.0)
            ),
            addons = listOf(
                FoodMenuItemAddon("add-rd-smk-1", "Extra Smoked Chicken", 110.0),
                FoodMenuItemAddon("add-rd-smk-2", "Spicy Chili Honey Dip", 50.0)
            )
        ),
        FoodMenuItem(
            id = "food-rd-03",
            restaurantId = "rest-roadhouse",
            title = "Bruschetta al Pomodoro",
            subtitle = "Toasted sourdough, garlic, olive oil, vine ripe tomatoes, parmesan",
            priceNpr = 380.0,
            veg = true,
            menuSection = "Starters"
        ),
        FoodMenuItem(
            id = "food-rd-04",
            restaurantId = "rest-roadhouse",
            title = "Spaghetti Carbonara",
            subtitle = "Crispy bacon, egg yolk emulsion, black pepper, aged pecorino",
            priceNpr = 720.0,
            veg = false,
            menuSection = "Pasta & Mains"
        ),
        FoodMenuItem(
            id = "food-rd-05",
            restaurantId = "rest-roadhouse",
            title = "Signature Warm Apple Pie",
            subtitle = "Mustang organic apples, cinnamon glaze, vanilla custard drizzle",
            priceNpr = 420.0,
            veg = true,
            menuSection = "Desserts"
        ),

        // Jimbu Thakali Kitchen Items
        FoodMenuItem(
            id = "food-jt-01",
            restaurantId = "rest-jimbuthakali",
            title = "Authentic Mutton Thakali Thali Set",
            subtitle = "Tender hill mutton curry, black Mustang dal, local ghee, gundruk, radish achar",
            priceNpr = 680.0,
            veg = false,
            isBestseller = true,
            menuSection = "Thakali Thali",
            options = listOf(
                FoodMenuItemOption("opt-jt-th-1", "Steamed White Basmati Rice", 0.0),
                FoodMenuItemOption("opt-jt-th-2", "Local Marsi Red Rice", 60.0),
                FoodMenuItemOption("opt-jt-th-3", "Buckwheat Kodo Dhindo", 80.0)
            ),
            addons = listOf(
                FoodMenuItemAddon("add-jt-th-1", "Extra Jimbu Ghee & Dal", 70.0),
                FoodMenuItemAddon("add-jt-th-2", "Timur Radish Achar (Pau)", 40.0),
                FoodMenuItemAddon("add-jt-th-3", "Mustang Sukuti Portion", 150.0)
            )
        ),
        FoodMenuItem(
            id = "food-jt-02",
            restaurantId = "rest-jimbuthakali",
            title = "Organic Vegetable Thakali Thali",
            subtitle = "Mustang daal, seasonal saag, aloo tareko, timur chutney, papad, curd",
            priceNpr = 480.0,
            veg = true,
            isBestseller = true,
            menuSection = "Thakali Thali",
            options = listOf(
                FoodMenuItemOption("opt-jt-vth-1", "Steamed White Rice", 0.0),
                FoodMenuItemOption("opt-jt-vth-2", "Kodo ko Dhindo", 60.0)
            ),
            addons = listOf(
                FoodMenuItemAddon("add-jt-vth-1", "Extra Organic Curd Cup", 50.0),
                FoodMenuItemAddon("add-jt-vth-2", "Spicy Gundruk Sadeko", 60.0)
            )
        ),
        FoodMenuItem(
            id = "food-jt-03",
            restaurantId = "rest-jimbuthakali",
            title = "Thakali Chicken Sukuti Fry",
            subtitle = "Sun-dried local chicken pan-fried with green chilies, onions & garlic",
            priceNpr = 450.0,
            veg = false,
            menuSection = "Starters & Khaja"
        ),
        FoodMenuItem(
            id = "food-jt-04",
            restaurantId = "rest-jimbuthakali",
            title = "Kodo ko Dhindo with Ghee",
            subtitle = "Traditional buckwheat porridge cooked with organic clarified butter",
            priceNpr = 350.0,
            veg = true,
            menuSection = "Traditional Khaja"
        ),

        // Dalle Restaurant Items
        FoodMenuItem(
            id = "food-dl-01",
            restaurantId = "rest-dalle",
            title = "Fiery C-Momo (Buff / Chicken)",
            subtitle = "10 deep fried dumplings tossed in ultra-spicy Dalle Khursani chili gravy",
            priceNpr = 290.0,
            veg = false,
            isBestseller = true,
            menuSection = "Momo Specials",
            options = listOf(
                FoodMenuItemOption("opt-dl-cm-1", "Chicken (Medium Spicy)", 0.0),
                FoodMenuItemOption("opt-dl-cm-2", "Buff (Extra Hot)", 20.0),
                FoodMenuItemOption("opt-dl-cm-3", "Paneer & Veg (Medium)", 10.0)
            ),
            addons = listOf(
                FoodMenuItemAddon("add-dl-cm-1", "Extra Hot Dalle Chili Sauce", 30.0),
                FoodMenuItemAddon("add-dl-cm-2", "Creamy Peanut Chutney Dip", 35.0),
                FoodMenuItemAddon("add-dl-cm-3", "Grated Yak Cheese Topping", 60.0)
            )
        ),
        FoodMenuItem(
            id = "food-dl-02",
            restaurantId = "rest-dalle",
            title = "Steamed Chicken Momo (10 Pcs)",
            subtitle = "Served with spicy yellow peanut chutney and red Dalle sauce",
            priceNpr = 240.0,
            veg = false,
            isBestseller = true,
            menuSection = "Momo Specials",
            options = listOf(
                FoodMenuItemOption("opt-dl-st-1", "Standard Steamed (10 Pcs)", 0.0),
                FoodMenuItemOption("opt-dl-st-2", "Jhol Momo Gravy Style", 40.0),
                FoodMenuItemOption("opt-dl-st-3", "Crispy Kothey Fried", 30.0)
            ),
            addons = listOf(
                FoodMenuItemAddon("add-dl-st-1", "Extra Yellow Peanut Jhol", 40.0),
                FoodMenuItemAddon("add-dl-st-2", "Spicy Sesame Chili Oil", 30.0)
            )
        ),
        FoodMenuItem(
            id = "food-dl-03",
            restaurantId = "rest-dalle",
            title = "Steamed Paneer & Veg Momo",
            subtitle = "Minced fresh cottage cheese, cabbage, ginger, scallions",
            priceNpr = 220.0,
            veg = true,
            menuSection = "Momo Specials"
        ),
        FoodMenuItem(
            id = "food-dl-04",
            restaurantId = "rest-dalle",
            title = "Dalle Special Chowmein",
            subtitle = "Wok tossed handmade noodles with shredded chicken, peppers & hot chili",
            priceNpr = 270.0,
            veg = false,
            menuSection = "Noodles & Rice"
        ),

        // Fire & Ice Pizzeria Items
        FoodMenuItem(
            id = "food-fi-01",
            restaurantId = "rest-fireandice",
            title = "Fire & Ice Classic Pizza",
            subtitle = "Tomato, mozzarella, ham, salami, mushrooms, chili oil",
            priceNpr = 890.0,
            veg = false,
            isBestseller = true,
            menuSection = "Pizzas"
        ),
        FoodMenuItem(
            id = "food-fi-02",
            restaurantId = "rest-fireandice",
            title = "Quattro Formaggi (4 Cheeses)",
            subtitle = "Mozzarella, gorgonzola, parmesan, yak cheese blend",
            priceNpr = 820.0,
            veg = true,
            menuSection = "Pizzas"
        ),
        FoodMenuItem(
            id = "food-fi-03",
            restaurantId = "rest-fireandice",
            title = "Homemade Italian Gelato (2 Scoops)",
            subtitle = "Choice of Dark Chocolate, Wild Pistachio, or Strawberry Cream",
            priceNpr = 350.0,
            veg = true,
            menuSection = "Desserts"
        ),

        // The Burger House Items
        FoodMenuItem(
            id = "food-bh-01",
            restaurantId = "rest-burgerhouse",
            title = "Crispy Fried Chicken Burger",
            subtitle = "Golden crumbed breast fillet, melted cheddar, lettuce, spicy mayo, toasted bun",
            priceNpr = 320.0,
            veg = false,
            isBestseller = true,
            menuSection = "Burgers"
        ),
        FoodMenuItem(
            id = "food-bh-02",
            restaurantId = "rest-burgerhouse",
            title = "CFC Bucket (4 Pcs Drumsticks)",
            subtitle = "Crispy pressure fried local chicken with garlic dip & chili sauce",
            priceNpr = 480.0,
            veg = false,
            isBestseller = true,
            menuSection = "Fried Chicken"
        ),
        FoodMenuItem(
            id = "food-bh-03",
            restaurantId = "rest-burgerhouse",
            title = "Cheesy Fries Basket",
            subtitle = "Crinkle cut potatoes smothered with warm cheese sauce and jalapeños",
            priceNpr = 210.0,
            veg = true,
            menuSection = "Sides"
        ),

        // Mitho Mitho Organic Pure Veg Items
        FoodMenuItem(
            id = "food-mm-01",
            restaurantId = "rest-mithomitho",
            title = "Masala Dosa with Sambar & 3 Chutneys",
            subtitle = "Crispy fermented crepe filled with spiced potato masala, coconut & tomato chutneys",
            priceNpr = 220.0,
            veg = true,
            isBestseller = true,
            menuSection = "South Indian"
        ),
        FoodMenuItem(
            id = "food-mm-02",
            restaurantId = "rest-mithomitho",
            title = "Special Chhoila Set (Organic Mushroom)",
            subtitle = "Smoky grilled oyster mushrooms tossed in mustard oil, fenugreek, beaten rice",
            priceNpr = 260.0,
            veg = true,
            isBestseller = true,
            menuSection = "Nepali Khaja"
        ),
        FoodMenuItem(
            id = "food-mm-03",
            restaurantId = "rest-mithomitho",
            title = "Kathmandu Lassi with Cashews & Kismis",
            subtitle = "Rich sweet churned organic yogurt, rose water, crushed nuts",
            priceNpr = 140.0,
            veg = true,
            menuSection = "Beverages"
        )
    )

    fun findRestaurant(id: String): Restaurant? = restaurants.find { it.id == id }
    fun menuByRestaurant(restaurantId: String): List<FoodMenuItem> = menuItems.filter { it.restaurantId == restaurantId }
}

fun FoodMenuItem.toCatalogItem(restaurant: Restaurant): CatalogItem {
    return CatalogItem(
        id = id,
        vertical = "food",
        title = title,
        subtitle = subtitle ?: "",
        priceNpr = priceNpr,
        vendorName = restaurant.name,
        vendorId = restaurant.id,
        rating = restaurant.rating,
        etaMinutes = restaurant.deliveryEtaMinutes,
        tags = if (isBestseller) listOf("Bestseller") else emptyList()
    )
}
