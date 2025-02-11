package com.devt.NmbKwaWote.presentation.screens.features

import CommonScreenLayout
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

// ViewModel
class SendMoneyViewModel : ViewModel() {
    private val _biometricResult = MutableStateFlow<BiometricResult>(BiometricResult.None)
    val biometricResult: StateFlow<BiometricResult> = _biometricResult.asStateFlow()

    fun sendMoney(amount: Double, recipient: String) {
        // Implement your send money logic here
        viewModelScope.launch {
            // Add your API calls or database operations
        }
    }

    fun resetBiometricResult() {
        _biometricResult.value = BiometricResult.None
    }
}

// Sealed class for biometric results
sealed class BiometricResult {
    object None : BiometricResult()
    object Success : BiometricResult()
    data class Error(val message: String) : BiometricResult()
}

// Main Screen Composable
@Composable
fun SendMoneyScreen(
    navController: NavHostController,
    viewModel: SendMoneyViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // State management
    var amount by remember { mutableStateOf("") }
    var recipient by remember { mutableStateOf("") }
    var isAmountError by remember { mutableStateOf(false) }
    var isRecipientError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // TTS management
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    // Error state holder for TTS callbacks
    val ttsError = remember { mutableStateOf<String?>(null) }

    // Biometric state
    val biometricStatus = remember { mutableStateOf(BiometricStatus.NOT_CHECKED) }

    // Effect to update error message when TTS error occurs
    LaunchedEffect(ttsError.value) {
        ttsError.value?.let {
            errorMessage = it
            ttsError.value = null
        }
    }

    // Biometric prompt setup
    val executor = remember(context) { ContextCompat.getMainExecutor(context) }
    val biometricPrompt = remember(executor) {
        BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    scope.launch {
                        viewModel.sendMoney(amount.toDouble(), recipient)
                        tts.value?.speak(
                            "Money sent successfully to $recipient",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "send_success"
                        )
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    errorMessage = errString.toString()
                    tts.value?.speak(
                        "Authentication failed: $errString",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "auth_error"
                    )
                }

                override fun onAuthenticationFailed() {
                    errorMessage = "Authentication failed"
                    tts.value?.speak(
                        "Authentication failed",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "auth_failed"
                    )
                }
            }
        )
    }

    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate to Send Money")
            .setSubtitle("Confirm your identity")
            .setNegativeButtonText("Cancel")
            .build()
    }

    var ttsInstance: TextToSpeech? = null
    DisposableEffect(lifecycleOwner) {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {

                tts.value = ttsInstance
                ttsInstance?.language = Locale.getDefault()
                ttsInstance?.setSpeechRate(1.0f)
                ttsInstance?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {}
                    override fun onError(p0: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        ttsError.value = "TTS error occurred (code: $errorCode)"
                    }
                })
                isTtsReady = true
                ttsInstance?.speak(
                    "Send money screen. Please enter amount and recipient number",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "screen_entry"
                )
            } else {
                ttsError.value = "TTS initialization failed"
            }
        }

        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
        }
    }

    // Check biometric availability
    LaunchedEffect(Unit) {
        val biometricManager = BiometricManager.from(context)
        biometricStatus.value = when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            else -> BiometricStatus.NOT_AVAILABLE
        }
    }

    // Main UI
    CommonScreenLayout(
        navController = navController,
        screenTitle = "Tuma Pesa"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .semantics { contentDescription = "Send money form" },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AccessibleCurrencyInput(
                value = amount,
                onValueChange = { newValue ->
                    amount = newValue
                    isAmountError = false
                    tts.value?.speak(
                        "Amount entered: $newValue",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "amount_update"
                    )
                },
                label = "Amount",
                isError = isAmountError,
                errorMessage = "Invalid amount",
                modifier = Modifier.fillMaxWidth()
            )

            AccessiblePhoneInput(
                value = recipient,
                onValueChange = { newValue ->
                    recipient = newValue
                    isRecipientError = false
                    tts.value?.speak(
                        "Recipient: ${newValue.chunked(1).joinToString(" ")}",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "recipient_update"
                    )
                },
                label = "Recipient Phone Number",
                isError = isRecipientError,
                errorMessage = "Invalid phone number",
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    when {
                        !validateForm(amount, recipient) -> {
                            tts.value?.speak(
                                "Form validation failed. Please check inputs",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "validation_error"
                            )
                        }
                        biometricStatus.value != BiometricStatus.AVAILABLE -> {
                            errorMessage = "Biometric authentication not available"
                            tts.value?.speak(
                                errorMessage!!,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "biometric_error"
                            )
                        }
                        else -> biometricPrompt.authenticate(promptInfo)
                    }
                },
                enabled = validateForm(amount, recipient),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Money")
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// Accessible Input Components
@Composable
private fun AccessibleCurrencyInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                    onValueChange(it)
                }
            },
            label = { Text(label) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "$label input field"
                },
            supportingText = { if (isError) Text(errorMessage) }
        )
    }
}

@Composable
private fun AccessiblePhoneInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= 12 && it.all { char -> char.isDigit() }) {
                onValueChange(it)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done
        ),
        isError = isError,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$label input field"
            },
        supportingText = { if (isError) Text(errorMessage) }
    )
}

// Helper Functions
private fun validateForm(amount: String, recipient: String): Boolean {
    val isAmountValid = amount.toDoubleOrNull()?.let { it > 0 } ?: false
    val isRecipientValid = recipient.length in 10..12 && recipient.all { it.isDigit() }
    return isAmountValid && isRecipientValid
}

// Enums
enum class BiometricStatus {
    NOT_CHECKED,
    AVAILABLE,
    NOT_AVAILABLE,
    NOT_ENROLLED
}