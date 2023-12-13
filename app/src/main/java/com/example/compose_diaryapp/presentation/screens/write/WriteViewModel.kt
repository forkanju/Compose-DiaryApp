package com.example.compose_diaryapp.presentation.screens.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.compose_diaryapp.model.Mood
import com.example.compose_diaryapp.util.Constants.WRITE_SCREEN_ARGUMENT_KEY

class WriteViewModel(
    //it will allow us to access diary id that we are passing to our right screen.
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    //will hold ui state
    var uiState by mutableStateOf(UiState())
        private set

    init {
        getDiaryIdArgument()
    }

    private fun getDiaryIdArgument() {
        uiState = uiState.copy(
            selectedDiaryId = savedStateHandle.get<String>(
                key = WRITE_SCREEN_ARGUMENT_KEY
            )
        )
    }

}

//UI Properties for our WriteScreen
data class UiState(
    val selectedDiaryId: String? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral
)