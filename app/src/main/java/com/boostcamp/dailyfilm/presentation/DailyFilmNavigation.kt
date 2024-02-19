package com.boostcamp.dailyfilm.presentation

import androidx.navigation.NavHostController

object DailyFilmDestinations {
    const val LOGIN_ROUTE = "login"
    const val CALENDAR_ROUTE = "calendar"
}

class DailyFilmNavigationActions(navController: NavHostController) {
    val navigateToLogin: () -> Unit = {
        navController.navigate(DailyFilmDestinations.LOGIN_ROUTE) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    val navigateToCalendar: () -> Unit = {
        navController.navigate(DailyFilmDestinations.CALENDAR_ROUTE) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }
}
