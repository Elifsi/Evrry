//
//  CourierCodLedgerSheet.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Cash-on-Delivery (COD) Cash in Hand Ledger & Daily Balance Sheet
//  Grounded in enatega-multivendor-rider
//

import SwiftUI

public struct CourierCodLedgerSheet: View {
    @ObservedObject var viewModel: CourierDeliveryViewModel
    @Environment(\.dismiss) private var dismiss
    
    public init(viewModel: CourierDeliveryViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        VStack(spacing: 16) {
            // Header
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Courier Wallet & COD Ledger")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.white)
                    Text("Physical Cash Collected & Daily Settlements")
                        .font(.system(size: 12))
                        .foregroundColor(.gray)
                }
                Spacer()
                Button {
                    viewModel.showLedgerSheet = false
                } label: {
                    Image(systemName: "xmark.circle.fill")
                        .font(.system(size: 24))
                        .foregroundColor(.gray)
                }
            }
            .padding(.horizontal, 20)
            .padding(.top, 16)
            
            // Balance Metrics Summary Cards
            HStack(spacing: 12) {
                // Cash In Hand Card
                VStack(alignment: .leading, spacing: 6) {
                    HStack {
                        Image(systemName: "banknote.fill")
                            .foregroundColor(.orange)
                        Text("CASH IN HAND")
                            .font(.system(size: 10, weight: .bold))
                            .foregroundColor(.orange)
                    }
                    Text("NPR \(Int(Double(viewModel.codCashInHandPaisa) / 100.0))")
                        .font(.system(size: 20, weight: .black))
                        .foregroundColor(.white)
                    Text("COD to settle at midnight")
                        .font(.system(size: 10))
                        .foregroundColor(.gray)
                }
                .padding(14)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(red: 0.14, green: 0.16, blue: 0.25))
                .cornerRadius(14)
                .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.orange.opacity(0.3), lineWidth: 1))
                
                // Today's Earnings Card
                VStack(alignment: .leading, spacing: 6) {
                    HStack {
                        Image(systemName: "arrow.up.right.circle.fill")
                            .foregroundColor(.green)
                        Text("TODAY'S EARNING")
                            .font(.system(size: 10, weight: .bold))
                            .foregroundColor(.green)
                    }
                    Text("NPR \(Int(Double(viewModel.todayEarningsPaisa) / 100.0))")
                        .font(.system(size: 20, weight: .black))
                        .foregroundColor(.white)
                    Text("\(viewModel.todayDeliveriesCount) trips completed")
                        .font(.system(size: 10))
                        .foregroundColor(.gray)
                }
                .padding(14)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(red: 0.14, green: 0.16, blue: 0.25))
                .cornerRadius(14)
                .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.green.opacity(0.3), lineWidth: 1))
            }
            .padding(.horizontal, 20)
            
            // Instant Settlement / Bank Deposit Action
            Button {
                viewModel.requestInstantCashout()
            } label: {
                HStack {
                    Image(systemName: "building.columns.fill")
                    Text("Deposit via NCHL / connectIPS")
                        .font(.system(size: 14, weight: .bold))
                    Spacer()
                    Image(systemName: "chevron.right")
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
                .background(Color.blue.opacity(0.15))
                .foregroundColor(.blue)
                .cornerRadius(12)
                .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.blue.opacity(0.3), lineWidth: 1))
            }
            .padding(.horizontal, 20)
            
            // Ledger Transaction History
            VStack(alignment: .leading, spacing: 10) {
                Text("TODAY'S TRANSACTIONS")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(.gray)
                    .padding(.horizontal, 20)
                
                ScrollView {
                    LazyVStack(spacing: 8) {
                        if viewModel.ledgerEntries.isEmpty {
                            Text("No transactions logged yet today")
                                .font(.system(size: 13))
                                .foregroundColor(.gray)
                                .padding(24)
                        } else {
                            ForEach(viewModel.ledgerEntries) { entry in
                                HStack {
                                    Image(systemName: entry.isCredit ? "plus.circle.fill" : "minus.circle.fill")
                                        .font(.system(size: 16))
                                        .foregroundColor(entry.isCredit ? .green : .orange)
                                    
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(entry.title)
                                            .font(.system(size: 13, weight: .bold))
                                            .foregroundColor(.white)
                                        Text(entry.type == "cod_collected" ? "Physical COD Cash" : "Delivery fee & tips")
                                            .font(.system(size: 11))
                                            .foregroundColor(.gray)
                                    }
                                    
                                    Spacer()
                                    
                                    Text("\(entry.isCredit ? "+" : "")NPR \(Int(entry.amountRs))")
                                        .font(.system(size: 14, weight: .bold))
                                        .foregroundColor(entry.isCredit ? .green : .white)
                                }
                                .padding(12)
                                .background(Color(red: 0.12, green: 0.14, blue: 0.22))
                                .cornerRadius(12)
                            }
                        }
                    }
                    .padding(.horizontal, 20)
                }
            }
            
            Spacer()
        }
        .background(Color(red: 0.08, green: 0.10, blue: 0.16).ignoresSafeArea())
    }
}
