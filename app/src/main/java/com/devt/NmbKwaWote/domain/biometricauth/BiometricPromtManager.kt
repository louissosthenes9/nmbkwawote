package com.devt.NmbKwaWote.domain.biometricauth

import androidx.biometric.BiometricManager
import androidx.appcompat.app.AppCompatActivity

class BiometricPromtManager(
    private val activity: AppCompatActivity
) {
    fun showBiometricPromt(
        title: String,
        description: String
    ){
       val manager = BiometricManager.from(activity)

    }
}