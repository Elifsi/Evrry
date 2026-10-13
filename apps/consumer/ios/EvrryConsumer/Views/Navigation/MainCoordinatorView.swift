//
//  MainCoordinatorView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Main Consumer App Navigation Controller (Matching Android)
//

import SwiftUI

public struct MainCoordinatorView: View {
    @StateObject private var authViewModel = AuthViewModel()
    @StateObject private var homeViewModel = HomeViewModel()
    @StateObject private var exploreViewModel = ExploreViewModel()
    @StateObject private var foodViewModel = FoodViewModel()
    @StateObject private var groceryViewModel = GroceryViewModel()
    @StateObject private var rideViewModel = RideViewModel()
    @StateObject private var activityViewModel = ActivityViewModel()
    @StateObject private var chatViewModel = ChatViewModel()
    @StateObject private var callViewModel = CallViewModel()
    @StateObject private var loyaltyViewModel = LoyaltyViewModel()
    @StateObject private var cameraViewModel = CameraViewModel()
    
    @State private var currentTab: String = "home"
    @State private var subRoute: String? = nil // "cart", "settings", "food_home", "restaurant_detail", "grocery_home", "order_success", "order_detail", "chat_room", "voice_call", "video_call", "camera", "memories", "snap_review"
    @State private var selectedRestaurantId: String = "rest-roadhouse"
    @State private var selectedGroceryStore: GroceryStore? = nil
    @State private var activeOrderId: String = ""
    @State private var selectedChat: SocialChat? = nil
    @State private var previousSubRoute: String? = nil
    @State private var capturedSnapUrl: String? = nil
    @State private var capturedSnapFilter: String = "Normal"
    @State private var isStorySnap: Bool = false
    
    public init() {}
    
    public var body: some View {
        ZStack {
            switch authViewModel.state {
            case .splash:
                SplashView(
                    viewModel: authViewModel,
                    hasSeenOnboarding: false
                )
                
            case .onboarding:
                OnboardingView(
                    viewModel: authViewModel,
                    onComplete: {
                        authViewModel.finishOnboarding()
                    }
                )
                
            case .idle, .error:
                PhoneInputView(
                    viewModel: authViewModel,
                    onContinue: {}
                )
                
            case .otpRequested:
                OtpVerifyView(
                    viewModel: authViewModel,
                    onBack: { authViewModel.resetToPhoneInput() },
                    onSuccess: { currentTab = "home" }
                )
                
            case .authenticated:
                VStack(spacing: 0) {
                    // MAIN CONTENT AREA
                    ZStack {
                        if let route = subRoute {
                            switch route {
                            case "food_home":
                                FoodHomeView(
                                    foodViewModel: foodViewModel,
                                    exploreViewModel: exploreViewModel,
                                    onBack: {
                                        subRoute = nil
                                        previousSubRoute = nil
                                    },
                                    onRestaurantClick: { restId in
                                        selectedRestaurantId = restId
                                        previousSubRoute = "food_home"
                                        subRoute = "restaurant_detail"
                                    },
                                    onNavigateToCart: {
                                        previousSubRoute = "food_home"
                                        subRoute = "cart"
                                    }
                                )
                            case "grocery_home":
                                GroceryHomeView(
                                    groceryViewModel: groceryViewModel,
                                    exploreViewModel: exploreViewModel,
                                    onBack: {
                                        subRoute = nil
                                        previousSubRoute = nil
                                    },
                                    onNavigateToCart: {
                                        previousSubRoute = "grocery_home"
                                        subRoute = "cart"
                                    },
                                    onStoreClick: { store in
                                        selectedGroceryStore = store
                                        previousSubRoute = "grocery_home"
                                        subRoute = "grocery_store_detail"
                                    }
                                )
                            case "grocery_store_detail":
                                if let store = selectedGroceryStore {
                                    GroceryStoreDetailView(
                                        store: store,
                                        groceryViewModel: groceryViewModel,
                                        exploreViewModel: exploreViewModel,
                                        onBack: {
                                            subRoute = "grocery_home"
                                        },
                                        onNavigateToCart: {
                                            previousSubRoute = "grocery_store_detail"
                                            subRoute = "cart"
                                        }
                                    )
                                } else {
                                    Color.clear.onAppear { subRoute = "grocery_home" }
                                }
                            case "restaurant_detail":
                                RestaurantDetailView(
                                    restaurantId: selectedRestaurantId,
                                    foodViewModel: foodViewModel,
                                    exploreViewModel: exploreViewModel,
                                    onBack: {
                                        subRoute = "food_home"
                                    },
                                    onNavigateToCart: {
                                        previousSubRoute = "restaurant_detail"
                                        subRoute = "cart"
                                    }
                                )
                            case "cart":
                                CartCheckoutView(
                                    exploreViewModel: exploreViewModel,
                                    activityViewModel: activityViewModel,
                                    onBack: {
                                        subRoute = previousSubRoute
                                        previousSubRoute = nil
                                    },
                                    onOrderSuccess: { orderId in
                                        activeOrderId = orderId
                                        previousSubRoute = nil
                                        subRoute = "order_success"
                                    }
                                )
                            case "order_success":
                                OrderSuccessView(
                                    orderId: activeOrderId,
                                    onTrackOrder: { orderId in
                                        activeOrderId = orderId
                                        subRoute = "order_detail"
                                    },
                                    onBackToHome: {
                                        subRoute = nil
                                        previousSubRoute = nil
                                        currentTab = "home"
                                    }
                                )
                            case "order_detail":
                                OrderDetailsView(
                                    orderId: activeOrderId,
                                    activityViewModel: activityViewModel,
                                    onBack: {
                                        subRoute = nil
                                        previousSubRoute = nil
                                        currentTab = "activity"
                                    },
                                    onReorder: { orderItem in
                                        exploreViewModel.reorderOrder(
                                            vendorName: orderItem.vendorName,
                                            title: orderItem.title,
                                            amountRs: orderItem.amountRs,
                                            lineItems: orderItem.lineItems
                                        )
                                        previousSubRoute = nil
                                        subRoute = "cart"
                                    },
                                    onChatWithRider: { orderId in
                                        let order = activityViewModel.getOrder(id: orderId)
                                        let chat = chatViewModel.openOrCreateOrderChat(
                                            orderId: orderId,
                                            vendorName: order?.vendorName ?? "Store Support",
                                            riderName: order?.riderName
                                        )
                                        selectedChat = chat
                                        previousSubRoute = "order_detail"
                                        subRoute = "chat_room"
                                    }
                                )
                            case "settings":
                                SettingsView(
                                    activeModel: homeViewModel.activeModel,
                                    authViewModel: authViewModel,
                                    loyaltyViewModel: loyaltyViewModel,
                                    onSelectModel: { homeViewModel.selectModel($0) },
                                    onBack: {
                                        subRoute = nil
                                        previousSubRoute = nil
                                    },
                                    onLogout: {
                                        subRoute = nil
                                        previousSubRoute = nil
                                        authViewModel.resetToPhoneInput()
                                    }
                                )
                            case "chat_room":
                                if let chat = selectedChat {
                                    ChatRoomView(
                                        chat: chat,
                                        chatViewModel: chatViewModel,
                                        onBack: {
                                            subRoute = previousSubRoute
                                            previousSubRoute = nil
                                            selectedChat = nil
                                        },
                                        onAudioCallClick: {
                                            callViewModel.startCall(
                                                chatId: chat.id,
                                                calleeName: chat.title,
                                                calleeAvatar: chat.avatarUrl,
                                                callType: .audio
                                            )
                                            previousSubRoute = "chat_room"
                                            subRoute = "voice_call"
                                        },
                                        onVideoCallClick: {
                                            callViewModel.startCall(
                                                chatId: chat.id,
                                                calleeName: chat.title,
                                                calleeAvatar: chat.avatarUrl,
                                                callType: .video
                                            )
                                            previousSubRoute = "chat_room"
                                            subRoute = "video_call"
                                        },
                                        onViewOrderOrRide: { refId in
                                            if refId.lowercased().starts(with: "ord_") {
                                                activeOrderId = refId
                                                previousSubRoute = "chat_room"
                                                subRoute = "order_detail"
                                            } else if refId.lowercased().starts(with: "ride_") {
                                                subRoute = nil
                                                previousSubRoute = nil
                                                currentTab = "rides"
                                            }
                                        }
                                    )
                                } else {
                                    EmptyView()
                                }
                            case "voice_call":
                                if let session = callViewModel.callSession {
                                    VoiceCallView(
                                        session: session,
                                        callViewModel: callViewModel,
                                        onCallEnded: {
                                            subRoute = "chat_room"
                                        }
                                    )
                                } else {
                                    EmptyView()
                                }
                            case "video_call":
                                if let session = callViewModel.callSession {
                                    VideoCallView(
                                        session: session,
                                        callViewModel: callViewModel,
                                        onCallEnded: {
                                            subRoute = "chat_room"
                                        }
                                    )
                                } else {
                                    EmptyView()
                                }
                            case "camera":
                                CameraView(
                                    viewModel: cameraViewModel,
                                    onBack: {
                                        isStorySnap = false
                                        subRoute = previousSubRoute
                                        previousSubRoute = nil
                                    },
                                    onOpenMemories: {
                                        previousSubRoute = "camera"
                                        subRoute = "memories"
                                    },
                                    onCaptureComplete: { url, filter in
                                        capturedSnapUrl = url
                                        capturedSnapFilter = filter
                                        previousSubRoute = "camera"
                                        subRoute = "snap_review"
                                    },
                                    onNavigateAction: { target in
                                        if target.hasPrefix("/chat") || target.hasPrefix("chat_room") {
                                            subRoute = "chat_room"
                                        } else if target.hasPrefix("cart") {
                                            subRoute = "cart"
                                        } else if target.hasPrefix("restaurant_detail") {
                                            subRoute = "restaurant_detail"
                                        }
                                    }
                                )
                            case "memories":
                                MemoriesView(
                                    cameraViewModel: cameraViewModel,
                                    chatViewModel: chatViewModel,
                                    onBack: {
                                        subRoute = previousSubRoute
                                        previousSubRoute = nil
                                    }
                                )
                            case "snap_review":
                                if let url = capturedSnapUrl {
                                    SnapReviewView(
                                        dataUrl: url,
                                        filterName: capturedSnapFilter,
                                        forStory: isStorySnap,
                                        cameraViewModel: cameraViewModel,
                                        chatViewModel: chatViewModel,
                                        onClose: {
                                            if isStorySnap {
                                                isStorySnap = false
                                                previousSubRoute = nil
                                                subRoute = nil
                                                currentTab = "chats"
                                            } else {
                                                subRoute = previousSubRoute
                                                previousSubRoute = nil
                                            }
                                        },
                                        onNavigateToChat: { chatId in
                                            isStorySnap = false
                                            if let chat = chatViewModel.chats.first(where: { $0.id == chatId }) {
                                                selectedChat = chat
                                                subRoute = "chat_room"
                                            }
                                        }
                                    )
                                } else {
                                    EmptyView()
                                }
                            default:
                                EmptyView()
                            }
                        } else {
                            switch currentTab {
                            case "home":
                                HomeAgentView(
                                    viewModel: homeViewModel,
                                    onMenuClick: {
                                        previousSubRoute = nil
                                        subRoute = "settings"
                                    },
                                    onProfileClick: {
                                        previousSubRoute = nil
                                        subRoute = "settings"
                                    },
                                    cartItemCount: exploreViewModel.cart.itemCount,
                                    onCartClick: {
                                        previousSubRoute = nil
                                        subRoute = "cart"
                                    }
                                )
                            case "explore":
                                ExploreView(
                                    viewModel: exploreViewModel,
                                    onNavigateToCart: {
                                        previousSubRoute = nil
                                        subRoute = "cart"
                                    },
                                    onNavigateToFood: {
                                        previousSubRoute = nil
                                        subRoute = "food_home"
                                    },
                                    onNavigateToGrocery: {
                                        previousSubRoute = nil
                                        subRoute = "grocery_home"
                                    },
                                    onProfileClick: {
                                        previousSubRoute = nil
                                        subRoute = "settings"
                                    }
                                )
                            case "chats":
                                ConversationListView(
                                    chatViewModel: chatViewModel,
                                    callViewModel: callViewModel,
                                    onChatClick: { chat in
                                        selectedChat = chat
                                        chatViewModel.markAsRead(chatId: chat.id)
                                        previousSubRoute = nil
                                        subRoute = "chat_room"
                                    },
                                    onStartAudioCall: { name, phone in
                                        callViewModel.startCall(
                                            chatId: "call-\(UUID().uuidString.prefix(8))",
                                            calleeName: name,
                                            calleePhone: phone,
                                            callType: .audio
                                        )
                                        previousSubRoute = nil
                                        subRoute = "voice_call"
                                    },
                                    onStartVideoCall: { name, phone in
                                        callViewModel.startCall(
                                            chatId: "call-\(UUID().uuidString.prefix(8))",
                                            calleeName: name,
                                            calleePhone: phone,
                                            callType: .video
                                        )
                                        previousSubRoute = nil
                                        subRoute = "video_call"
                                    },
                                    onProfileClick: {
                                        previousSubRoute = nil
                                        subRoute = "settings"
                                    },
                                    onCameraClick: {
                                        previousSubRoute = nil
                                        subRoute = "camera"
                                    },
                                    onAddStoryClick: {
                                        isStorySnap = true
                                        previousSubRoute = nil
                                        subRoute = "camera"
                                    }
                                )
                            case "rides":
                                RideBookingView(
                                    viewModel: rideViewModel,
                                    onBack: { currentTab = "home" },
                                    onProfileClick: {
                                        previousSubRoute = nil
                                        subRoute = "settings"
                                    },
                                    onChatClick: { driverName, driverPhone in
                                        _ = chatViewModel.openOrCreateChatByPhoneNumber(driverPhone)
                                        previousSubRoute = "rides"
                                        subRoute = "chat_room"
                                    },
                                    onInAppCallClick: { driverName, driverPhone in
                                        let chat = chatViewModel.openOrCreateChatByPhoneNumber(driverPhone)
                                        callViewModel.startCall(
                                            chatId = chat.id,
                                            calleeName = driverName,
                                            calleeAvatar = nil,
                                            calleePhone = driverPhone,
                                            callType = .audio
                                        )
                                        previousSubRoute = "rides"
                                        subRoute = "voice_call"
                                    }
                                )
                            case "activity":
                                ActivityView(
                                    viewModel: activityViewModel,
                                    onBack: { currentTab = "home" },
                                    onSelectOrder: { orderId in
                                        activeOrderId = orderId
                                        previousSubRoute = nil
                                        subRoute = "order_detail"
                                    },
                                    onReorder: { orderItem in
                                        exploreViewModel.reorderOrder(
                                            vendorName: orderItem.vendorName,
                                            title: orderItem.title,
                                            amountRs: orderItem.amountRs,
                                            lineItems: orderItem.lineItems
                                        )
                                        previousSubRoute = nil
                                        subRoute = "cart"
                                    }
                                )
                            case "settings":
                                SettingsView(
                                    activeModel: homeViewModel.activeModel,
                                    authViewModel: authViewModel,
                                    loyaltyViewModel: loyaltyViewModel,
                                    onSelectModel: { homeViewModel.selectModel($0) },
                                    onBack: { currentTab = "home" },
                                    onLogout: {
                                        authViewModel.resetToPhoneInput()
                                    }
                                )
                            default:
                                EmptyView()
                            }
                        }
                    }
                    
                    // BOTTOM APP BAR (Shown only on top-level tabs)
                    if subRoute == nil {
                        EvrryBottomBar(
                            currentRoute: currentTab,
                            chatsUnreadCount: chatViewModel.totalUnreadCount,
                            onNavigate: {
                                subRoute = nil
                                previousSubRoute = nil
                                currentTab = $0
                            }
                        )
                    }
                }
                .ignoresSafeArea(.keyboard, edges: .bottom)
            }
        }
    }
}
