package net.soeki.randommemo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.soeki.randommemo.db.NoteData
import net.soeki.randommemo.db.NoteOnList

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
    delete: (Long) -> Unit
) {

    val note = if (id == 0L) {
        NoteData()
    } else {
        getNote(id)
    }

    Column {
        OutlinedTextField(
            value = "",
            onValueChange = { note.text = it },
            label = { Text("One line text input") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = "",
            onValueChange = { /* TODO */ },
            label = { Text("Three line text input") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Switch(checked = false, onCheckedChange = {})
    }
}
