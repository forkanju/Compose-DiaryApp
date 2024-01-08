package com.example.compose_diaryapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.compose_diaryapp.navigation.Screen
import com.example.compose_diaryapp.navigation.SetupNavGraph
import com.example.compose_diaryapp.ui.theme.ComposeDiaryAppTheme
import com.example.compose_diaryapp.util.Constants.APP_ID
import com.google.firebase.FirebaseApp
import io.realm.kotlin.mongodb.App

class MainActivity : ComponentActivity() {

    var keepSplashOpened = true

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        FirebaseApp.initializeApp(this)
        setContent {
            ComposeDiaryAppTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navController = navController,
                    onDataLoaded = {
                        keepSplashOpened = false
                    }
                )
            }
        }
    }
}

private fun getStartDestination(): String {
    val user = App.create(APP_ID).currentUser
    return if (user != null && user.loggedIn) Screen.Home.route
    else Screen.Authentication.route
}


