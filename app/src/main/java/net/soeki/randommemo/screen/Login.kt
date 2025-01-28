@file:OptIn(ExperimentalMaterial3Api::class)

package net.soeki.randommemo.screen

import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrhwsn.composelock.ComposeLock
import net.soeki.randommemo.auth.getBiometricPrompt
import net.soeki.randommemo.auth.getIsEnableBio
import net.soeki.randommemo.auth.getPatternLockCallback

@Composable
fun LoginScreen(onAuthSuccess: () -> Unit) {
    val context = LocalContext.current
    val (isEnableBio, isEnablePIN) = getIsEnableBio(context = context)
    val biometricPrompt = getBiometricPrompt(context = context, onAuthSuccess)

    var isPatternMode by rememberSaveable { mutableStateOf(false) }
    var showErrorMessage by rememberSaveable { mutableStateOf(false) }
    var showFirstMessage by rememberSaveable { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val size = minOf(screenHeight, screenWidth)

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("ログイン")
        .setSubtitle("生体認証を使用します")
        .setNegativeButtonText("キャンセル")
        .build()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isPatternMode) {
            if (isEnableBio) // 生体認証が有効のときだけ表示
                Button(onClick = { biometricPrompt.authenticate(promptInfo) }) {
                    Text("Biometrics")
                }
            Button(onClick = { isPatternMode = true }) {
                Text("Pattern")
            }
        } else {
            Scaffold(
                // 左上に戻るボタンを表示
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = {
                                isPatternMode = false
                                showErrorMessage = false
                            }) {
                                Icon(Icons.Default.ArrowBack, "back")
                            }
                        })
                }
            ) {
                Column(
                    modifier = Modifier.padding(it),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (showErrorMessage)
                        Text(text = "try again", color = Color.Red, textAlign = TextAlign.Center)
                    if (showFirstMessage)
                        Text(text = "set the pattern", textAlign = TextAlign.Center)
                    ComposeLock(
                        modifier = Modifier
                            .offset(0.dp, 100.dp)
                            .fillMaxWidth()
                            .size(size.dp),
                        dimension = 3,
                        sensitivity = 100f,
                        dotsColor = MaterialTheme.colorScheme.inversePrimary,
                        dotsSize = 15f,
                        linesColor = MaterialTheme.colorScheme.inversePrimary,
                        linesStroke = 10f,
                        callback = getPatternLockCallback(
                            context = context,
                            onAuthSuccess = onAuthSuccess,
                            onAuthFail = { showErrorMessage = true },
                            onFirstTime = { showFirstMessage = true },
                            onSecondTime = { showFirstMessage = false })
                    )
                }
            }
        }
    }
}