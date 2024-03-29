package com.example.compose_diaryapp.data.repository

import com.example.compose_diaryapp.model.Diary
import com.example.compose_diaryapp.model.RequestState
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.flow.Flow

import java.time.LocalDate
typealias Diaries = RequestState<Map<LocalDate, List<Diary>>>

interface MongoRepository {
    fun configureTheRealm()
    fun getAllDiaries(): Flow<Diaries>
    fun getSelectedDiary(diaryId: ObjectId): Flow<RequestState<Diary>>

    suspend fun insertDiary(diary: Diary): RequestState<Diary>
    suspend fun updateDiary(diary: Diary): RequestState<Diary>
    suspend fun deleteDiary(id: ObjectId): RequestState<Diary>


}