package com.example.compose_diaryapp.presentation.screens.write

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_diaryapp.data.repository.MongoDB
import com.example.compose_diaryapp.model.Diary
import com.example.compose_diaryapp.model.GalleryImage
import com.example.compose_diaryapp.model.GalleryState
import com.example.compose_diaryapp.model.Mood
import com.example.compose_diaryapp.model.RequestState
import com.example.compose_diaryapp.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.example.compose_diaryapp.util.fetchImagesFromFirebase
import com.example.compose_diaryapp.util.toRealmInstant
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

class WriteViewModel(
    //it will allow us to access diary id that we are passing to our right screen.
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val galleryState = GalleryState()

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
                MongoDB.getSelectedDiary(
                    diaryId = ObjectId.from(uiState.selectedDiaryId!!)
                )
                    .catch {
                        emit(RequestState.Error(Exception("Diary is already deleted.")))
                    }
                    .collect { diary ->
                        if (diary is RequestState.Success) {
                            setSelectedDiary(diary = diary.data)
                            setTitle(diary.data.title)
                            setDescription(diary.data.description)
                            setMood(Mood.valueOf(diary.data.mood))

                            fetchImagesFromFirebase(
                                remoteImagePaths = diary.data.images,
                                onImageDownload = { downloadedImage ->
                                    galleryState.addImage(
                                        GalleryImage(
                                            image = downloadedImage,
                                            remoteImagePath = extractImagePath(
                                                fullImageUrl = downloadedImage.toString()
                                            ),
                                        )
                                    )
                                }
                            )
                        }
                    }
            }
        }
    }

    private fun setSelectedDiary(diary: Diary) {
        uiState = uiState.copy(selectedDiary = diary)
    }

    fun setTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    private fun setMood(mood: Mood) {
        uiState = uiState.copy(mood = mood)
    }

    fun updateDateTime(zonedDateTime: ZonedDateTime) {
        uiState = uiState.copy(updatedDateTime = zonedDateTime.toInstant().toRealmInstant())
    }


    /**upsert -> short form of Update or Insert*/
    fun upsertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedDiaryId != null) {
                updateDiary(
                    diary = diary,
                    onSuccess = onSuccess,
                    onError = onError
                )
            } else {
                insertDiary(
                    diary = diary,
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
        }
    }

    private suspend fun insertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.insertDiary(diary = diary.apply {
            if (uiState.updatedDateTime != null) {
                date = uiState.updatedDateTime!!
            }
        })
        if (result is RequestState.Success) {
            uploadImageToFirebase()
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.message.toString())
            }
        }
    }


    private suspend fun updateDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.updateDiary(diary = diary.apply {
            _id = ObjectId.from(uiState.selectedDiaryId!!)
            date = if (uiState.updatedDateTime != null) {
                uiState.updatedDateTime!!
            } else {
                uiState.selectedDiary!!.date
            }
        })
        if (result is RequestState.Success) {
            uploadImageToFirebase()
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.message.toString())
            }
        }
    }


    fun deleteDiary(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedDiaryId != null) {
                val result = MongoDB.deleteDiary(id = ObjectId.from(uiState.selectedDiaryId!!))
                if (result is RequestState.Success) {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else if (result is RequestState.Error) {
                    withContext(Dispatchers.Main) {
                        onError(result.error.message.toString())
                    }
                }
            }
        }
    }

    fun addImage(image: Uri, imageType: String) {
        val remoteImagePath = "images/${FirebaseAuth.getInstance().currentUser?.uid}/" +
                "${image.lastPathSegment}-${System.currentTimeMillis()}.$imageType"
        Log.d("WriteViewModel", remoteImagePath)
        galleryState.addImage(
            GalleryImage(
                image = image,
                remoteImagePath = remoteImagePath
            )
        )
    }


    private fun uploadImageToFirebase() {
        val storage = FirebaseStorage.getInstance().reference
        galleryState.images.forEach { galleryImage ->
            val imagePath = storage.child(galleryImage.remoteImagePath)
            imagePath.putFile(galleryImage.image)
        }
    }


    private fun extractImagePath(fullImageUrl: String): String {
        val chunks = fullImageUrl.split("%2F")
        val imageName = chunks[2].split("?").first()
        return "images/${Firebase.auth.currentUser?.uid}/$imageName"
    }


}


//UI Properties for our WriteScreen
data class UiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val updatedDateTime: RealmInstant? = null
)