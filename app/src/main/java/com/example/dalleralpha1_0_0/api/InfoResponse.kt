package com.example.dalleralpha1_0_0.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Info(
    val success:String,
    val email:String,
    val username:String,
    val score:Int,
    val level:String
): Parcelable
