@file:OptIn(ExperimentalMaterial3Api::class)

package net.soeki.randommemo

import android.annotation.SuppressLint
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.soeki.randommemo.db.NoteData
import net.soeki.randommemo.db.NoteOnList
import kotlin.random.Random


enum class ScreenURL(name: String) {
    List("list-screen"),
    Edit("edit-screen/")
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

            Row(Modifier.padding(1.dp, 0.dp, 1.dp, 8.dp)) {
                Switch(
                    checked = includeOption,
                    onCheckedChange = { includeOption = !includeOption })
                Button(
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
                            delete(id)
                            backScreen()
                        }
                    ) {
                        Icon(Icons.Default.Delete, "delete")
                    }
            }
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
