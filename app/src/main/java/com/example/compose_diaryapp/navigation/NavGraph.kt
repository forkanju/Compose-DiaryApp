package com.example.compose_diaryapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.compose_diaryapp.presentation.screens.auth.AuthenticationScreen
import com.example.compose_diaryapp.util.Constants.WRITE_SCREEN_ARGUMENT_KEY

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun SetupNavGraph(startDestination: String, navController: NavHostController) {

    NavHost(startDestination = startDestination, navController = navController) {
        authenticationRoute()
        homeRoute()
        writeRoute()
    }

}

@ExperimentalMaterial3Api
fun NavGraphBuilder.authenticationRoute() {
    composable(route = Screen.Authentication.route) {
        AuthenticationScreen(
            loadingState = false,
            onButtonClicked = {

            }
        )
    }
}

fun NavGraphBuilder.homeRoute() {
    composable(route = Screen.Home.route) {

    }
}

fun NavGraphBuilder.writeRoute() {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = WRITE_SCREEN_ARGUMENT_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {

    }
}