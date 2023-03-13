package net.soeki.randommemo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.soeki.randommemo.db.NoteData
import net.soeki.randommemo.db.NoteOnList
import kotlin.random.Random

enum class ScreenURL(name: String) {
    List("list-screen"),
    Edit("edit-screen/")
}

@Composable
fun ListScreen(notes: List<NoteOnList>, onListClick: (Long) -> Unit) {
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
        FloatingActionButton(
            onClick = { onListClick(0) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

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

    Column {
        OutlinedTextField(
            value = note.text,
            onValueChange = { note.text = it },
            label = { Text("") },
            modifier = Modifier.fillMaxWidth()
        )
        Row() {
            Switch(checked = includeOption, onCheckedChange = { includeOption = !includeOption })
            Button(
                onClick = { note.text = generatePass(includeOption) }
            ) {
                Text(text = "Generate")
            }
        }
        OutlinedTextField(
            value = note.description,
            onValueChange = { note.description = it },
            label = { Text("description here") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Row() {
            FloatingActionButton(
                onClick = {
                    delete(id)
                    backScreen()
                }
            ) {
                Icon(Icons.Default.Delete, "delete")
            }
            FloatingActionButton(
                onClick = {
                    if (id == 0L) {
                        create(note)
                        backScreen()
                    } else {
                        update(note)
                        backScreen()
                    }
                }
            ) {
                if (id == 0L)
                    Icon(Icons.Default.Create, "create")
                else
                    Icon(Icons.Default.Check, "update")
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
