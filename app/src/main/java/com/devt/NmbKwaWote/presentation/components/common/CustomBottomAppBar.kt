package com.devt.NmbKwaWote.presentation.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class NavItem(val label: String, val icon: ImageVector)

@Composable
fun CustomBottomAppBar(
    navItems: List<NavItem> = listOf(),
    onNavItemClick: (NavItem) -> Unit = {},
    onQrScanClick: () -> Unit = {}
) {
    BottomAppBar(
        containerColor = Color(0xFFFFA500),
        contentColor = Color.White,
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    BottomNavigationItem(item = item, onItemClick = { onNavItemClick(item) })
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onQrScanClick,
                containerColor = Color.White,
                contentColor = Color(0xFFFFA500)
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription = "Scan QR Code"
                )
            }
        }
    )
}
@Composable
fun BottomNavigationItem(
    item: NavItem,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onItemClick) // Fixed: Added clickable modifier
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}
