package com.devt.NmbKwaWote.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.biometric.BiometricPrompt
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope
import com.devt.NmbKwaWote.presentation.theme.NmbKwaWoteTheme
import com.devt.NmbKwaWote.presentation.components.common.CustomBottomAppBar
import com.devt.NmbKwaWote.presentation.components.common.CustomTopAppBar
import com.devt.NmbKwaWote.presentation.components.common.NavItem
import com.devt.NmbKwaWote.presentation.navigation.NavGraph
import com.devt.NmbKwaWote.presentation.navigation.Screen
import android.widget.Toast
import androidx.biometric.BiometricManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


class MainActivity : FragmentActivity() {
    private val mainScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize biometric related stuff in background
        lifecycleScope.launch(Dispatchers.IO) {
            initializeBiometricDependencies()
        }

        setContent {
            NmbKwaWoteTheme {
                val navController = rememberNavController()
                var showBiometricAuth by remember { mutableStateOf(true) }
                MainScreen(
                    navController = navController,
                    showBiometricAuth = showBiometricAuth,
                    coroutineScope = mainScope
                )
            }
        }
    }

    private suspend fun initializeBiometricDependencies() {
        withContext(Dispatchers.IO) {
            // Create a BiometricManager instance using the current Activity context.
            val biometricManager = BiometricManager.from(this@MainActivity)

            // Check if the device can authenticate using biometrics.
            val authStatus = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )

            when (authStatus) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    // Simulate heavy initialization (e.g., key generation)
                    delay(2000L)
                    // Switch to the Main thread to show the Toast
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Biometric is available and initialized successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Biometric Error: No biometric hardware available.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Biometric Error: Biometric hardware is currently unavailable.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Biometric Error: No biometrics enrolled.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Biometric Error: Unknown error occurred.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel() // Clean up coroutines
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    showBiometricAuth: Boolean,
    coroutineScope: CoroutineScope
) {
    val context = LocalContext.current
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val executor = remember { ContextCompat.getMainExecutor(context) }
    val fragmentActivity = context as FragmentActivity

    val biometricPrompt = remember {
        BiometricPrompt(fragmentActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    coroutineScope.launch(Dispatchers.Main) {
                        errorMessage = errString.toString()
                        showError = true
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    coroutineScope.launch(Dispatchers.Main) {
                        navController.navigate(Screen.Home.route)
                    }
                }

                override fun onAuthenticationFailed() {
                    coroutineScope.launch(Dispatchers.Main) {
                        errorMessage = "Authentication failed. Please try again."
                        showError = true
                    }
                }
            })
    }

    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate with your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()
    }

    LaunchedEffect(Unit) {

        withContext(Dispatchers.IO) {

        }
    }

    Scaffold(
        snackbarHost = {
            if (showError) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    showError = false
                                }
                            }
                        ) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(errorMessage)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showBiometricAuth) {
                BiometricAuthScreen(
                    biometricPrompt = biometricPrompt,
                    promptInfo = promptInfo
                )
            } else {
                MainContent(navController, paddingValues)
            }
        }
    }
}

@Composable
private fun MainContent(navController: NavHostController, paddingValues: PaddingValues) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                userName = "louis kookaburra",
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
                        "Home" -> navController.navigate(Screen.Home.route) // Use Screen.Home.route
                        "Transactions" -> navController.navigate(Screen.Transactions.route)
                        "Card" -> navController.navigate(Screen.Card.route)
                        "Favourites" -> navController.navigate(Screen.Favourites.route)
                    }
                },
                onQrScanClick = { /* ... */ }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavGraph(navController = navController)
        }
    }
}

@Composable
fun BiometricAuthScreen(biometricPrompt: BiometricPrompt, promptInfo: BiometricPrompt.PromptInfo) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            biometricPrompt.authenticate(promptInfo)  // Launch biometric prompt
        }) {
            Text("Authenticate with Biometrics")
        }
    }
}