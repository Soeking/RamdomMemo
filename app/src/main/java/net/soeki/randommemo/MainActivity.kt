package net.soeki.randommemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.soeki.randommemo.db.*
import net.soeki.randommemo.ui.theme.RandomMemoTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: AccessDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AccessDatabase(applicationContext)
        setContent {
            RandomMemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListScreen(notes = database.getList(), onItemClick = {})
                }
            }
        }
    }
}

@Composable
fun ListScreen(notes: List<NoteOnList>, onItemClick: (String) -> Unit) {
    Column {
        LazyColumn {
            items(items = notes, key = { it.id }) { note ->
                Text(
                    text = note.text,
                    modifier = Modifier
                        .clickable { onItemClick(note.text) }
                        .padding(16.dp)
                )
            }
        }
        FloatingActionButton(
            onClick = { /* TODO */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Preview
@Composable
fun EditScreen() {
    Column {
        OutlinedTextField(
            value = "",
            onValueChange = { /* TODO */ },
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
