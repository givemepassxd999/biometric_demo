package com.example.biometricdemo

import android.hardware.biometrics.BiometricManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    private val availableCodes = listOf(
        BiometricManager.BIOMETRIC_SUCCESS,
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        biometric.setOnClickListener {
            checkBiometric()
        }
    }

    private fun checkBiometric() {
        canAuthenticateWithBiometrics()?.let {
            if (it) {
                showBiometricPrompt()
            } else {
                Toast.makeText(this, getString(R.string.cannot_use_biometric), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun canAuthenticateWithBiometrics(): Boolean? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val fingerprintManagerCompat = FingerprintManagerCompat.from(this)
            fingerprintManagerCompat.hasEnrolledFingerprints() && fingerprintManagerCompat.isHardwareDetected
        } else {
            val biometricManager = this.getSystemService(BiometricManager::class.java)
            biometricManager?.let {
                return availableCodes.contains(biometricManager.canAuthenticate())
            } ?: false
        }
    }

    private fun showBiometricPrompt() {
        val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(
                    applicationContext,
                    getString(R.string.fingerprint_success),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(
                    applicationContext,
                    errString.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.fingerprint_fail),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val mBiometricPrompt = BiometricPrompt(this, mainExecutor, authenticationCallback)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setDescription(getString(R.string.plz_input_ur_fingerprint))
            .setTitle(getString(R.string.fingerprint))
            .setNegativeButtonText(getString(R.string.cancel))
            .build()
        mBiometricPrompt.authenticate(promptInfo)
    }
}
