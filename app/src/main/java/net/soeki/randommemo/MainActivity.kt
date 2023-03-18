package net.soeki.randommemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import net.soeki.randommemo.db.NoteData
import net.soeki.randommemo.ui.theme.RandomMemoTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: AccessDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AccessDatabase(applicationContext)
        setContent {
            RandomMemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenManage()
                }
            }
        }
    }

    @Composable
    fun ScreenManage() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = ScreenURL.List.name) {
            composable(ScreenURL.List.name) {
                ListScreen(
                    notes = database.getList(),
                    onListClick = { navController.navigate(ScreenURL.Edit.name + it) }
                )
            }
            composable(
                ScreenURL.Edit.name + "{Id}",
                arguments = listOf(navArgument("Id") { type = NavType.LongType })
            ) {
                EditScreen(
                    it.arguments?.getLong("Id") ?: 0L,
                    database::getNote,
                    database::insertNote,
                    database::updateNote,
                    database::deleteNote,
                    navController::navigateUp
                )
            }
        }
    }
}

