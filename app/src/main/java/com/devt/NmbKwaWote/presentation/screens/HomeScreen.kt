@file:Suppress("DEPRECATION")

package com.devt.NmbKwaWote.presentation.screens
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@Composable
fun ActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    val haptic = LocalHapticFeedback.current

    val isDark = isSystemInDarkTheme()

    val backgroundColor = if (isDark) Color.Black else Color.White
    val iconColor = if (isDark) Color(0xFFFFA500) else Color.Blue  // Orange in dark, blue in light
    val textColor = if (isDark) Color.White else Color.LightGray

    ElevatedButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = Modifier.size(100.dp), // Square button; adjust size as needed
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = backgroundColor
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        // Arrange the icon and label in a vertical column.
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label, // for accessibility (screen readers)
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun HomeScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Chagua Huduma",
                style = MaterialTheme.typography.headlineSmall
            )
            // First row with 3 buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    label = "Send Money",
                    icon = Icons.Outlined.Send,
                    onClick = { /* handle send money */ }
                )
                ActionButton(
                    label = "Pay Bills",
                    icon = Icons.Outlined.ReceiptLong,
                    onClick = { /* handle pay bills */ }
                )
                ActionButton(
                    label = "Withdraw Money",
                    icon = Icons.Outlined.AccountBalanceWallet,
                    onClick = { /* handle withdraw money */ }
                )
            }
            // Second row with 3 buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    label = "Save Money",
                    icon = Icons.Outlined.Savings,
                    onClick = { /* handle save money */ }
                )
                ActionButton(
                    label = "Scan to Pay",
                    icon = Icons.Outlined.QrCodeScanner,
                    onClick = { /* handle scan to pay */ }
                )
                ActionButton(
                    label = "Salary Advance",
                    icon = Icons.Outlined.AttachMoney,
                    onClick = { /* handle salary advance */ }
                )
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    label = "Jiwekee",
                    icon = Icons.Outlined.ShoppingBag,
                    onClick = { /* handle save money */ }
                )
                ActionButton(
                    label = "Spend to Save",
                    icon = Icons.Outlined.Wallet,
                    onClick = { /* handle scan to pay */ }
                )
                ActionButton(
                    label = "Zaidi",
                    icon = Icons.Filled.MoreHoriz,
                    onClick = { /* handle salary advance */ }
                )
            }
        }
    }
}
