package com.example.compose_diaryapp.data.repository

import com.example.compose_diaryapp.model.Diary
import com.example.compose_diaryapp.util.RequestState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

typealias Diaries = RequestState<Map<LocalDate, List<Diary>>>
interface MongoRepository {
    fun configureTheRealm()
    fun getAllDiaries(): Flow<Diaries>
}