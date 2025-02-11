@file:Suppress("DEPRECATION")

package com.devt.NmbKwaWote.presentation.screens.features

import CommonScreenLayout
import android.speech.tts.TextToSpeech
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

class PayBillViewModel : ViewModel() {
    private val _biometricResult = MutableStateFlow<BiometricResult>(BiometricResult.None)
    val biometricResult: StateFlow<BiometricResult> = _biometricResult.asStateFlow()

    fun payBill(amount: Double, billNumber: String, billType: String) {
        viewModelScope.launch {

        }
    }

    fun resetBiometricResult() {
        _biometricResult.value = BiometricResult.None
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayBillScreen(
    navController: NavHostController,
    viewModel: PayBillViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // State management
    var amount by remember { mutableStateOf("") }
    var billNumber by remember { mutableStateOf("") }
    var selectedBillType by remember { mutableStateOf("") }
    var isAmountError by remember { mutableStateOf(false) }
    var isBillNumberError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // TTS management
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    val ttsError = remember { mutableStateOf<String?>(null) }

    // Biometric state
    val biometricStatus = remember { mutableStateOf(BiometricStatus.NOT_CHECKED) }

    // Bill types
    val billTypes = listOf("Electricity", "Water", "Internet", "TV Subscription")

    var ttsInstance: TextToSpeech? = null
    DisposableEffect(lifecycleOwner) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value = ttsInstance
                ttsInstance?.language = Locale.getDefault()
                ttsInstance?.setSpeechRate(1.0f)
                isTtsReady = true
                ttsInstance?.speak(
                    "Pay Bill screen. Please select bill type, enter bill number and amount",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "screen_entry"
                )
            } else {
                ttsError.value = "TTS initialization failed"
            }
        }

        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
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
                        viewModel.payBill(amount.toDouble(), billNumber, selectedBillType)
                        tts.value?.speak(
                            "Bill payment successful for $selectedBillType",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "payment_success"
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
            }
        )
    }

    // Main UI
    CommonScreenLayout(
        navController = navController,
        screenTitle = "Lipa Bill"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .semantics { contentDescription = "Pay bill form" },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Bill Type Dropdown
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Bill type selector" }
            ) {
                OutlinedTextField(
                    value = selectedBillType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Bill Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = false,
                    onDismissRequest = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    billTypes.forEach { billType ->
                        DropdownMenuItem(
                            text = { Text(billType) },
                            onClick = {
                                selectedBillType = billType
                                tts.value?.speak(
                                    "Selected bill type: $billType",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "bill_type_selected"
                                )
                            }
                        )
                    }
                }
            }

            // Bill Number Input
            OutlinedTextField(
                value = billNumber,
                onValueChange = { newValue ->
                    if (newValue.length <= 20 && newValue.all { it.isDigit() }) {
                        billNumber = newValue
                        isBillNumberError = false
                        tts.value?.speak(
                            "Bill number: ${newValue.chunked(1).joinToString(" ")}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "bill_number_update"
                        )
                    }
                },
                label = { Text("Bill Number") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                isError = isBillNumberError,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Bill number input field" }
            )


            AccessibleInput(
                value = amount,
                onValueChange = { newValue: String->
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

            // Pay Button
            Button(
                onClick = {
                    when {
                        !validatePayBillForm(amount, billNumber, selectedBillType) -> {
                            tts.value?.speak(
                                "Please fill all fields correctly",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "validation_error"
                            )
                        }
                        else -> biometricPrompt.authenticate(
                            BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Authenticate to Pay Bill")
                                .setSubtitle("Confirm your identity")
                                .setNegativeButtonText("Cancel")
                                .build()
                        )
                    }
                },
                enabled = validatePayBillForm(amount, billNumber, selectedBillType),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pay Bill")
            }

            // Error Message
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

private fun validatePayBillForm(amount: String, billNumber: String, billType: String): Boolean {
    val isAmountValid = amount.toDoubleOrNull()?.let { it > 0 } ?: false
    val isBillNumberValid = billNumber.isNotEmpty() && billNumber.all { it.isDigit() }
    val isBillTypeValid = billType.isNotEmpty()
    return isAmountValid && isBillNumberValid && isBillTypeValid
}

@Composable
fun AccessibleInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    // Wrap the input and the error text in a Column.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                // Provide a content description that informs about the input field and its error state.
                contentDescription = "$label input field" + if (isError) " with error: $errorMessage" else ""
            }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
