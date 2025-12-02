@file:OptIn(ExperimentalMaterial3Api::class, FlowPreview::class, ExperimentalCoroutinesApi::class)

package net.soeki.randommemo.screen

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import net.soeki.randommemo.db.NoteOnList

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    notesGetter: (String) -> List<NoteOnList>,
    onListClick: (Long) -> Unit,
    transitionToMigration: () -> Unit,
    transitionToReset: () -> Unit
) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(notesGetter("")) }

    LaunchedEffect(searchText) {
        snapshotFlow { searchText }
            .debounce { 300L }
            .flatMapLatest {
                flowOf(notesGetter(it))
            }
            .collect { notes = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.drawBehind {
                    drawLine(
                        color = Color.White, start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.cornerPathEffect(4f)
                    )
                },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                title = {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("search") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                navigationIcon = {
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
                                onClick = { transitionToMigration() }
                            )
                            DropdownMenuItem(
                                text = { Text("Setting Pattern") },
                                onClick = { transitionToReset() }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onListClick(0) }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        },
        floatingActionButton = {}
    ) {
        Column(modifier = Modifier.padding(top = it.calculateTopPadding())) {
            LazyColumn {
                items(items = notes, key = { note -> note.id }) { note ->
                    listRow(note, context, onListClick)
                    Divider(thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun listRow(note: NoteOnList, context: Context, onListClick: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 4.dp)
            .clickable { onListClick(note.id) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
            Text(
                text = note.text
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = note.description,
                fontSize = 12.sp,
                lineHeight = 1.em
            )
        }
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