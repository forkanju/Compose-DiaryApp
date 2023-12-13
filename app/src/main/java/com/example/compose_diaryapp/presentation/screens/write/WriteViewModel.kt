package com.example.compose_diaryapp.presentation.screens.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_diaryapp.data.repository.MongoDB
import com.example.compose_diaryapp.model.Diary
import com.example.compose_diaryapp.model.Mood
import com.example.compose_diaryapp.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.example.compose_diaryapp.util.RequestState
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WriteViewModel(
    //it will allow us to access diary id that we are passing to our right screen.
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    //will hold ui state
    var uiState by mutableStateOf(UiState())
        private set

    init {
        getDiaryIdArgument()
        fetchSelectedDiary()
    }

    private fun getDiaryIdArgument() {
        uiState = uiState.copy(
            selectedDiaryId = savedStateHandle.get<String>(
                key = WRITE_SCREEN_ARGUMENT_KEY
            )
        )
    }

    private fun fetchSelectedDiary() {
        if (uiState.selectedDiaryId != null) {
            viewModelScope.launch(Dispatchers.Main) {
                val diary = MongoDB.getSelectedDiary(
                    diaryId = ObjectId.from(uiState.selectedDiaryId!!)
                )
                if (diary is RequestState.Success) {
                    setSelectedDiary(diary = diary.data)
                    setTitle(diary.data.title)
                    setDescription(diary.data.description)
                    setMood(Mood.valueOf(diary.data.mood))
                }
            }
        }
    }

    fun setSelectedDiary(diary: Diary){
        uiState = uiState.copy(selectedDiary = diary)
    }
    fun setTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    fun setMood(mood: Mood) {
        uiState = uiState.copy(mood = mood)
    }


}

//UI Properties for our WriteScreen
data class UiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral
)