package net.soeki.randommemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import net.soeki.randommemo.db.*
import net.soeki.randommemo.ui.theme.RandomMemoTheme

class MainActivity : ComponentActivity() {
    private lateinit var database:AccessDatabase

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
                    val list = (1..100).map { "list $it" }
                    ListScreen(list)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RandomMemoTheme {
        Greeting("Android")
    }
}

@Composable
fun ListScreen(recordList: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(recordList) { todo ->
            Text(todo)
        }
    }
}