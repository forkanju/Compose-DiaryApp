package com.example.compose_diaryapp.presentation.screens.auth

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_diaryapp.util.Constants.APP_ID
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthenticationViewModel : ViewModel() {

    //this loadingState can be modified only inside this class as i set private setter
    // and can read from any other class as i set it's as public getter with public var keyword
    var loadingState = mutableStateOf(false)
        private set

    fun setLoading(loading: Boolean) {
        loadingState.value = loading
    }

    fun signInWithMongodbAtlas(
        tokenId: String,
        onSuccess: (Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                //this result will hold whether the user successfully loggedIn or not boolean value
                val result = withContext(Dispatchers.IO) {
                    App.create(APP_ID).login(
                        Credentials.jwt(tokenId)
//                        Credentials.google(tokenId, GoogleAuthType.ID_TOKEN)
                    ).loggedIn
                }
                //we do it here in Main thread because this onSuccess will triggered from our composable(screen) function
                withContext(Dispatchers.Main) {
                    Log.d("AuthenticationViewModel", "onSuccess: $result")
                    onSuccess(result)
                }
            } catch (error: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("AuthenticationViewModel", "onError: $error")
                    onError(error)
                }
            }
        }
    }
}