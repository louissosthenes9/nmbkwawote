import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devt.NmbKwaWote.presentation.components.common.CustomBottomAppBar
import com.devt.NmbKwaWote.presentation.components.common.CustomTopAppBar
import com.devt.NmbKwaWote.presentation.components.common.NavItem
import com.devt.NmbKwaWote.presentation.navigation.Screen


@Composable
fun CommonScreenLayout(
    navController: NavHostController,
    userName: String = "louis kookaburra", // Can be made dynamic if needed
    screenTitle: String,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                userName = userName,
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        },
        bottomBar = {
            CustomBottomAppBar(
                navItems = listOf(
                    NavItem("Home", Icons.Filled.Home),
                    NavItem("Transactions", Icons.Filled.Receipt),
                    NavItem("Card", Icons.Filled.CreditCard),
                    NavItem("Favourites", Icons.Filled.Star)
                ),
                onNavItemClick = { item ->
                    when (item.label) {
                        "Home" -> navController.navigate(Screen.Home.route)
                        "Transactions" -> navController.navigate(Screen.Transactions.route)
                        "Card" -> navController.navigate(Screen.Card.route)
                        "Favourites" -> navController.navigate(Screen.Favourites.route)
                    }
                },
                onQrScanClick = { /* Handle QR scan */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Screen title
            Text(
                text = screenTitle,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            // Screen-specific content
            content(paddingValues)
        }
    }
}