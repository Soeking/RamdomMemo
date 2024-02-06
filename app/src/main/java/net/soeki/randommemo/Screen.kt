@file:OptIn(ExperimentalMaterial3Api::class)

package net.soeki.randommemo

import android.annotation.SuppressLint
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import net.soeki.randommemo.db.NoteData
import net.soeki.randommemo.db.NoteOnList
import kotlin.random.Random

enum class ScreenURL(name: String) {
    Login("login-screen"),
    List("list-screen"),
    Edit("edit-screen/")
}

@Composable
fun LoinScreen(onAuthSuccess:()->Unit){
    val context = LocalContext.current
    val biometricManager = remember { BiometricManager.from(context) }
    val isBiometricAvailable = remember {
        biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
    }
    when (isBiometricAvailable) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            // Biometric features are available
        }

        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            // No biometric features available on this device
        }

        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            // Biometric features are currently unavailable.
        }

        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
            // Biometric features available but a security vulnerability has been discovered
        }

        BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
            // Biometric features are currently unavailable because the specified options are incompatible with the current Android version..
        }

        BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
            // Unable to determine whether the user can authenticate using biometrics
        }

        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            // The user can't authenticate because no biometric or device credential is enrolled.
        }
    }

    val executor = remember { ContextCompat.getMainExecutor(context) }
    val biometricPrompt = BiometricPrompt(
        context as FragmentActivity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // handle authentication error here
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // handle authentication failure here
            }
        }
    )

    val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric Authentication")
        .setSubtitle("Log in using your biometric credential")
        .setNegativeButtonText("Cancel")
        .build()

    Button(onClick = { biometricPrompt.authenticate(promptInfo) }) {
        Text("Authenticate with Biometrics")
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(notes: List<NoteOnList>, onListClick: (Long) -> Unit) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onListClick(0) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) {
        Column {
            LazyColumn {
                items(items = notes, key = { it.id }) { note ->
                    Text(
                        text = note.text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onListClick(note.id) }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditScreen(
    id: Long,
    getNote: (Long) -> NoteData,
    create: (NoteData) -> Unit,
    update: (NoteData) -> Unit,
    delete: (Long) -> Unit,
    backScreen: () -> Unit
) {
    val note =
        if (id == 0L)
            NoteData()
        else
            getNote(id)

    var includeOption by rememberSaveable { mutableStateOf(false) }
    var showAlert by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(note.text) }
    var description by rememberSaveable { mutableStateOf(note.description) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    note.text = text
                    note.description = description
                    if (id == 0L) {
                        create(note)
                        backScreen()
                    } else {
                        update(note)
                        backScreen()
                    }
                }
            ) {
                Icon(Icons.Default.Check, "update")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        Column {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            Spacer(modifier = Modifier.size(3.dp))

            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Switch(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    checked = includeOption,
                    onCheckedChange = { includeOption = !includeOption })
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                        .wrapContentWidth(Alignment.End),
                    onClick = { text = generatePass(includeOption) }
                ) {
                    Text(text = "Generate")
                }
            }
            Spacer(modifier = Modifier.size(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("description here") },
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )

            Text(text = note.updateDate, style = TextStyle(fontSize = 8.sp, color = Color.Gray))
            Spacer(modifier = Modifier.size(10.dp))

            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                if (id != 0L)
                    FloatingActionButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                            .wrapContentWidth(Alignment.End),
                        onClick = {
                            showAlert = true
                        }
                    ) {
                        Icon(Icons.Default.Delete, "delete")
                    }
            }

            if (showAlert)
                AlertDialog(onDismissRequest = { showAlert = false },
                    title = { Text(text = "delete?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                delete(id)
                                showAlert = false
                                backScreen()
                            }) {
                            Text(text = "✓")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showAlert = false }) {
                            Text(text = "×")
                        }
                    }
                )
        }
    }
}

private fun generatePass(include: Boolean): String {
    val chars =
        ('a'..'x').toList() + ('A'..'Z').toList() + ('0'..'9').toList() +
                if (include) listOf('!', '$', '@', '+', '#', '*') else emptyList()
    var pass = ""
    val random = Random(System.currentTimeMillis())
    repeat(10) { pass += chars.random(random) }
    return pass
}
