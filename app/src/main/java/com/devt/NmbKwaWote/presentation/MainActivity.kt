package com.devt.NmbKwaWote.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devt.NmbKwaWote.BuildConfig
import com.devt.NmbKwaWote.presentation.components.common.CustomBottomAppBar
import com.devt.NmbKwaWote.presentation.components.common.CustomTopAppBar
import com.devt.NmbKwaWote.presentation.components.common.NavItem
import com.devt.NmbKwaWote.presentation.navigation.Screen
import com.devt.NmbKwaWote.presentation.screens.CardScreen
import com.devt.NmbKwaWote.presentation.screens.FavouritesScreen
import com.devt.NmbKwaWote.presentation.screens.SettingScreen
import com.devt.NmbKwaWote.presentation.screens.TransactionsScreen
import com.devt.NmbKwaWote.presentation.theme.NmbKwaWoteTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// --- Authentication-related classes ---

// Events that can occur in the app
sealed class AuthEvent {
    object InitiateBiometric : AuthEvent()
    object AuthenticationSucceeded : AuthEvent()
    data class AuthenticationFailed(val error: String) : AuthEvent()
    object RetryAuthentication : AuthEvent()
    object SkipAuthentication : AuthEvent() // For debug mode
}

// UI States
sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

// Side effects that need to be handled (such as navigation)
sealed class AuthEffect {
    object NavigateToHome : AuthEffect()
    data class ShowError(val message: String) : AuthEffect()
    object ShowBiometricPrompt : AuthEffect()
}

class MainViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    // Channel for events
    private val eventChannel = Channel<AuthEvent>(Channel.BUFFERED)
    // Channel for side effects
    private val _effectChannel = Channel<AuthEffect>(Channel.BUFFERED)
    val effectFlow = _effectChannel.receiveAsFlow()

    init {
        processEvents()
    }

    private fun processEvents() {
        viewModelScope.launch {
            eventChannel.consumeAsFlow().collect { event ->
                when (event) {
                    is AuthEvent.InitiateBiometric -> handleBiometricInitiation()
                    is AuthEvent.AuthenticationSucceeded -> handleAuthSuccess()
                    is AuthEvent.AuthenticationFailed -> handleAuthFailure(event.error)
                    is AuthEvent.RetryAuthentication -> handleRetry()
                    is AuthEvent.SkipAuthentication -> handleSkipAuth()
                }
            }
        }
    }

    fun sendEvent(event: AuthEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private suspend fun handleBiometricInitiation() {
        _authState.value = AuthState.Loading
        _effectChannel.send(AuthEffect.ShowBiometricPrompt)
    }

    private suspend fun handleAuthSuccess() {
        _authState.value = AuthState.Authenticated
        _effectChannel.send(AuthEffect.NavigateToHome)
    }

    private suspend fun handleAuthFailure(error: String) {
        _authState.value = AuthState.Error(error)
        _effectChannel.send(AuthEffect.ShowError(error))
    }

    private suspend fun handleRetry() {
        _authState.value = AuthState.Initial
        _effectChannel.send(AuthEffect.ShowBiometricPrompt)
    }

    private suspend fun handleSkipAuth() {
        _authState.value = AuthState.Authenticated
        _effectChannel.send(AuthEffect.NavigateToHome)
    }
}

// --- MainActivity and AppContent ---

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NmbKwaWoteTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent(mainViewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val navController = rememberNavController()
    val authState by mainViewModel.authState.collectAsState()
    val context = LocalContext.current

    // Handle side effects (navigation, errors)
    LaunchedEffect(Unit) {
        mainViewModel.effectFlow.collect { effect ->
            when (effect) {
                is AuthEffect.NavigateToHome -> {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
                is AuthEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is AuthEffect.ShowBiometricPrompt -> {
                    // You could trigger biometric UI here if needed.
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(onAuthSuccess = {
                // When authentication succeeds, send an event to the view model.
                mainViewModel.sendEvent(AuthEvent.AuthenticationSucceeded)
            })
        }
        composable(Screen.Home.route) {
            HomeContent(navController)
        }
        composable(Screen.Settings.route) { SettingScreen() }
        composable(Screen.Transactions.route) { TransactionsScreen() }
        composable(Screen.Card.route) { CardScreen() }
        composable(Screen.Favourites.route) { FavouritesScreen() }
    }
}


@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val executor = remember { ContextCompat.getMainExecutor(context) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val biometricPrompt = remember {
        BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onAuthSuccess()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                showError = true
                errorMessage = errString.toString()
            }
            override fun onAuthenticationFailed() {
                showError = true
                errorMessage = "Authentication failed. Please try again."
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { biometricPrompt.authenticate(promptInfo) }) {
            Text("Authenticate with Biometrics")
        }
        if (BuildConfig.DEBUG) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAuthSuccess,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Debug: Skip Authentication")
            }
        }
        if (showError) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { showError = false }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(errorMessage)
            }
        }
    }
}

@Composable
fun HomeContent(navController: NavHostController) {
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
                        "Home" -> navController.navigate(Screen.Home.route)
                        "Transactions" -> navController.navigate(Screen.Transactions.route)
                        "Card" -> navController.navigate(Screen.Card.route)
                        "Favourites" -> navController.navigate(Screen.Favourites.route)
                    }
                },
                onQrScanClick = { /* ... */ }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

        }
    }
}
