package com.devt.NmbKwaWote.presentation.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    userName: String,
    onSettingsClick: () -> Unit
) {
    // Equivalent to CSS: position: fixed; top: 0; width: 100%;
    TopAppBar(
        // Equivalent to CSS: display: flex; justify-content: space-between;
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                // Equivalent to CSS: gap: 8px;
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Equivalent to HTML: <img src="account-icon.png" alt="account"/>
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Account"
                )
                // Equivalent to HTML: <span>Username</span>
                Text(text = userName)
            }
        },
        // Equivalent to CSS: display: flex; align-items: center;
        actions = {
            // Equivalent to HTML: <button><img src="settings-icon.png"/></button>
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(

            containerColor = MaterialTheme.colorScheme.surface,

            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}