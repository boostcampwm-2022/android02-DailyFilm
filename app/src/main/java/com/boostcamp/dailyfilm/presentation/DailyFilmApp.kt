package com.boostcamp.dailyfilm.presentation

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun DailyFilmApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navActions = remember(navController) {
        DailyFilmNavigationActions(navController)
    }
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    DailyFilmNavGraph(
        navController = navController,
        navActions = navActions,
        onShowSnackBar = { message -> snackBarHostState.showSnackbar(message) },
        coroutineScope = coroutineScope
    )
}
