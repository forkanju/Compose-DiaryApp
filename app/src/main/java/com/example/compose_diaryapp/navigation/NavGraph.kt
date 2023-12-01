package com.example.compose_diaryapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.compose_diaryapp.presentation.screens.auth.AuthenticationScreen
import com.example.compose_diaryapp.presentation.screens.auth.AuthenticationViewModel
import com.example.compose_diaryapp.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController
) {

    NavHost(startDestination = startDestination, navController = navController) {
        authenticationRoute()
        homeRoute()
        writeRoute()
    }

}

@ExperimentalMaterial3Api
fun NavGraphBuilder.authenticationRoute() {
    composable(route = Screen.Authentication.route) {

        val viewModel: AuthenticationViewModel = viewModel()
        //here using by delegated keyword its changing loadingState and viewModel.loadingState vice-versa
        val loadingState by viewModel.loadingState
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        AuthenticationScreen(
            loadingState = loadingState,
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onTokenIdReceived = { tokenId ->
                viewModel.signInWithMongodbAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        if (it){
                            messageBarState.addSuccess("Successfully Authenticated!")
                            viewModel.setLoading(false)
                        }

                    },
                    onError = { error ->
                        messageBarState.addError(error)
                        viewModel.setLoading(false)
                    }
                )

            },
            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
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