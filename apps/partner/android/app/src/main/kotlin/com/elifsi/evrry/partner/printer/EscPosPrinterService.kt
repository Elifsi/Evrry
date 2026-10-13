package com.elifsi.evrry.partner.printer

import com.elifsi.evrry.partner.model.RestaurantOrder
import com.elifsi.evrry.partner.model.RestaurantStore
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ESC/POS Thermal Printing Service for Kitchen Partner (Android)
 * Platform: Android (Kotlin)
 * Elifsi Technologies Private Limited
 *
 * Implements standard ESC/POS command protocol for Bluetooth, USB, and LAN thermal receipt printers (58mm & 80mm).
 * Grounded in enatega-multivendor-store format-receipt.ts and print.ts.
 */
@Singleton
class EscPosPrinterService @Inject constructor() {

    // ESC/POS Command Byte Constants
    companion object {
        val ESC_INIT = byteArrayOf(0x1B, 0x40) // Initialize printer
        val ESC_ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)
        val ESC_ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
        val ESC_ALIGN_RIGHT = byteArrayOf(0x1B, 0x61, 0x02)
        val ESC_BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)
        val ESC_BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)
        val GS_DOUBLE_SIZE = byteArrayOf(0x1D, 0x21, 0x11) // 2x width + 2x height
        val GS_NORMAL_SIZE = byteArrayOf(0x1D, 0x21, 0x00)
        val GS_FEED_AND_CUT = byteArrayOf(0x1D, 0x56, 0x41, 0x03) // Cut with 3-line feed
        val LF = "\n".toByteArray(Charsets.US_ASCII)
    }

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    /**
     * Generates ESC/POS byte stream for Kitchen Order Ticket (KOT)
     */
    fun buildKotBytes(order: RestaurantOrder, store: RestaurantStore): ByteArray {
        val stream = ByteArrayOutputStream()

        fun write(bytes: ByteArray) = stream.write(bytes)
        fun writeText(text: String) = stream.write(text.toByteArray(Charsets.UTF_8))
        fun line(text: String = "") {
            writeText(text)
            write(LF)
        }

        write(ESC_INIT)

        // Header: Store Name & KOT Title
        write(ESC_ALIGN_CENTER)
        write(ESC_BOLD_ON)
        write(GS_DOUBLE_SIZE)
        line("*** KITCHEN ORDER TICKET ***")
        write(GS_NORMAL_SIZE)
        line(store.name)
        line("------------------------------------------")

        // Order Info
        write(ESC_ALIGN_LEFT)
        write(ESC_BOLD_ON)
        line("ORDER NO: #${order.orderNo}")
        write(ESC_BOLD_OFF)
        line("Placed At: ${dateFormatter.format(Date(order.placedAt))}")
        line("Prep Target: ${order.prepTimeMinutes} Minutes")
        line("Type: Delivery Courier Pickup")
        line("==========================================")

        // Items Section
        line("QTY   ITEM NAME")
        line("------------------------------------------")
        write(ESC_BOLD_ON)
        order.items.forEach { item ->
            line("${item.quantity}x   ${item.name.uppercase()}")
            if (item.customizations.isNotEmpty()) {
                write(ESC_BOLD_OFF)
                item.customizations.forEach { custom ->
                    line("      * $custom")
                }
                write(ESC_BOLD_ON)
            }
        }
        write(ESC_BOLD_OFF)
        line("==========================================")

        // Special Instructions / Allergies
        if (!order.customerNote.isNullOrBlank()) {
            write(ESC_BOLD_ON)
            line("SPECIAL INSTRUCTIONS / ALLERGIES:")
            line(">> ${order.customerNote} <<")
            write(ESC_BOLD_OFF)
            line("------------------------------------------")
        }

        // Footer
        write(ESC_ALIGN_CENTER)
        line("PRINTED BY EVRRY KDS · KITCHEN COPY")
        line("")
        write(GS_FEED_AND_CUT)

        return stream.toByteArray()
    }

    /**
     * Generates ESC/POS byte stream for Customer Receipt / Tax Bill
     */
    fun buildCustomerBillBytes(order: RestaurantOrder, store: RestaurantStore): ByteArray {
        val stream = ByteArrayOutputStream()

        fun write(bytes: ByteArray) = stream.write(bytes)
        fun writeText(text: String) = stream.write(text.toByteArray(Charsets.UTF_8))
        fun line(text: String = "") {
            writeText(text)
            write(LF)
        }

        write(ESC_INIT)

        // Header
        write(ESC_ALIGN_CENTER)
        write(ESC_BOLD_ON)
        write(GS_DOUBLE_SIZE)
        line(store.name)
        write(GS_NORMAL_SIZE)
        write(ESC_BOLD_OFF)
        line(store.addressText)
        line("Tax Invoice / Receipt")
        line("------------------------------------------")

        // Order Meta
        write(ESC_ALIGN_LEFT)
        line("Order ID: #${order.orderNo}")
        line("Date: ${dateFormatter.format(Date(order.placedAt))}")
        line("Customer: ${order.consumerName} (${order.consumerPhone})")
        line("Payment Mode: ${order.paymentMethod.uppercase()} ${if (order.isPaid) "[PAID]" else "[COLLECT COD]"}")
        line("==========================================")

        // Itemized List
        line("QTY  ITEM                     AMOUNT (NPR)")
        line("------------------------------------------")
        order.items.forEach { item ->
            val name = if (item.name.length > 20) item.name.take(19) + "." else item.name.padEnd(20)
            val qty = "${item.quantity}x".padEnd(4)
            val amt = String.format(Locale.US, "%8.2f", item.lineTotalRs)
            line("$qty $name $amt")
        }
        line("------------------------------------------")

        // Financial Tally
        write(ESC_ALIGN_RIGHT)
        line("Subtotal:  NPR ${String.format(Locale.US, "%.2f", order.subtotalRs)}")
        if (order.taxPaisa > 0) {
            line("VAT (13%): NPR ${String.format(Locale.US, "%.2f", order.taxPaisa / 100.0)}")
        }
        line("Delivery Fee: NPR ${String.format(Locale.US, "%.2f", order.deliveryFeePaisa / 100.0)}")
        line("Platform Fee: NPR ${String.format(Locale.US, "%.2f", order.platformFeePaisa / 100.0)}")
        line("------------------------------------------")
        write(ESC_BOLD_ON)
        write(GS_DOUBLE_SIZE)
        line("TOTAL: NPR ${String.format(Locale.US, "%.2f", order.totalRs)}")
        write(GS_NORMAL_SIZE)
        write(ESC_BOLD_OFF)
        line("==========================================")

        // Footer
        write(ESC_ALIGN_CENTER)
        line("Dhanyabad! Thank you for dining with us.")
        line("Powered by evrry Super App Nepal")
        line("")
        write(GS_FEED_AND_CUT)

        return stream.toByteArray()
    }

    /**
     * Formats receipt as printable Monospace text for in-app preview dialog
     */
    fun formatKotTextPreview(order: RestaurantOrder, store: RestaurantStore): String {
        val sb = StringBuilder()
        sb.appendLine("==========================================")
        sb.appendLine("        *** KITCHEN ORDER TICKET ***      ")
        sb.appendLine("            ${store.name}                 ")
        sb.appendLine("==========================================")
        sb.appendLine("ORDER NO: #${order.orderNo}")
        sb.appendLine("Date: ${dateFormatter.format(Date(order.placedAt))}")
        sb.appendLine("Target Prep: ${order.prepTimeMinutes} Minutes")
        sb.appendLine("------------------------------------------")
        sb.appendLine("ITEMS:")
        order.items.forEach { item ->
            sb.appendLine("${item.quantity}x ${item.name.uppercase()}")
            item.customizations.forEach { custom ->
                sb.appendLine("    * $custom")
            }
        }
        if (!order.customerNote.isNullOrBlank()) {
            sb.appendLine("------------------------------------------")
            sb.appendLine("SPECIAL INSTRUCTIONS:")
            sb.appendLine(">> ${order.customerNote} <<")
        }
        sb.appendLine("==========================================")
        sb.appendLine("         evrry Kitchen Display System     ")
        return sb.toString()
    }

    /**
     * Formats customer receipt preview
     */
    fun formatCustomerBillPreview(order: RestaurantOrder, store: RestaurantStore): String {
        val sb = StringBuilder()
        sb.appendLine("==========================================")
        sb.appendLine("           ${store.name}                  ")
        sb.appendLine("       ${store.addressText}               ")
        sb.appendLine("           OFFICIAL TAX RECEIPT           ")
        sb.appendLine("==========================================")
        sb.appendLine("Order ID: #${order.orderNo}")
        sb.appendLine("Customer: ${order.consumerName} · ${order.consumerPhone}")
        sb.appendLine("Payment:  ${order.paymentMethod.uppercase()} ${if (order.isPaid) "[PAID]" else "[COLLECT CASH]"}")
        sb.appendLine("------------------------------------------")
        sb.appendLine("QTY  ITEM                     TOTAL (NPR) ")
        sb.appendLine("------------------------------------------")
        order.items.forEach { item ->
            val name = if (item.name.length > 20) item.name.take(19) + "." else item.name.padEnd(20)
            val qty = "${item.quantity}x".padEnd(4)
            val amt = String.format(Locale.US, "%8.2f", item.lineTotalRs)
            sb.appendLine("$qty $name $amt")
        }
        sb.appendLine("------------------------------------------")
        sb.appendLine("Subtotal:     NPR ${String.format(Locale.US, "%.2f", order.subtotalRs)}")
        sb.appendLine("Delivery Fee: NPR ${String.format(Locale.US, "%.2f", order.deliveryFeePaisa / 100.0)}")
        sb.appendLine("Platform Fee: NPR ${String.format(Locale.US, "%.2f", order.platformFeePaisa / 100.0)}")
        sb.appendLine("------------------------------------------")
        sb.appendLine("GRAND TOTAL:  NPR ${String.format(Locale.US, "%.2f", order.totalRs)}")
        sb.appendLine("==========================================")
        sb.appendLine("     Dhanyabad! Thank you for ordering!   ")
        return sb.toString()
    }
}
