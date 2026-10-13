//
//  EscPosPrinterService.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Bluetooth & Network ESC/POS Thermal Receipt Printer Service
//

import Foundation

public enum PrintDocType: String, CaseIterable, Identifiable {
    case kot = "Kitchen Order Ticket (KOT)"
    case customerBill = "Customer Tax Bill"
    
    public var id: String { rawValue }
}

public final class EscPosPrinterService {
    public static let shared = EscPosPrinterService()
    
    private init() {}
    
    // MARK: - ESC/POS Byte Commands
    private let ESC: UInt8 = 0x1B
    private let GS: UInt8  = 0x1D
    private let LF: UInt8  = 0x0A
    
    /// Generates raw binary ESC/POS payload for 58mm/80mm thermal printers
    public func generateEscPosBytes(order: RestaurantOrder, store: RestaurantStore, docType: PrintDocType) -> Data {
        var data = Data()
        
        // Initialize printer: ESC @
        data.append(contentsOf: [ESC, 0x40])
        
        // Center alignment: ESC a 1
        data.append(contentsOf: [ESC, 0x61, 0x01])
        
        // Double height & width for header: GS ! 0x11
        data.append(contentsOf: [GS, 0x21, 0x11])
        
        if docType == .kot {
            data.append(contentsOf: "** KITCHEN ORDER TICKET **\n".utf8)
        } else {
            data.append(contentsOf: "\(store.name.uppercased())\n".utf8)
        }
        
        // Normal text size: GS ! 0x00
        data.append(contentsOf: [GS, 0x21, 0x00])
        
        if docType == .customerBill {
            data.append(contentsOf: "\(store.address)\n".utf8)
            data.append(contentsOf: "PAN: \(store.panNumber) | Tel: \(store.phone)\n".utf8)
            data.append(contentsOf: "TAX INVOICE / CASH BILL\n".utf8)
        }
        
        // Left alignment: ESC a 0
        data.append(contentsOf: [ESC, 0x61, 0x00])
        data.append(contentsOf: "--------------------------------\n".utf8)
        data.append(contentsOf: "Order #: \(order.orderNo)\n".utf8)
        
        let dateStr = formattedDate(epoch: order.placedAtEpochSeconds)
        data.append(contentsOf: "Time:    \(dateStr)\n".utf8)
        
        if docType == .kot {
            data.append(contentsOf: "Prep:    \(order.prepTimeMinutes) mins\n".utf8)
            if let note = order.customerNote, !note.isEmpty {
                // Bold on: ESC E 1
                data.append(contentsOf: [ESC, 0x45, 0x01])
                data.append(contentsOf: "SPECIAL NOTE: \(note.uppercased())\n".utf8)
                data.append(contentsOf: [ESC, 0x45, 0x00])
            }
        } else {
            data.append(contentsOf: "Customer: \(order.consumerName) (\(order.consumerPhone))\n".utf8)
            data.append(contentsOf: "Payment:  \(order.paymentMethod.uppercased())\n".utf8)
        }
        
        data.append(contentsOf: "--------------------------------\n".utf8)
        
        // Item Table
        if docType == .kot {
            data.append(contentsOf: "QTY   ITEM DESCRIPTION\n".utf8)
            data.append(contentsOf: "--------------------------------\n".utf8)
            for item in order.items {
                let qtyStr = String(format: "%-5d", item.quantity)
                data.append(contentsOf: "\(qtyStr) \(item.name)\n".utf8)
                if let note = item.customerNotes, !note.isEmpty {
                    data.append(contentsOf: "      * \(note)\n".utf8)
                }
            }
        } else {
            data.append(contentsOf: "QTY  ITEM             RATE   TOTAL\n".utf8)
            data.append(contentsOf: "--------------------------------\n".utf8)
            for item in order.items {
                let qtyStr = String(format: "%-4d", item.quantity)
                let nameTrunc = item.name.count > 15 ? String(item.name.prefix(13)) + ".." : item.name.padding(toLength: 15, withPad: " ", startingAt: 0)
                let rate = Double(item.unitPricePaisa) / 100.0
                let total = Double(item.lineTotalPaisa) / 100.0
                let line = String(format: "%@ %@ %5.0f %6.0f\n", qtyStr, nameTrunc, rate, total)
                data.append(contentsOf: line.utf8)
            }
            data.append(contentsOf: "--------------------------------\n".utf8)
            
            // Grand Total
            data.append(contentsOf: [ESC, 0x45, 0x01]) // Bold on
            let totalLine = String(format: "GRAND TOTAL:          NPR %7.2f\n", order.totalNpr)
            data.append(contentsOf: totalLine.utf8)
            data.append(contentsOf: [ESC, 0x45, 0x00]) // Bold off
            data.append(contentsOf: "--------------------------------\n".utf8)
            data.append(contentsOf: "Thank you for dining with us!\n".utf8)
            data.append(contentsOf: "Powered by Evrry Super App\n".utf8)
        }
        
        // Feed and partial cut: GS V 65 0
        data.append(contentsOf: [LF, LF, LF])
        data.append(contentsOf: [GS, 0x56, 0x41, 0x10])
        
        return data
    }
    
    /// Generates human-readable monospaced string for UI preview
    public func generateReceiptPreviewText(order: RestaurantOrder, store: RestaurantStore, docType: PrintDocType) -> String {
        var text = ""
        let sep = "================================"
        let thinSep = "--------------------------------"
        
        if docType == .kot {
            text += "      *** KITCHEN TICKET ***     \n"
            text += "         \(store.name)          \n"
        } else {
            text += "       \(store.name.uppercased())       \n"
            text += "          \(store.address)         \n"
            text += "     PAN: \(store.panNumber) | \(store.phone)    \n"
            text += "        TAX INVOICE / CASH BILL  \n"
        }
        text += "\(sep)\n"
        text += "Order No:  #\(order.orderNo)\n"
        text += "Date/Time: \(formattedDate(epoch: order.placedAtEpochSeconds))\n"
        
        if docType == .kot {
            text += "Est Prep:  \(order.prepTimeMinutes) mins\n"
            if let note = order.customerNote, !note.isEmpty {
                text += "INSTRUCTION: >> \(note.uppercased()) <<\n"
            }
            text += "\(thinSep)\n"
            text += "QTY   ITEM\n"
            text += "\(thinSep)\n"
            for item in order.items {
                text += String(format: "%-5d %@\n", item.quantity, item.name)
                if let itemNote = item.customerNotes, !itemNote.isEmpty {
                    text += "      * \(itemNote)\n"
                }
            }
        } else {
            text += "Customer:  \(order.consumerName)\n"
            text += "Phone:     \(order.consumerPhone)\n"
            text += "Payment:   \(order.paymentMethod.uppercased())\n"
            text += "\(thinSep)\n"
            text += "QTY  ITEM              RATE  TOTAL\n"
            text += "\(thinSep)\n"
            for item in order.items {
                let nameTrunc = item.name.count > 14 ? String(item.name.prefix(12)) + ".." : item.name.padding(toLength: 14, withPad: " ", startingAt: 0)
                let rate = Double(item.unitPricePaisa) / 100.0
                let total = Double(item.lineTotalPaisa) / 100.0
                text += String(format: "%-4d %@ %5.0f %6.0f\n", item.quantity, nameTrunc, rate, total)
            }
            text += "\(sep)\n"
            text += String(format: "GRAND TOTAL:          NPR %7.2f\n", order.totalNpr)
            text += "\(sep)\n"
            text += "    Thank you! Powered by Evrry  \n"
        }
        
        return text
    }
    
    private func formattedDate(epoch: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(epoch))
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm"
        return formatter.string(from: date)
    }
}
