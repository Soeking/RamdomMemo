@file:OptIn(ExperimentalMaterial3Api::class)

package net.soeki.randommemo.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mrhwsn.composelock.ComposeLock
import net.soeki.randommemo.auth.getPatternLockResetCallback

@Composable
fun ResetScreen(backScreen: () -> Unit) {
    val context = LocalContext.current
    val screenSize =
        minOf(LocalConfiguration.current.screenHeightDp, LocalConfiguration.current.screenWidthDp)
    var isNotSet by rememberSaveable { mutableStateOf(false) }

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
            if (isNotSet) {
                Text("set pattern")
            } else {
                Text("set again")
            }
            ComposeLock(
                modifier = Modifier
                    .offset(0.dp, 100.dp)
                    .fillMaxWidth()
                    .size(screenSize.dp),
                dimension = 3,
                sensitivity = 100f,
                dotsColor = MaterialTheme.colorScheme.inversePrimary,
                dotsSize = 15f,
                linesColor = MaterialTheme.colorScheme.inversePrimary,
                linesStroke = 10f,
                callback = getPatternLockResetCallback(
                    context = context,
                    firstInput = { isNotSet = true },
                    onUpdated = {
                        isNotSet = false
                        Toast.makeText(context, "reset pattern", Toast.LENGTH_SHORT).show()
                    }
                )
            )
        }
    }
}