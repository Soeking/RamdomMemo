package net.soeki.randommemo.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.mrhwsn.composelock.ComposeLockCallback
import com.mrhwsn.composelock.Dot

class Auth(val context: Context) {
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

    fun updateCode(code: String) {
        context.deleteSharedPreferences(userPrefName)
        setPatternCode(code)
    }
}

fun getPatternLockLoginCallback(
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

fun getPatternLockResetCallback(
    context: Context,
    firstInput: () -> Unit,
    secondInput: () -> Unit,
    onUpdated: () -> Unit
): ComposeLockCallback {
    val auth = Auth(context)

    return object : ComposeLockCallback {
        var temporary = ""

        override fun onDotConnected(dot: Dot) {
        }

        override fun onResult(result: List<Dot>) {
            val resultCode = result.map { it.id }.joinToString("_")

            if (temporary == "") {
                setTmp(resultCode)
                firstInput()
            } else {
                if (temporary == resultCode) {
                    onUpdated()
                    auth.updateCode(resultCode)
                }
                setTmp("")
                secondInput()
            }
        }

        override fun onStart(dot: Dot) {
        }

        private fun setTmp(code: String) {
            temporary = code
        }
    }
}
