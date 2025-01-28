@file:OptIn(ExperimentalMaterial3Api::class)

package net.soeki.randommemo

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrhwsn.composelock.ComposeLock
import net.soeki.randommemo.db.NoteData
import net.soeki.randommemo.db.NoteOnList
import kotlin.random.Random

enum class ScreenURL(name: String) {
    Login("login-screen"),
    List("list-screen"),
    Edit("edit-screen/"),
    DataMigrate("data-migrate")
}

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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    notes: List<NoteOnList>,
    onListClick: (Long) -> Unit,
    onTransitionMigration: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF19181A)),
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                title = {},
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Data Migration") },
                                onClick = { onTransitionMigration() }
                            )
                            DropdownMenuItem(
                                text = { Text("Setting Pattern") },
                                onClick = { }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onListClick(0) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) {
        Column(modifier = Modifier.padding(top = it.calculateTopPadding())) {
            LazyColumn {
                items(items = notes, key = { it.id }) { note ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp, 0.dp)
                            .clickable { onListClick(note.id) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = note.text
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                val clipManager =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clipData = ClipData.newPlainText("random text", note.text)
                                clipManager.setPrimaryClip(clipData)
                                Toast.makeText(context, "copied!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.Share, "copy")
                        }
                    }
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
            // 登録更新ボタン
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
                //削除ボタン
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
                        // 続行
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
                        // キャンセル
                        Button(
                            onClick = { showAlert = false }) {
                            Text(text = "×")
                        }
                    }
                )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DataMigrate() {
    Column { }
}

private fun generatePass(isInclude: Boolean): String {
    val chars =
        ('a'..'x').toList() + ('A'..'Z').toList() + ('0'..'9').toList() +
                if (isInclude) listOf('!', '$', '@', '+', '#', '*') else emptyList()
    var pass = ""
    val random = Random(System.currentTimeMillis())

    // 10文字固定
    repeat(10) { pass += chars.random(random) }
    return pass
}
