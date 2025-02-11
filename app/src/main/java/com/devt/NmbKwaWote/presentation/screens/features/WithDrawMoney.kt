@file:Suppress("DEPRECATION")

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
class WithdrawMoneyViewModel : ViewModel() {
    private val _biometricResult = MutableStateFlow<BiometricResult>(BiometricResult.None)
    val biometricResult: StateFlow<BiometricResult> = _biometricResult.asStateFlow()

    fun withdrawMoney(amount: Double, accountNumber: String) {
        viewModelScope.launch {

        }
    }

    fun resetBiometricResult() {
        _biometricResult.value = BiometricResult.None
    }
}

@Composable
fun WithdrawMoneyScreen(
    navController: NavHostController,
    viewModel: WithdrawMoneyViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // State management
    var amount by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var selectedDenomination by remember { mutableStateOf<Int?>(null) }
    var isAmountError by remember { mutableStateOf(false) }
    var isAccountError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // TTS management
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    val ttsError = remember { mutableStateOf<String?>(null) }

    // Available denominations
    val denominations = listOf(1000, 2000, 5000, 10000)

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
                        viewModel.withdrawMoney(amount.toDouble(), accountNumber)
                        tts.value?.speak(
                            "Withdrawal of $amount shillings initiated from account $accountNumber",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "withdraw_success"
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
            .setTitle("Authenticate to Withdraw Money")
            .setSubtitle("Confirm your identity")
            .setNegativeButtonText("Cancel")
            .build()
    }
    var ttsInstance: TextToSpeech? = null
    // Initialize TTS with lifecycle management
    DisposableEffect(lifecycleOwner) {
        val ttsInstance = TextToSpeech(context) { status ->
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
                    "Withdraw money screen. Please enter withdrawal amount and account number",
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

    CommonScreenLayout(
        navController = navController,
        screenTitle = "Toa Pesa"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .semantics { contentDescription = "Withdraw money form" },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account number input
            AccessibleAccountInput(
                value = accountNumber,
                onValueChange = { newValue ->
                    accountNumber = newValue
                    isAccountError = false
                    tts.value?.speak(
                        "Account number entered: ${newValue.chunked(4).joinToString(" ")}",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "account_update"
                    )
                },
                label = "Account Number",
                isError = isAccountError,
                errorMessage = "Invalid account number",
                modifier = Modifier.fillMaxWidth()
            )

            // Amount input
            AccessibleCurrencyInput(
                value = amount,
                onValueChange = { newValue ->
                    amount = newValue
                    isAmountError = false
                    tts.value?.speak(
                        "Amount entered: $newValue shillings",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "amount_update"
                    )
                },
                label = "Withdrawal Amount",
                isError = isAmountError,
                errorMessage = "Invalid amount",
                modifier = Modifier.fillMaxWidth()
            )

            // Denominations section
            Text(
                "Select Denomination",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                denominations.forEach { denomination ->
                    Button(
                        onClick = {
                            selectedDenomination = denomination
                            amount = denomination.toString()
                            tts.value?.speak(
                                "Selected denomination: $denomination shillings",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "denomination_selected"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedDenomination == denomination) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.secondary
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text("${denomination/1000}K")
                    }
                }
            }

            // Withdraw button
            Button(
                onClick = {
                    when {
                        !validateWithdrawForm(amount, accountNumber) -> {
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
                enabled = validateWithdrawForm(amount, accountNumber),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Withdraw Money")
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

@Composable
private fun AccessibleAccountInput(
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
            if (it.length <= 16 && it.all { char -> char.isDigit() }) {
                onValueChange(it)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
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

@Composable
private fun AccessibleCurrencyInput(
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
            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                onValueChange(it)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
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

private fun validateWithdrawForm(amount: String, accountNumber: String): Boolean {
    val isAmountValid = amount.toDoubleOrNull()?.let { it > 0 && it % 1000 == 0.0 } ?: false
    val isAccountValid = accountNumber.length == 16 && accountNumber.all { it.isDigit() }
    return isAmountValid && isAccountValid
}
