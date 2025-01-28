package net.soeki.randommemo.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

// 生体認証、画面ロック認証が有効か
@Composable
fun getIsEnableBio(context: Context): Pair<Boolean, Boolean> {
    val biometricManager = remember { BiometricManager.from(context) }
    // 生体認証
    val isFingerprintAvailable = remember {
        biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
    }
    // 画面ロック認証
    val isPINAvailable = remember {
        biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
    }

    return Pair(
        isFingerprintAvailable == BiometricManager.BIOMETRIC_SUCCESS,
        isPINAvailable == BiometricManager.BIOMETRIC_SUCCESS
    )
}

// 生体認証の認証時の設定
@Composable
fun getBiometricPrompt(context: Context, onAuthSuccess: () -> Unit): BiometricPrompt {
    val executor = remember { ContextCompat.getMainExecutor(context) }

    return BiometricPrompt(
        context as FragmentActivity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }
        }
    )
}