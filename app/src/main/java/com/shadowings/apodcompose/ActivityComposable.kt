package com.shadowings.apodcompose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shadowings.apodcompose.detail.DetailComposable
import com.shadowings.apodcompose.home.HomeComposable
import com.shadowings.apodcompose.redux.AppState
import com.shadowings.apodcompose.redux.store

@Composable
fun ActivityComposable(
    appState: AppState,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        NavHost(navController = navController, startDestination = "home") {
            composable(route = "home") {
                HomeComposable(
                    navController = navController,
                    appState = appState,
                    lifecycleOwner = lifecycleOwner
                )
            }
            composable(
                route = "detail/{date}",
                arguments = listOf(navArgument("date") { type = NavType.StringType })
            ) {
                DetailComposable(
                    date = it.arguments?.getString("date") ?: store.state.deps.formattedToday(),
                    appState = appState,
                    lifecycleOwner = lifecycleOwner
                )
            }
        }
    }
}