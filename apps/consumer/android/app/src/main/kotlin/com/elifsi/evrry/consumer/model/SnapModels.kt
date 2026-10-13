package com.elifsi.evrry.consumer.model

import java.net.URLDecoder

data class SnapItem(
    val id: String,
    val imageUrl: String,
    val filterName: String,
    val location: String = "Kathmandu, Nepal",
    val caption: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isLocked: Boolean = false
)

enum class MemoryTab {
    SNAPS,
    CAMERA_ROLL,
    MY_LOCKER
}

data class QrScanResult(
    val raw: String,
    val type: String, // "fonepay", "contact", "restaurant", "url", "text"
    val title: String,
    val subtitle: String,
    val actionLabel: String,
    val target: String,
    val amount: Double? = null
)

object QrParser {
    fun parse(raw: String): QrScanResult {
        val trimmed = raw.trim()

        // 1. Fonepay & Nepal QR
        if (trimmed.startsWith("000201") || trimmed.contains("fonepay", ignoreCase = true) || trimmed.contains("nepalqr", ignoreCase = true)) {
            var merchant = "Himalayan Java Coffee"
            var amount = 350.0

            val amtRegex = "amount=([0-9.]+)".toRegex(RegexOption.IGNORE_CASE)
            val amtMatch = amtRegex.find(trimmed)
            if (amtMatch != null) {
                amtMatch.groupValues.getOrNull(1)?.toDoubleOrNull()?.let { amount = it }
            }

            val merRegex = "merchant=([^&]+)".toRegex(RegexOption.IGNORE_CASE)
            val merMatch = merRegex.find(trimmed)
            if (merMatch != null) {
                merchant = URLDecoder.decode(merMatch.groupValues[1], "UTF-8").replace("+", " ")
            }

            return QrScanResult(
                raw = trimmed,
                type = "fonepay",
                title = "Fonepay Merchant QR",
                subtitle = "$merchant · Rs $amount",
                actionLabel = "Pay with Fonepay",
                target = "cart",
                amount = amount
            )
        }

        // 2. Evrry Friend Contact QR
        if (trimmed.startsWith("evrry://user/") || trimmed.startsWith("evrry://chat/") || trimmed.contains("evrry.com/u/") || trimmed.startsWith("@")) {
            val handle = trimmed
                .replace("evrry://user/", "")
                .replace("evrry://chat/", "")
                .replace("https://evrry.com/u/", "")
                .replace("@", "")

            val name = handle.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

            return QrScanResult(
                raw = trimmed,
                type = "contact",
                title = "Evrry User · @$handle",
                subtitle = "$name Sharma · Available for Chat & Split Pay",
                actionLabel = "Open Chat",
                target = "chat_room"
            )
        }

        // 3. Restaurant Table QR
        if (trimmed.startsWith("evrry://restaurant/") || trimmed.contains("table=")) {
            val tableMatch = "table=([0-9A-Za-z]+)".toRegex(RegexOption.IGNORE_CASE).find(trimmed)
            val tableNum = tableMatch?.groupValues?.getOrNull(1) ?: "4"

            return QrScanResult(
                raw = trimmed,
                type = "restaurant",
                title = "Table Service Order",
                subtitle = "Roadhouse Cafe (Pulchowk) · Table #$tableNum",
                actionLabel = "View Digital Menu",
                target = "restaurant_detail"
            )
        }

        // 4. Standard Web Link
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return QrScanResult(
                raw = trimmed,
                type = "url",
                title = "Website Link",
                subtitle = trimmed.replace("https://", "").replace("http://", ""),
                actionLabel = "Open Link",
                target = trimmed
            )
        }

        // 5. Plain Text
        return QrScanResult(
            raw = trimmed,
            type = "text",
            title = "QR Code Content",
            subtitle = if (trimmed.length > 36) trimmed.take(33) + "..." else trimmed,
            actionLabel = "Copy Text",
            target = trimmed
        )
    }
}

data class CameraFilterLens(
    val id: String,
    val name: String,
    val subtitle: String
)
