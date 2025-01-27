package net.soeki.randommemo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.mrhwsn.composelock.ComposeLockCallback
import com.mrhwsn.composelock.Dot

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

fun getPatternLockCallback(
    context: Context,
    onAuthSuccess: () -> Unit,
    onAuthFail: () -> Unit,
    onFirstTime: () -> Unit,
    onSecondTime: () -> Unit
): ComposeLockCallback {
    val auth = Auth(context)
    if (auth.isSetPattern())
        onSecondTime()
    else
        onFirstTime()

    return object : ComposeLockCallback {
        override fun onStart(dot: Dot) {
            Log.d("pattern", "start on dot with id : ${dot.id}")
        }

        override fun onDotConnected(dot: Dot) {
            Log.d("pattern", "dot connected with id : ${dot.id}")
        }

        override fun onResult(result: List<Dot>) {
            val resultCode = result.map { it.id }.joinToString("_")

            if (auth.isSetPattern()) {
                if (auth.isCorrectPatternCode(resultCode)) onAuthSuccess()
                else onAuthFail()
            } else {
                auth.setPatternCode(resultCode)
                onAuthSuccess()
            }
        }
    }
}

class Auth(context: Context) {

    private var masterKey: MasterKey
    private var userPref: SharedPreferences
    private val userPrefName = "user_pref"
    private val pattern = "pattern_code"

    init {
        try {
            masterKey =
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
            userPref = EncryptedSharedPreferences.create(
                context,
                userPrefName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            e.printStackTrace()
            context.deleteSharedPreferences(userPrefName)

            masterKey =
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
            userPref = EncryptedSharedPreferences.create(
                context,
                userPrefName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    // first time -> false, after second -> true
    fun isSetPattern(): Boolean = userPref.getString(pattern, null) != null

    fun setPatternCode(code: String) {
        with(userPref.edit()) {
            putString(pattern, code)
            apply()
        }
    }

    // true -> auth success
    fun isCorrectPatternCode(code: String): Boolean = userPref.getString(pattern, "") == code
}