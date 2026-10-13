package com.elifsi.evrry.consumer.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.AuthViewModel

/**
 * Dedicated Account Profile Management Screen
 * Dedicated screen for managing Name, Username, Phone number, and Bio status.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val userProfile by authViewModel.userProfile.collectAsState()

    var fullNameInput by remember { mutableStateOf(userProfile.fullName) }
    var usernameInput by remember { mutableStateOf(userProfile.username) }
    var bioStatusInput by remember { mutableStateOf(userProfile.bioStatus) }
    var savedSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Account Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                        Text("Personal identity & public username", fontSize = 11.sp, color = EvrryMutedText)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = EvrryDarkCharcoal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF9FBFF))
            )
        },
        containerColor = Color(0xFFF9FBFF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // AVATAR PREVIEW
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = EvrryPrimaryBlue,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = fullNameInput.take(1).ifBlank { "R" }.uppercase(),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Text(
                    text = "Tap to update avatar",
                    fontSize = 12.sp,
                    color = EvrryPrimaryBlue,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // FORM FIELDS
            Text("FULL NAME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            OutlinedTextField(
                value = fullNameInput,
                onValueChange = { fullNameInput = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EvrryMutedText) }
            )

            Text("PUBLIC USERNAME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            OutlinedTextField(
                value = usernameInput,
                onValueChange = { usernameInput = it.filter { ch -> ch.isLetterOrDigit() || ch == '_' } },
                prefix = { Text("@", color = EvrryPrimaryBlue, fontWeight = FontWeight.Bold) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Text("VERIFIED MOBILE NUMBER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            OutlinedTextField(
                value = userProfile.phone,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EvrryMutedText) },
                trailingIcon = {
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("VERIFIED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            )

            Text("STATUS / BIO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            OutlinedTextField(
                value = bioStatusInput,
                onValueChange = { bioStatusInput = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            if (savedSuccess) {
                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                        Text("Profile successfully updated!", fontSize = 13.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    authViewModel.updateProfile(
                        fullName = fullNameInput,
                        username = usernameInput,
                        bioStatus = bioStatusInput
                    )
                    savedSuccess = true
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
            ) {
                Text("Save Profile Changes", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
