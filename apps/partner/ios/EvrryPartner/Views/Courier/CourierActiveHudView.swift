//
//  CourierActiveHudView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Two-Stage Navigation HUD (Store Pickup -> Customer Dropoff)
//  Grounded in enatega-multivendor-rider & kaaneneskpc_Deliverr
//

import SwiftUI
import MapKit

public struct CourierActiveHudView: View {
    @ObservedObject var viewModel: CourierDeliveryViewModel
    
    public init(viewModel: CourierDeliveryViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        guard let order = viewModel.activeOrder else {
            return AnyView(EmptyView())
        }
        
        return AnyView(
            VStack(spacing: 0) {
                // Top Navigation Guidance Card
                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Image(systemName: viewModel.deliveryStage == .toStore || viewModel.deliveryStage == .atStore ? "building.2.fill" : "person.crop.circle.fill")
                            .font(.system(size: 18))
                            .foregroundColor(.white)
                            .frame(width: 36, height: 36)
                            .background(viewModel.deliveryStage == .toStore || viewModel.deliveryStage == .atStore ? Color.orange : Color.purple)
                            .clipShape(Circle())
                        
                        VStack(alignment: .leading, spacing: 2) {
                            Text(viewModel.deliveryStage.stepTitle.uppercased())
                                .font(.system(size: 11, weight: .black))
                                .foregroundColor(Color.orange)
                            
                            Text(viewModel.deliveryStage == .toStore || viewModel.deliveryStage == .atStore ? order.storeName : order.consumerName)
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.white)
                        }
                        
                        Spacer()
                        
                        // Action Buttons: Chat & Call
                        HStack(spacing: 8) {
                            Button {
                                viewModel.activeChatTab = (viewModel.deliveryStage == .toStore || viewModel.deliveryStage == .atStore) ? .store : .customer
                                viewModel.showChatSheet = true
                            } label: {
                                Image(systemName: "message.fill")
                                    .font(.system(size: 14))
                                    .foregroundColor(.white)
                                    .frame(width: 36, height: 36)
                                    .background(Color.blue)
                                    .clipShape(Circle())
                            }
                            
                            Button {
                                let target = (viewModel.deliveryStage == .toStore || viewModel.deliveryStage == .atStore) ? "store" : "customer"
                                viewModel.openDualCall(targetRole: target)
                            } label: {
                                Image(systemName: "phone.fill")
                                    .font(.system(size: 14))
                                    .foregroundColor(.white)
                                    .frame(width: 36, height: 36)
                                    .background(Color.green)
                                    .clipShape(Circle())
                            }
                        }
                    }
                    
                    Divider().background(Color.white.opacity(0.15))
                    
                    // Turn / Address instruction
                    HStack(spacing: 10) {
                        Image(systemName: "arrow.triangle.turn.up.right.diamond.fill")
                            .font(.system(size: 20))
                            .foregroundColor(.yellow)
                        
                        VStack(alignment: .leading, spacing: 2) {
                            Text(viewModel.deliveryStage == .toStore || viewModel.deliveryStage == .atStore ? order.storeAddress : order.deliveryAddress)
                                .font(.system(size: 13, weight: .medium))
                                .foregroundColor(.white)
                                .lineLimit(2)
                            
                            if let notes = order.specialInstructions, !notes.isEmpty {
                                Text("Note: \(notes)")
                                    .font(.system(size: 11))
                                    .foregroundColor(.yellow)
                                    .lineLimit(1)
                            }
                        }
                        
                        Spacer()
                        
                        VStack(alignment: .trailing, spacing: 2) {
                            Text("\(viewModel.deliveryStage == .toStore || viewModel.deliveryStage == .atStore ? order.pickupDistanceMeters : order.dropoffDistanceMeters) m")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundColor(.white)
                            Text("~ \(order.estimatedTimeMinutes) min")
                                .font(.system(size: 11))
                                .foregroundColor(.gray)
                        }
                    }
                }
                .padding(14)
                .background(Color(red: 0.10, green: 0.12, blue: 0.18))
                .cornerRadius(16)
                .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.white.opacity(0.1), lineWidth: 1))
                .padding(.horizontal, 16)
                .padding(.top, 8)
                
                Spacer()
                
                // Bottom Operational Action Card
                VStack(spacing: 12) {
                    // Order Number & Package Verification Bar
                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("ORDER #\(order.orderNo)")
                                .font(.system(size: 14, weight: .black))
                                .foregroundColor(.white)
                            Text("\(order.items.count) item(s) • NPR \(Int(order.totalRs))")
                                .font(.system(size: 12))
                                .foregroundColor(.secondary)
                        }
                        
                        Spacer()
                        
                        // COD Warning Pill
                        if order.isCod {
                            HStack(spacing: 4) {
                                Image(systemName: "banknote.fill")
                                Text("COLLECT NPR \(Int(order.totalRs))")
                                    .font(.system(size: 11, weight: .bold))
                            }
                            .padding(.horizontal, 10)
                            .padding(.vertical, 5)
                            .background(Color.red.opacity(0.2))
                            .foregroundColor(.red)
                            .cornerRadius(12)
                            .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.red, lineWidth: 1))
                        } else {
                            HStack(spacing: 4) {
                                Image(systemName: "checkmark.circle.fill")
                                Text("PAID ONLINE")
                                    .font(.system(size: 11, weight: .bold))
                            }
                            .padding(.horizontal, 10)
                            .padding(.vertical, 5)
                            .background(Color.green.opacity(0.2))
                            .foregroundColor(.green)
                            .cornerRadius(12)
                        }
                    }
                    
                    // Stage-Specific Progression Buttons
                    switch viewModel.deliveryStage {
                    case .toStore:
                        Button {
                            viewModel.markArrivedAtStore()
                        } label: {
                            HStack {
                                Image(systemName: "location.fill")
                                Text("Arrived at Store 🏬")
                                    .font(.system(size: 15, weight: .bold))
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                            .background(Color.orange)
                            .foregroundColor(.white)
                            .cornerRadius(14)
                        }
                        
                    case .atStore:
                        VStack(spacing: 8) {
                            Text("Present Order #\(order.orderNo) to clerk")
                                .font(.system(size: 12, weight: .semibold))
                                .foregroundColor(.yellow)
                            
                            Button {
                                viewModel.confirmOrderPickedUp()
                            } label: {
                                HStack {
                                    Image(systemName: "shippingbox.fill")
                                    Text("Confirm Picked Up -> Head to Customer 🚀")
                                        .font(.system(size: 15, weight: .bold))
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 14)
                                .background(Color.green)
                                .foregroundColor(.white)
                                .cornerRadius(14)
                            }
                        }
                        
                    case .toCustomer:
                        Button {
                            viewModel.markArrivedAtCustomer()
                        } label: {
                            HStack {
                                Image(systemName: "bell.fill")
                                Text("Arrived at Customer Door 🚪")
                                    .font(.system(size: 15, weight: .bold))
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                            .background(Color.purple)
                            .foregroundColor(.white)
                            .cornerRadius(14)
                        }
                        
                    case .atCustomer:
                        Button {
                            viewModel.showOtpSheet = true
                        } label: {
                            HStack {
                                Image(systemName: "key.fill")
                                Text("Enter 4-Digit Customer OTP 🔐")
                                    .font(.system(size: 15, weight: .bold))
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(14)
                        }
                        
                    case .completed:
                        EmptyView()
                    }
                }
                .padding(16)
                .background(Color(red: 0.12, green: 0.14, blue: 0.22))
                .cornerRadius(20)
                .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.white.opacity(0.12), lineWidth: 1))
                .padding(.horizontal, 16)
                .padding(.bottom, 12)
            }
        )
    }
}
