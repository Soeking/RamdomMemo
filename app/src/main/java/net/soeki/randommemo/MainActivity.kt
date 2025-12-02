@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class,
    ExperimentalCoroutinesApi::class, FlowPreview::class
)

package net.soeki.randommemo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import net.soeki.randommemo.db.AccessDatabase
import net.soeki.randommemo.screen.MigrateScreen
import net.soeki.randommemo.screen.EditScreen
import net.soeki.randommemo.screen.ListScreen
import net.soeki.randommemo.screen.LoginScreen
import net.soeki.randommemo.screen.ResetScreen
import net.soeki.randommemo.screen.ScreenURL
import net.soeki.randommemo.ui.theme.RandomMemoTheme

class MainActivity : FragmentActivity() {
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

        NavHost(navController = navController, startDestination = ScreenURL.Login.name) {
            composable(ScreenURL.Login.name) {
                LoginScreen {
                    navController.navigate(ScreenURL.List.name) {
                        // ログイン画面に戻らないように
                        popUpTo(ScreenURL.Login.name) { inclusive = true }
                    }
                }
            }
            composable(ScreenURL.List.name) {
                ListScreen(
                    notesGetter = database::getList,
                    onListClick = { navController.navigate(ScreenURL.Edit.name + it) },
                    transitionToMigration = { navController.navigate(ScreenURL.Migrate.name) },
                    transitionToReset = { navController.navigate(ScreenURL.Reset.name) }
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
            composable(ScreenURL.Migrate.name) {
                MigrateScreen(
                    navController::navigateUp,
                    database::getAllNote,
                    database::bulkInsertNote
                )
            }
            composable(ScreenURL.Reset.name) {
                ResetScreen(navController::navigateUp)
            }
        }
    }
}

