package com.elifsi.evrry.consumer.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elifsi.evrry.consumer.model.AuthScreenState
import com.elifsi.evrry.consumer.ui.activity.ActivityScreen
import com.elifsi.evrry.consumer.ui.auth.OtpVerifyScreen
import com.elifsi.evrry.consumer.ui.auth.PhoneInputScreen
import com.elifsi.evrry.consumer.ui.auth.SplashScreen
import com.elifsi.evrry.consumer.ui.onboarding.OnboardingScreen
import com.elifsi.evrry.consumer.ui.cart.CartCheckoutScreen
import com.elifsi.evrry.consumer.ui.components.EvrryBottomBar
import com.elifsi.evrry.consumer.ui.explore.ExploreScreen
import com.elifsi.evrry.consumer.ui.home.HomeAgentScreen
import com.elifsi.evrry.consumer.ui.rides.RideBookingScreen
import com.elifsi.evrry.consumer.ui.settings.SettingsScreen
import com.elifsi.evrry.consumer.model.CallType
import com.elifsi.evrry.consumer.model.GroceryStore
import com.elifsi.evrry.consumer.ui.food.FoodHomeScreen
import com.elifsi.evrry.consumer.ui.food.RestaurantDetailScreen
import com.elifsi.evrry.consumer.ui.chat.ChatRoomScreen
import com.elifsi.evrry.consumer.ui.chat.ConversationListScreen
import com.elifsi.evrry.consumer.ui.chat.VoiceCallScreen
import com.elifsi.evrry.consumer.ui.chat.VideoCallScreen
import com.elifsi.evrry.consumer.ui.camera.CameraScreen
import com.elifsi.evrry.consumer.ui.camera.MemoriesScreen
import com.elifsi.evrry.consumer.ui.camera.SnapReviewScreen
import com.elifsi.evrry.consumer.ui.grocery.GroceryHomeScreen
import com.elifsi.evrry.consumer.ui.grocery.GroceryStoreDetailScreen
import com.elifsi.evrry.consumer.ui.orders.OrderDetailsScreen
import com.elifsi.evrry.consumer.ui.orders.OrderSuccessScreen
import com.elifsi.evrry.consumer.ui.profile.ProfileScreen
import com.elifsi.evrry.consumer.ui.profile.AddressBookScreen
import com.elifsi.evrry.consumer.ui.profile.AccountDetailsScreen
import com.elifsi.evrry.consumer.ui.profile.PaymentMethodsScreen
import com.elifsi.evrry.consumer.ui.profile.VouchersReferralsScreen
import com.elifsi.evrry.consumer.ui.profile.LoyaltyRewardsScreen
import com.elifsi.evrry.consumer.ui.profile.PrivacySecurityScreen
import com.elifsi.evrry.consumer.viewmodel.*

/**
 * Main Consumer App Navigation Controller
 * Connects Login Flow with 5 Main Tabs (AI, Explore, Chats, Rides, Activity, Profile)
 * And Sub-verticals: Food Delivery (Commerce / Commerce), Grocery Quick Mart (Quick Mart), WhatsApp Chat, Voice & Video Call, Cart, Order Success & Tracking, Snapchat Camera & Memories
 * Elifsi Technologies Private Limited
 */
@Composable
fun EvrryConsumerNavHost(
    authViewModel: AuthViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    exploreViewModel: ExploreViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel(),
    callViewModel: CallViewModel = viewModel(),
    foodViewModel: FoodViewModel = viewModel(),
    groceryViewModel: GroceryViewModel = viewModel(),
    rideViewModel: RideViewModel = viewModel(),
    activityViewModel: ActivityViewModel = viewModel(),
    loyaltyViewModel: LoyaltyViewModel = viewModel(),
    cameraViewModel: CameraViewModel = viewModel()
) {
    val authState by authViewModel.state.collectAsState()
    val chats by chatViewModel.chats.collectAsState()
    val totalUnreadChats = remember(chats) { chats.sumOf { it.unreadCount } }
    var currentTab by remember { mutableStateOf("home") }
    var inSubRoute by remember { mutableStateOf<String?>(null) } // "cart", "settings", "food_home", "restaurant_detail", "order_success", "order_detail", "chat_room", "voice_call", "video_call", "camera", "memories", "snap_review"
    var activeRestaurantId by remember { mutableStateOf<String?>("rest-roadhouse") }
    var activeGroceryStore by remember { mutableStateOf<GroceryStore?>(null) }
    var activeOrderId by remember { mutableStateOf<String>("ord-001") }
    var previousSubRoute by remember { mutableStateOf<String?>(null) }
    var capturedSnapUrl by remember { mutableStateOf<String?>(null) }
    var capturedSnapFilter by remember { mutableStateOf("Normal") }
    var isStorySnap by remember { mutableStateOf(false) }

    when (authState) {
        is AuthScreenState.Splash -> {
            SplashScreen(
                viewModel = authViewModel,
                hasSeenOnboarding = false
            )
        }
        is AuthScreenState.Onboarding -> {
            OnboardingScreen(
                viewModel = authViewModel,
                onComplete = { authViewModel.finishOnboarding() }
            )
        }
        is AuthScreenState.Idle -> {
            PhoneInputScreen(
                viewModel = authViewModel,
                onContinue = { /* state transitions internally to OtpRequested */ }
            )
        }
        is AuthScreenState.OtpRequested -> {
            OtpVerifyScreen(
                viewModel = authViewModel,
                onBack = { authViewModel.resetToPhoneInput() },
                onSuccess = { currentTab = "home" }
            )
        }
        is AuthScreenState.Authenticated, is AuthScreenState.Error -> {
            val showBottomBar = inSubRoute == null || inSubRoute in listOf("food_home", "grocery_home", "rides")

            // SYSTEM BACK BUTTON HANDLER: Pops sub-routes or returns to Home tab instead of closing the app
            BackHandler(enabled = inSubRoute != null || currentTab != "home") {
                if (inSubRoute != null) {
                    if (previousSubRoute != null) {
                        inSubRoute = previousSubRoute
                        previousSubRoute = null
                    } else {
                        inSubRoute = null
                    }
                } else if (currentTab != "home") {
                    currentTab = "home"
                }
            }

            // MAIN 5-TAB APP EXPERIENCE
            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        EvrryBottomBar(
                            currentRoute = if (inSubRoute in listOf("food_home", "grocery_home", "rides")) "explore" else currentTab,
                            chatsUnreadCount = totalUnreadChats,
                            onNavigate = {
                                inSubRoute = null
                                previousSubRoute = null
                                currentTab = it
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    when (inSubRoute) {
                        "food_home" -> {
                            FoodHomeScreen(
                                foodViewModel = foodViewModel,
                                exploreViewModel = exploreViewModel,
                                onBack = { inSubRoute = null },
                                onRestaurantClick = { restId ->
                                    activeRestaurantId = restId
                                    previousSubRoute = "food_home"
                                    inSubRoute = "restaurant_detail"
                                },
                                onNavigateToCart = {
                                    previousSubRoute = "food_home"
                                    inSubRoute = "cart"
                                }
                            )
                        }
                        "grocery_home" -> {
                            GroceryHomeScreen(
                                groceryViewModel = groceryViewModel,
                                exploreViewModel = exploreViewModel,
                                onBack = { inSubRoute = null },
                                onNavigateToCart = {
                                    previousSubRoute = "grocery_home"
                                    inSubRoute = "cart"
                                },
                                onStoreClick = { store ->
                                    activeGroceryStore = store
                                    previousSubRoute = "grocery_home"
                                    inSubRoute = "grocery_store_detail"
                                }
                            )
                        }
                        "grocery_store_detail" -> {
                            activeGroceryStore?.let { store ->
                                GroceryStoreDetailScreen(
                                    store = store,
                                    groceryViewModel = groceryViewModel,
                                    exploreViewModel = exploreViewModel,
                                    onBack = {
                                        inSubRoute = "grocery_home"
                                    },
                                    onNavigateToCart = {
                                        previousSubRoute = "grocery_store_detail"
                                        inSubRoute = "cart"
                                    }
                                )
                            } ?: run {
                                inSubRoute = "grocery_home"
                            }
                        }
                        "restaurant_detail" -> {
                            RestaurantDetailScreen(
                                restaurantId = activeRestaurantId ?: "rest-roadhouse",
                                foodViewModel = foodViewModel,
                                exploreViewModel = exploreViewModel,
                                onBack = { inSubRoute = "food_home" },
                                onNavigateToCart = {
                                    previousSubRoute = "restaurant_detail"
                                    inSubRoute = "cart"
                                }
                            )
                        }
                        "cart" -> {
                            CartCheckoutScreen(
                                exploreViewModel = exploreViewModel,
                                activityViewModel = activityViewModel,
                                onBack = {
                                    inSubRoute = previousSubRoute
                                    previousSubRoute = null
                                },
                                onOrderPlaced = { orderId ->
                                    activeOrderId = orderId
                                    previousSubRoute = null
                                    inSubRoute = "order_success"
                                }
                            )
                        }
                        "order_success" -> {
                            OrderSuccessScreen(
                                orderId = activeOrderId,
                                onTrackOrder = { orderId ->
                                    activeOrderId = orderId
                                    inSubRoute = "order_detail"
                                },
                                onBackToHome = {
                                    inSubRoute = null
                                    previousSubRoute = null
                                    currentTab = "explore"
                                }
                            )
                        }
                        "order_detail" -> {
                            OrderDetailsScreen(
                                orderId = activeOrderId,
                                activityViewModel = activityViewModel,
                                onBack = {
                                    inSubRoute = null
                                    previousSubRoute = null
                                    currentTab = "activity"
                                },
                                onReorder = { orderItem ->
                                    exploreViewModel.reorderOrder(
                                        orderItem.vendorName,
                                        orderItem.title,
                                        orderItem.amountRs,
                                        orderItem.lineItems
                                    )
                                    inSubRoute = "cart"
                                },
                                onChatWithRider = { orderId ->
                                    val order = activityViewModel.getOrder(orderId)
                                    chatViewModel.openOrCreateOrderChat(
                                        orderId = orderId,
                                        vendorName = order?.vendorName ?: "Store Support",
                                        riderName = order?.riderName
                                    )
                                    previousSubRoute = "order_detail"
                                    inSubRoute = "chat_room"
                                }
                            )
                        }
                        "settings" -> {
                            val activeModel by homeViewModel.activeModel.collectAsState()
                            SettingsScreen(
                                activeModel = activeModel,
                                authViewModel = authViewModel,
                                loyaltyViewModel = loyaltyViewModel,
                                onSelectModel = { homeViewModel.selectModel(it) },
                                onBack = { inSubRoute = null },
                                onLogout = {
                                    inSubRoute = null
                                    previousSubRoute = null
                                    authViewModel.resetToPhoneInput()
                                }
                            )
                        }
                        "rides" -> {
                            RideBookingScreen(
                                viewModel = rideViewModel,
                                onBack = { inSubRoute = null },
                                onProfileClick = {
                                    inSubRoute = null
                                    currentTab = "profile"
                                },
                                onChatClick = { driverName, driverPhone ->
                                    chatViewModel.startOrOpenDriverChat(driverName, driverPhone)
                                    previousSubRoute = "rides"
                                    inSubRoute = "chat_room"
                                },
                                onInAppCallClick = { driverName, driverPhone ->
                                    val chat = chatViewModel.startOrOpenDriverChat(driverName, driverPhone)
                                    callViewModel.startCall(
                                        chatId = chat.id,
                                        calleeName = driverName,
                                        calleePhone = driverPhone,
                                        callType = CallType.AUDIO
                                    )
                                    previousSubRoute = "rides"
                                    inSubRoute = "voice_call"
                                }
                            )
                        }
                        "activity" -> {
                            ActivityScreen(
                                viewModel = activityViewModel,
                                onBack = { inSubRoute = null },
                                onOrderClick = { orderId ->
                                    activeOrderId = orderId
                                    previousSubRoute = "activity"
                                    inSubRoute = "order_detail"
                                },
                                onReorderClick = { orderItem ->
                                    exploreViewModel.reorderOrder(
                                        orderItem.vendorName,
                                        orderItem.title,
                                        orderItem.amountRs,
                                        orderItem.lineItems
                                    )
                                    inSubRoute = "cart"
                                }
                            )
                        }
                        "addresses" -> {
                            AddressBookScreen(
                                onBack = { inSubRoute = null }
                            )
                        }
                        "account_details" -> {
                            AccountDetailsScreen(
                                authViewModel = authViewModel,
                                onBack = { inSubRoute = null }
                            )
                        }
                        "payment_methods" -> {
                            PaymentMethodsScreen(
                                onBack = { inSubRoute = null }
                            )
                        }
                        "vouchers" -> {
                            VouchersReferralsScreen(
                                loyaltyViewModel = loyaltyViewModel,
                                onBack = { inSubRoute = null }
                            )
                        }
                        "rewards" -> {
                            LoyaltyRewardsScreen(
                                loyaltyViewModel = loyaltyViewModel,
                                onBack = { inSubRoute = null }
                            )
                        }
                        "privacy_security" -> {
                            PrivacySecurityScreen(
                                authViewModel = authViewModel,
                                onBack = { inSubRoute = null }
                            )
                        }
                        "chat_room" -> {
                            val selectedChat by chatViewModel.selectedChat.collectAsState()
                            if (selectedChat != null) {
                                ChatRoomScreen(
                                    chat = selectedChat!!,
                                    chatViewModel = chatViewModel,
                                    onBack = {
                                        inSubRoute = previousSubRoute
                                        previousSubRoute = null
                                    },
                                    onAudioCallClick = {
                                        callViewModel.startCall(
                                            chatId = selectedChat!!.id,
                                            calleeName = selectedChat!!.title,
                                            calleeAvatar = selectedChat!!.avatarUrl,
                                            callType = CallType.AUDIO
                                        )
                                        previousSubRoute = "chat_room"
                                        inSubRoute = "voice_call"
                                    },
                                    onVideoCallClick = {
                                        callViewModel.startCall(
                                            chatId = selectedChat!!.id,
                                            calleeName = selectedChat!!.title,
                                            calleeAvatar = selectedChat!!.avatarUrl,
                                            callType = CallType.VIDEO
                                        )
                                        previousSubRoute = "chat_room"
                                        inSubRoute = "video_call"
                                    },
                                    onViewOrderOrRide = { refId ->
                                        activeOrderId = refId
                                        previousSubRoute = "chat_room"
                                        inSubRoute = "order_detail"
                                    },
                                    onCameraClick = {
                                        previousSubRoute = "chat_room"
                                        inSubRoute = "camera"
                                    }
                                )
                            } else {
                                inSubRoute = null
                            }
                        }
                        "voice_call" -> {
                            val callSession by callViewModel.callSession.collectAsState()
                            if (callSession != null) {
                                VoiceCallScreen(
                                    session = callSession!!,
                                    callViewModel = callViewModel,
                                    onCallEnded = { inSubRoute = "chat_room" }
                                )
                            } else {
                                inSubRoute = "chat_room"
                            }
                        }
                        "video_call" -> {
                            val callSession by callViewModel.callSession.collectAsState()
                            if (callSession != null) {
                                VideoCallScreen(
                                    session = callSession!!,
                                    callViewModel = callViewModel,
                                    onCallEnded = { inSubRoute = "chat_room" }
                                )
                            } else {
                                inSubRoute = "chat_room"
                            }
                        }
                        "camera" -> {
                            CameraScreen(
                                viewModel = cameraViewModel,
                                onBack = {
                                    isStorySnap = false
                                    inSubRoute = previousSubRoute
                                    previousSubRoute = null
                                },
                                onOpenMemories = {
                                    previousSubRoute = "camera"
                                    inSubRoute = "memories"
                                },
                                onCaptureComplete = { imgUrl, filter ->
                                    capturedSnapUrl = imgUrl
                                    capturedSnapFilter = filter
                                    previousSubRoute = "camera"
                                    inSubRoute = "snap_review"
                                },
                                onNavigateAction = { target ->
                                    if (target.startsWith("/chat")) {
                                        val contactHandle = target.removePrefix("/chat/")
                                        val foundChat = chats.find { (it.subtitle?.contains(contactHandle) ?: false) || it.title.contains(contactHandle) }
                                        if (foundChat != null) {
                                            chatViewModel.selectChat(foundChat)
                                            previousSubRoute = null
                                            inSubRoute = "chat_room"
                                        } else {
                                            currentTab = "chats"
                                            inSubRoute = null
                                        }
                                    } else if (target.startsWith("nepalqr://") || target.startsWith("000201")) {
                                        inSubRoute = null
                                        currentTab = "home"
                                    } else if (target.startsWith("/restaurant")) {
                                        val restId = target.removePrefix("/restaurant/")
                                        activeRestaurantId = restId
                                        previousSubRoute = null
                                        inSubRoute = "restaurant_detail"
                                    }
                                }
                            )
                        }
                        "memories" -> {
                            MemoriesScreen(
                                cameraViewModel = cameraViewModel,
                                chatViewModel = chatViewModel,
                                onBack = {
                                    inSubRoute = previousSubRoute
                                    previousSubRoute = null
                                }
                            )
                        }
                        "snap_review" -> {
                            capturedSnapUrl?.let { url ->
                                SnapReviewScreen(
                                    dataUrl = url,
                                    filterName = capturedSnapFilter,
                                    forStory = isStorySnap,
                                    cameraViewModel = cameraViewModel,
                                    chatViewModel = chatViewModel,
                                    onClose = {
                                        if (isStorySnap) {
                                            isStorySnap = false
                                            previousSubRoute = null
                                            inSubRoute = null
                                            currentTab = "chats"
                                        } else {
                                            inSubRoute = previousSubRoute
                                            previousSubRoute = null
                                        }
                                    },
                                    onNavigateToChat = { chatId ->
                                        isStorySnap = false
                                        chats.find { it.id == chatId }?.let { chatViewModel.selectChat(it) }
                                        previousSubRoute = null
                                        inSubRoute = "chat_room"
                                    }
                                )
                            } ?: run {
                                inSubRoute = "camera"
                            }
                        }
                        else -> {
                            when (currentTab) {
                                "home" -> {
                                    val cartState by exploreViewModel.cart.collectAsState()
                                    val userProf by authViewModel.userProfile.collectAsState()
                                    HomeAgentScreen(
                                        viewModel = homeViewModel,
                                        displayName = userProf.fullName.ifEmpty { "Rahul" },
                                        cartItemCount = cartState.itemCount,
                                        onCartClick = {
                                            previousSubRoute = null
                                            inSubRoute = "cart"
                                        },
                                        onMenuClick = { currentTab = "profile" },
                                        onProfileClick = { currentTab = "profile" }
                                    )
                                }
                                "explore" -> {
                                    ExploreScreen(
                                        viewModel = exploreViewModel,
                                        onNavigateToCart = {
                                            previousSubRoute = null
                                            inSubRoute = "cart"
                                        },
                                        onNavigateToFood = {
                                            previousSubRoute = null
                                            inSubRoute = "food_home"
                                        },
                                        onNavigateToGrocery = {
                                            previousSubRoute = null
                                            inSubRoute = "grocery_home"
                                        },
                                        onNavigateToRides = {
                                            previousSubRoute = null
                                            inSubRoute = "rides"
                                        },
                                        onNavigateToHome = {
                                            currentTab = "home"
                                        },
                                        onNavigateToCamera = {
                                            previousSubRoute = null
                                            inSubRoute = "camera"
                                        },
                                        onProfileClick = { currentTab = "profile" }
                                    )
                                }
                                "camera" -> {
                                    CameraScreen(
                                        viewModel = cameraViewModel,
                                        onBack = { currentTab = "home" },
                                        onOpenMemories = {
                                            previousSubRoute = "camera"
                                            inSubRoute = "memories"
                                        },
                                        onCaptureComplete = { imgUrl, filter ->
                                            capturedSnapUrl = imgUrl
                                            capturedSnapFilter = filter
                                            previousSubRoute = "camera"
                                            inSubRoute = "snap_review"
                                        },
                                        onNavigateAction = { target ->
                                            if (target.startsWith("/chat")) {
                                                val contactHandle = target.removePrefix("/chat/")
                                                val foundChat = chats.find { (it.subtitle?.contains(contactHandle) ?: false) || it.title.contains(contactHandle) }
                                                if (foundChat != null) {
                                                    chatViewModel.selectChat(foundChat)
                                                    previousSubRoute = null
                                                    inSubRoute = "chat_room"
                                                } else {
                                                    currentTab = "chats"
                                                    inSubRoute = null
                                                }
                                            } else {
                                                currentTab = "home"
                                            }
                                        }
                                    )
                                }
                                "chats" -> {
                                    ConversationListScreen(
                                        chatViewModel = chatViewModel,
                                        callViewModel = callViewModel,
                                        onChatClick = { _ ->
                                            previousSubRoute = null
                                            inSubRoute = "chat_room"
                                        },
                                        onStartAudioCall = { calleeName, calleePhone ->
                                            callViewModel.startCall(
                                                chatId = "call-${System.currentTimeMillis()}",
                                                calleeName = calleeName,
                                                calleePhone = calleePhone,
                                                callType = CallType.AUDIO
                                            )
                                            previousSubRoute = null
                                            inSubRoute = "voice_call"
                                        },
                                        onStartVideoCall = { calleeName, calleePhone ->
                                            callViewModel.startCall(
                                                chatId = "call-${System.currentTimeMillis()}",
                                                calleeName = calleeName,
                                                calleePhone = calleePhone,
                                                callType = CallType.VIDEO
                                            )
                                            previousSubRoute = null
                                            inSubRoute = "video_call"
                                        },
                                        onCameraClick = {
                                            previousSubRoute = null
                                            inSubRoute = "camera"
                                        },
                                        onAddStoryClick = {
                                            isStorySnap = true
                                            previousSubRoute = null
                                            inSubRoute = "camera"
                                        },
                                        onProfileClick = { currentTab = "profile" }
                                    )
                                }
                                "profile" -> {
                                    ProfileScreen(
                                        authViewModel = authViewModel,
                                        loyaltyViewModel = loyaltyViewModel,
                                        onNavigateToAccount = {
                                            previousSubRoute = null
                                            inSubRoute = "account_details"
                                        },
                                        onNavigateToPaymentMethods = {
                                            previousSubRoute = null
                                            inSubRoute = "payment_methods"
                                        },
                                        onNavigateToVouchers = {
                                            previousSubRoute = null
                                            inSubRoute = "vouchers"
                                        },
                                        onNavigateToRewards = {
                                            previousSubRoute = null
                                            inSubRoute = "rewards"
                                        },
                                        onNavigateToActivity = {
                                            previousSubRoute = null
                                            inSubRoute = "activity"
                                        },
                                        onNavigateToAddressBook = {
                                            previousSubRoute = null
                                            inSubRoute = "addresses"
                                        },
                                        onNavigateToMemories = {
                                            previousSubRoute = null
                                            inSubRoute = "memories"
                                        },
                                        onNavigateToAiSettings = {
                                            previousSubRoute = null
                                            inSubRoute = "settings"
                                        },
                                        onNavigateToAppSettings = {
                                            previousSubRoute = null
                                            inSubRoute = "settings"
                                        },
                                        onNavigateToPrivacySecurity = {
                                            previousSubRoute = null
                                            inSubRoute = "privacy_security"
                                        },
                                        onLogout = {
                                            authViewModel.resetToPhoneInput()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
