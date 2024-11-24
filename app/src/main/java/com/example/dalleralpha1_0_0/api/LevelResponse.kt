package com.example.dalleralpha1_0_0.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Question(
    val id: String,
    val levelId: String,
    val questionText: String,
    val correctAnswer: String,
    val options1: String,
    val options2: String,
    val options3: String,
    val options4: String
) : Parcelable

typealias QuestionsResponse = List<Question>