package com.elifsi.evrry.partner

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.elifsi.evrry.partner.ui.navigation.PartnerNavHost

/**
 * Main Activity for Evrry Unified Native Partner App
 * Platform: Android (Kotlin + Jetpack Compose)
 * Elifsi Technologies Private Limited
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen awake for active Kitchen Display Systems (KDS)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            PartnerNavHost()
        }
    }
}
