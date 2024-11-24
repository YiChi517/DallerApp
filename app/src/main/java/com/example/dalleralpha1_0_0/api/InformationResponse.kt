package com.example.dalleralpha1_0_0.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Information(
    val id: String,
    val levelNumber: Int,
    val gameTitle: String,
    val difficulty: String,
    val description: String,
    val reward: Int
) : Parcelable

