@file:OptIn(ExperimentalMaterial3Api::class)

package net.soeki.randommemo.screen

import android.annotation.SuppressLint
import android.os.Environment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.soeki.randommemo.db.NoteData
import java.io.File

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Migrate(
    backScreen: () -> Unit,
    getAllDataFunction: () -> List<NoteData>,
    bulkInsertFunction: (List<BackUpData>) -> Boolean
) {
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
            Row {
                Button(onClick = { saveBackUp(getAllDataFunction) }) { Text("save backup") }
            }
            Row {
                Button(onClick = { insertBackUp(bulkInsertFunction) }) { Text("add from backup") }
            }
        }
    }
}

@Preview
@Composable
fun CheckDesign() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .drawBehind {
                    drawLine(
                        color = Color.White, start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.cornerPathEffect(4f)
                    )
                }
                .padding(10.dp)
        ) {
            Text("test", color = Color.Blue)
        }
        Row { Icon(Icons.Default.CheckCircle, contentDescription = "") }
    }
}

//@Suppress("PLUGIN_IS_NOT_ENABLED")
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

private fun insertBackUp(bulkInsertFunction: (List<BackUpData>) -> Boolean) {

}
