@file:OptIn(ExperimentalMaterial3Api::class)

package net.soeki.randommemo.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import net.soeki.randommemo.db.NoteData

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Migrate(
    backScreen: () -> Unit,
    getAllDataFunction: () -> List<NoteData>,
    bulkInsertFunction: (List<NoteData>) -> Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { backScreen() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "")
                    }
                }
            )
        }
    ) {

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