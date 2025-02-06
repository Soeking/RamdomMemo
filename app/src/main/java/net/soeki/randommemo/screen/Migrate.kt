@file:OptIn(ExperimentalMaterial3Api::class)

package net.soeki.randommemo.screen

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.soeki.randommemo.db.NoteData
import java.io.File
import java.io.FileOutputStream

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Migrate(
    backScreen: () -> Unit,
    getAllDataFunction: () -> List<NoteData>,
    bulkInsertFunction: (List<BackUpData>) -> Boolean
) {
    var isSelectedFile by rememberSaveable { mutableStateOf(false) }
    var showFilePicker by rememberSaveable { mutableStateOf(false) }
    var filePath by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    var selectedFile: File? = null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                modifier = Modifier.drawBehind {
                    drawLine(
                        color = Color.White, start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.cornerPathEffect(4f)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { backScreen() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(top = it.calculateTopPadding())
        ) {
            Spacer(Modifier.size(10.dp))
            Row {
                Button(
                    onClick = {
                        saveBackUp(getAllDataFunction)
                        Toast.makeText(context, "saved", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("save backup")
                }
            }
            Spacer(Modifier.size(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { showFilePicker = true }) {
                    Text("choose a file")
                }
                Text("chosen: $filePath", modifier = Modifier.padding(start = 20.dp))
            }
            Row {
                FilePicker(
                    showFilePicker
                ) { file ->
                    if (file != null) {
                        isSelectedFile = true
                        filePath = file.second ?: ""
                        selectedFile = file.first
                    } else {
                        isSelectedFile = false
                        filePath = ""
                        selectedFile = null
                    }
                    showFilePicker = false
                }
                Button(
                    enabled = isSelectedFile,
                    onClick = {
                        insertBackUp(selectedFile!!, bulkInsertFunction)
                        Toast.makeText(context, "added successfully", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("add from backup")
                }
            }
        }
    }
}

@Composable
fun FilePicker(
    show: Boolean,
    onFileSelected: (Pair<File, String?>?) -> Unit
) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File.createTempFile("tmp", null)
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                onFileSelected(Pair(file, getFileNameFromUri(context, uri)))
            } else {
                onFileSelected(null)
            }
        }

    LaunchedEffect(show) {
        if (show) {
            launcher.launch(arrayOf("*/*"))
        }
    }
}

@Serializable
data class BackUpData(
    val text: String,
    val description: String,
    val updateDate: String
)

private fun saveBackUp(getAllDataFunction: () -> List<NoteData>) {
    val file = createFile()
    file.writeText(generateBackUpData(getAllDataFunction))
}

private fun createFile(): File {
    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadDir, "random_backup")

    return file
}

private fun generateBackUpData(getAllDataFunction: () -> List<NoteData>): String {
    val data = getAllDataFunction()
    return Json.encodeToString(data.map { it.toBackUpData() })
}

private fun NoteData.toBackUpData(): BackUpData = BackUpData(text, description, updateDate)

private fun insertBackUp(file: File, bulkInsertFunction: (List<BackUpData>) -> Boolean) {
    val jsonText = file.readText()
    val list = Json.decodeFromString<List<BackUpData>>(jsonText)
    bulkInsertFunction(list)
}

fun getFileNameFromUri(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            return displayName
        }
    }
    return null
}
