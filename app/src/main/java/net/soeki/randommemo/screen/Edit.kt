@file:OptIn(ExperimentalMaterial3Api::class)

package net.soeki.randommemo.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.soeki.randommemo.db.NoteData
import kotlin.random.Random

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